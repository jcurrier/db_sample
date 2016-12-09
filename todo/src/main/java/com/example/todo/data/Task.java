package com.example.todo.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by Jeff on 11/12/16.
 */
public class Task {
    private String m_taskId;
    private String m_title;
    private String m_description;
    private DateTime m_dueDate;
    private String m_assignrdUserId;
    private String m_ownerId;
    private String m_state;
    private DateTime m_createdOn;
    private DateTime m_lastUpdated;

    @JsonProperty("Id")
    public String getId() {
        return m_taskId;
    }

    @JsonProperty("Id")
    public void setId(String id) { this.m_taskId = id; }

    @JsonProperty("Title")
    public String getTitle() {
        return m_title;
    }

    @JsonProperty("Title")
    public void setTitle(String title) {
        this.m_title = title;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return m_description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.m_description = description;
    }

    @JsonProperty("DueDate")
    public String getDueDate() {
        return m_dueDate.toString();
    }

    @JsonProperty("DueDate")
    public void setDueDate(String dueDate) {
        this.m_dueDate = DateTime.parse(dueDate);
    }

    @JsonProperty("Assignee")
    public String getAssignedUserId() {
        return m_assignrdUserId;
    }

    @JsonProperty("Assignee")
    public void setAssignedUserId(String assignedUserId) {
        this.m_assignrdUserId = assignedUserId;
    }

    @JsonProperty("TaskOwner")
    public String getOwnerId() {
        return m_ownerId;
    }

    @JsonProperty("TaskOwner")
    public void setOwnerId(String ownerId) {
        this.m_ownerId = ownerId;
    }

    @JsonProperty("TaskState")
    public String getState() {
        return m_state;
    }

    @JsonProperty("TaskState")
    public void setState(String state) {
        this.m_state = state;
    }

    @JsonProperty("CreatedOn")
    public String getCreatedOn() {
        return m_createdOn.toString();
    }

    @JsonProperty("CreatedOn")
    public void setCreatedOn(String createdOn) {
        this.m_createdOn = DateTime.parse(createdOn);
    }

    @JsonProperty("LastUpdated")
    public String getLastUpdated() {
        return m_lastUpdated.toString();
    }

    @JsonProperty("LastUpdated")
    public void setLastUpdated(String lastUpdated) {
        this.m_lastUpdated = DateTime.parse(lastUpdated);
    }


    public Task() {
    }

    public Task(String id, String title, String description, DateTime due, String assigneeId, String ownerUserId,
                String state, DateTime createdOn, DateTime lastUpdated) {
        this.m_taskId = id;
        this.m_title = title;
        this.m_description = description;
        this.m_dueDate = due;
        this.m_assignrdUserId = assigneeId;
        this.m_ownerId = ownerUserId;
        this.m_state = state;
        this.m_createdOn = createdOn;
        this.m_lastUpdated = lastUpdated;
    }

    public Task(String title, String description, DateTime due, String assigneeId, String ownerUserId,
                String state, DateTime createdOn, DateTime lastUpdated) {
        this(UUID.randomUUID().toString(), title, description, due, assigneeId, ownerUserId, state, createdOn,
                lastUpdated);
    }
}
