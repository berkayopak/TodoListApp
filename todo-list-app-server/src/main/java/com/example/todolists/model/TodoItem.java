package com.example.todolists.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.time.Instant;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.NotFoundAction;
import com.example.todolists.model.audit.DateAudit;


@Entity
@Table(name = "todoitems")
public class TodoItem extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "todolist_id", nullable = false)
    private TodoList todoList;

    public TodoItem()
    {

    }
    public TodoItem(TodoItem another) {
        this.id = another.id;
        this.todoList = another.todoList;
        this.name = another.name;
        this.description = another.description;
        this.status = another.status;
        this.expirationDateTime = another.expirationDateTime;
        this.dependentTodoItem = another.dependentTodoItem;
    }

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean status;

    @NotNull
    private Instant expirationDateTime;

    @OneToOne(
        cascade = CascadeType.ALL,
            orphanRemoval=true
    )
    @OnDelete(action= OnDeleteAction.CASCADE)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "dependent_todoitem_id")
    private TodoItem dependentTodoItem;

    public TodoItem(String name, String description, Boolean status, Instant expirationDateTime)
    {
        this.name = name;
        this.description = description;
        this.status = status;
        this.expirationDateTime = expirationDateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TodoList getTodoList() {
        return todoList;
    }

    public void setTodoList(TodoList todoList) {
        this.todoList = todoList;
    }

    public TodoItem getDependentTodoItem() {
        return dependentTodoItem;
    }

    public void setDependentTodoItem(TodoItem dependentTodoItem) {
        this.dependentTodoItem = dependentTodoItem;
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

    public Instant getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(Instant expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return Objects.equals(id, todoItem.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}