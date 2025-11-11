package com.catijr.backend_java.controller;

import com.catijr.backend_java.model.TodoList;
import com.catijr.backend_java.service.ListService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/lists")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) { 
        this.listService = listService; 
    }

    @PostMapping
    public TodoList createList(@RequestBody TodoList list) {
        return listService.createList(list);
    }

    @GetMapping
    public List<TodoList> getAllLists() {
        return listService.getAllLists();
    }

    @GetMapping("/{id}")
    public TodoList getListById(@PathVariable int id) {
        return listService.getListById(id);
    }

    @GetMapping("/name/{name}")
    public TodoList getListByName(@PathVariable String name) {
        return listService.getListByName(name);
    }

    @PutMapping("/{id}")
    public TodoList updateList(@PathVariable int id, @RequestBody TodoList list) {
        return listService.updateList(id, list);
    }
    

    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable int id) {
        listService.deleteList(id);
    }
}