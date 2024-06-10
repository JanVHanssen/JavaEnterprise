package com.example.application.service;

import com.example.application.dto.TodoDTO;
import com.example.application.jms.JmsSender;
import com.example.application.model.Todo;
import com.example.application.model.User;
import com.example.application.repository.TodoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Transactional
    public Todo save(Todo todo) {
        try {
            return todoRepository.save(todo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Todo> findAllByUser(User user) {
        return todoRepository.findAllByUser(user);
    }

    public Todo update(Todo todo) {
        try {
            if (todoRepository.existsById(todo.getTodoId())) {
                return todoRepository.save(todo);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteById(Long todoId) {
        try {
            todoRepository.deleteById(todoId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(TodoDTO todoDTO) {
        Todo todo = new Todo();
        todo.setTitle(todoDTO.getTitle());
        todo.setComment(todoDTO.getComment());
        todo.setCompleted(todoDTO.getCompleted());
        todo.setExpire(todoDTO.getExpire());

        User user = new User();
        user.setUserId(todoDTO.getUserId());
        todo.setUser(user);

        save(todo);
    }
}
