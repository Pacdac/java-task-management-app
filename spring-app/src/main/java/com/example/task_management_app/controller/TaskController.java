package com.example.task_management_app.controller;

import com.example.task_management_app.dto.TaskDTO;
import com.example.task_management_app.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get all tasks
     * 
     * @return List of all tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get a task by ID
     * 
     * @param id Task ID
     * @return Task details
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Integer id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Get tasks by user ID
     * 
     * @param userId User ID
     * @return List of tasks for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksByUserId(@PathVariable Integer userId) {
        List<TaskDTO> tasks = taskService.getTasksByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Create a new task
     * 
     * @param taskDTO Task data
     * @return Created task
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Update an existing task
     * 
     * @param id      Task ID
     * @param taskDTO Updated task data
     * @return Updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id, @Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete a task
     * 
     * @param id Task ID
     * @return Empty response with 204 status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search tasks by title
     * 
     * @param keyword Search keyword
     * @return List of matching tasks
     */
    @GetMapping("/search")
    public ResponseEntity<List<TaskDTO>> searchTasks(@RequestParam String keyword) {
        List<TaskDTO> tasks = taskService.searchTasksByTitle(keyword);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get overdue tasks for a user
     * 
     * @param userId User ID
     * @return List of overdue tasks
     */
    @GetMapping("/overdue/{userId}")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks(@PathVariable Integer userId) {
        List<TaskDTO> tasks = taskService.getOverdueTasks(userId);
        return ResponseEntity.ok(tasks);
    }
}