package com.example.todo.providers.dax;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.example.todo.api.OperationContext;
import com.example.todo.common.TaskQueryType;
import com.example.todo.data.Task;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.TaskServiceProvider;
import com.example.todo.util.ClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jeff on 11/12/16.
 */
public class DaxTasksServiceProvider implements TaskServiceProvider {

    private final String TASKS_TABLE_NAME = "Tasks";
    private final String ASSIGNED_TASKS_IDX = "Assignee-TaskState-index";
    private final String OWNED_TASKS_IDX = "TaskOwner-TaskState-index";
    private DynamoDB m_dynamo = null;

    public DaxTasksServiceProvider() {

        // This client will default to US West (Oregon)
        AmazonDynamoDBClient client = ClientHelper.instance().getDynamoClient();
        //AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
        // client.setRegion(Region.getRegion(Regions.US_WEST_2));
        m_dynamo = new DynamoDB(client);
    }

    @Override
    public Task createTask(OperationContext ctx, Task newTask) throws ServiceException {

        try {
            Table tasksTable = m_dynamo.getTable(TASKS_TABLE_NAME);
            Item taskItem = new Item()
                    .withPrimaryKey("UserId", newTask.getAssignedUserId())
                    .withString("Id", newTask.getId())
                    .withString("Title", newTask.getTitle())
                    .withString("Description", newTask.getDescription())
                    .withString("TaskOwner", newTask.getOwnerId())
                    .withString("Assignee", newTask.getAssignedUserId())
                    .withString("DueDate", newTask.getDueDate())
                    .withString("TaskState", newTask.getState())
                    .withString("CreatedOn", DateTime.now().toString())
                    .withString("LastUpdated", DateTime.now().toString());

            tasksTable.putItem(taskItem);

            if(!newTask.getAssignedUserId().equals(newTask.getOwnerId())) {
                taskItem = new Item()
                        .withPrimaryKey("UserId", newTask.getOwnerId())
                        .withString("Id", newTask.getId())
                        .withString("Title", newTask.getTitle())
                        .withString("Description", newTask.getDescription())
                        .withString("TaskOwner", newTask.getOwnerId())
                        .withString("Assignee", newTask.getAssignedUserId())
                        .withString("DueDate", newTask.getDueDate())
                        .withString("TaskState", newTask.getState())
                        .withString("CreatedOn", DateTime.now().toString())
                        .withString("LastUpdated", DateTime.now().toString());

                tasksTable.putItem(taskItem);
            }
        } catch (Exception ex) {
            throw new ServiceException("Error creating task", ex);
        }

        return newTask;
    }

