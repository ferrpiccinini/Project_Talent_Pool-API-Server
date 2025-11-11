package com.catijr.backend_java.service;

import com.catijr.backend_java.model.Task;
import com.catijr.backend_java.model.TodoList;
import com.catijr.backend_java.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ListService listService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_success_whenListIdProvidedAsPath() {
        Task t = new Task();
        t.setName("Comprar leite");
        t.setExpectedFinishDate(LocalDateTime.now().plusDays(1));

        TodoList list = new TodoList();
        list.setId(1);
        list.setName("lista fer");
        list.setTasks(new ArrayList<>());

        when(listService.getListById(1)).thenReturn(list);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task saved = taskService.createTask(1, t);

        assertNotNull(saved);
        assertEquals("Comprar leite", saved.getName());
        assertSame(list, saved.getList());
        assertTrue(list.getTasks().contains(saved));
        verify(taskRepository).save(saved);
    }

    @Test
    void createTask_missingName_throwsBadRequest() {
        Task t = new Task();
        t.setExpectedFinishDate(LocalDateTime.now().plusDays(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> taskService.createTask(1, t));
        assertEquals(400, ex.getStatusCode().value());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getTasksByList_delegatesToRepository() {
        TodoList list = new TodoList();
        list.setId(2);
        list.setName("L");
        List<Task> mocked = new ArrayList<>();
        when(listService.getListById(2)).thenReturn(list);
        when(taskRepository.findByList(list)).thenReturn(mocked);

        List<Task> result = taskService.getTasksByList(2);

        assertSame(mocked, result);
        verify(taskRepository).findByList(list);
    }

    @Test
    void updateTask_updatesFields() {
        Task existing = new Task();
        existing.setId(5);
        existing.setName("old");
        existing.setDescription("olddesc");
        existing.setExpectedFinishDate(LocalDateTime.now().plusDays(2));

        when(taskRepository.findById(5)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task newData = new Task();
        newData.setName("new name");
        newData.setDescription("new desc");
        newData.setExpectedFinishDate(LocalDateTime.now().plusDays(5));
        newData.setPriority(existing.getPriority());

        Task updated = taskService.updateTask(5, newData);

        assertEquals("new name", updated.getName());
        assertEquals("new desc", updated.getDescription());
        assertEquals(newData.getExpectedFinishDate(), updated.getExpectedFinishDate());
        verify(taskRepository).save(existing);
    }
}