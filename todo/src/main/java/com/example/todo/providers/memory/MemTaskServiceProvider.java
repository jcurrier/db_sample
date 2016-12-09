package com.example.todo.providers.memory;

import com.example.todo.api.OperationContext;
import com.example.todo.common.TaskQueryType;
import com.example.todo.data.Task;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.TaskServiceProvider;
import org.glassfish.hk2.api.Operation;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jeff on 11/14/16.
 */
public class MemTaskServiceProvider implements TaskServiceProvider {
    private HashMap<String, Task> m_tasks = new HashMap<>();
    private HashMap<String, ArrayList<Task>> m_userTaskIndex = new HashMap<>();

   public MemTaskServiceProvider() {
        Task t = new Task("Create TPS Report", "Create that damn TPS report", DateTime.now(),
                "2", "1", "Open", DateTime.now(), DateTime.now());
        t.setId("1");
        m_tasks.put(t.getId(), t);

        ArrayList<Task> taskIndex = new ArrayList<>();
        taskIndex.add(t);
        m_userTaskIndex.put(t.getOwnerId(), taskIndex);
    }

    @Override
    public Task createTask(OperationContext ctx, Task newTask) throws ServiceException {

        newTask.setId(UUID.randomUUID().toString());
        m_tasks.put(newTask.getId(), newTask);
        String ownerId = newTask.getOwnerId();

        if(m_userTaskIndex.containsKey(ownerId)) {
            m_userTaskIndex.get(ownerId).add(newTask);
        } else {
            ArrayList<Task> taskIndex = new ArrayList<>();
            taskIndex.add(newTask);

            m_userTaskIndex.put(ownerId, taskIndex);
        }

        return newTask;
    }

    @Override
    public void deleteTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {

        if(m_tasks.containsKey(taskId)) {
            String ownerId = m_tasks.get(taskId).getOwnerId();
            m_userTaskIndex.get(ownerId).remove(taskId);
            m_tasks.remove(taskId);
        } else {
            throw new NotFoundException("Unable to find task");
        }
    }

    @Override
    public Task updateTask(OperationContext ctx, Task taskToUpdate) throws NotFoundException, ServiceException {

        if(m_tasks.containsKey(taskToUpdate.getId())) {

            m_tasks.replace(taskToUpdate.getId(), taskToUpdate);

            ArrayList<Task> data = m_userTaskIndex.get(taskToUpdate.getOwnerId());

            for(int index=0;index<data.size();index++) {
                if(data.get(index).getId().equals(taskToUpdate.getId())) {
                    data.add(taskToUpdate);
                    data.remove(index);
                    break;
                }
            }

            return taskToUpdate;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public Task getTask(OperationContext ctx, String taskId) throws NotFoundException, ServiceException {

        if(m_tasks.containsKey(taskId)) {
            return m_tasks.get(taskId);
        } else {
            throw new NotFoundException("Unable to find task");
        }
    }

    @Override
    public List<Task> getTasks(OperationContext ctx, TaskQueryType queryType) throws NotFoundException, ServiceException {

        /*
        if(m_userTaskIndex.containsKey(userId)) {
            return m_userTaskIndex.get(userId);
        } else {
            throw new NotFoundException("Unable to find task");
        }
        */

        return null;
    }
}
