package com.example.task_management_app.controller;

import com.example.task_management_app.TaskManagementAppApplication;
import com.example.task_management_app.dto.TaskCategoryDTO;
import com.example.task_management_app.model.TaskCategory;
import com.example.task_management_app.model.User;
import com.example.task_management_app.repository.TaskCategoryRepository;
import com.example.task_management_app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = { TaskManagementAppApplication.class })
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
class TaskCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskCategoryRepository taskCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCategory testTaskCategory;
    private User testUser;

    @BeforeEach
    void setUp() {

        taskCategoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        testUser.setRole("USER");
        testUser = userRepository.save(testUser);

        testTaskCategory = new TaskCategory();
        testTaskCategory.setName("Work");
        testTaskCategory.setDescription("Work related tasks");
        testTaskCategory.setColor("#0066CC");
        testTaskCategory = taskCategoryRepository.save(testTaskCategory);
    }

    @Test
    @WithMockUser
    void getAllTaskCategories_ShouldReturnAllCategories() throws Exception {

        TaskCategory anotherCategory = new TaskCategory();
        anotherCategory.setName("Personal");
        anotherCategory.setDescription("Personal tasks");
        anotherCategory.setColor("#FF6600");
        taskCategoryRepository.save(anotherCategory);

        mockMvc.perform(get("/api/task-categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Work")))
                .andExpect(jsonPath("$[0].description", is("Work related tasks")))
                .andExpect(jsonPath("$[0].color", is("#0066CC")))
                .andExpect(jsonPath("$[1].name", is("Personal")))
                .andExpect(jsonPath("$[1].description", is("Personal tasks")))
                .andExpect(jsonPath("$[1].color", is("#FF6600")));
    }

    @Test
    @WithMockUser
    void getTaskCategoryById_ShouldReturnCategory_WhenCategoryExists() throws Exception {
        mockMvc.perform(get("/api/task-categories/{id}", testTaskCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskCategory.getId())))
                .andExpect(jsonPath("$.name", is("Work")))
                .andExpect(jsonPath("$.description", is("Work related tasks")))
                .andExpect(jsonPath("$.color", is("#0066CC")));
    }

    @Test
    @WithMockUser
    void getTaskCategoryById_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/task-categories/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getTaskCategoryByName_ShouldReturnCategory_WhenCategoryExists() throws Exception {
        mockMvc.perform(get("/api/task-categories/name/{name}", "Work"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskCategory.getId())))
                .andExpect(jsonPath("$.name", is("Work")))
                .andExpect(jsonPath("$.description", is("Work related tasks")))
                .andExpect(jsonPath("$.color", is("#0066CC")));
    }

    @Test
    @WithMockUser
    void getTaskCategoryByName_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/task-categories/name/{name}", "NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createTaskCategory_ShouldCreateCategory_WhenValidDataProvided() throws Exception {
        TaskCategoryDTO newTaskCategoryDTO = new TaskCategoryDTO();
        newTaskCategoryDTO.setName("Study");
        newTaskCategoryDTO.setDescription("Study related tasks");
        newTaskCategoryDTO.setColor("#00CC66");

        String jsonContent = objectMapper.writeValueAsString(newTaskCategoryDTO);

        mockMvc.perform(post("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Study")))
                .andExpect(jsonPath("$.description", is("Study related tasks")))
                .andExpect(jsonPath("$.color", is("#00CC66")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser
    void createTaskCategory_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        TaskCategoryDTO newTaskCategoryDTO = new TaskCategoryDTO();
        newTaskCategoryDTO.setName("");
        newTaskCategoryDTO.setDescription("Study related tasks");
        newTaskCategoryDTO.setColor("#00CC66");

        String jsonContent = objectMapper.writeValueAsString(newTaskCategoryDTO);

        mockMvc.perform(post("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createTaskCategory_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
        TaskCategoryDTO newTaskCategoryDTO = new TaskCategoryDTO();
        newTaskCategoryDTO.setName(null);
        newTaskCategoryDTO.setDescription("Study related tasks");
        newTaskCategoryDTO.setColor("#00CC66");

        String jsonContent = objectMapper.writeValueAsString(newTaskCategoryDTO);

        mockMvc.perform(post("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateTaskCategory_ShouldUpdateCategory_WhenValidDataProvided() throws Exception {
        TaskCategoryDTO updateTaskCategoryDTO = new TaskCategoryDTO();
        updateTaskCategoryDTO.setName("Updated Category");
        updateTaskCategoryDTO.setDescription("Updated description");
        updateTaskCategoryDTO.setColor("#FF00FF");

        String jsonContent = objectMapper.writeValueAsString(updateTaskCategoryDTO);

        mockMvc.perform(put("/api/task-categories/{id}", testTaskCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskCategory.getId())))
                .andExpect(jsonPath("$.name", is("Updated Category")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.color", is("#FF00FF")));
    }

    @Test
    @WithMockUser
    void updateTaskCategory_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        TaskCategoryDTO updateTaskCategoryDTO = new TaskCategoryDTO();
        updateTaskCategoryDTO.setName("Updated Category");
        updateTaskCategoryDTO.setDescription("Updated description");
        updateTaskCategoryDTO.setColor("#FF00FF");

        String jsonContent = objectMapper.writeValueAsString(updateTaskCategoryDTO);

        mockMvc.perform(put("/api/task-categories/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateTaskCategory_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        TaskCategoryDTO updateTaskCategoryDTO = new TaskCategoryDTO();
        updateTaskCategoryDTO.setName("");
        updateTaskCategoryDTO.setDescription("Updated description");
        updateTaskCategoryDTO.setColor("#FF00FF");

        String jsonContent = objectMapper.writeValueAsString(updateTaskCategoryDTO);

        mockMvc.perform(put("/api/task-categories/{id}", testTaskCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteTaskCategory_ShouldDeleteCategory_WhenCategoryExists() throws Exception {
        mockMvc.perform(delete("/api/task-categories/{id}", testTaskCategory.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/task-categories/{id}", testTaskCategory.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteTaskCategory_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/task-categories/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
