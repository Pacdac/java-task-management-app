package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskStatusDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.TaskStatus;
import com.example.task_management_app.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    /**
     * Get all task statuses
     * 
     * @return List of task status DTOs
     */
    public List<TaskStatusDTO> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get task status by ID
     * 
     * @param id Status ID
     * @return Task status DTO
     */
    public TaskStatusDTO getTaskStatusById(Integer id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with id: " + id));
        return convertToDTO(taskStatus);
    }

    /**
     * Get task status by name
     * 
     * @param name Status name
     * @return Task status DTO
     */
    public TaskStatusDTO getTaskStatusByName(String name) {
        TaskStatus taskStatus = taskStatusRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with name: " + name));
        return convertToDTO(taskStatus);
    }

    /**
     * Create a new task status
     * 
     * @param taskStatusDTO Task status DTO
     * @return Created task status DTO
     */
    @Transactional
    public TaskStatusDTO createTaskStatus(TaskStatusDTO taskStatusDTO) {

        if (taskStatusRepository.existsByName(taskStatusDTO.getName())) {
            throw new IllegalArgumentException(
                    "Task status with name '" + taskStatusDTO.getName() + "' already exists");
        }

        TaskStatus taskStatus = convertToEntity(taskStatusDTO);
        TaskStatus savedTaskStatus = taskStatusRepository.save(taskStatus);
        return convertToDTO(savedTaskStatus);
    }

    /**
     * Update an existing task status
     * 
     * @param id            Status ID
     * @param taskStatusDTO Task status DTO
     * @return Updated task status DTO
     */
    @Transactional
    public TaskStatusDTO updateTaskStatus(Integer id, TaskStatusDTO taskStatusDTO) {
        TaskStatus existingTaskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found with id: " + id));

        if (!existingTaskStatus.getName().equals(taskStatusDTO.getName()) &&
                taskStatusRepository.existsByName(taskStatusDTO.getName())) {
            throw new IllegalArgumentException(
                    "Task status with name '" + taskStatusDTO.getName() + "' already exists");
        }

        existingTaskStatus.setName(taskStatusDTO.getName());
        existingTaskStatus.setDescription(taskStatusDTO.getDescription());
        existingTaskStatus.setColor(taskStatusDTO.getColor());

        TaskStatus updatedTaskStatus = taskStatusRepository.save(existingTaskStatus);
        return convertToDTO(updatedTaskStatus);
    }

    /**
     * Delete a task status
     * 
     * @param id Status ID
     */
    @Transactional
    public void deleteTaskStatus(Integer id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task status not found with id: " + id);
        }
        taskStatusRepository.deleteById(id);
    }

    /**
     * Convert TaskStatus entity to TaskStatusDTO
     * 
     * @param taskStatus Task status entity
     * @return Task status DTO
     */
    private TaskStatusDTO convertToDTO(TaskStatus taskStatus) {
        TaskStatusDTO dto = new TaskStatusDTO();
        dto.setId(taskStatus.getId());
        dto.setName(taskStatus.getName());
        dto.setDescription(taskStatus.getDescription());
        dto.setColor(taskStatus.getColor());
        return dto;
    }

    /**
     * Convert TaskStatusDTO to TaskStatus entity
     * 
     * @param taskStatusDTO Task status DTO
     * @return Task status entity
     */
    private TaskStatus convertToEntity(TaskStatusDTO taskStatusDTO) {
        TaskStatus taskStatus = new TaskStatus();

        if (taskStatusDTO.getId() != null) {
            taskStatus.setId(taskStatusDTO.getId());
        }

        taskStatus.setName(taskStatusDTO.getName());
        taskStatus.setDescription(taskStatusDTO.getDescription());
        taskStatus.setColor(taskStatusDTO.getColor());

        return taskStatus;
    }
}