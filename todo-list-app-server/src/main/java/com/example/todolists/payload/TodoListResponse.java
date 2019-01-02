package com.example.todolists.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public class TodoListResponse {
    private Long id;
    private String name;
    private List<TodoItemResponse> todoItems;
    private UserSummary createdBy;
    private Instant creationDateTime;
    private Instant expirationDateTime;
    private Boolean isExpired;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCompleted;

    public TodoListResponse(Long id, String name, List<TodoItemResponse> todoItems, UserSummary createdBy,
                            Instant creationDateTime, Instant expirationDateTime, Boolean isExpired, Boolean isCompleted)
    {
        this.id=id;
        this.name=name;
        this.todoItems=todoItems;
        this.createdBy=createdBy;
        this.creationDateTime=creationDateTime;
        this.expirationDateTime=expirationDateTime;
        this.isExpired=isExpired;
        this.isCompleted=isCompleted;
    }

    public TodoListResponse()
    {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TodoItemResponse> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(List<TodoItemResponse> todoItems) {
        this.todoItems = todoItems;
    }

    public UserSummary getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSummary createdBy) {
        this.createdBy = createdBy;
    }


    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Instant getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(Instant expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }

}

