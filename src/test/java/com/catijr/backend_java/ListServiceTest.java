package com.catijr.backend_java.service;

import com.catijr.backend_java.model.Task;
import com.catijr.backend_java.model.TodoList;
import com.catijr.backend_java.repository.ListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListServiceTest {

    @Mock
    private ListRepository listRepository;

    @InjectMocks
    private ListService listService;

    @Test
    void createList_success() {
        TodoList toCreate = new TodoList();
        toCreate.setName("Minha Lista");

        when(listRepository.findByName("Minha Lista")).thenReturn(Optional.empty());
        when(listRepository.save(toCreate)).thenReturn(toCreate);

        TodoList created = listService.createList(toCreate);

        assertNotNull(created);
        assertEquals("Minha Lista", created.getName());
        verify(listRepository).findByName("Minha Lista");
        verify(listRepository).save(toCreate);
    }

    @Test
    void createList_duplicateName_throwsConflict() {
        TodoList existing = new TodoList();
        existing.setId(1);
        existing.setName("Lista X");

        TodoList toCreate = new TodoList();
        toCreate.setName("Lista X");

        when(listRepository.findByName("Lista X")).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> listService.createList(toCreate));
        assertEquals(409, ex.getStatusCode().value());
        verify(listRepository).findByName("Lista X");
        verify(listRepository, never()).save(any());
    }

    @Test
    void updateList_setsBidirectionalTasks() {
        TodoList existing = new TodoList();
        existing.setId(1);
        existing.setName("old");
        existing.setTasks(new ArrayList<>());

        Task t = new Task();
        t.setName("Tarefa A");

        TodoList newData = new TodoList();
        newData.setName("new");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(t);
        newData.setTasks(tasks);

        when(listRepository.findById(1)).thenReturn(Optional.of(existing));
        when(listRepository.findByName("new")).thenReturn(Optional.empty());
        when(listRepository.save(existing)).thenReturn(existing);

        TodoList updated = listService.updateList(1, newData);

        assertEquals("new", updated.getName());
        assertEquals(1, updated.getTasks().size());
        assertSame(existing, updated.getTasks().get(0).getList());
        verify(listRepository).save(existing);
    }
}