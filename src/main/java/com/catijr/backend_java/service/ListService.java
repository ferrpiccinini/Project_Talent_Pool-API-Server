package com.catijr.backend_java.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catijr.backend_java.model.TodoList;
import com.catijr.backend_java.model.Task;
import com.catijr.backend_java.repository.ListRepository;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ListService {
    
    private final ListRepository listRepository;

    public ListService(ListRepository listRepository) {
        this.listRepository = listRepository;
    }

    @Transactional
    public TodoList createList(TodoList list) {
        if (list == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lista não pode ser nula");
        }
        if (list.getName() == null || list.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome da lista não pode ser vazio");
        }
        if (listRepository.findByName(list.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma lista com esse nome");
        }
        
        return listRepository.save(list);
    }

    public List<TodoList> getAllLists() {
        List<TodoList> lists = listRepository.findAll();
        if(lists.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma lista encontrada");
        }
        return lists;
    }

    public TodoList getListById(int id) {
        return listRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista com ID " + id + " não encontrada"));
    }

    public TodoList getListByName(String name) {
        return listRepository.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Lista com nome " + name + " não encontrada"));
    }

    @Transactional
    public TodoList updateList(int id, TodoList newListData) {
        TodoList existingList = listRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista com ID " + id + " não encontrada"));

        if (newListData.getName() == null || newListData.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome da lista não pode ser vazio");
        }

        listRepository.findByName(newListData.getName()).ifPresent(other -> {
            if (other.getId() != existingList.getId()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outra lista com esse nome");
            }
        });

        existingList.setName(newListData.getName());
        existingList.getTasks().clear();

        if (newListData.getTasks() != null) {
            for (Task t : new ArrayList<>(newListData.getTasks())) {
                t.setList(existingList);
                existingList.getTasks().add(t);
            }
        }
        return listRepository.save(existingList);
    }

    @Transactional
    public void deleteList(int id) {
        getListById(id);
        listRepository.deleteById(id);
    }
}