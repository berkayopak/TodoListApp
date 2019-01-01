import React, { Component } from 'react';
import { createTodoList } from '../util/APIUtils';
import { TODO_LIST_TEXT_MAX_LENGTH } from '../constants';
import './NewTodoList.css';
import { Form, Input, Button, Select, Col, notification } from 'antd';
const Option = Select.Option;
const FormItem = Form.Item;
const { TextArea } = Input

class NewTodoList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            name: {
                text: ''
            },
            todoLength: {
                days: 1,
                hours: 0
            }
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleTodoListDaysChange = this.handleTodoListDaysChange.bind(this);
        this.handleTodoListHoursChange = this.handleTodoListHoursChange.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
    }

    handleSubmit(event) {
        event.preventDefault();
        const todoData = {
            name: this.state.name.text,
            todoLength: this.state.todoLength
        };

        createTodoList(todoData)
            .then(response => {
                this.props.history.push("/");
            }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create todo list.');
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
                errorMsg: 'Please enter list name!'
            }
        } else if (nameText.length > TODO_LIST_TEXT_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `List name is too long (Maximum ${TODO_LIST_TEXT_MAX_LENGTH} characters allowed)`
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
        if(this.state.name.validateStatus !== 'success') {
            return true;
        }
    }

    render() {
        return (
            <div className="new-todo-list-container">
                <h1 className="page-title">Create To-do List</h1>
                <div className="new-todo-list-content">
                    <Form onSubmit={this.handleSubmit} className="create-todo-list-form">
                        <FormItem validateStatus={this.state.name.validateStatus}
                                  help={this.state.name.errorMsg} className="todo-list-form-row">
                        <TextArea
                            placeholder="Enter your to-do list name"
                            style = {{ fontSize: '16px' }}
                            autosize={{ minRows: 3, maxRows: 6 }}
                            name = "name"
                            value = {this.state.name.text}
                            onChange = {this.handleNameChange} />
                        </FormItem>
                        <FormItem className="todo-list-form-row">
                            <Col xs={24} sm={4}>
                                To-do list length:
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
                        <FormItem className="todo-list-form-row">
                            <Button type="primary"
                                    htmlType="submit"
                                    size="large"
                                    disabled={this.isFormInvalid()}
                                    className="create-todo-list-form-button">Create To-Do List</Button>
                        </FormItem>
                    </Form>
                </div>
            </div>
        );
    }
}

export default NewTodoList;