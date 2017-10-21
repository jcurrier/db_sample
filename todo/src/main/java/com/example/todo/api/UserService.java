package com.example.todo.api;

import com.example.todo.TodoServiceConfiguration;
import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.UserServiceProvider;
import com.example.todo.providers.dax.DaxUserServiceProvider;
import com.example.todo.providers.dynamo.DynamoUserServiceProvider;
import com.example.todo.providers.memory.MemUserServiceProvider;
import com.example.todo.providers.mongo.MongoUserServiceProvider;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by Jeff on 11/12/16.
 */
public class UserService {
    private TodoServiceConfiguration m_config = null;
    private UserServiceProvider m_svcProvider = null;

    public UserService(TodoServiceConfiguration config) {
        this.m_config = config;

        initalize();
    }

    public User createUser(User newUser) throws ServiceException {
        User createdUser = null;

        if(newUser.getId() == null ||
           newUser.getId().isEmpty()) {
            newUser.setId(UUID.randomUUID().toString());
        }

        createdUser =  m_svcProvider.createUser(newUser);

        return createdUser;
    }

    public User updateUser(User updatedUser) throws NotFoundException, ServiceException {

        return m_svcProvider.updateUser(updatedUser);
    }

    public void deleteUser(String userToRemove) throws NotFoundException, ServiceException {

        m_svcProvider.deleteUser(userToRemove);
    }

    public User findUser(String userId) throws NotFoundException, ServiceException {

        User user = null;

        user = m_svcProvider.findUser(userId);

        return user;
    }

    private void initalize() {

        //TODO: This should really be done with dependency injection.  doing this for now though.
        switch(m_config.getStore()) {
            case "memory": {
                m_svcProvider = new MemUserServiceProvider();
                break;
            }
            case "dynamo": {
                m_svcProvider = new DynamoUserServiceProvider();
                break;
            }
            case "dax": {
                m_svcProvider = new DaxUserServiceProvider(m_config.getDaxCluster());
                break;
            }
            case "mongo": {
                m_svcProvider = new MongoUserServiceProvider();
                break;
            }
            default: {
                m_svcProvider = new MemUserServiceProvider();
                break;
            }
        }
    }
}
