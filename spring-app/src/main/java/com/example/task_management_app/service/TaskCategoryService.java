package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskCategoryDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.TaskCategory;
import com.example.task_management_app.repository.TaskCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;

    @Autowired
    public TaskCategoryService(TaskCategoryRepository taskCategoryRepository) {
        this.taskCategoryRepository = taskCategoryRepository;
    }

    /**
     * Get all task categories
     * 
     * @return List of task category DTOs
     */
    public List<TaskCategoryDTO> getAllTaskCategories() {
        return taskCategoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get task category by ID
     * 
     * @param id Category ID
     * @return Task category DTO
     */
    public TaskCategoryDTO getTaskCategoryById(Integer id) {
        TaskCategory taskCategory = taskCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task category not found with id: " + id));
        return convertToDTO(taskCategory);
    }

    /**
     * Get task category by name
     * 
     * @param name Category name
     * @return Task category DTO
     */
    public TaskCategoryDTO getTaskCategoryByName(String name) {
        TaskCategory taskCategory = taskCategoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Task category not found with name: " + name));
        return convertToDTO(taskCategory);
    }

    /**
     * Create a new task category
     * 
     * @param taskCategoryDTO Task category DTO
     * @return Created task category DTO
     */
    @Transactional
    public TaskCategoryDTO createTaskCategory(TaskCategoryDTO taskCategoryDTO) {
        // Check if category name already exists
        if (taskCategoryRepository.existsByName(taskCategoryDTO.getName())) {
            throw new IllegalArgumentException(
                    "Task category with name '" + taskCategoryDTO.getName() + "' already exists");
        }

        TaskCategory taskCategory = convertToEntity(taskCategoryDTO);
        TaskCategory savedTaskCategory = taskCategoryRepository.save(taskCategory);
        return convertToDTO(savedTaskCategory);
    }

    /**
     * Update an existing task category
     * 
     * @param id              Category ID
     * @param taskCategoryDTO Task category DTO
     * @return Updated task category DTO
     */
    @Transactional
    public TaskCategoryDTO updateTaskCategory(Integer id, TaskCategoryDTO taskCategoryDTO) {
        TaskCategory existingTaskCategory = taskCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task category not found with id: " + id));

        // Check if name is being changed and already exists
        if (!existingTaskCategory.getName().equals(taskCategoryDTO.getName()) &&
                taskCategoryRepository.existsByName(taskCategoryDTO.getName())) {
            throw new IllegalArgumentException(
                    "Task category with name '" + taskCategoryDTO.getName() + "' already exists");
        }

        // Update fields
        existingTaskCategory.setName(taskCategoryDTO.getName());
        existingTaskCategory.setDescription(taskCategoryDTO.getDescription());
        existingTaskCategory.setColor(taskCategoryDTO.getColor());

        TaskCategory updatedTaskCategory = taskCategoryRepository.save(existingTaskCategory);
        return convertToDTO(updatedTaskCategory);
    }

    /**
     * Delete a task category
     * 
     * @param id Category ID
     */
    @Transactional
    public void deleteTaskCategory(Integer id) {
        if (!taskCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task category not found with id: " + id);
        }
        taskCategoryRepository.deleteById(id);
    }

    /**
     * Convert TaskCategory entity to TaskCategoryDTO
     * 
     * @param taskCategory Task category entity
     * @return Task category DTO
     */
    private TaskCategoryDTO convertToDTO(TaskCategory taskCategory) {
        TaskCategoryDTO dto = new TaskCategoryDTO();
        dto.setId(taskCategory.getId());
        dto.setName(taskCategory.getName());
        dto.setDescription(taskCategory.getDescription());
        dto.setColor(taskCategory.getColor());
        return dto;
    }

    /**
     * Convert TaskCategoryDTO to TaskCategory entity
     * 
     * @param taskCategoryDTO Task category DTO
     * @return Task category entity
     */
    private TaskCategory convertToEntity(TaskCategoryDTO taskCategoryDTO) {
        TaskCategory taskCategory = new TaskCategory();

        // Don't set ID for new task categories, let the database generate it
        if (taskCategoryDTO.getId() != null) {
            taskCategory.setId(taskCategoryDTO.getId());
        }

        taskCategory.setName(taskCategoryDTO.getName());
        taskCategory.setDescription(taskCategoryDTO.getDescription());
        taskCategory.setColor(taskCategoryDTO.getColor());

        return taskCategory;
    }
}