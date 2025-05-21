package com.example.task_management_app.repository;

import com.example.task_management_app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // Find tasks by user id
    List<Task> findByUserId(Integer userId);

    // Find tasks by status id
    List<Task> findByStatusId(Integer statusId);

    // Find tasks by category id
    List<Task> findByCategoryId(Integer categoryId);

    // Find tasks by title containing keyword
    List<Task> findByTitleContainingIgnoreCase(String keyword);

    // Custom query to find tasks by priority and status
    @Query("SELECT t FROM Task t WHERE t.priority = :priority AND t.status.id = :statusId")
    List<Task> findByPriorityAndStatus(@Param("priority") Integer priority, @Param("statusId") Integer statusId);

    // Custom query to find tasks by user and due date before today
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate < CURRENT_DATE")
    List<Task> findOverdueTasks(@Param("userId") Integer userId);
}