package com.catijr.backend_java.controller;

import com.catijr.backend_java.model.Task;
import com.catijr.backend_java.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lists/{listId}/tasks")
public class TaskController {

    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(@PathVariable int listId, @RequestBody Task task) {
        return taskService.createTask(listId, task);
    }

    @GetMapping
    public List<Task> getTasksByList(@PathVariable int listId) {
        return taskService.getTasksByList(listId);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable int id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }
    
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
    }
}