    @Override
    public void deleteTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {

        Task taskToDelete = getTask(ctx, taskId);

        String currentUserId = ctx.getSecurityContext().getUserPrincipal().getName();

        if(!taskToDelete.getAssignedUserId().equals(currentUserId) &&
           !taskToDelete.getOwnerId().equals(currentUserId)) {
            // TODO: Fix exception
            throw new ServiceException("Not Authorized");
        }

        try {
            Table tasksTable = m_dynamo.getTable(TASKS_TABLE_NAME);
            boolean duelDeleteRequired = false;

            if(!taskToDelete.getAssignedUserId().equals(taskToDelete.getOwnerId())) {
                duelDeleteRequired = true;
            }

            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new KeyAttribute("UserId", taskToDelete.getOwnerId()),
                                    new KeyAttribute("Id", taskId))
                    .withConditionExpression("Id = :val")
                    .withValueMap(new ValueMap()
                            .withString(":val", taskId))
                    .withReturnValues(ReturnValue.ALL_OLD);

            DeleteItemOutcome outcome = tasksTable.deleteItem(deleteItemSpec);
            Item deletedItem = outcome.getItem();

            if(duelDeleteRequired) {
                deleteItemSpec = new DeleteItemSpec()
                        .withPrimaryKey(new KeyAttribute("UserId", taskToDelete.getAssignedUserId()),
                                new KeyAttribute("Id", taskId))
                        .withConditionExpression("Id = :val")
                        .withValueMap(new ValueMap()
                                .withString(":val", taskId))
                        .withReturnValues(ReturnValue.ALL_OLD);

                outcome = tasksTable.deleteItem(deleteItemSpec);
                deletedItem = outcome.getItem();

            }

        }catch (ConditionalCheckFailedException ex) {
            if(ex.getErrorCode().equals("ConditionalCheckFailedException")) {
                throw new NotFoundException("Unable to locate task " + taskId);
            }
        }catch (Exception ex) {
            throw new ServiceException("delete failed", ex);
        }
    }

    @Override
    public Task updateTask(OperationContext ctx, Task taskToUpdate) throws NotFoundException, ServiceException {
        Task updatedTask = null;

        Task oldTask = getTask(ctx, taskToUpdate.getId());

        try {

            Table tasksTable = m_dynamo.getTable(TASKS_TABLE_NAME);
            UpdateItemSpec updateItemSpec = null;
            UpdateItemOutcome outcome = null;

            if(oldTask.getOwnerId().equals(ctx.getSecurityContext().getUserPrincipal().getName())) {
                updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey(new KeyAttribute("UserId", taskToUpdate.getOwnerId()),
                                    new KeyAttribute("Id", taskToUpdate.getId()))
                    .withConditionExpression("Id = :val")
                    .withUpdateExpression("set Title = :title, Description = :dsc, DueDate = :dd, Assignee = :as, TaskOwner = :o, TaskState = :st, LastUpdated = :lu")
                    .withValueMap(new ValueMap()
                            .withString(":val", taskToUpdate.getId())
                            .withString(":title", taskToUpdate.getTitle())
                            .withString(":dsc", taskToUpdate.getDescription())
                            .withString(":dd", taskToUpdate.getDueDate())
                            .withString(":as", taskToUpdate.getAssignedUserId())
                            .withString(":o", taskToUpdate.getOwnerId())
                            .withString(":st", taskToUpdate.getState())
                            .withString(":lu", DateTime.now().toString())
                            )
                    .withReturnValues(ReturnValue.ALL_NEW);

                outcome = tasksTable.updateItem(updateItemSpec);
            }

            if(oldTask.getAssignedUserId().equals(ctx.getSecurityContext().getUserPrincipal().getName())) {
                updateItemSpec = new UpdateItemSpec()
                        .withPrimaryKey(new KeyAttribute("UserId", taskToUpdate.getAssignedUserId()),
                                new KeyAttribute("Id", taskToUpdate.getId()))
                        .withConditionExpression("Id = :val")
                        .withUpdateExpression("set Title = :title, Description = :dsc, DueDate = :dd, Assignee = :as, TaskOwner = :o, TaskState = :st, LastUpdated = :lu")
                        .withValueMap(new ValueMap()
                                .withString(":val", taskToUpdate.getId())
                                .withString(":title", taskToUpdate.getTitle())
                                .withString(":dsc", taskToUpdate.getDescription())
                                .withString(":dd", taskToUpdate.getDueDate())
                                .withString(":as", taskToUpdate.getAssignedUserId())
                                .withString(":o", taskToUpdate.getOwnerId())
                                .withString(":st", taskToUpdate.getState())
                                .withString(":lu", DateTime.now().toString())
                        )
                        .withReturnValues(ReturnValue.ALL_NEW);

                outcome = tasksTable.updateItem(updateItemSpec);
            }

        }catch (ConditionalCheckFailedException ex) {
            if(ex.getErrorCode().equals("ConditionalCheckFailedException")) {
                throw new NotFoundException("Unable to locate task " + taskToUpdate.getId());
            }
        }catch (Exception ex) {
            throw new ServiceException("update failed", ex);
        }

        return taskToUpdate;
    }

    @Override
    public Task getTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {
        Task foundTask = null;

        try {
            Table tasksTable = m_dynamo.getTable(TASKS_TABLE_NAME);
            String userId = ctx.getSecurityContext().getUserPrincipal().getName();

            QuerySpec spec = new QuerySpec()
                    .withProjectionExpression("Id, Title, Description, DueDate, Assignee, TaskOwner, TaskState, CreatedOn, LastUpdated")
                    .withKeyConditionExpression("UserId = :user_id and Id = :task_id")
                    .withMaxResultSize(1)
                    .withValueMap(new ValueMap()
                            .withString(":user_id", userId)
                            .withString(":task_id", taskId));
            ItemCollection<QueryOutcome> items = tasksTable.query(spec);

            Iterator<Item> itr = items.iterator();
            while(itr.hasNext()) {
                Item item = itr.next();
                ObjectMapper mapper = new ObjectMapper();
                String s = item.toJSONPretty();
                foundTask = mapper.readValue(item.toJSON(), Task.class);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException("Error fetching Task", ex);
        }

        if(foundTask == null) {
            throw new NotFoundException("Unable to locate item " + taskId);
        }

        return foundTask;
    }

    @Override
    public List<Task> getTasks(OperationContext ctx, TaskQueryType queryType)
            throws NotFoundException, ServiceException {
        ArrayList<Task> foundTasks = new ArrayList<Task>();

        try {
            Table tasksTable = m_dynamo.getTable(TASKS_TABLE_NAME);
            String userId = ctx.getSecurityContext().getUserPrincipal().getName();
            QuerySpec spec = null;
            ItemCollection<QueryOutcome> items = null;

            if(queryType == TaskQueryType.AssignedTask) {
                Index assignedTasksIdx = tasksTable.getIndex(ASSIGNED_TASKS_IDX);

                spec = new QuerySpec()
                        .withProjectionExpression("Id, Title, Description, DueDate, Assignee, TaskOwner, TaskState, CreatedOn, LastUpdated")
                        .withKeyConditionExpression("Assignee = :user_id")
                        .withFilterExpression("UserId = :user_id")
                        .withValueMap(new ValueMap()
                                .withString(":user_id", userId));
                items = assignedTasksIdx.query(spec);
            } else if(queryType == TaskQueryType.OwnedTask) {
                Index ownedTasksIdx = tasksTable.getIndex(OWNED_TASKS_IDX);

                spec = new QuerySpec()
                        .withProjectionExpression("Id, Title, Description, DueDate, Assignee, TaskOwner, TaskState, CreatedOn, LastUpdated")
                        .withKeyConditionExpression("TaskOwner = :user_id")
                        .withFilterExpression("UserId = :user_id")
                        .withValueMap(new ValueMap()
                                .withString(":user_id", userId));
                items = ownedTasksIdx.query(spec);
            } else {
                Index assignedTasksIdx = tasksTable.getIndex(ASSIGNED_TASKS_IDX);

                spec = new QuerySpec()
                        .withProjectionExpression("Id, Title, Description, DueDate, Assignee, TaskOwner, TaskState, CreatedOn, LastUpdated")
                        .withKeyConditionExpression("Assignee = :user_id OR TaskOwner = :user_id")
                        .withFilterExpression("UserId = :user_id")
                        .withValueMap(new ValueMap()
                                .withString(":user_id", userId));
                items = assignedTasksIdx.query(spec);
            }

            Iterator<Item> itr = items.iterator();
            while(itr.hasNext()) {
                Item item = itr.next();
                ObjectMapper mapper = new ObjectMapper();
                foundTasks.add(mapper.readValue(item.toJSON(), Task.class));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException("Error fetching Task", ex);
        }

        return foundTasks;
    }
}
