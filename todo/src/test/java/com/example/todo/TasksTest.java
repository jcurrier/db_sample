package com.example.todo;

import com.example.todo.data.Task;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;

/**
 * Created by Jeff on 11/16/16.
 */
public class TasksTest {
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
    public void postAndGetTest() {

        Task t = new Task("Create TPS Report", "Create that damn TPS report", DateTime.now(),
                "test", "test", "Open", DateTime.now(), DateTime.now());

        final Task newTask = createTask(t);
        final Task fetchedTask = getTask(newTask.getId());
    }

    @Test
    public void postAndDeleteTest() {
        Task t = new Task("Create TPS Report", "Create that damn TPS report", DateTime.now(),
                TestUtil.TEST_USER_ID, TestUtil.TEST_USER_ID, "Open", DateTime.now(), DateTime.now());

        final Task newTask = createTask(t);

        int stat = deleteTask(newTask);

        stat = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/" + newTask.getId())
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .delete()
                .getStatus();

        assertThat(stat).isEqualTo(HttpStatus.NOT_FOUND_404);
    }



    @Test
    public void postAndPutTest() {
        Task t = new Task("Create TPS Report", "Create that damn TPS report", DateTime.now(),
                "test", "2", "Open", DateTime.now(), DateTime.now());

        final Task newTask = createTask(t);
        String newTitle = "updated title";
        String newDescription = "updated description";

        newTask.setTitle(newTitle);
        newTask.setDescription(newDescription);

        int stat = updateTask(newTask);
        assertThat(stat).isEqualTo(HttpStatus.OK_200);

        Task updatedTask = getTask(newTask.getId());

        assertThat(updatedTask.getDescription()).isEqualTo(newDescription);
        assertThat(updatedTask.getTitle()).isEqualTo(newTitle);
        assertThat(updatedTask.getId()).isEqualTo(newTask.getId());
        assertThat(updatedTask.getOwnerId()).isEqualTo(newTask.getOwnerId());
        assertThat(updatedTask.getAssignedUserId()).isEqualTo(newTask.getAssignedUserId());
    }

    @Test
    public void testGetOwnedTasks() {

        Task taskOne = new Task("Task One", "Owned Task Description", DateTime.now(),
                "2", TestUtil.TEST_USER_ID, "Open", DateTime.now(), DateTime.now());
        Task taskTwo = new Task("Task Two", "Owned Task Description", DateTime.now(),
                TestUtil.TEST_USER_ID, TestUtil.TEST_USER_ID, "Open", DateTime.now(), DateTime.now());
        Task taskThree = new Task("Task Three", "Owned Task Description", DateTime.now(),
                "2", TestUtil.TEST_USER_ID, "Open", DateTime.now(), DateTime.now());

        taskOne = createTask(taskOne);
        taskTwo = createTask(taskTwo);
        taskThree = createTask(taskThree);

        Task[] tasks = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/?type=owned")
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .get()
                .readEntity(Task[].class);

        boolean foundTaskOne = false;
        boolean foundTaskTwo = false;
        boolean foundTaskThree = false;

        for(Task currentTask : tasks) {
            if(currentTask.getId().equals(taskOne.getId())) {
                foundTaskOne = true;
                continue;
            }

            if(currentTask.getId().equals(taskTwo.getId())) {
                foundTaskTwo = true;
                continue;
            }

            if(currentTask.getId().equals(taskThree.getId())) {
                foundTaskThree = true;
                continue;
            }
        }

        assertThat(foundTaskOne == true);
        assertThat(foundTaskTwo == true);
        assertThat(foundTaskThree == true);

        deleteTask(taskOne);
        deleteTask(taskTwo);
        deleteTask(taskThree);
    }

    @Test
    public void testGetAssignedTasks() {

        Task taskOne = new Task("Task One", "Owned Task Description", DateTime.now(),
                TestUtil.TEST_USER_ID, "1", "Open", DateTime.now(), DateTime.now());
        Task taskTwo = new Task("Task Two", "Owned Task Description", DateTime.now(),
                TestUtil.TEST_USER_ID, TestUtil.TEST_USER_ID, "Open", DateTime.now(), DateTime.now());
        Task taskThree = new Task("Task Three", "Owned Task Description", DateTime.now(),
                TestUtil.TEST_USER_ID, "2", "Open", DateTime.now(), DateTime.now());

        taskOne = createTask(taskOne);
        taskTwo = createTask(taskTwo);
        taskThree = createTask(taskThree);

        Task[] tasks = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/?type=assigned")
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .get()
                .readEntity(Task[].class);

        boolean foundTaskOne = false;
        boolean foundTaskTwo = false;
        boolean foundTaskThree = false;

        for(Task currentTask : tasks) {
            if(currentTask.getId().equals(taskOne.getId())) {
                foundTaskOne = true;
                continue;
            }

            if(currentTask.getId().equals(taskTwo.getId())) {
                foundTaskTwo = true;
                continue;
            }

            if(currentTask.getId().equals(taskThree.getId())) {
                foundTaskThree = true;
                continue;
            }
        }

        assertThat(foundTaskOne == true);
        assertThat(foundTaskTwo == true);
        assertThat(foundTaskThree == true);

        deleteTask(taskOne);
        deleteTask(taskTwo);
        deleteTask(taskThree);
    }

    private Task createTask(Task taskToCreate) {
        final Task newTask = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/")
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .post(Entity.entity(taskToCreate, MediaType.APPLICATION_JSON_TYPE))
                .readEntity(Task.class);

        assertThat(newTask.getTitle()).isEqualTo(taskToCreate.getTitle());
        assertThat(newTask.getDescription()).isEqualTo(taskToCreate.getDescription());
        assertThat(newTask.getOwnerId()).isEqualTo(taskToCreate.getOwnerId());
        assertThat(newTask.getAssignedUserId()).isEqualTo(taskToCreate.getAssignedUserId());
        assertThat(newTask.getDueDate().toString()).isEqualTo(taskToCreate.getDueDate());

        return newTask;
    }

    private int deleteTask(Task newTask) {
        int stat = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/" + newTask.getId())
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .delete()
                .getStatus();

        assertThat(stat).isEqualTo(HttpStatus.OK_200);
        return stat;
    }

    private int updateTask(Task taskToUpdate) {
        final int stat = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/" + taskToUpdate.getId())
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .put(Entity.entity(taskToUpdate, MediaType.APPLICATION_JSON))
                .getStatus();
        return stat;
    }

    private Task getTask(String taskId) {

        final int stat = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/" + taskId)
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .get()
                .getStatus();

        final Task task = client.target("http://localhost:" + RULE.getLocalPort() + "/tasks/" + taskId)
                .request()
                .header(TestUtil.BASIC_AUTH_HEADER, TestUtil.BASIC_AUTH_VALUE)
                .get()
                .readEntity(Task.class);

        assertThat(task.getId()).isEqualTo(taskId);

        return task;
    }

    private ArrayList<Task> getTasks(String ownerId) {
       return null;
    }
}
