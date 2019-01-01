package com.example.todolists.util;
import com.example.todolists.model.TodoList;
import com.example.todolists.model.TodoItem;
import com.example.todolists.model.User;
import com.example.todolists.payload.TodoListResponse;
import com.example.todolists.payload.TodoItemResponse;
import com.example.todolists.payload.TodoItemRequest;
import com.example.todolists.payload.UserSummary;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {

    public static TodoListResponse mapTodoListToTodoListResponse(TodoList todoList, User creator) {
        TodoListResponse todoListResponse = new TodoListResponse();
        todoListResponse.setId(todoList.getId());
        todoListResponse.setName(todoList.getName());
        todoListResponse.setCreationDateTime(todoList.getCreatedAt());
        todoListResponse.setExpirationDateTime(todoList.getExpirationDateTime());
        todoListResponse.setCompleted(todoList.getStatus());
        Instant now = Instant.now();
        todoListResponse.setExpired(todoList.getExpirationDateTime().isBefore(now));

        List<TodoItemResponse> todoItemResponses = todoList.getTodoItems().stream().map(todoItem -> {
            TodoItemResponse todoItemResponse = new TodoItemResponse();
            todoItemResponse.setId(todoItem.getId());
            todoItemResponse.setName(todoItem.getName());
            todoItemResponse.setDescription(todoItem.getDescription());
            todoItemResponse.setIsCompleted(todoItem.getStatus());
            if(todoItem.getDependentTodoItem() != null) {
                todoItemResponse.setDependentItem(mapTodoItemToTodoItemResponse(todoItem.getDependentTodoItem()));
            }
            todoItemResponse.setCreationDateTime(todoItem.getCreatedAt());
            todoItemResponse.setExpirationDateTime(todoItem.getExpirationDateTime());
            Instant nowTodoItem = Instant.now();
            todoItemResponse.setIsExpired(todoItem.getExpirationDateTime().isBefore(nowTodoItem));
            System.out.println("\n\nmapTodoListToTodoListResponse enter!!!\n\n expired : " + todoItemResponse.getIsExpired());

            return todoItemResponse;}).collect(Collectors.toList());

        todoListResponse.setTodoItems(todoItemResponses);
        UserSummary creatorSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName());
        todoListResponse.setCreatedBy(creatorSummary);

        return todoListResponse;
    }

    private static TodoItemResponse mapTodoItemToTodoItemResponse(TodoItem todoItem)
    {
        TodoItemResponse todoItemResponse = new TodoItemResponse();
        todoItemResponse.setId(todoItem.getId());
        todoItemResponse.setName(todoItem.getName());
        todoItemResponse.setDescription(todoItem.getDescription());
        todoItemResponse.setIsCompleted(todoItem.getStatus());
        todoItemResponse.setCreationDateTime(todoItem.getCreatedAt());
        todoItemResponse.setExpirationDateTime(todoItem.getExpirationDateTime());
        Instant now = Instant.now();
        todoItemResponse.setIsExpired(todoItem.getExpirationDateTime().isBefore(now));


        return todoItemResponse;
    }

    public static TodoItem mapTodoItemRequestToTodoItem(TodoItemRequest todoItemRequest, TodoList todoList)
    {
        TodoItem newTodoItem = new TodoItem(todoItemRequest.getName(),
                todoItemRequest.getDescription(),
                todoItemRequest.getStatus(),
                Instant.now().plus(Duration.ofDays(todoItemRequest.getTodoLength().getDays()))
                        .plus(Duration.ofHours(todoItemRequest.getTodoLength().getHours())));

        newTodoItem.setTodoList(todoList);
        if(todoItemRequest.getDependentItem() != null)
        {
            newTodoItem.setDependentTodoItem(mapTodoItemRequestToTodoItem(todoItemRequest.getDependentItem(), todoList));
        }
        return newTodoItem;
    }

    public static TodoItem mapTodoItemResponseToTodoItem(TodoItemResponse todoItemResponse, TodoList todoList)
    {
        TodoItem newTodoItem = new TodoItem(todoItemResponse.getName(),
                todoItemResponse.getDescription(),
                todoItemResponse.getIsCompleted(),
                todoItemResponse.getExpirationDateTime());

        newTodoItem.setId(todoItemResponse.getId());
        newTodoItem.setTodoList(todoList);
        if(todoItemResponse.getDependentItem() != null)
        {
            newTodoItem.setDependentTodoItem(mapTodoItemResponseToTodoItem(todoItemResponse.getDependentItem(), todoList));
        }
        return newTodoItem;
    }

}
