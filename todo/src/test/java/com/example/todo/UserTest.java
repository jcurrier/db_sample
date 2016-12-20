package com.example.todo;

import com.example.helloworld.core.Person;
import com.example.todo.data.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Jeff on 11/13/16.
 */
public class UserTest {
    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-example.yml");

    @ClassRule
    public static final DropwizardAppRule<TodoServiceConfiguration> RULE = new DropwizardAppRule<>(
            TodoServiceApplication.class, CONFIG_PATH);

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testPostAndGetUser() throws Exception {
        ArrayList<String> userRoles = new ArrayList<String>();
        userRoles.add("admin");

        final User newUser = createUser("test user", "password", userRoles);
        final User fetchedUser = fetchUser(newUser.getId());

        assertThat(fetchedUser.getUserName()).isEqualTo(newUser.getUserName());
        assertThat(fetchedUser.getPassword()).isEqualTo(newUser.getPassword());
        assertThat(fetchedUser.getRoles()).isEqualTo(newUser.getRoles());
    }

    @Test
    public void testDeleteUser() throws Exception {
        ArrayList<String> userRoles = new ArrayList<String>();
        userRoles.add("admin");

        final User user = createUser("test user", "pw", userRoles);

        client.target("http://localhost:" + RULE.getLocalPort() + "/user/" + user.getId())
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .delete();

        int stat = client.target("http://localhost:" + RULE.getLocalPort() + "/user/" + user.getId())
                        .request()
                        .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                        .delete()
                        .getStatus();

        assertThat(stat).isEqualTo(HttpStatus.NOT_FOUND_404);
    }

    @Test
    public void testUpdateUser() throws Exception {
        ArrayList<String> userRoles = new ArrayList<String>();
        userRoles.add("admin");

        final User user = createUser("test user", "pw", userRoles);

        userRoles.add("dev");
        user.setRoles(userRoles);

        User updatedUser = client.target("http://localhost:" + RULE.getLocalPort() + "/user/" + user.getId())
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON))
                .readEntity(User.class);

        assertThat(updatedUser.getUserName()).isEqualTo(user.getUserName());
        assertThat(updatedUser.getRoles()).isEqualTo(user.getRoles());

        // Now do a fresh read from the DB to validate the updates.

        updatedUser = fetchUser(user.getId());

        assertThat(updatedUser.getUserName()).isEqualTo(user.getUserName());
        assertThat(updatedUser.getRoles()).isEqualTo(user.getRoles());

        String missingUser = user.getId() + "-foo";

        int stat = client.target("http://localhost:" + RULE.getLocalPort() + "/user/" + missingUser)
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON))
                .getStatus();

        assertThat(stat).isEqualTo(HttpStatus.BAD_REQUEST_400);

        user.setId(missingUser);
        stat = client.target("http://localhost:" + RULE.getLocalPort() + "/user/" + user.getId())
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON))
                .getStatus();

        assertThat(stat).isEqualTo(HttpStatus.NOT_FOUND_404);
    }

    private User createUser(String userName, String password, List<String> roles) throws Exception {

        final User user = new User(userName, password, roles);
        final User newUser = client.target("http://localhost:" + RULE.getLocalPort() + "/user/")
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(User.class);

        assertThat(newUser.getId()).isNotNull();
        assertThat(newUser.getId()).isNotEqualTo("");

        return newUser;
    }

    private User fetchUser(String userId) throws Exception {

        final User fetchedUser = client.target("http://localhost:" + RULE.getLocalPort() + "/user/" + userId)
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .get()
                .readEntity(User.class);

        return fetchedUser;
    }
}
