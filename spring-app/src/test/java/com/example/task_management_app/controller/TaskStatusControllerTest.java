package com.example.task_management_app.controller;

import com.example.task_management_app.TaskManagementAppApplication;
import com.example.task_management_app.dto.TaskStatusDTO;
import com.example.task_management_app.model.TaskStatus;
import com.example.task_management_app.model.User;
import com.example.task_management_app.repository.TaskStatusRepository;
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
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskStatus testTaskStatus;
    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        testUser.setRole("USER");
        testUser = userRepository.save(testUser);

        // Create test admin
        testAdmin = new User();
        testAdmin.setUsername("admin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPasswordHash(passwordEncoder.encode("password"));
        testAdmin.setRole("ADMIN");
        testAdmin = userRepository.save(testAdmin);

        // Create test task status
        testTaskStatus = new TaskStatus();
        testTaskStatus.setName("In Progress");
        testTaskStatus.setDescription("Task is being worked on");
        testTaskStatus.setColor("#FFA500");
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
    }

    @Test
    @WithMockUser
    void getAllTaskStatuses_ShouldReturnAllStatuses() throws Exception {
        // Create additional test status
        TaskStatus anotherStatus = new TaskStatus();
        anotherStatus.setName("Completed");
        anotherStatus.setDescription("Task is completed");
        anotherStatus.setColor("#00FF00");
        taskStatusRepository.save(anotherStatus);

        mockMvc.perform(get("/api/task-statuses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("In Progress")))
                .andExpect(jsonPath("$[0].description", is("Task is being worked on")))
                .andExpect(jsonPath("$[0].color", is("#FFA500")))
                .andExpect(jsonPath("$[1].name", is("Completed")))
                .andExpect(jsonPath("$[1].description", is("Task is completed")))
                .andExpect(jsonPath("$[1].color", is("#00FF00")));
    }

    @Test
    @WithMockUser
    void getTaskStatusById_ShouldReturnStatus_WhenStatusExists() throws Exception {
        mockMvc.perform(get("/api/task-statuses/{id}", testTaskStatus.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskStatus.getId())))
                .andExpect(jsonPath("$.name", is("In Progress")))
                .andExpect(jsonPath("$.description", is("Task is being worked on")))
                .andExpect(jsonPath("$.color", is("#FFA500")));
    }

    @Test
    @WithMockUser
    void getTaskStatusById_ShouldReturnNotFound_WhenStatusDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/task-statuses/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getTaskStatusByName_ShouldReturnStatus_WhenStatusExists() throws Exception {
        mockMvc.perform(get("/api/task-statuses/name/{name}", "In Progress"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskStatus.getId())))
                .andExpect(jsonPath("$.name", is("In Progress")))
                .andExpect(jsonPath("$.description", is("Task is being worked on")))
                .andExpect(jsonPath("$.color", is("#FFA500")));
    }

    @Test
    @WithMockUser
    void getTaskStatusByName_ShouldReturnNotFound_WhenStatusDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/task-statuses/name/{name}", "NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTaskStatus_ShouldCreateStatus_WhenValidDataProvided() throws Exception {
        TaskStatusDTO newTaskStatusDTO = new TaskStatusDTO();
        newTaskStatusDTO.setName("Review");
        newTaskStatusDTO.setDescription("Task is under review");
        newTaskStatusDTO.setColor("#FFFF00");

        String jsonContent = objectMapper.writeValueAsString(newTaskStatusDTO);

        mockMvc.perform(post("/api/task-statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Review")))
                .andExpect(jsonPath("$.description", is("Task is under review")))
                .andExpect(jsonPath("$.color", is("#FFFF00")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTaskStatus_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        TaskStatusDTO newTaskStatusDTO = new TaskStatusDTO();
        newTaskStatusDTO.setName(""); // Empty name
        newTaskStatusDTO.setDescription("Task is under review");
        newTaskStatusDTO.setColor("#FFFF00");

        String jsonContent = objectMapper.writeValueAsString(newTaskStatusDTO);

        mockMvc.perform(post("/api/task-statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTaskStatus_ShouldUpdateStatus_WhenValidDataProvided() throws Exception {
        TaskStatusDTO updateTaskStatusDTO = new TaskStatusDTO();
        updateTaskStatusDTO.setName("Updated Status");
        updateTaskStatusDTO.setDescription("Updated description");
        updateTaskStatusDTO.setColor("#FF00FF");

        String jsonContent = objectMapper.writeValueAsString(updateTaskStatusDTO);

        mockMvc.perform(put("/api/task-statuses/{id}", testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskStatus.getId())))
                .andExpect(jsonPath("$.name", is("Updated Status")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.color", is("#FF00FF")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTaskStatus_ShouldReturnNotFound_WhenStatusDoesNotExist() throws Exception {
        TaskStatusDTO updateTaskStatusDTO = new TaskStatusDTO();
        updateTaskStatusDTO.setName("Updated Status");
        updateTaskStatusDTO.setDescription("Updated description");
        updateTaskStatusDTO.setColor("#FF00FF");

        String jsonContent = objectMapper.writeValueAsString(updateTaskStatusDTO);

        mockMvc.perform(put("/api/task-statuses/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTaskStatus_ShouldDeleteStatus_WhenStatusExists() throws Exception {
        mockMvc.perform(delete("/api/task-statuses/{id}", testTaskStatus.getId()))
                .andExpect(status().isNoContent());

        // Verify status was deleted
        mockMvc.perform(get("/api/task-statuses/{id}", testTaskStatus.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTaskStatus_ShouldReturnNotFound_WhenStatusDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/task-statuses/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
