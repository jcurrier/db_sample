package com.example.todo.auth;

import com.example.todo.TodoServiceConfiguration;
import com.example.todo.api.UserService;
import com.example.todo.data.User;
import com.example.todo.exceptions.NotFoundException;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by Jeff on 11/20/16.
 */
public class TodoAuthenticator implements Authenticator<BasicCredentials, User> {
    private TodoServiceConfiguration m_config = null;
    private UserService m_svc = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoAuthenticator.class);

    public TodoAuthenticator(TodoServiceConfiguration configuration) {
        this.m_config = configuration;
        m_svc = new UserService(configuration);
    }

    @Override
    public Optional<User> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {

        LOGGER.info("Attempting to authenticate user" + basicCredentials.getUsername());
        try {
            User user = m_svc.findUser(basicCredentials.getUsername());

            LOGGER.info("User located attempting to validate creds");

            if(user.getPassword().equals(basicCredentials.getPassword())) {
                LOGGER.info("User authenticated!");
                return Optional.of(user);
            }

            LOGGER.info("User is *NOT* authenticated");
        }catch(Exception ex) {
            LOGGER.info("Error encountered while authenticating", ex);
            return Optional.empty();
        }

        return Optional.empty();
    }
}
