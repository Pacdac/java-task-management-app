package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.Task;
import com.example.task_management_app.model.TaskCategory;
import com.example.task_management_app.model.TaskPriority;
import com.example.task_management_app.model.TaskStatus;
import com.example.task_management_app.model.User;
import com.example.task_management_app.repository.TaskCategoryRepository;
import com.example.task_management_app.repository.TaskPriorityRepository;
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
    private final TaskPriorityRepository taskPriorityRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
            UserRepository userRepository,
            TaskStatusRepository taskStatusRepository,
            TaskCategoryRepository taskCategoryRepository,
            TaskPriorityRepository taskPriorityRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.taskCategoryRepository = taskCategoryRepository;
        this.taskPriorityRepository = taskPriorityRepository;
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

        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setDueDate(taskDTO.getDueDate());

        if (taskDTO.getPriorityId() != null) {
            TaskPriority priority = taskPriorityRepository.findById(taskDTO.getPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Priority not found with id: " + taskDTO.getPriorityId()));
            existingTask.setPriority(priority);
        }

        if (taskDTO.getUserId() != null) {
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getUserId()));
            existingTask.setUser(user);
        }

        if (taskDTO.getStatusId() != null) {
            TaskStatus status = taskStatusRepository.findById(taskDTO.getStatusId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Status not found with id: " + taskDTO.getStatusId()));
            existingTask.setStatus(status);
        }

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
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getUser() != null) {
            dto.setUserId(task.getUser().getId());
            dto.setUsername(task.getUser().getUsername());
        }

        if (task.getPriority() != null) {
            dto.setPriorityId(task.getPriority().getId());
            dto.setPriorityName(task.getPriority().getName());
            dto.setPriorityValue(task.getPriority().getValue());
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

        if (taskDTO.getId() != null) {
            task.setId(taskDTO.getId());
        }
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());

        if (taskDTO.getUserId() != null) {
            User user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + taskDTO.getUserId()));
            task.setUser(user);
        }

        if (taskDTO.getPriorityId() != null) {
            TaskPriority priority = taskPriorityRepository.findById(taskDTO.getPriorityId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Priority not found with id: " + taskDTO.getPriorityId()));
            task.setPriority(priority);
        } else if (taskDTO.getPriorityValue() != null) {
            TaskPriority priority = taskPriorityRepository.findByValue(taskDTO.getPriorityValue())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Priority not found with value: " + taskDTO.getPriorityValue()));
            task.setPriority(priority);
        }

        if (taskDTO.getStatusId() != null) {
            TaskStatus status = taskStatusRepository.findById(taskDTO.getStatusId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Status not found with id: " + taskDTO.getStatusId()));
            task.setStatus(status);
        }

        if (taskDTO.getCategoryId() != null) {
            TaskCategory category = taskCategoryRepository.findById(taskDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + taskDTO.getCategoryId()));
            task.setCategory(category);
        }

        return task;
    }
}