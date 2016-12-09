package com.example.todo.data;

import java.util.ArrayList;

/**
 * Created by Jeff on 11/12/16.
 */
public class TodoList {
    private ArrayList<Task> m_tasks = new ArrayList<Task>();

    public TodoList() {

    }

    public void addTask(Task newTask) {
        this.m_tasks.add(newTask);
    }

    public void removeTask(Task taskToRemove) {
        this.m_tasks.remove(taskToRemove);
    }

}
