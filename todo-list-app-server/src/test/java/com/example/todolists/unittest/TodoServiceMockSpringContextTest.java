package com.example.todolists.unittest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.todolists.repository.TodoRepository;
import com.example.todolists.repository.UserRepository;
import com.example.todolists.security.UserPrincipal;
import com.example.todolists.service.TodoService;
import com.example.todolists.model.*;
import com.example.todolists.payload.TodoListResponse;
import com.example.todolists.payload.UserSummary;
import java.time.Instant;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoServiceMockSpringContextTest {

    @MockBean
    TodoRepository todoRepositoryMock;

    @MockBean
    UserRepository userRepositoryMock;

    @Autowired
    TodoService todoServiceImpl;

    @Test
    public void testGetTodoListById() {
        Instant nowDate = Instant.now();
        Long idLong = Long.valueOf(1);
        UserSummary userSummary = new UserSummary(idLong,"xyzxyzxyz","xyzxyz");
        User user = new User("xyzxyz", "xyzxyzxyz", "xyzxyz@gmail.com", "123456");
        TodoList todoList = new TodoList(idLong, "todoList", null, nowDate, false);
        user.setId(idLong);
        UserPrincipal userPrincipal = new UserPrincipal(idLong, "xyzxyz", "xyzxyzxyz", "xyzxyz@gmail.com", "123456", null);
        TodoListResponse todoListResponse = new TodoListResponse(idLong, "todoList", null, userSummary, nowDate, nowDate, false, false);
        when(todoRepositoryMock.findById(idLong)).thenReturn(Optional.of(todoList));
        when(userRepositoryMock.findById(idLong)).thenReturn(Optional.of(user));

        assertEquals(todoListResponse, todoServiceImpl.getTodoListById(idLong, userPrincipal));
    }
}