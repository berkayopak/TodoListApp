package com.example.todolists.payload;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class TodoListRequest {
    @NotBlank
    @Size(max = 140)
    private String name;

    @NotNull
    @Valid
    private TodoLength todoLength;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TodoLength getTodoLength() {
        return todoLength;
    }

    public void setTodoLength(TodoLength todoLength) {
        this.todoLength = todoLength;
    }
}
