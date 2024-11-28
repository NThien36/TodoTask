package com.example.todolist.models;

import java.util.Date;

public class Task {
    private String name;
    private String description;
    private boolean isCompleted;
    private String dueDate;

    public Task(String name, String description, boolean isCompleted, String dueDate) {
        this.name = name;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
