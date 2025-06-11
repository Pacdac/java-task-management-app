package com.example.task_management_app.service;

import com.example.task_management_app.dto.TaskCategoryDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.TaskCategory;
import com.example.task_management_app.repository.TaskCategoryRepository;
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
class TaskCategoryServiceTest {

    @Mock
    private TaskCategoryRepository taskCategoryRepository;

    @InjectMocks
    private TaskCategoryService taskCategoryService;

    private TaskCategory testTaskCategory;
    private TaskCategoryDTO testTaskCategoryDTO;

    @BeforeEach
    void setUp() {
        testTaskCategory = new TaskCategory();
        testTaskCategory.setId(1);
        testTaskCategory.setName("Work");
        testTaskCategory.setDescription("Work related tasks");
        testTaskCategory.setColor("#0000FF");

        testTaskCategoryDTO = new TaskCategoryDTO();
        testTaskCategoryDTO.setId(1);
        testTaskCategoryDTO.setName("Work");
        testTaskCategoryDTO.setDescription("Work related tasks");
        testTaskCategoryDTO.setColor("#0000FF");
    }

    @Test
    void getAllTaskCategories_ReturnsListOfTaskCategoryDTOs() {
        when(taskCategoryRepository.findAll()).thenReturn(Arrays.asList(testTaskCategory));

        List<TaskCategoryDTO> result = taskCategoryService.getAllTaskCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Work");
        assertThat(result.get(0).getDescription()).isEqualTo("Work related tasks");
    }

    @Test
    void getTaskCategoryById_WhenTaskCategoryExists_ReturnsTaskCategoryDTO() {
        when(taskCategoryRepository.findById(1)).thenReturn(Optional.of(testTaskCategory));

        TaskCategoryDTO result = taskCategoryService.getTaskCategoryById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Work");
    }

    @Test
    void getTaskCategoryById_WhenTaskCategoryDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskCategoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskCategoryService.getTaskCategoryById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task category not found with id: 99");
    }

    @Test
    void getTaskCategoryByName_WhenTaskCategoryExists_ReturnsTaskCategoryDTO() {
        when(taskCategoryRepository.findByName("Work")).thenReturn(Optional.of(testTaskCategory));

        TaskCategoryDTO result = taskCategoryService.getTaskCategoryByName("Work");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Work");
        assertThat(result.getDescription()).isEqualTo("Work related tasks");
    }

    @Test
    void getTaskCategoryByName_WhenTaskCategoryDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskCategoryRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskCategoryService.getTaskCategoryByName("Unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task category not found with name: Unknown");
    }

    @Test
    void createTaskCategory_WithValidData_ReturnsCreatedTaskCategoryDTO() {
        TaskCategoryDTO newTaskCategoryDTO = new TaskCategoryDTO();
        newTaskCategoryDTO.setName("Personal");
        newTaskCategoryDTO.setDescription("Personal tasks");
        newTaskCategoryDTO.setColor("#00FF00");

        when(taskCategoryRepository.existsByName("Personal")).thenReturn(false);
        when(taskCategoryRepository.save(any(TaskCategory.class))).thenReturn(testTaskCategory);

        TaskCategoryDTO result = taskCategoryService.createTaskCategory(newTaskCategoryDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Work");
    }

    @Test
    void createTaskCategory_WithDuplicateName_ThrowsIllegalArgumentException() {
        TaskCategoryDTO newTaskCategoryDTO = new TaskCategoryDTO();
        newTaskCategoryDTO.setName("Work");
        newTaskCategoryDTO.setDescription("Work tasks");

        when(taskCategoryRepository.existsByName("Work")).thenReturn(true);

        assertThatThrownBy(() -> taskCategoryService.createTaskCategory(newTaskCategoryDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Task category with name 'Work' already exists");
    }

    @Test
    void updateTaskCategory_WithValidData_ReturnsUpdatedTaskCategoryDTO() {
        TaskCategoryDTO updateDTO = new TaskCategoryDTO();
        updateDTO.setName("Business");
        updateDTO.setDescription("Business related tasks");
        updateDTO.setColor("#FF00FF");

        TaskCategory updatedTaskCategory = new TaskCategory();
        updatedTaskCategory.setId(1);
        updatedTaskCategory.setName("Business");
        updatedTaskCategory.setDescription("Business related tasks");
        updatedTaskCategory.setColor("#FF00FF");

        when(taskCategoryRepository.findById(1)).thenReturn(Optional.of(testTaskCategory));
        when(taskCategoryRepository.existsByName("Business")).thenReturn(false);
        when(taskCategoryRepository.save(any(TaskCategory.class))).thenReturn(updatedTaskCategory);

        TaskCategoryDTO result = taskCategoryService.updateTaskCategory(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Business");
        assertThat(result.getDescription()).isEqualTo("Business related tasks");
    }

    @Test
    void updateTaskCategory_WhenTaskCategoryDoesNotExist_ThrowsResourceNotFoundException() {
        TaskCategoryDTO updateDTO = new TaskCategoryDTO();
        updateDTO.setName("Updated");

        when(taskCategoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskCategoryService.updateTaskCategory(99, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task category not found with id: 99");
    }

    @Test
    void deleteTaskCategory_WhenTaskCategoryExists_DeletesSuccessfully() {
        when(taskCategoryRepository.existsById(1)).thenReturn(true);

        taskCategoryService.deleteTaskCategory(1);

        // No exception should be thrown
    }

    @Test
    void deleteTaskCategory_WhenTaskCategoryDoesNotExist_ThrowsResourceNotFoundException() {
        when(taskCategoryRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> taskCategoryService.deleteTaskCategory(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task category not found with id: 99");
    }
}
