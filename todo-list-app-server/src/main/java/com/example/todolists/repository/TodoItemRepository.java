package com.example.todolists.repository;

import com.example.todolists.model.TodoList;
import com.example.todolists.model.TodoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

    Optional<TodoItem> findById(Long todoItemId);

    List<TodoItem> findByIdIn(List<TodoItem> todoItemIds);

    List<TodoItem> findByIdIn(List<Long> todoItemIds, Sort sort);

    @Transactional
    @Modifying
    @Query("delete from TodoItem where id=:x")
    public void deleteItem(@Param("x") Long idTodoItem);

    List<TodoItem> findByDependentTodoItem(TodoItem dependentTodoItem);
}
