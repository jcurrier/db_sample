package com.example.todo.providers.mongo;

import com.example.todo.api.OperationContext;
import com.example.todo.common.TaskQueryType;
import com.example.todo.data.Task;
import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.TaskServiceProvider;

import java.util.List;

/**
 * Created by Jeff on 12/19/16.
 */
public class MongoTasksServiceProvider implements TaskServiceProvider {

    @Override
    public Task createTask(OperationContext ctx, Task newTask) throws ServiceException {
        return null;
    }

    @Override
    public void deleteTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {

    }

    @Override
    public Task updateTask(OperationContext ctx, Task taskToUpdate) throws NotFoundException, ServiceException {
        return null;
    }

    @Override
    public Task getTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {
        return null;
    }

    @Override
    public List<Task> getTasks(OperationContext ctx, TaskQueryType queryType) throws NotFoundException, ServiceException {
        return null;
    }
}
