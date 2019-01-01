package com.example.todolists.model;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.example.todolists.model.audit.UserDateAudit;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "todolists")
public class TodoList extends UserDateAudit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(
            mappedBy = "todoList",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    @OnDelete(action= OnDeleteAction.CASCADE)
    @NotFound(action=NotFoundAction.IGNORE)
    private List<TodoItem> todoItems = new ArrayList<>();

    @NotNull
    private Instant expirationDateTime;

    @NotNull
    private Boolean status = false;

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(List<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    public Instant getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(Instant expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public void addTodoItem(TodoItem todoItem) {
        todoItems.add(todoItem);
        todoItem.setTodoList(this);
    }

    public void removeTodoItem(TodoItem todoItem) {
        todoItems.remove(todoItems.stream()
                .filter(todoitem -> todoitem.getId().equals(todoItem.getId()))
                .findAny()
                .orElse(null));
    }
}
