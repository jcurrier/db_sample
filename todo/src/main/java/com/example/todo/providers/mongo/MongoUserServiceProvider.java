package com.example.todo.providers.mongo;

import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.UserServiceProvider;
import com.example.todo.util.ClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Jeff on 12/19/16.
 */
public class MongoUserServiceProvider implements UserServiceProvider {
    private final String DB_NAME="sample_db";
    private final String COLLECTION_NAME = "Users";
    private MongoClient m_client = null;
    private MongoDatabase m_db = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoUserServiceProvider.class);

    public MongoUserServiceProvider() {
        LOGGER.info("Creating Mongo client");

        try {
            MongoClient m_client = ClientHelper.instance().getMongoClient();
            m_db = m_client.getDatabase("sample_db");
        }catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to create mongo client", ex);
        }
    }
    @Override
    public User createUser(User user) throws ServiceException {

        HashMap<String, Object> newUser = new HashMap<String, Object>();
        newUser.put("_id", user.getId());
        newUser.put("UserId", user.getId());
        newUser.put("UserName", user.getUserName());
        newUser.put("Password", user.getPassword());

        if(user.getRoles() != null) {
            newUser.put("UserRoles", user.getRoles());
        }

        try {
            MongoCollection<Document> userCollection = m_db.getCollection(COLLECTION_NAME);
            userCollection.insertOne(new Document(newUser));
        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to create user", ex);
        }

        return user;
    }

    @Override
    public void deleteUser(String userId) throws NotFoundException, ServiceException {
        boolean itemFound = false;

        try {
            MongoCollection<Document> userCollection = m_db.getCollection(COLLECTION_NAME);

            Document foundUserDoc = userCollection.findOneAndDelete(eq("UserId", userId));

            if(foundUserDoc != null) {
                itemFound = true;
            } else {
                LOGGER.info("Got back a null user item");
                itemFound = false;
            }

        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to delete user", ex);
        }

        if(!itemFound) {
            throw new NotFoundException("User: " + userId + " not found!");
        }
    }

    @Override
    public User updateUser(User user) throws NotFoundException, ServiceException {
        boolean itemFound = false;

        HashMap<String, Object> updatedUser = new HashMap<String, Object>();
        updatedUser.put("_id", user.getId());
        updatedUser.put("UserId", user.getId());
        updatedUser.put("UserName", user.getUserName());
        updatedUser.put("Password", user.getPassword());

        if(user.getRoles() != null) {
            updatedUser.put("UserRoles", user.getRoles());
        }

        try {
            MongoCollection<Document> userCollection = m_db.getCollection(COLLECTION_NAME);

            Document result = userCollection.findOneAndReplace(eq("UserId", user.getId()), new Document(updatedUser));
            if(result != null) {
                itemFound = true;
            } else {
                LOGGER.info("Got back a null user item");
                itemFound = false;
            }

        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to delete user", ex);
        }

        if(!itemFound) {
            throw new NotFoundException("User: " + user.getId() + " not found!");
        }
        return user;
    }

    @Override
    public User findUser(String userId) throws NotFoundException, ServiceException {

        User foundUser = null;
        boolean itemFound = false;

        try {
            MongoCollection<Document> userCollection = m_db.getCollection(COLLECTION_NAME);

            Document foundUserDoc = userCollection.find(eq("UserId", userId)).first();

            if(foundUserDoc != null) {
                ObjectMapper mapper = new ObjectMapper();
                String s = foundUserDoc.toJson();
                foundUser = mapper.readValue(s, User.class);
                itemFound = true;
            } else {
                LOGGER.info("Got back a null user item");
                itemFound = false;
            }

        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to find user", ex);
        }

        if(!itemFound) {
            throw new NotFoundException("User: " + userId + " not found!");
        }

        return foundUser;
    }
}
