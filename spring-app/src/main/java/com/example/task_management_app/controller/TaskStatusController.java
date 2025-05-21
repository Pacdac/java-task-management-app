package com.example.task_management_app.controller;

import com.example.task_management_app.dto.TaskStatusDTO;
import com.example.task_management_app.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-statuses")
@CrossOrigin(origins = "*") // For development - restrict this in production
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @Autowired
    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    /**
     * Get all task statuses
     * 
     * @return List of all task statuses
     */
    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> getAllTaskStatuses() {
        List<TaskStatusDTO> statuses = taskStatusService.getAllTaskStatuses();
        return ResponseEntity.ok(statuses);
    }

    /**
     * Get a task status by ID
     * 
     * @param id Status ID
     * @return Task status details
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> getTaskStatusById(@PathVariable Integer id) {
        TaskStatusDTO status = taskStatusService.getTaskStatusById(id);
        return ResponseEntity.ok(status);
    }

    /**
     * Get a task status by name
     * 
     * @param name Status name
     * @return Task status details
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<TaskStatusDTO> getTaskStatusByName(@PathVariable String name) {
        TaskStatusDTO status = taskStatusService.getTaskStatusByName(name);
        return ResponseEntity.ok(status);
    }

    /**
     * Create a new task status
     * 
     * @param taskStatusDTO Task status data
     * @return Created task status
     */
    @PostMapping
    public ResponseEntity<TaskStatusDTO> createTaskStatus(@Valid @RequestBody TaskStatusDTO taskStatusDTO) {
        TaskStatusDTO createdStatus = taskStatusService.createTaskStatus(taskStatusDTO);
        return new ResponseEntity<>(createdStatus, HttpStatus.CREATED);
    }

    /**
     * Update an existing task status
     * 
     * @param id            Status ID
     * @param taskStatusDTO Updated task status data
     * @return Updated task status
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> updateTaskStatus(@PathVariable Integer id,
            @Valid @RequestBody TaskStatusDTO taskStatusDTO) {
        TaskStatusDTO updatedStatus = taskStatusService.updateTaskStatus(id, taskStatusDTO);
        return ResponseEntity.ok(updatedStatus);
    }

    /**
     * Delete a task status
     * 
     * @param id Status ID
     * @return Empty response with 204 status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Integer id) {
        taskStatusService.deleteTaskStatus(id);
        return ResponseEntity.noContent().build();
    }
}