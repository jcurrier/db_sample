package com.example.todo.auth;

import com.example.todo.data.User;
import io.dropwizard.auth.Authorizer;

/**
 * Created by Jeff on 11/20/16.
 */
public class TodoAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return true;
    }
}
