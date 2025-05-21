package com.example.task_management_app.repository;

import com.example.task_management_app.model.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Integer> {

    // Find task category by name
    Optional<TaskCategory> findByName(String name);

    // Check if category name exists
    boolean existsByName(String name);
}