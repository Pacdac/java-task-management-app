package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskPriorityDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.TaskPriority;
import com.example.task_management_app.repository.TaskPriorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskPriorityService {

    private final TaskPriorityRepository taskPriorityRepository;

    @Autowired
    public TaskPriorityService(TaskPriorityRepository taskPriorityRepository) {
        this.taskPriorityRepository = taskPriorityRepository;
    }

    /**
     * Get all task priorities ordered by display order and value
     * 
     * @return List of task priority DTOs
     */
    public List<TaskPriorityDTO> getAllTaskPriorities() {
        return taskPriorityRepository.findAllByOrderByDisplayOrderAscValueAsc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get task priority by ID
     * 
     * @param id Priority ID
     * @return Task priority DTO
     */
    public TaskPriorityDTO getTaskPriorityById(Integer id) {
        TaskPriority taskPriority = taskPriorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority not found with id: " + id));
        return convertToDTO(taskPriority);
    }

    /**
     * Get task priority by name
     * 
     * @param name Priority name
     * @return Task priority DTO
     */
    public TaskPriorityDTO getTaskPriorityByName(String name) {
        TaskPriority taskPriority = taskPriorityRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority not found with name: " + name));
        return convertToDTO(taskPriority);
    }

    /**
     * Get task priority by value
     * 
     * @param value Priority value
     * @return Task priority DTO
     */
    public TaskPriorityDTO getTaskPriorityByValue(Integer value) {
        TaskPriority taskPriority = taskPriorityRepository.findByValue(value)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority not found with value: " + value));
        return convertToDTO(taskPriority);
    }

    /**
     * Create a new task priority
     * 
     * @param taskPriorityDTO Task priority DTO
     * @return Created task priority DTO
     */
    @Transactional
    public TaskPriorityDTO createTaskPriority(TaskPriorityDTO taskPriorityDTO) {
        // Check if priority name already exists
        if (taskPriorityRepository.existsByName(taskPriorityDTO.getName())) {
            throw new IllegalArgumentException(
                    "Task priority with name '" + taskPriorityDTO.getName() + "' already exists");
        }

        // Check if priority value already exists
        if (taskPriorityRepository.existsByValue(taskPriorityDTO.getValue())) {
            throw new IllegalArgumentException(
                    "Task priority with value '" + taskPriorityDTO.getValue() + "' already exists");
        }

        TaskPriority taskPriority = convertToEntity(taskPriorityDTO);
        TaskPriority savedTaskPriority = taskPriorityRepository.save(taskPriority);
        return convertToDTO(savedTaskPriority);
    }

    /**
     * Update an existing task priority
     * 
     * @param id              Priority ID
     * @param taskPriorityDTO Task priority DTO
     * @return Updated task priority DTO
     */
    @Transactional
    public TaskPriorityDTO updateTaskPriority(Integer id, TaskPriorityDTO taskPriorityDTO) {
        TaskPriority existingTaskPriority = taskPriorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task priority not found with id: " + id));

        // Check if name is being changed and already exists
        if (!existingTaskPriority.getName().equals(taskPriorityDTO.getName()) &&
                taskPriorityRepository.existsByName(taskPriorityDTO.getName())) {
            throw new IllegalArgumentException(
                    "Task priority with name '" + taskPriorityDTO.getName() + "' already exists");
        }

        // Check if value is being changed and already exists
        if (!existingTaskPriority.getValue().equals(taskPriorityDTO.getValue()) &&
                taskPriorityRepository.existsByValue(taskPriorityDTO.getValue())) {
            throw new IllegalArgumentException(
                    "Task priority with value '" + taskPriorityDTO.getValue() + "' already exists");
        }

        // Update fields
        existingTaskPriority.setName(taskPriorityDTO.getName());
        existingTaskPriority.setValue(taskPriorityDTO.getValue());
        existingTaskPriority.setDescription(taskPriorityDTO.getDescription());
        existingTaskPriority.setColor(taskPriorityDTO.getColor());
        existingTaskPriority.setDisplayOrder(taskPriorityDTO.getDisplayOrder());

        TaskPriority updatedTaskPriority = taskPriorityRepository.save(existingTaskPriority);
        return convertToDTO(updatedTaskPriority);
    }

    /**
     * Delete a task priority
     * 
     * @param id Priority ID
     */
    @Transactional
    public void deleteTaskPriority(Integer id) {
        if (!taskPriorityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task priority not found with id: " + id);
        }
        taskPriorityRepository.deleteById(id);
    }

    /**
     * Convert TaskPriority entity to TaskPriorityDTO
     * 
     * @param taskPriority Task priority entity
     * @return Task priority DTO
     */
    private TaskPriorityDTO convertToDTO(TaskPriority taskPriority) {
        TaskPriorityDTO dto = new TaskPriorityDTO();
        dto.setId(taskPriority.getId());
        dto.setName(taskPriority.getName());
        dto.setValue(taskPriority.getValue());
        dto.setDescription(taskPriority.getDescription());
        dto.setColor(taskPriority.getColor());
        dto.setDisplayOrder(taskPriority.getDisplayOrder());
        return dto;
    }

    /**
     * Convert TaskPriorityDTO to TaskPriority entity
     * 
     * @param taskPriorityDTO Task priority DTO
     * @return Task priority entity
     */
    private TaskPriority convertToEntity(TaskPriorityDTO taskPriorityDTO) {
        TaskPriority taskPriority = new TaskPriority();

        // Don't set ID for new task priorities, let the database generate it
        if (taskPriorityDTO.getId() != null) {
            taskPriority.setId(taskPriorityDTO.getId());
        }

        taskPriority.setName(taskPriorityDTO.getName());
        taskPriority.setValue(taskPriorityDTO.getValue());
        taskPriority.setDescription(taskPriorityDTO.getDescription());
        taskPriority.setColor(taskPriorityDTO.getColor());
        taskPriority.setDisplayOrder(taskPriorityDTO.getDisplayOrder());

        return taskPriority;
    }
}
