import React, { Component } from 'react';
import './TodoList.css';
import { Link } from 'react-router-dom';
import { getAvatarColor } from '../util/Colors';
import { formatDateTime } from '../util/Helpers';
import {addItemToTodoList, removeTodoItem, completeTodoItem, getTodoListById} from '../util/APIUtils';
import { Form, Input, Button, Select, Col, notification, Avatar, Collapse, Table, Popconfirm } from 'antd';
import {TODO_LIST_TEXT_MAX_LENGTH} from "../constants";
const Option = Select.Option;
const FormItem = Form.Item;
const Panel = Collapse.Panel;
const { TextArea } = Input


class TodoList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            name: {
                text: ''
            },
            description: {
                text: ''
            },
            todoLength: {
                days: 1,
                hours: 0
            },
            dependentItemId:null,
            isHidden:true,
            isHiddenItemActions:true,
            todoList:props.todoList,
            filteredInfo: null,
            sortedInfo: null,
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handleTodoListDaysChange = this.handleTodoListDaysChange.bind(this);
        this.handleTodoListHoursChange = this.handleTodoListHoursChange.bind(this);
        this.handleTodoListDependentItem = this.handleTodoListDependentItem.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
    }

    handleChangeTable = (pagination, filters, sorter) => {
        console.log('Various parameters', pagination, filters, sorter);
        this.setState({
            filteredInfo: filters,
            sortedInfo: sorter,
        });
    }
    clearFilters = () => {
        this.setState({ filteredInfo: null });
    }
    clearAll = () => {
        this.setState({
            filteredInfo: null,
            sortedInfo: null,
        });
    }

    toggleHidden () {
        this.setState({
            isHidden: !this.state.isHidden
        })
    }

    toggleHiddenItemActions () {
        this.setState({
            isHiddenItemActions: !this.state.isHiddenItemActions
        })
    }

    handleSubmit(event) {
        event.preventDefault();
        const todoItemData = {
            name: this.state.name.text,
            description: this.state.description.text,
            todoLength: this.state.todoLength,
            status: false
        };
        this.setState({
            isHidden: !this.state.isHidden
        })

        addItemToTodoList(this.state.todoList.id, todoItemData, this.state.dependentItemId)
            .then(response => {
                this.setState({
                    todoList: response
                });
                console.log("response received!");
            }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create todo item.');
            } else {
                notification.error({
                    message: 'Todo List App',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    validateName = (nameText) => {
        if(nameText.length === 0) {
            return {
                validateStatus: 'error',
                errorMsg: 'Please enter a something!'
            }
        } else if (nameText.length > TODO_LIST_TEXT_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Text is too long (Maximum ${TODO_LIST_TEXT_MAX_LENGTH} characters allowed)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    handleNameChange(event) {
        const value = event.target.value;
        this.setState({
            name: {
                text: value,
                ...this.validateName(value)
            }
        });
        console.log('name changed =' + value);
    }

    handleDescriptionChange(event) {
        const value = event.target.value;
        this.setState({
            description: {
                text: value,
                ...this.validateName(value)
            }
        });
    }

    handleTodoListDependentItem(value) {

        this.setState({
            dependentItemId: value
        });
    }

    handleTodoListDaysChange(value) {
        const todoLength = Object.assign(this.state.todoLength, {days: value});
        this.setState({
            todoLength: todoLength
        });
    }

    handleTodoListHoursChange(value) {
        const todoLength = Object.assign(this.state.todoLength, {hours: value});
        this.setState({
            todoLength: todoLength
        });
    }

    isFormInvalid() {
        if(this.state.name.validateStatus !== 'success' || this.state.description.validateStatus !== 'success') {
            return true;
        }
    }

    getTimeRemaining = (todoList) => {
        const expirationTime = new Date(todoList.expirationDateTime).getTime();
        const currentTime = new Date().getTime();

        var difference_ms = expirationTime - currentTime;
        var seconds = Math.floor( (difference_ms/1000) % 60 );
        var minutes = Math.floor( (difference_ms/1000/60) % 60 );
        var hours = Math.floor( (difference_ms/(1000*60*60)) % 24 );
        var days = Math.floor( difference_ms/(1000*60*60*24) );

        let timeRemaining;

        if(days > 0) {
            timeRemaining = days + " days left";
        } else if (hours > 0) {
            timeRemaining = hours + " hours left";
        } else if (minutes > 0) {
            timeRemaining = minutes + " minutes left";
        } else if(seconds > 0) {
            timeRemaining = seconds + " seconds left";
        } else {
            timeRemaining = "less than a second left";
        }

        return timeRemaining;
    }

    removeItem = (todoItem) => {
        removeTodoItem(this.state.todoList.id,todoItem)
            .then(response => {
                this.setState({
                    todoList: response
                });
                this.getTodoList();
            }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login remove todo item.');
            } else {
                notification.error({
                    message: 'Todo List App',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    getTodoList = () => {
        getTodoListById(this.state.todoList.id)
            .then(response => {
                this.setState({
                    todoList: response
                });
            }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login get todo list by id.');
            } else {
                notification.error({
                    message: 'Todo List App',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    completeItem = (todoItem) => {
        completeTodoItem(this.state.todoList.id,todoItem)
            .then(response => {
                this.setState({
                    todoList: response
                });
            }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login complete todo item.');
            } else {
                notification.error({
                    message: 'Todo List App',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    compareByAlph = (a, b) => {
        return a.localeCompare(b)
    }

    render() {
        let { sortedInfo, filteredInfo } = this.state;
        sortedInfo = sortedInfo || {};
        filteredInfo = filteredInfo || {};

        const columns = [{
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
            filters: [
                { text: 'Important', value: 'Important' },
                { text: 'Optional', value: 'Optional' },
            ],
            filteredValue: filteredInfo.name || null,
            onFilter: (value, record) => record.name.includes(value),
            sorter: (a, b) => this.compareByAlph(a.name, b.name),
            sortOrder: sortedInfo.columnKey === 'name' && sortedInfo.order,
        }, {
            title: 'Description',
            dataIndex: 'description',
            key: 'description',
        }, {
            title: 'Creation DateTime',
            dataIndex: 'creationDateTime',
            key: 'creationDateTime',
            sorter: (a, b) => this.compareByAlph(a.creationDateTime,b.creationDateTime),
            sortOrder: sortedInfo.columnKey === 'creationDateTime' && sortedInfo.order
        }, {
            title: 'Expiration DateTime',
            dataIndex: 'expirationDateTime',
            key: 'expirationDateTime',
            sorter: (a, b) => this.compareByAlph(a.expirationDateTime,b.expirationDateTime),
            sortOrder: sortedInfo.columnKey === 'expirationDateTime' && sortedInfo.order
        }, {
            title: 'Completed',
            dataIndex: 'isCompleted',
            key: 'isCompleted',
            render: val => (val ? 'Completed' : 'Not Completed'),
            filters: [
                { text: 'Completed', value: 1 },
                { text: 'Not Completed', value: 0 },
            ],
            filteredValue: filteredInfo.isCompleted || null,
            onFilter: (value, record) => record.isCompleted == value,
            sorter: (a, b) => this.compareByAlph(a.isCompleted.toString(),b.isCompleted.toString()),
            sortOrder: sortedInfo.columnKey === 'isCompleted' && sortedInfo.order
        },
            {
                title: 'Expired',
                dataIndex: 'isExpired',
                key: 'isExpired',
                render: val => (val ? 'Expired' : 'Not Expired'),
                filters: [
                    { text: 'Expired', value: 1 },
                    { text: 'Not Expired', value: 0 },
                ],
                filteredValue: filteredInfo.isExpired || null,
                onFilter: (value, record) => record.isExpired == value
            },
            {
                title: 'Action',
                dataIndex: 'action',
                render: (text, record) => (
                    (
                            <div>
                                <Popconfirm title="Sure to delete?" onConfirm={() => this.removeItem(record)}>
                                    <a style={{marginRight: '15px'}}  href="javascript:;">Delete</a>
                                </Popconfirm>
                                <Popconfirm title="Sure to complete?" onConfirm={() => this.completeItem(record)}>
                                    <a href="javascript:;">Complete</a>
                                </Popconfirm>
                            </div>
                        )
                ),
            }
        ];

            return (
                <div className="todo-list-content">
                    <div className="todo-list-header">
                        <div className="todo-list-creator-info">
                            <Link className="creator-link" to={`/users/${this.state.todoList.createdBy.username}`}>
                                <Avatar className="todo-list-creator-avatar"
                                        style={{backgroundColor: getAvatarColor(this.state.todoList.createdBy.name)}}>
                                    {this.state.todoList.createdBy.name[0].toUpperCase()}
                                </Avatar>
                                <span className="todo-list-creator-name">
                                {this.state.todoList.createdBy.name}
                            </span>
                                <span className="todo-list-creator-username">
                                @{this.state.todoList.createdBy.username}
                            </span>
                                <span className="todo-list-creation-date">
                                {formatDateTime(this.state.todoList.creationDateTime)}
                            </span>
                            </Link>
                        </div>
                        <div className="todo-list-text">
                            {this.state.todoList.name}
                        </div>
                    </div>
                    <div className="todo-list-choices">
                        <Table columns={columns} dataSource={this.state.todoList.todoItems} onChange={this.handleChangeTable} />
                        {this.state.isHidden &&
                            <Button type="primary"
                                size="large"
                                onClick={this.toggleHidden.bind(this)}
                                style={{marginTop:'20px'}}>Add To-do Item</Button>
                        }
                        {!this.state.isHidden &&
                        <Form onSubmit={this.handleSubmit} className="create-todo-list-form">
                            <FormItem validateStatus={this.state.name.validateStatus}
                                      help={this.state.name.errorMsg}>
                        <TextArea
                            placeholder="Enter your to-do item name"
                            style = {{ fontSize: '16px' }}
                            autosize={{ minRows: 3, maxRows: 6 }}
                            name = "name"
                            value = {this.state.name.text}
                            onChange = {this.handleNameChange} />
                                <br></br><br></br>
                                <TextArea
                                    placeholder="Enter your to-do item description"
                                    style = {{ fontSize: '16px' }}
                                    autosize={{ minRows: 3, maxRows: 6 }}
                                    name = "description"
                                    value = {this.state.description.text}
                                    onChange = {this.handleDescriptionChange} />
                                <Col xs={24} sm={20} align="middle">
                                <span style = {{ marginRight: '18px'}} >
                                    <Select
                                        name="dependentItemId"
                                        onChange={this.handleTodoListDependentItem}
                                        value={this.state.dependentItemId}>
                                        {
                                            this.state.todoList.todoItems.map(i =>
                                                <Option key={i.id}>{i.name}</Option>
                                            )
                                        }
                                    </Select> &nbsp;Please enter if item have a dependency, otherwise it can be empty(null)
                                </span>
                                </Col>
                            </FormItem>
                            <FormItem>
                                <Col xs={24} sm={4}>
                                    Length:
                                </Col>
                                <Col xs={24} sm={20}>
                                <span style = {{ marginRight: '18px' }}>
                                    <Select
                                        name="days"
                                        defaultValue="1"
                                        onChange={this.handleTodoListDaysChange}
                                        value={this.state.todoLength.days}
                                        style={{ width: 60 }} >
                                        {
                                            Array.from(Array(8).keys()).map(i =>
                                                <Option key={i}>{i}</Option>
                                            )
                                        }
                                    </Select> &nbsp;Days
                                </span>
                                    <span>
                                    <Select
                                        name="hours"
                                        defaultValue="0"
                                        onChange={this.handleTodoListHoursChange}
                                        value={this.state.todoLength.hours}
                                        style={{ width: 60 }} >
                                        {
                                            Array.from(Array(24).keys()).map(i =>
                                                <Option key={i}>{i}</Option>
                                            )
                                        }
                                    </Select> &nbsp;Hours
                                </span>
                                </Col>
                            </FormItem>
                            <FormItem>
                                <Button type="primary"
                                        htmlType="submit"
                                        size="large"
                                        disabled={this.isFormInvalid()}
                                        className="create-todo-list-form-button"
                                        style={{ marginTop: '20'}}>Add To-do Item</Button>
                            </FormItem>
                        </Form>}

                        </div>
                    </div>
            );
        }
}


export default TodoList;