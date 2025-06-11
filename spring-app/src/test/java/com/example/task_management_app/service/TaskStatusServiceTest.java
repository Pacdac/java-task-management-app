package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskStatusDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.TaskStatus;
import com.example.task_management_app.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @InjectMocks
    private TaskStatusService taskStatusService;

    private TaskStatus testTaskStatus;
    private TaskStatusDTO testTaskStatusDTO;

    @BeforeEach
    void setUp() {
        testTaskStatus = new TaskStatus();
        testTaskStatus.setId(1);
        testTaskStatus.setName("TODO");
        testTaskStatus.setDescription("To Do");
        testTaskStatus.setColor("#FF0000");

        testTaskStatusDTO = new TaskStatusDTO();
        testTaskStatusDTO.setId(1);
        testTaskStatusDTO.setName("TODO");
        testTaskStatusDTO.setDescription("To Do");
        testTaskStatusDTO.setColor("#FF0000");
    }

    @Test
    void getAllTaskStatuses_ReturnsListOfTaskStatusDTOs() {
        when(taskStatusRepository.findAll()).thenReturn(Arrays.asList(testTaskStatus));

        List<TaskStatusDTO> result = taskStatusService.getAllTaskStatuses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("TODO");
        assertThat(result.get(0).getDescription()).isEqualTo("To Do");
    }

    @Test
    void getTaskStatusById_WhenTaskStatusExists_ReturnsTaskStatusDTO() {
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testTaskStatus));

        TaskStatusDTO result = taskStatusService.getTaskStatusById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TODO");
    }

    @Test
    void getTaskStatusById_WhenTaskStatusDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskStatusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskStatusService.getTaskStatusById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task status not found with id: 99");
    }

    @Test
    void getTaskStatusByName_WhenTaskStatusExists_ReturnsTaskStatusDTO() {
        when(taskStatusRepository.findByName("TODO")).thenReturn(Optional.of(testTaskStatus));

        TaskStatusDTO result = taskStatusService.getTaskStatusByName("TODO");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TODO");
        assertThat(result.getDescription()).isEqualTo("To Do");
    }

    @Test
    void getTaskStatusByName_WhenTaskStatusDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskStatusRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskStatusService.getTaskStatusByName("UNKNOWN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task status not found with name: UNKNOWN");
    }

    @Test
    void createTaskStatus_WithValidData_ReturnsCreatedTaskStatusDTO() {
        TaskStatusDTO newTaskStatusDTO = new TaskStatusDTO();
        newTaskStatusDTO.setName("IN_PROGRESS");
        newTaskStatusDTO.setDescription("In Progress");
        newTaskStatusDTO.setColor("#FFFF00");

        when(taskStatusRepository.existsByName("IN_PROGRESS")).thenReturn(false);
        when(taskStatusRepository.save(any(TaskStatus.class))).thenReturn(testTaskStatus);

        TaskStatusDTO result = taskStatusService.createTaskStatus(newTaskStatusDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TODO");
    }

    @Test
    void createTaskStatus_WithDuplicateName_ThrowsIllegalArgumentException() {
        TaskStatusDTO newTaskStatusDTO = new TaskStatusDTO();
        newTaskStatusDTO.setName("TODO");
        newTaskStatusDTO.setDescription("To Do");

        when(taskStatusRepository.existsByName("TODO")).thenReturn(true);

        assertThatThrownBy(() -> taskStatusService.createTaskStatus(newTaskStatusDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task status with name 'TODO' already exists");
    }

    @Test
    void updateTaskStatus_WithValidData_ReturnsUpdatedTaskStatusDTO() {
        TaskStatusDTO updateDTO = new TaskStatusDTO();
        updateDTO.setName("COMPLETED");
        updateDTO.setDescription("Completed");
        updateDTO.setColor("#00FF00");

        TaskStatus updatedTaskStatus = new TaskStatus();
        updatedTaskStatus.setId(1);
        updatedTaskStatus.setName("COMPLETED");
        updatedTaskStatus.setDescription("Completed");
        updatedTaskStatus.setColor("#00FF00");

        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testTaskStatus));
        when(taskStatusRepository.existsByName("COMPLETED")).thenReturn(false);
        when(taskStatusRepository.save(any(TaskStatus.class))).thenReturn(updatedTaskStatus);

        TaskStatusDTO result = taskStatusService.updateTaskStatus(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("COMPLETED");
        assertThat(result.getDescription()).isEqualTo("Completed");
    }

    @Test
    void updateTaskStatus_WhenTaskStatusDoesNotExist_ThrowsResourceNotFoundException() {
        TaskStatusDTO updateDTO = new TaskStatusDTO();
        updateDTO.setName("UPDATED");

        when(taskStatusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskStatusService.updateTaskStatus(99, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task status not found with id: 99");
    }

    @Test
    void deleteTaskStatus_WhenTaskStatusExists_DeletesSuccessfully() {
        when(taskStatusRepository.existsById(1)).thenReturn(true);

        taskStatusService.deleteTaskStatus(1);

    }

    @Test
    void deleteTaskStatus_WhenTaskStatusDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskStatusRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> taskStatusService.deleteTaskStatus(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task status not found with id: 99");
    }
}
