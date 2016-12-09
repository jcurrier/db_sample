package com.example.todo.data;

/**
 * Created by Jeff on 11/23/16.
 */
public enum TaskState {
    OPEN("Open"),
    IN_PROGRESS("In-Progress"),
    WAITING("Waiting"),
    CLOSED("Closed");

    private String m_string = "";

    private TaskState(String state) {
        m_string = state;
    }
}
