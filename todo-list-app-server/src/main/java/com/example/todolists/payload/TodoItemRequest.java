package com.example.todolists.payload;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class TodoItemRequest {

    private Long id;

    @NotBlank
    @Size(max = 140)
    private String name;

    @NotBlank
    @Size(max = 140)
    private String description;

    @NotNull
    private Boolean status;

    @NotNull
    @Valid
    private TodoLength todoLength;

    private TodoItemRequest dependentItem;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public TodoItemRequest getDependentItem() {
        return dependentItem;
    }

    public void setTodoItemRequest(TodoItemRequest dependentItem) {
        this.dependentItem = dependentItem;
    }

    public TodoLength getTodoLength() {
        return todoLength;
    }

    public void setTodoLength(TodoLength todoLength) {
        this.todoLength = todoLength;
    }
}
