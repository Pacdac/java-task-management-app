package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.Task;
import com.example.task_management_app.model.TaskCategory;
import com.example.task_management_app.model.TaskStatus;
import com.example.task_management_app.model.User;
import com.example.task_management_app.repository.TaskCategoryRepository;
import com.example.task_management_app.repository.TaskRepository;
import com.example.task_management_app.repository.TaskStatusRepository;
import com.example.task_management_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskCategoryRepository taskCategoryRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
            UserRepository userRepository,
            TaskStatusRepository taskStatusRepository,
            TaskCategoryRepository taskCategoryRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.taskCategoryRepository = taskCategoryRepository;
    }

    /**
     * Get all tasks
     * 
     * @return List of task DTOs
     */
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a task by ID
     * 
     * @param id Task ID
     * @return Task DTO
     */
    public TaskDTO getTaskById(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return convertToDTO(task);
    }

    /**
     * Get tasks by user ID
     * 
     * @param userId User ID
     * @return List of task DTOs
     */
    public List<TaskDTO> getTasksByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return taskRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new task
     * 
     * @param taskDTO Task DTO
     * @return Created task DTO
     */
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = convertToEntity(taskDTO);
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    /**
     * Update an existing task
     * 
     * @param id      Task ID
     * @param taskDTO Task DTO
     * @return Updated task DTO
     */
    @Transactional
    public TaskDTO updateTask(Integer id, TaskDTO taskDTO) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // Update the fields
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setDueDate(taskDTO.getDueDate());
        existingTask.setPriority(taskDTO.getPriority());

        // Update user if present
        if (taskDTO.getUserId() != null) {
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getUserId()));
            existingTask.setUser(user);
        }

        // Update status if present
        if (taskDTO.getStatusId() != null) {
            TaskStatus status = taskStatusRepository.findById(taskDTO.getStatusId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Status not found with id: " + taskDTO.getStatusId()));
            existingTask.setStatus(status);
        }

        // Update category if present
        if (taskDTO.getCategoryId() != null) {
            TaskCategory category = taskCategoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + taskDTO.getCategoryId()));
            existingTask.setCategory(category);
        }

        Task updatedTask = taskRepository.save(existingTask);
        return convertToDTO(updatedTask);
    }

    /**
     * Delete a task
     * 
     * @param id Task ID
     */
    @Transactional
    public void deleteTask(Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Search tasks by title
     * 
     * @param keyword Search keyword
     * @return List of matching task DTOs
     */
    public List<TaskDTO> searchTasksByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue tasks for a user
     * 
     * @param userId User ID
     * @return List of overdue task DTOs
     */
    public List<TaskDTO> getOverdueTasks(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return taskRepository.findOverdueTasks(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Task entity to TaskDTO
     * 
     * @param task Task entity
     * @return Task DTO
     */
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getUser() != null) {
            dto.setUserId(task.getUser().getId());
            dto.setUsername(task.getUser().getUsername());
        }

        if (task.getStatus() != null) {
            dto.setStatusId(task.getStatus().getId());
            dto.setStatusName(task.getStatus().getName());
        }

        if (task.getCategory() != null) {
            dto.setCategoryId(task.getCategory().getId());
            dto.setCategoryName(task.getCategory().getName());
        }

        return dto;
    }

    /**
     * Convert TaskDTO to Task entity
     * 
     * @param taskDTO Task DTO
     * @return Task entity
     */
    private Task convertToEntity(TaskDTO taskDTO) {
        Task task = new Task();

        // Don't set ID for new tasks, let the database generate it
        if (taskDTO.getId() != null) {
            task.setId(taskDTO.getId());
        }

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setPriority(taskDTO.getPriority());

        // Set user if userId is present
        if (taskDTO.getUserId() != null) {
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getUserId()));
            task.setUser(user);
        }

        // Set status if statusId is present
        if (taskDTO.getStatusId() != null) {
            TaskStatus status = taskStatusRepository.findById(taskDTO.getStatusId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Status not found with id: " + taskDTO.getStatusId()));
            task.setStatus(status);
        }

        // Set category if categoryId is present
        if (taskDTO.getCategoryId() != null) {
            TaskCategory category = taskCategoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + taskDTO.getCategoryId()));
            task.setCategory(category);
        }

        return task;
    }
}