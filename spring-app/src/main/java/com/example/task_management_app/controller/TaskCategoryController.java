package com.example.task_management_app.controller;

import com.example.task_management_app.dto.TaskCategoryDTO;
import com.example.task_management_app.service.TaskCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-categories")
@CrossOrigin(origins = "*") // For development - restrict this in production
public class TaskCategoryController {

    private final TaskCategoryService taskCategoryService;

    @Autowired
    public TaskCategoryController(TaskCategoryService taskCategoryService) {
        this.taskCategoryService = taskCategoryService;
    }

    /**
     * Get all task categories
     * 
     * @return List of all task categories
     */
    @GetMapping
    public ResponseEntity<List<TaskCategoryDTO>> getAllTaskCategories() {
        List<TaskCategoryDTO> categories = taskCategoryService.getAllTaskCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get a task category by ID
     * 
     * @param id Category ID
     * @return Task category details
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskCategoryDTO> getTaskCategoryById(@PathVariable Integer id) {
        TaskCategoryDTO category = taskCategoryService.getTaskCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Get a task category by name
     * 
     * @param name Category name
     * @return Task category details
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<TaskCategoryDTO> getTaskCategoryByName(@PathVariable String name) {
        TaskCategoryDTO category = taskCategoryService.getTaskCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    /**
     * Create a new task category
     * 
     * @param taskCategoryDTO Task category data
     * @return Created task category
     */
    @PostMapping
    public ResponseEntity<TaskCategoryDTO> createTaskCategory(@Valid @RequestBody TaskCategoryDTO taskCategoryDTO) {
        TaskCategoryDTO createdCategory = taskCategoryService.createTaskCategory(taskCategoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Update an existing task category
     * 
     * @param id              Category ID
     * @param taskCategoryDTO Updated task category data
     * @return Updated task category
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskCategoryDTO> updateTaskCategory(@PathVariable Integer id,
            @Valid @RequestBody TaskCategoryDTO taskCategoryDTO) {
        TaskCategoryDTO updatedCategory = taskCategoryService.updateTaskCategory(id, taskCategoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Delete a task category
     * 
     * @param id Category ID
     * @return Empty response with 204 status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskCategory(@PathVariable Integer id) {
        taskCategoryService.deleteTaskCategory(id);
        return ResponseEntity.noContent().build();
    }
}