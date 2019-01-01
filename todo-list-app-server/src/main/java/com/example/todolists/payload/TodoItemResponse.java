package com.example.todolists.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

public class TodoItemResponse {
    private Long id;
    private String name;
    private String description;
    private TodoItemResponse dependentItem;
    private UserSummary createdBy;
    private Instant creationDateTime;
    private Instant expirationDateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCompleted;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isExpired;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public TodoItemResponse getDependentItem() {
        return dependentItem;
    }

    public void setDependentItem(TodoItemResponse dependentItem) {
        this.dependentItem = dependentItem;
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

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

}
