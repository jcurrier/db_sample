package com.example.todo.health;

import com.codahale.metrics.health.HealthCheck;

/**
 * Created by Jeff on 11/12/16.
 */
public class ServiceHealthCheck extends HealthCheck {

    public ServiceHealthCheck() {
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
