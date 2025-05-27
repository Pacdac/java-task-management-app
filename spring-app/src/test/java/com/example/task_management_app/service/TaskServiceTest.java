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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private TaskCategoryRepository taskCategoryRepository;

    @Mock
    private TaskPriorityRepository taskPriorityRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskDTO testTaskDTO;
    private User testUser;
    private TaskStatus testStatus;
    private TaskCategory testCategory;
    private TaskPriority testPriority;

    @BeforeEach
    void setUp() {
        // Setup test entities
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole("ROLE_USER");

        testStatus = new TaskStatus();
        testStatus.setId(1);
        testStatus.setName("TODO");
        testStatus.setDescription("To Do");
        testStatus.setColor("#FF0000");

        testCategory = new TaskCategory();
        testCategory.setId(1);
        testCategory.setName("Work");
        testCategory.setDescription("Work related tasks");
        testCategory.setColor("#0000FF");

        testPriority = new TaskPriority();
        testPriority.setId(1);
        testPriority.setName("High");
        testPriority.setValue(3);
        testPriority.setDescription("High priority");
        testPriority.setColor("#FF0000");
        testPriority.setDisplayOrder(1);

        testTask = new Task();
        testTask.setId(1);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test task description");
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setUser(testUser);
        testTask.setStatus(testStatus);
        testTask.setCategory(testCategory);
        testTask.setPriority(testPriority);
        testTask.setCreatedAt(OffsetDateTime.now());
        testTask.setUpdatedAt(OffsetDateTime.now());

        testTaskDTO = new TaskDTO();
        testTaskDTO.setId(1);
        testTaskDTO.setTitle("Test Task");
        testTaskDTO.setDescription("Test task description");
        testTaskDTO.setDueDate(LocalDate.now().plusDays(7));
        testTaskDTO.setUserId(1);
        testTaskDTO.setStatusId(1);
        testTaskDTO.setCategoryId(1);
        testTaskDTO.setPriorityId(1);
    }

    @Test
    void getAllTasks_ReturnsListOfTaskDTOs() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask));

        List<TaskDTO> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        assertThat(result.get(0).getDescription()).isEqualTo("Test task description");
    }

    @Test
    void getTaskById_WhenTaskExists_ReturnsTaskDTO() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));

        TaskDTO result = taskService.getTaskById(1);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found with id: 99");
    }

    @Test
    void getTasksByUserId_WhenUserExists_ReturnsTaskDTOs() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(taskRepository.findByUserId(1)).thenReturn(Arrays.asList(testTask));

        List<TaskDTO> result = taskService.getTasksByUserId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
    }

    @Test
    void getTasksByUserId_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> taskService.getTasksByUserId(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 99");
    }

    @Test
    void createTask_WithValidData_ReturnsCreatedTaskDTO() {
        TaskDTO newTaskDTO = new TaskDTO();
        newTaskDTO.setTitle("New Task");
        newTaskDTO.setDescription("New task description");
        newTaskDTO.setUserId(1);
        newTaskDTO.setStatusId(1);
        newTaskDTO.setPriorityId(1);
        newTaskDTO.setCategoryId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testStatus));
        when(taskPriorityRepository.findById(1)).thenReturn(Optional.of(testPriority));
        when(taskCategoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskDTO result = taskService.createTask(newTaskDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void updateTask_WithValidData_ReturnsUpdatedTaskDTO() {
        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("Updated Task");
        updateDTO.setDescription("Updated description");
        updateDTO.setStatusId(1);

        Task updatedTask = new Task();
        updatedTask.setId(1);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated description");
        updatedTask.setStatus(testStatus);

        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testStatus));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskDTO result = taskService.updateTask(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Task");
        assertThat(result.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_ThrowsResourceNotFoundException() {
        TaskDTO updateDTO = new TaskDTO();
        updateDTO.setTitle("Updated Task");

        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(99, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found with id: 99");
    }

    @Test
    void deleteTask_WhenTaskExists_DeletesSuccessfully() {
        when(taskRepository.existsById(1)).thenReturn(true);

        taskService.deleteTask(1);

        // No exception should be thrown
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> taskService.deleteTask(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found with id: 99");
    }

    @Test
    void searchTasksByTitle_ReturnsMatchingTasks() {
        when(taskRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(Arrays.asList(testTask));

        List<TaskDTO> result = taskService.searchTasksByTitle("Test");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
    }

}
