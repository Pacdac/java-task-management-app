package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskPriorityDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.TaskPriority;
import com.example.task_management_app.repository.TaskPriorityRepository;
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
class TaskPriorityServiceTest {

    @Mock
    private TaskPriorityRepository taskPriorityRepository;

    @InjectMocks
    private TaskPriorityService taskPriorityService;

    private TaskPriority testTaskPriority;
    private TaskPriorityDTO testTaskPriorityDTO;

    @BeforeEach
    void setUp() {
        testTaskPriority = new TaskPriority();
        testTaskPriority.setId(1);
        testTaskPriority.setName("High");
        testTaskPriority.setValue(3);
        testTaskPriority.setDescription("High priority");
        testTaskPriority.setColor("#FF0000");
        testTaskPriority.setDisplayOrder(1);

        testTaskPriorityDTO = new TaskPriorityDTO();
        testTaskPriorityDTO.setId(1);
        testTaskPriorityDTO.setName("High");
        testTaskPriorityDTO.setValue(3);
        testTaskPriorityDTO.setDescription("High priority");
        testTaskPriorityDTO.setColor("#FF0000");
        testTaskPriorityDTO.setDisplayOrder(1);
    }

    @Test
    void getAllTaskPriorities_ReturnsListOfTaskPriorityDTOs() {
        when(taskPriorityRepository.findAllByOrderByDisplayOrderAscValueAsc())
                .thenReturn(Arrays.asList(testTaskPriority));

        List<TaskPriorityDTO> result = taskPriorityService.getAllTaskPriorities();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("High");
        assertThat(result.get(0).getValue()).isEqualTo(3);
    }

    @Test
    void getTaskPriorityById_WhenTaskPriorityExists_ReturnsTaskPriorityDTO() {
        when(taskPriorityRepository.findById(1)).thenReturn(Optional.of(testTaskPriority));

        TaskPriorityDTO result = taskPriorityService.getTaskPriorityById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("High");
        assertThat(result.getValue()).isEqualTo(3);
    }

    @Test
    void getTaskPriorityById_WhenTaskPriorityDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskPriorityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskPriorityService.getTaskPriorityById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task priority not found with id: 99");
    }

    @Test
    void getTaskPriorityByName_WhenTaskPriorityExists_ReturnsTaskPriorityDTO() {
        when(taskPriorityRepository.findByName("High")).thenReturn(Optional.of(testTaskPriority));

        TaskPriorityDTO result = taskPriorityService.getTaskPriorityByName("High");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("High");
        assertThat(result.getValue()).isEqualTo(3);
    }

    @Test
    void getTaskPriorityByName_WhenTaskPriorityDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskPriorityRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskPriorityService.getTaskPriorityByName("Unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task priority not found with name: Unknown");
    }

    @Test
    void getTaskPriorityByValue_WhenTaskPriorityExists_ReturnsTaskPriorityDTO() {
        when(taskPriorityRepository.findByValue(3)).thenReturn(Optional.of(testTaskPriority));

        TaskPriorityDTO result = taskPriorityService.getTaskPriorityByValue(3);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("High");
        assertThat(result.getValue()).isEqualTo(3);
    }

    @Test
    void getTaskPriorityByValue_WhenTaskPriorityDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskPriorityRepository.findByValue(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskPriorityService.getTaskPriorityByValue(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task priority not found with value: 99");
    }

    @Test
    void createTaskPriority_WithValidData_ReturnsCreatedTaskPriorityDTO() {
        TaskPriorityDTO newTaskPriorityDTO = new TaskPriorityDTO();
        newTaskPriorityDTO.setName("Medium");
        newTaskPriorityDTO.setValue(2);
        newTaskPriorityDTO.setDescription("Medium priority");

        when(taskPriorityRepository.existsByName("Medium")).thenReturn(false);
        when(taskPriorityRepository.existsByValue(2)).thenReturn(false);
        when(taskPriorityRepository.save(any(TaskPriority.class))).thenReturn(testTaskPriority);

        TaskPriorityDTO result = taskPriorityService.createTaskPriority(newTaskPriorityDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("High");
    }

    @Test
    void createTaskPriority_WithDuplicateName_ThrowsIllegalArgumentException() {
        TaskPriorityDTO newTaskPriorityDTO = new TaskPriorityDTO();
        newTaskPriorityDTO.setName("High");
        newTaskPriorityDTO.setValue(2);

        when(taskPriorityRepository.existsByName("High")).thenReturn(true);

        assertThatThrownBy(() -> taskPriorityService.createTaskPriority(newTaskPriorityDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task priority with name 'High' already exists");
    }

    @Test
    void createTaskPriority_WithDuplicateValue_ThrowsIllegalArgumentException() {
        TaskPriorityDTO newTaskPriorityDTO = new TaskPriorityDTO();
        newTaskPriorityDTO.setName("Medium");
        newTaskPriorityDTO.setValue(3);

        when(taskPriorityRepository.existsByName("Medium")).thenReturn(false);
        when(taskPriorityRepository.existsByValue(3)).thenReturn(true);

        assertThatThrownBy(() -> taskPriorityService.createTaskPriority(newTaskPriorityDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task priority with value '3' already exists");
    }

    @Test
    void updateTaskPriority_WithValidData_ReturnsUpdatedTaskPriorityDTO() {
        TaskPriorityDTO updateDTO = new TaskPriorityDTO();
        updateDTO.setName("Very High");
        updateDTO.setValue(4);
        updateDTO.setDescription("Very high priority");

        TaskPriority updatedTaskPriority = new TaskPriority();
        updatedTaskPriority.setId(1);
        updatedTaskPriority.setName("Very High");
        updatedTaskPriority.setValue(4);
        updatedTaskPriority.setDescription("Very high priority");

        when(taskPriorityRepository.findById(1)).thenReturn(Optional.of(testTaskPriority));
        when(taskPriorityRepository.existsByName("Very High")).thenReturn(false);
        when(taskPriorityRepository.existsByValue(4)).thenReturn(false);
        when(taskPriorityRepository.save(any(TaskPriority.class))).thenReturn(updatedTaskPriority);

        TaskPriorityDTO result = taskPriorityService.updateTaskPriority(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Very High");
        assertThat(result.getValue()).isEqualTo(4);
    }

    @Test
    void updateTaskPriority_WhenTaskPriorityDoesNotExist_ThrowsResourceNotFoundException() {
        TaskPriorityDTO updateDTO = new TaskPriorityDTO();
        updateDTO.setName("Updated Priority");

        when(taskPriorityRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskPriorityService.updateTaskPriority(99, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task priority not found with id: 99");
    }

    @Test
    void deleteTaskPriority_WhenTaskPriorityExists_DeletesSuccessfully() {
        when(taskPriorityRepository.existsById(1)).thenReturn(true);

        taskPriorityService.deleteTaskPriority(1);

    }

    @Test
    void deleteTaskPriority_WhenTaskPriorityDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskPriorityRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> taskPriorityService.deleteTaskPriority(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task priority not found with id: 99");
    }
}
