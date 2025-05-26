package com.example.task_management_app.repository;

import com.example.task_management_app.model.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Integer> {

    // Find task priority by name
    Optional<TaskPriority> findByName(String name);

    // Find task priority by value
    Optional<TaskPriority> findByValue(Integer value);

    // Check if priority name exists
    boolean existsByName(String name);

    // Check if priority value exists
    boolean existsByValue(Integer value);

    // Find all priorities ordered by display order and value
    List<TaskPriority> findAllByOrderByDisplayOrderAscValueAsc();
}
