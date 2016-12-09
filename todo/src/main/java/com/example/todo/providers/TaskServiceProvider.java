package com.example.todo.providers;

import com.example.todo.api.OperationContext;
import com.example.todo.common.TaskQueryType;
import com.example.todo.data.Task;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.google.common.util.concurrent.Service;

import java.util.List;

/**
 * Created by Jeff on 11/14/16.
 */
public interface TaskServiceProvider {

    public Task createTask(OperationContext ctx, Task newTask) throws ServiceException;

    public void deleteTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException;

    public Task updateTask(OperationContext ctx, Task taskToUpdate) throws NotFoundException, ServiceException;

    public Task getTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException;

    public List<Task> getTasks(OperationContext ctx, TaskQueryType queryType) throws NotFoundException, ServiceException;
}
