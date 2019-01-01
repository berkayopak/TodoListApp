package com.example.todolists.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import com.example.todolists.repository.UserRepository;
import com.example.todolists.repository.TodoRepository;
import com.example.todolists.repository.TodoItemRepository;
import com.example.todolists.exception.BadRequestException;
import com.example.todolists.exception.ResourceNotFoundException;
import com.example.todolists.model.*;
import com.example.todolists.payload.PagedResponse;
import com.example.todolists.payload.TodoItemRequest;
import com.example.todolists.payload.TodoItemResponse;
import com.example.todolists.payload.TodoListRequest;
import com.example.todolists.payload.TodoListResponse;
import com.example.todolists.security.UserPrincipal;
import com.example.todolists.util.AppConstants;
import com.example.todolists.util.ModelMapper;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    public PagedResponse<TodoListResponse> getTodoListsCreatedBy(String username, UserPrincipal currentUser, int page, int size) {
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Retrieve all polls created by the given username
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<TodoList> todolists = todoRepository.findByCreatedBy(user.getId(), pageable);

        if (todolists.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), todolists.getNumber(),
                    todolists.getSize(), todolists.getTotalElements(), todolists.getTotalPages(), todolists.isLast());
        }

        // Map Polls to PollResponses containing vote counts and poll creator details
        List<Long> todolistIds = todolists.map(TodoList::getId).getContent();

        List<TodoListResponse> todoListResponses = todolists.map(todolist -> {
            return ModelMapper.mapTodoListToTodoListResponse(todolist,
                    user);
        }).getContent();

        return new PagedResponse<>(todoListResponses, todolists.getNumber(),
                todolists.getSize(), todolists.getTotalElements(), todolists.getTotalPages(), todolists.isLast());
    }

    public TodoList createTodoList(TodoListRequest todoListRequest) {
        TodoList todoList = new TodoList();
        todoList.setName(todoListRequest.getName());

        Instant now = Instant.now();
        Instant expirationDateTime = now.plus(Duration.ofDays(todoListRequest.getTodoLength().getDays()))
                .plus(Duration.ofHours(todoListRequest.getTodoLength().getHours()));

        todoList.setExpirationDateTime(expirationDateTime);

        return todoRepository.save(todoList);
    }

    public void deleteTodoList(Long todoListId) {
        todoRepository.deleteList(todoListId);
    }

    public TodoListResponse getTodoListById(Long todoListId, UserPrincipal currentUser) {
        TodoList todoList = todoRepository.findById(todoListId).orElseThrow(
                () -> new ResourceNotFoundException("TodoList", "id", todoListId));

        // Retrieve todolist creator details
        User creator = userRepository.findById(todoList.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", todoList.getCreatedBy()));

        return ModelMapper.mapTodoListToTodoListResponse(todoList, creator);
    }

    public TodoListResponse addItemAndGetUpdatedTodoList(Long todoListId, TodoItemRequest todoItemRequest, UserPrincipal currentUser, Long dependentItemId) {
        TodoList todoList = todoRepository.findById(todoListId)
                .orElseThrow(() -> new ResourceNotFoundException("TodoList", "id", todoListId));

        if(todoList.getExpirationDateTime().isBefore(Instant.now())) {
            throw new BadRequestException("Sorry! This todo list has already expired");
        }

        User user = userRepository.getOne(currentUser.getId());

        TodoItem newTodoItem = ModelMapper.mapTodoItemRequestToTodoItem(todoItemRequest, todoList);
        if(dependentItemId != null && dependentItemId != 0) {
            TodoItem dependentItem = todoItemRepository.getOne(dependentItemId);
            newTodoItem.setDependentTodoItem(dependentItem);
        }
        todoList.addTodoItem(newTodoItem);

        todoList = todoRepository.save(todoList);
        Long userId = todoList.getCreatedBy();
        // Retrieve todolist creator details
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return ModelMapper.mapTodoListToTodoListResponse(todoList, creator);
    }

    public TodoListResponse removeItemAndGetUpdatedTodoList(Long todoListId, TodoItemResponse todoItemResponse, UserPrincipal currentUser) {
        TodoList todoList = todoRepository.findById(todoListId)
                .orElseThrow(() -> new ResourceNotFoundException("TodoList", "id", todoListId));

        if(todoList.getExpirationDateTime().isBefore(Instant.now())) {
            throw new BadRequestException("Sorry! This todo list has already expired");
        }

        TodoItem trashTodoItem = new TodoItem(todoItemRepository.getOne(todoItemResponse.getId()));

        if(trashTodoItem.getDependentTodoItem() != null)
        {
            throw new BadRequestException("Sorry! This todo item has a dependent todo item. To delete this item, firstly delete dependent todo item(Name : "
                    + todoItemResponse.getDependentItem().getName() +")");
        }
        todoList.removeTodoItem(trashTodoItem);
        todoItemRepository.deleteItem(todoItemResponse.getId());

        // Retrieve todolist creator details
        Long userId = todoList.getCreatedBy();
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        todoList = todoRepository.getOne(todoListId);
        System.out.println("\n\nremoveItemAndGetUpdatedTodoList enter!!!\n\n"+ "Size : " + todoList.getTodoItems().size());

        return ModelMapper.mapTodoListToTodoListResponse(todoList, creator);
    }

    public TodoListResponse changeStatusOfItemAndGetUpdatedTodoList(Long todoListId, TodoItemResponse todoItemResponse, UserPrincipal currentUser) {
        TodoList todoList = todoRepository.findById(todoListId)
                .orElseThrow(() -> new ResourceNotFoundException("TodoList", "id", todoListId));

        if(todoList.getExpirationDateTime().isBefore(Instant.now())) {
            throw new BadRequestException("Sorry! This todo list has already expired");
        }

        User user = userRepository.getOne(currentUser.getId());

        TodoItem todoItem = todoList.getTodoItems().stream()
                .filter(todoitem -> todoitem.getId().equals(todoItemResponse.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("TodoItem", "id", todoItemResponse.getId()));

        if(todoItem.getDependentTodoItem() != null  && todoItem.getDependentTodoItem().getStatus() != true)
        {
            throw new BadRequestException("Sorry! This todo item has a dependent todo item. To complete this item, firstly complete dependent todo item(Name : "
                    + todoItemResponse.getDependentItem().getName() +")");
        }

        todoItem.setStatus(true);
        todoList = todoRepository.save(todoList);

        // Retrieve todolist creator details
        Long userId = todoList.getCreatedBy();
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return ModelMapper.mapTodoListToTodoListResponse(todoList, creator);
    }

    private void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }
}
