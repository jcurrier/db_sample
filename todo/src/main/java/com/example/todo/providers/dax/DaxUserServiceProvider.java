package com.example.todo.providers.dax;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.UserServiceProvider;
import com.example.todo.util.ClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jeff on 11/12/16.
 */
public class DaxUserServiceProvider implements UserServiceProvider {

    private final String USER_TABLE_NAME = "Users";
    private DynamoDB m_dynamo = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(DaxUserServiceProvider.class);

    public DaxUserServiceProvider(String clusterUrl) {

        LOGGER.info("Creating DAX client");
        LOGGER.info("DAX Cluster = " + clusterUrl);
        m_dynamo = new DynamoDB(ClientHelper.instance().getDAXClient(clusterUrl));
    }

    @Override
    public User createUser(User user) throws ServiceException {

        try {
            Table userTable = m_dynamo.getTable(USER_TABLE_NAME);
            Item userItem = new Item()
                .withPrimaryKey("id", user.getId())
                .withString("UserId", user.getId())
                .withString("UserName", user.getUserName())
                .withString("Password", user.getPassword());

            if(user.getRoles() != null) {
                List<String> roles = user.getRoles();
                userItem.withList("UserRoles", user.getRoles());
            }

            userTable.putItem(userItem);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException("Error creating user", ex);
        }

        return user;
    }

    @Override
    public void deleteUser(String userId) throws NotFoundException, ServiceException {

        DeleteItemOutcome result = null;

        try {

            Table userTable = m_dynamo.getTable(USER_TABLE_NAME);

            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey("id", userId)
                    .withConditionExpression("UserId = :val")
                    .withValueMap(new ValueMap()
                            .withString(":val", userId))
                    .withReturnValues(ReturnValue.ALL_OLD);

            result = userTable.deleteItem(deleteItemSpec);
        }catch (ConditionalCheckFailedException ex) {
            if(ex.getErrorCode().equals("ConditionalCheckFailedException")) {
                throw new NotFoundException("Unable to locate user " + userId);
            }
        }catch (Exception ex) {
            throw new ServiceException("delete failed", ex);
        }

        System.out.println("result is " + result.getDeleteItemResult().toString());
    }

    @Override
    public User updateUser(User user) throws NotFoundException, ServiceException {

        User oldUser = findUser(user.getId());

        try {
            Table userTable = m_dynamo.getTable(USER_TABLE_NAME);

            UpdateItemSpec updateItemSpec = null;
            UpdateItemOutcome outcome = null;

            updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", user.getId())
                .withUpdateExpression("set UserId = :user_id, UserName = :name, Password = :pw, UserRoles = :roles")
                        .withValueMap(new ValueMap()
                                .withString(":user_id", user.getId())
                                .withString(":name", user.getUserName())
                                .withString(":pw", user.getPassword())
                                .withStringSet(":roles", new HashSet<String>(user.getRoles()))
                        )
                        .withReturnValues(ReturnValue.ALL_OLD);

            outcome = userTable.updateItem(updateItemSpec);
            outcome.getUpdateItemResult().getAttributes();
        }catch (ConditionalCheckFailedException ex) {
            if (ex.getErrorCode().equals("ConditionalCheckFailedException")) {
                throw new NotFoundException("Unable to locate user " + user.getId());
            }
        }catch (Exception ex) {
            throw new ServiceException("delete failed", ex);
        }

        return user;
    }

    @Override
    public User findUser(String userId) throws NotFoundException, ServiceException {
        User foundUser = null;
        boolean itemFound = false;

        try {
            LOGGER.info("Attempting to retrieve User table");

            Table userTable = m_dynamo.getTable(USER_TABLE_NAME);
            if(userTable == null) {
                LOGGER.info("failed to retrieve user Table!!!");
            }

            LOGGER.info("Attempting to get Dynamo user item with id " + userId);
            Item item = userTable.getItem("id", userId,  "UserId, UserName, Password, UserRoles", null);

            if(item != null) {
                ObjectMapper mapper = new ObjectMapper();
                String s = item.toJSONPretty();
                foundUser = mapper.readValue(item.toJSON(), User.class);
                itemFound = true;
            } else {
                LOGGER.info("Got back a null user item");
                itemFound = false;
            }

        } catch (Exception ex) {
            LOGGER.info("Caught exception while attempting to retrieve user item", ex);
            throw new ServiceException("Error creating user", ex);
        }

        if(!itemFound) {
            throw new NotFoundException("User: " + userId + " not found!");
        }
        return foundUser;
    }
}
