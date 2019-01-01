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
public interface TodoRepository extends JpaRepository<TodoList, Long> {

    Optional<TodoList> findById(Long todoListId);

    Page<TodoList> findByCreatedBy(Long userId, Pageable pageable);

    long countByCreatedBy(Long userId);

    List<TodoList> findByIdIn(List<TodoList> todoListIds);

    List<TodoList> findByIdIn(List<Long> todoListIds, Sort sort);

    @Transactional
    @Modifying
    @Query("delete from TodoList where id=:x")
    public void deleteList(@Param("x") Long idTodoList);
}
