package com.example.todo.providers;

import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.google.common.util.concurrent.Service;

/**
 * Created by Jeff on 11/12/16.
 */
public interface UserServiceProvider {

    public User createUser(User user) throws ServiceException;

    public void deleteUser(String userId) throws NotFoundException, ServiceException;

    public User updateUser(User user) throws NotFoundException, ServiceException;

    public User findUser(String userId) throws NotFoundException, ServiceException;
}
