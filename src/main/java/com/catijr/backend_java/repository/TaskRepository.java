package com.catijr.backend_java.repository;

import com.catijr.backend_java.model.Task;
import com.catijr.backend_java.model.TodoList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByListId(int listId);
    List<Task> findByList(TodoList list);

}