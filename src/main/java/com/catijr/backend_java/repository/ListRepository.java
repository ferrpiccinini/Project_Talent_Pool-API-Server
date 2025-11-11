package com.catijr.backend_java.repository;

import com.catijr.backend_java.model.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListRepository extends JpaRepository<TodoList, Integer> {
    Optional<TodoList> findByName(String name);
}