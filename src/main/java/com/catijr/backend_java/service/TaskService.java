package com.catijr.backend_java.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.catijr.backend_java.model.TodoList;
import com.catijr.backend_java.model.Priority;
import com.catijr.backend_java.model.Task;
import com.catijr.backend_java.repository.TaskRepository;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.catijr.backend_java.service.ListService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ListService listService;
    
    @Autowired
    public TaskService(TaskRepository taskRepository, ListService listService) {
        this.taskRepository = taskRepository;
        this.listService = listService;
    }

    @Transactional
    public Task createTask(int listId, Task task) {
        TodoList list = listService.getListById(listId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task cannot be null");
        }
        if(task.getName() == null || task.getName().trim().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task name cannot be empty");
        }
        if(task.getExpectedFinishDate().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expected finish date cannot be in the past");
        }
       
        task.setList(list);
        list.getTasks().add(task);
        return taskRepository.save(task);
    }
    

    public List<Task> getTasksByList(int listId) {
        TodoList list = listService.getListById(listId);
        return taskRepository.findByList(list);
    }

    public Task getTaskById(int id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task with ID " + id + " not found"));
    }

    @Transactional
    public Task updateTask(int id, Task newTaskData) {
        Task existingTask = getTaskById(id);

        if (newTaskData.getName() == null || newTaskData.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task name cannot be empty");
        }
        if (newTaskData.getExpectedFinishDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expected finish date cannot be in the past");
        }

        existingTask.setName(newTaskData.getName());
        existingTask.setDescription(newTaskData.getDescription());
        existingTask.setExpectedFinishDate(newTaskData.getExpectedFinishDate());
        existingTask.setPriority(newTaskData.getPriority());
        existingTask.setFinishDate(newTaskData.getFinishDate());

        return taskRepository.save(existingTask);
    }   

    @Transactional
    public void deleteTask(int id) {
        Task existingTask = getTaskById(id);
        TodoList list = existingTask.getList();
        if (list != null) {
            list.getTasks().remove(existingTask);
        }
        taskRepository.delete(existingTask);
    
    }
}
