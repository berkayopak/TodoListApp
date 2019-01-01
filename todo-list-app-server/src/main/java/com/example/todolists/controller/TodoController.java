package com.example.todolists.controller;

import com.example.todolists.model.*;
import com.example.todolists.payload.*;
import com.example.todolists.repository.TodoRepository;
import com.example.todolists.repository.UserRepository;
import com.example.todolists.security.CurrentUser;
import com.example.todolists.security.UserPrincipal;
import com.example.todolists.service.TodoService;
import com.example.todolists.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoService todoService;

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @GetMapping
    public PagedResponse<TodoListResponse> getTodoLists(@CurrentUser UserPrincipal currentUser,
                                                        @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                        @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return todoService.getTodoListsCreatedBy(currentUser.getUsername(), currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createTodoList(@Valid @RequestBody TodoListRequest todoListRequest) {
        System.out.println("\n\ncreateToDoList enter!!!\n\n");
        TodoList todoList = todoService.createTodoList(todoListRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{todoListId}")
                .buildAndExpand(todoList.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Todo List Created Successfully"));
    }

    @PostMapping("/{todoListId}/delete")
    @PreAuthorize("hasRole('USER')")
    public void deleteTodoList(@PathVariable Long todoListId) {
        System.out.println("\n\ndeleteTodoList enter!!!\n\n");
        todoService.deleteTodoList(todoListId);
    }


    @GetMapping("/{todoListId}")
    public TodoListResponse getTodoListById(@CurrentUser UserPrincipal currentUser,
                                    @PathVariable Long todoListId) {
        return todoService.getTodoListById(todoListId, currentUser);
    }

    @PostMapping("/{todoListId}/addItem")
    @PreAuthorize("hasRole('USER')")
    public TodoListResponse addItemToTodoList(@CurrentUser UserPrincipal currentUser,
                                 @PathVariable Long todoListId,
                                 @Valid @RequestBody TodoItemRequest todoItemRequest,
                                              @RequestParam(value = "dependentItemId", defaultValue = "0") String dependentItemId_STR) {
        System.out.println("\n\naddItemToTodoList !!!" + dependentItemId_STR + "\n\n");
        Long dependentItemId = new Long(0);
        try {
            dependentItemId = Long.parseLong(dependentItemId_STR.trim());  //<-- String to long here
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }

        return todoService.addItemAndGetUpdatedTodoList(todoListId, todoItemRequest, currentUser, dependentItemId);
    }

    @PostMapping("/{todoListId}/removeItem")
    @PreAuthorize("hasRole('USER')")
    public TodoListResponse removeItemFromTodoList(@CurrentUser UserPrincipal currentUser,
                                     @PathVariable Long todoListId,
                                     @Valid @RequestBody TodoItemResponse todoItemResponse) {
        System.out.println("\n\nremoveItemFromTodoList enter!!!\n\n");
        return todoService.removeItemAndGetUpdatedTodoList(todoListId, todoItemResponse, currentUser);
    }

    @PostMapping("/{todoListId}/completeItem")
    @PreAuthorize("hasRole('USER')")
    public TodoListResponse completeItemFromTodoList(@CurrentUser UserPrincipal currentUser,
                                     @PathVariable Long todoListId,
                                     @Valid @RequestBody TodoItemResponse todoItemResponse) {
        return todoService.changeStatusOfItemAndGetUpdatedTodoList(todoListId, todoItemResponse, currentUser);
    }

}
