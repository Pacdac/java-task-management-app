package com.example.task_management_app.repository;

import com.example.task_management_app.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {

    // Find task status by name
    Optional<TaskStatus> findByName(String name);

    // Check if status name exists
    boolean existsByName(String name);
}