package com.example.todo.api;

import com.example.todo.TodoServiceConfiguration;
import com.example.todo.common.TaskQueryType;
import com.example.todo.data.Task;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.TaskServiceProvider;
import com.example.todo.providers.dynamo.DynamoTasksServiceProvider;
import com.example.todo.providers.dax.DaxTasksServiceProvider;
import com.example.todo.providers.memory.MemTaskServiceProvider;
import com.example.todo.providers.mongo.MongoTasksServiceProvider;

import java.util.List;

/**
 * Created by Jeff on 11/14/16.
 */
public class TaskService {
    private TodoServiceConfiguration m_config = null;
    private TaskServiceProvider m_svcProvider = null;

    public TaskService(TodoServiceConfiguration config) {
        this.m_config = config;

        initalize();
    }

    public Task createTask(OperationContext ctx, Task newTask) throws ServiceException {

        return m_svcProvider.createTask(ctx, newTask);
    }

    public Task updateTask(OperationContext ctx, Task updatedTask) throws NotFoundException, ServiceException {
        return m_svcProvider.updateTask(ctx, updatedTask);
    }

    public void deleteTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {
        m_svcProvider.deleteTask(ctx, taskId);
    }

    public Task getTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {
        return m_svcProvider.getTask(ctx, taskId);
    }

    public List<Task> getTasks(OperationContext secCtx, TaskQueryType queryType) throws NotFoundException, ServiceException {
        return m_svcProvider.getTasks(secCtx, queryType);
    }

    private void initalize() {

        //TODO: This should really be done with dependency injection.  doing this for now though.
        switch(m_config.getStore()) {
            case "memory": {
                m_svcProvider = new MemTaskServiceProvider();
                break;
            }
            case "dynamo": {
                m_svcProvider = new DynamoTasksServiceProvider();
                break;
            }
            case "dax": {
                m_svcProvider = new DaxTasksServiceProvider();
                break;
            }
            case "mongo": {
                m_svcProvider = new MongoTasksServiceProvider();
                break;
            }
            default: {
                //m_svcProvider = new MemTaskServiceProvider();
                break;
            }
        }
    }
}
