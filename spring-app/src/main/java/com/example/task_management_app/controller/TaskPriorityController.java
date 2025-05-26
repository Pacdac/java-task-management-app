package com.example.task_management_app.controller;

import com.example.task_management_app.dto.TaskPriorityDTO;
import com.example.task_management_app.service.TaskPriorityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-priorities")
public class TaskPriorityController {

    private final TaskPriorityService taskPriorityService;

    @Autowired
    public TaskPriorityController(TaskPriorityService taskPriorityService) {
        this.taskPriorityService = taskPriorityService;
    }

    /**
     * Get all task priorities
     * 
     * @return List of all task priorities
     */
    @GetMapping
    public ResponseEntity<List<TaskPriorityDTO>> getAllTaskPriorities() {
        List<TaskPriorityDTO> priorities = taskPriorityService.getAllTaskPriorities();
        return ResponseEntity.ok(priorities);
    }

    /**
     * Get a task priority by ID
     * 
     * @param id Priority ID
     * @return Task priority details
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskPriorityDTO> getTaskPriorityById(@PathVariable Integer id) {
        TaskPriorityDTO priority = taskPriorityService.getTaskPriorityById(id);
        return ResponseEntity.ok(priority);
    }

    /**
     * Get a task priority by name
     * 
     * @param name Priority name
     * @return Task priority details
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<TaskPriorityDTO> getTaskPriorityByName(@PathVariable String name) {
        TaskPriorityDTO priority = taskPriorityService.getTaskPriorityByName(name);
        return ResponseEntity.ok(priority);
    }

    /**
     * Get a task priority by value
     * 
     * @param value Priority value
     * @return Task priority details
     */
    @GetMapping("/value/{value}")
    public ResponseEntity<TaskPriorityDTO> getTaskPriorityByValue(@PathVariable Integer value) {
        TaskPriorityDTO priority = taskPriorityService.getTaskPriorityByValue(value);
        return ResponseEntity.ok(priority);
    }

    /**
     * Create a new task priority (Admin only)
     * 
     * @param taskPriorityDTO Task priority data
     * @return Created task priority
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskPriorityDTO> createTaskPriority(@Valid @RequestBody TaskPriorityDTO taskPriorityDTO) {
        TaskPriorityDTO createdPriority = taskPriorityService.createTaskPriority(taskPriorityDTO);
        return new ResponseEntity<>(createdPriority, HttpStatus.CREATED);
    }

    /**
     * Update an existing task priority (Admin only)
     * 
     * @param id              Priority ID
     * @param taskPriorityDTO Updated task priority data
     * @return Updated task priority
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskPriorityDTO> updateTaskPriority(@PathVariable Integer id,
            @Valid @RequestBody TaskPriorityDTO taskPriorityDTO) {
        TaskPriorityDTO updatedPriority = taskPriorityService.updateTaskPriority(id, taskPriorityDTO);
        return ResponseEntity.ok(updatedPriority);
    }

    /**
     * Delete a task priority (Admin only)
     * 
     * @param id Priority ID
     * @return Empty response with 204 status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTaskPriority(@PathVariable Integer id) {
        taskPriorityService.deleteTaskPriority(id);
        return ResponseEntity.noContent().build();
    }
}
