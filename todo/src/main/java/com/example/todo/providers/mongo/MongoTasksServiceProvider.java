package com.example.todo.providers.mongo;

import com.example.todo.api.OperationContext;
import com.example.todo.common.TaskQueryType;
import com.example.todo.data.Task;
import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.TaskServiceProvider;
import com.example.todo.util.ClientHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Jeff on 12/19/16.
 */
public class MongoTasksServiceProvider implements TaskServiceProvider {
    private final String DB_NAME="sample_db";
    private final String COLLECTION_NAME = "Tasks";
    private MongoClient m_client = null;
    private MongoDatabase m_db = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoTasksServiceProvider.class);

    public MongoTasksServiceProvider() {
        LOGGER.info("Creating Mongo client");

        try {
            MongoClient m_client = ClientHelper.instance().getMongoClient();
            m_db = m_client.getDatabase("sample_db");
        }catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to create mongo client", ex);
        }
    }

    @Override
    public Task createTask(OperationContext ctx, Task newTask) throws ServiceException {

        try {
            newTask.setCreatedOn(DateTime.now().toString());
            newTask.setLastUpdated(DateTime.now().toString());

            HashMap<String, Object> taskProps = newTask.toHashMap();
            Document taskDoc = new Document(taskProps);

            MongoCollection<Document> tasksCollection = m_db.getCollection(COLLECTION_NAME);
            tasksCollection.insertOne(new Document(taskDoc));

        } catch(Exception ex) {
            LOGGER.error("Caught exception creating task", ex);
        }

        return newTask;
    }

    @Override
    public void deleteTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {
        boolean itemFound = false;

        LOGGER.info("Attempting to delete task. Task id->{"+taskId+"}");

        try {
            MongoCollection<Document> tasksCollection = m_db.getCollection(COLLECTION_NAME);

            Document foundTaskDoc = tasksCollection.findOneAndDelete(eq("Id", taskId));

            if(foundTaskDoc != null) {
                itemFound = true;
            } else {
                LOGGER.info("Got back a null user task");
                itemFound = false;
            }

        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to delete task", ex);
        }

        if(!itemFound) {
            throw new NotFoundException("Task: " + taskId + " not found!");
        }
    }

    @Override
    public Task updateTask(OperationContext ctx, Task taskToUpdate) throws NotFoundException, ServiceException {

        boolean itemFound = false;

        try {
            //update lastUpate time.
            taskToUpdate.setLastUpdated(DateTime.now().toString());

            HashMap<String, Object> taskProps = taskToUpdate.toHashMap();
            Document updatedTask = new Document(taskProps);

            MongoCollection<Document> tasksCollection = m_db.getCollection(COLLECTION_NAME);

            Document result = tasksCollection.findOneAndUpdate(eq("Id", taskToUpdate.getId()),
                    new Document("$set", updatedTask));
            if(result != null) {
                itemFound = true;
            } else {
                LOGGER.info("Got back a null task item");
                itemFound = false;
            }

        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to update task", ex);
        }

        if(!itemFound) {
            throw new NotFoundException("Task: " + taskToUpdate.getId() + " not found!");
        }

        return taskToUpdate;
    }

    @Override
    public Task getTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {
        Task foundTask = null;
        boolean itemFound = false;

        try {
            MongoCollection<Document> tasksCollection = m_db.getCollection(COLLECTION_NAME);

            Document foundTaskDoc = tasksCollection.find(eq("Id", taskId)).first();

            if(foundTaskDoc != null) {
                ObjectMapper mapper = new ObjectMapper();
                String s = foundTaskDoc.toJson();
                foundTask = mapper.readValue(s, Task.class);
                itemFound = true;
            } else {
                LOGGER.info("Got back a null task item");
                itemFound = false;
            }

        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to find task", ex);
        }

        if(!itemFound) {
            throw new NotFoundException("Task: " + taskId + " not found!");
        }

        return foundTask;
    }

    @Override
    public List<Task> getTasks(OperationContext ctx, TaskQueryType queryType) throws NotFoundException, ServiceException {

        List<Task> tasks = new ArrayList<Task>();
        try {
            MongoCollection<Document> tasksCollection = m_db.getCollection(COLLECTION_NAME);
            HashMap<String, Object> queryVals = new HashMap<>();

            Document queryPredicates;
            if(queryType == TaskQueryType.OwnedTask) {
                queryVals.put("TaskOwner", ctx.getSecurityContext().getUserPrincipal().getName());
                queryPredicates = new Document(queryVals);
            } else if(queryType == TaskQueryType.AssignedTask) {
                queryVals.put("Assignee", ctx.getSecurityContext().getUserPrincipal().getName());
                queryPredicates = new Document(queryVals);
            } else {
                Document predOne = new Document("TaskOwner", ctx.getSecurityContext().getUserPrincipal().getName());
                Document predTwo = new Document("Assignee", ctx.getSecurityContext().getUserPrincipal().getName());

                BasicDBList queryClauses = new BasicDBList();
                queryClauses .add(predOne);
                queryClauses .add(predTwo);
                queryPredicates = new Document("$or", queryClauses);
            }

            FindIterable result = tasksCollection.find(new Document(queryPredicates));
            MongoCursor cursor = result.iterator();
            ObjectMapper mapper = new ObjectMapper();
            while(cursor.hasNext()) {
                Document taskDoc = (Document) cursor.next();
                Task t = mapper.readValue(taskDoc.toJson(), Task.class);
                tasks.add(t);
            }
            cursor.close();

        } catch(Exception ex) {
            LOGGER.error("Caught exception while attempting to find task", ex);
        }
        return tasks;
    }

}
