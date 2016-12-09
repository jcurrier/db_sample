package com.example.todo.providers.memory;

import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import com.example.todo.exceptions.ServiceException;
import com.example.todo.providers.UserServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Jeff on 11/12/16.
 */
public class MemUserServiceProvider implements UserServiceProvider {
    private HashMap<String, User> m_userMap = new HashMap<String, User>();

    public MemUserServiceProvider() {
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("admin");

        User seedUser = new User("1", "password", roles);
        m_userMap.put(seedUser.getName(), seedUser);
    }

    @Override
    public User createUser(User user) throws ServiceException {

        m_userMap.put(user.getName(), user);
        return user;
    }

    @Override
    public void deleteUser(String userId) throws NotFoundException, ServiceException {

        if(m_userMap.containsKey(userId)) {
            m_userMap.remove(userId);
        } else {
            throw new NotFoundException("Unable to locate user id: " + userId);
        }
    }

    @Override
    public User updateUser(User user) throws NotFoundException, ServiceException {

        if(m_userMap.containsKey(user.getName())) {
            m_userMap.replace(user.getName(), user);
        } else {
            throw new NotFoundException("Unable to locate user id: "+user.getName());
        }

        return user;
    }

    @Override
    public User findUser(String userId) throws NotFoundException, ServiceException {

        if(m_userMap.containsKey(userId)) {
            return m_userMap.get(userId);
        } else {
            throw new NotFoundException("Unable to locate user id: " + userId);
        }
    }
}
