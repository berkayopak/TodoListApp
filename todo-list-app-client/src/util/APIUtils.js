import { API_BASE_URL, TODO_LIST_LIST_SIZE, ACCESS_TOKEN } from '../constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    })
    
    if(localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
    .then(response => 
        response.json().then(json => {
            if(!response.ok) {
                return Promise.reject(json);
            }
            return json;
        })
    );
};

export function getAllTodoLists(page, size) {
    page = page || 0;
    size = size || TODO_LIST_LIST_SIZE;

    return request({
        url: API_BASE_URL + "/todos?page=" + page + "&size=" + size,
        method: 'GET'
    });
}

export function getTodoListById(todoListId) {
    return request({
        url: API_BASE_URL + "/todos/"+todoListId,
        method: 'GET'
    });
}

export function createTodoList(todoListData) {
    return request({
        url: API_BASE_URL + "/todos",
        method: 'POST',
        body: JSON.stringify(todoListData)
    });
}

export function deleteTodoList(todoList) {
    console.log(todoList.id);
    return request({
        url: API_BASE_URL + "/todos/"+ todoList.id + "/delete",
        method: 'POST'
    });
}

export function addItemToTodoList(todoListId, todoItemData, dependentItemId) {
    console.log("Dependent item id: " + dependentItemId);
    return request({
        url: API_BASE_URL + "/todos/" + todoListId + "/addItem?dependentItemId=" + dependentItemId,
        method: 'POST',
        body: JSON.stringify(todoItemData)
    });
}

export function removeTodoItem(todoListId, todoItemData) {
    console.log("Completed: " + todoItemData.isExpired);
    return request({
        url: API_BASE_URL + "/todos/" + todoListId + "/removeItem" ,
        method: 'POST',
        body: JSON.stringify(todoItemData)
    });
}

export function completeTodoItem(todoListId, todoItemData) {
    return request({
        url: API_BASE_URL + "/todos/" + todoListId + "/completeItem" ,
        method: 'POST',
        body: JSON.stringify(todoItemData)
    });
}

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/signin",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function signup(signupRequest) {
    return request({
        url: API_BASE_URL + "/auth/signup",
        method: 'POST',
        body: JSON.stringify(signupRequest)
    });
}

export function checkUsernameAvailability(username) {
    return request({
        url: API_BASE_URL + "/user/checkUsernameAvailability?username=" + username,
        method: 'GET'
    });
}

export function checkEmailAvailability(email) {
    return request({
        url: API_BASE_URL + "/user/checkEmailAvailability?email=" + email,
        method: 'GET'
    });
}


export function getCurrentUser() {
    if(!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/user/me",
        method: 'GET'
    });
}

