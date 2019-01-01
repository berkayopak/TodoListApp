import React, { Component } from 'react';
import { getAllTodoLists, deleteTodoList } from '../util/APIUtils';
import LoadingIndicator  from '../common/LoadingIndicator';
import {Button, Icon, notification, Popconfirm} from 'antd';
import { TODO_LIST_LIST_SIZE } from '../constants';
import { withRouter } from 'react-router-dom';
import './TodoListList.css';
import TodoList from "./TodoList";

class TodoListList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            todolists: [],
            page: 0,
            size: 10,
            totalElements: 0,
            totalPages: 0,
            last: true,
            isLoading: false
        };
        this.loadTodoList = this.loadTodoList.bind(this);
        this.handleLoadMore = this.handleLoadMore.bind(this);
        this.deleteList = this.deleteList.bind(this);
    }

    deleteList(todoList, page = 0, size = TODO_LIST_LIST_SIZE)
    {
        deleteTodoList(todoList).then(response => {

            }).catch(error => {
        });
        window.location.reload();

    }

    loadTodoList(page = 0, size = TODO_LIST_LIST_SIZE) {
        let promise;
        promise = getAllTodoLists(page, size);

        if(!promise) {
            return;
        }

        this.setState({
            isLoading: true
        });

        promise
            .then(response => {
                const todolists = this.state.todolists.slice();

                this.setState({
                    todolists: todolists.concat(response.content),
                    page: response.page,
                    size: response.size,
                    totalElements: response.totalElements,
                    totalPages: response.totalPages,
                    last: response.last,
                    isLoading: false
                })
            }).catch(error => {
            this.setState({
                isLoading: false
            })
        });

    }

    componentDidMount() {
        this.loadTodoList();
    }

    componentDidUpdate(nextProps) {
        if(this.props.isAuthenticated !== nextProps.isAuthenticated) {
            // Reset State
            this.setState({
                todolists: [],
                page: 0,
                size: 10,
                totalElements: 0,
                totalPages: 0,
                last: true,
                isLoading: false
            });
            this.loadTodoList();
        }
    }

    handleLoadMore() {
        this.loadTodoList(this.state.page + 1);
    }

    render() {
        const todoListViews = [];
        this.state.todolists.forEach((todoList, todoListIndex) => {
            todoListViews.push(
                <div>
                <TodoList
                key={todoList.id}
                todoList={todoList}/>
                    <Popconfirm title="Sure to DELETE Todo List?" onConfirm={() => this.deleteList(todoList)}>
                        <Button type="primary"
                                size="large"
                                style={{marginBottom:'60px'}}>Delete To-do List</Button>
                    </Popconfirm>
                </div>
            )
        });

        return (
            <div className="todo-lists-container">
                {todoListViews}
                {
                    !this.state.isLoading && this.state.todolists.length === 0 ? (
                        <div className="no-todo-lists-found">
                            <span>No Todo List Found.</span>
                        </div>
                    ): null
                }
                {
                    !this.state.isLoading && !this.state.last ? (
                        <div className="load-more-todo-lists">
                            <Button type="dashed" onClick={this.handleLoadMore} disabled={this.state.isLoading}>
                                <Icon type="plus" /> Load more
                            </Button>
                        </div>): null
                }
                {
                    this.state.isLoading ?
                        <LoadingIndicator />: null
                }
            </div>
        );
    }
}

export default withRouter(TodoListList);