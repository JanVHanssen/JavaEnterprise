package com.example.application.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;

@XmlRootElement
public class TodoDTO {

        private Long todoId;
        private String title;
        private String comment;
        private Boolean completed;
        private LocalDate expire;
        private Integer userId;

    public TodoDTO() {
    }

    public TodoDTO(Long todoId, String title, String comment, Boolean completed, LocalDate expire, Integer userId) {
        this.todoId = todoId;
        this.title = title;
        this.comment = comment;
        this.completed = completed;
        this.expire = expire;
        this.userId = userId;
    }


    public Long getTodoId() {
        return todoId;
    }

    public void setTodoId(Long todoId) {
        this.todoId = todoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getExpire() {
        return expire;
    }

    public void setExpire(LocalDate expire) {
        this.expire = expire;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}