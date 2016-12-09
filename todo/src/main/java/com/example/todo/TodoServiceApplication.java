package com.example.todo;

import com.example.helloworld.HelloWorldConfiguration;
import com.example.todo.auth.TodoAuthenticator;
import com.example.todo.auth.TodoAuthorizer;
import com.example.todo.data.User;
import com.example.todo.health.ServiceHealthCheck;
import com.example.todo.resources.LoginResource;
import com.example.todo.resources.LogoutResource;
import com.example.todo.resources.TasksResource;
import com.example.todo.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.example.helloworld.HelloWorldConfiguration;
import com.example.todo.auth.TodoAuthenticator;
import com.example.todo.auth.TodoAuthorizer;
import com.example.todo.data.User;
import com.example.todo.health.ServiceHealthCheck;
import com.example.todo.resources.LoginResource;
import com.example.todo.resources.LogoutResource;
import com.example.todo.resources.TasksResource;
import com.example.todo.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Created by Jeff on 11/12/16.
 */
public class TodoServiceApplication extends Application<TodoServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new TodoServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "todo-svc";
    }

    @Override
    public void initialize(Bootstrap<TodoServiceConfiguration> bootstrap) {

        /*
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        */
    }

    @Override
    public void run(TodoServiceConfiguration configuration, Environment environment) {

        // register health checks
        environment.healthChecks().register("todo", new ServiceHealthCheck());

        // register resources
        environment.jersey().register(new TasksResource(configuration));
        environment.jersey().register(new UserResource(configuration));

        String store = configuration.getStore();
        if(store.equalsIgnoreCase("dynamo")) {
            environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                    .setAuthenticator(new TodoAuthenticator(configuration))
                    .setAuthorizer(new TodoAuthorizer())
                    .setRealm("TODO_REALM")
                    .buildAuthFilter()));
        }

        /*
        environment.healthChecks().register("template", new TemplateHealthCheck(template));
        environment.admin().addTask(new EchoTask());
        environment.jersey().register(DateRequiredFeature.class);
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new ToDoAuthenticator())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new ViewResource());
        environment.jersey().register(new FilteredResource());
        */
    }
}
