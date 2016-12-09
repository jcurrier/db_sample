package com.example.todo.resources;

import com.example.todo.TodoServiceConfiguration;
import com.example.todo.api.OperationContext;
import com.example.todo.api.TaskService;
import com.example.todo.common.TaskQueryType;
import com.example.todo.data.Task;
import com.example.todo.data.User;
import com.example.todo.exceptions.*;
import com.example.todo.exceptions.NotFoundException;
import io.dropwizard.auth.Auth;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 11/12/16.
 */
@Path("/tasks")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
public class TasksResource {

    private TodoServiceConfiguration m_config = null;
    private TaskService m_svc = null;

    public TasksResource(TodoServiceConfiguration config) {
        this.m_config = config;
        m_svc = new TaskService(m_config);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTask(@Context SecurityContext secCtx, Task newTask) {
        Task task = null;

        try {

            task = m_svc.createTask(new OperationContext(secCtx), newTask);

            return Response.ok(task, MediaType.APPLICATION_JSON).build();

        }catch (Exception ex) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("{id}")
    public Response getTask(@Context SecurityContext secCtx, @PathParam("id") String taskId) {

        try {
            Task task = m_svc.getTask(new OperationContext(secCtx), taskId);

            return Response.ok(task, MediaType.APPLICATION_JSON).build();

        } catch (NotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ServiceException ex) {
            return Response.serverError().build();
        }
    }

    @GET
    public Response getTasks(@Context SecurityContext secCtx, @QueryParam("type") String taskQueryType) {

        try {
            TaskQueryType queryType = TaskQueryType.AssignedTask;

            if(taskQueryType.equals(("owned"))) {
               queryType = TaskQueryType.OwnedTask;
            }

            List<Task> tasks = m_svc.getTasks(new OperationContext(secCtx), queryType);

            return Response.ok(tasks, MediaType.APPLICATION_JSON).build();

        } catch (NotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ServiceException ex) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteTask(@Context SecurityContext secCtx, @PathParam("id") String taskId)
            throws NotFoundException, ServiceException {
        try {
            m_svc.deleteTask(new OperationContext(secCtx), taskId);

            return Response.ok().build();

        } catch (NotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ServiceException ex) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response updateTask(@Context SecurityContext secCtx, @PathParam("id") String taskId, Task updatedTask) {
        Task refreshedTask = null;

        try {
            refreshedTask = m_svc.updateTask(new OperationContext(secCtx), updatedTask);

            return Response.ok(refreshedTask, MediaType.APPLICATION_JSON).build();
        } catch (NotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception ex) {
            return Response.serverError().build();
        }
    }
}
