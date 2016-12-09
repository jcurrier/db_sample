package com.example.todo.resources;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.example.todo.TodoServiceConfiguration;
import com.example.todo.api.UserService;
import com.example.todo.data.User;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.exceptions.NotFoundException;
import io.dropwizard.auth.Auth;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jeff on 11/12/16.
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserService m_userSvc = null;
    private TodoServiceConfiguration m_serviceConfig = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    public UserResource(TodoServiceConfiguration config) {
        this.m_serviceConfig = config;
        m_userSvc = new UserService(m_serviceConfig);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User newUser) {

        LOGGER.info("Attempting to create User: " + newUser);

        try {
            User createdUser = m_userSvc.createUser(newUser);
            return Response.ok(createdUser, MediaType.APPLICATION_JSON).build();
        } catch (ServiceException ex) {
            LOGGER.info("Error creating user: ", ex);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") String userId, User user) {

        User updatedUser = null;

        LOGGER.info("Updating user with id:" + userId + "with new details" + user);

        try {
            if(user == null ||
               user.getId().equals(userId) != true) {

                LOGGER.info("Invalid user object passed to User::update");
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            updatedUser = m_userSvc.updateUser(user);

        } catch(NotFoundException ex) {
            LOGGER.info("Unable to find user with id:" + userId);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ServiceException ex) {
            LOGGER.info("Error attempting to update user", ex);
            return Response.serverError().build();
        }

        return Response.ok(updatedUser, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") String userId) {
        User foundUser = null;

        LOGGER.info("Attempting to retrieve user with id:" + userId);

        try {
            foundUser = m_userSvc.findUser(userId);
        } catch(NotFoundException ex) {
            LOGGER.info("Unable to find user with id:" + userId);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ServiceException ex) {
            LOGGER.info("Error attempting to retrieve user", ex);
            return Response.serverError().build();
        }

        return Response.ok(foundUser, MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("{id}")
    public Response removeUser(@PathParam("id") String id) {

        LOGGER.info("Removing user with id:" + id);
        try {

            m_userSvc.deleteUser(id);

            return Response.ok().build();
        } catch(NotFoundException ex) {
            LOGGER.info("Unable to find user with id:" + id);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ServiceException ex) {
            LOGGER.info("Error attempting to remove user" ,ex);
            return Response.serverError().build();
        }
    }
}
