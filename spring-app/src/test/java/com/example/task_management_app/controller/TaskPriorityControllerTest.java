package com.example.task_management_app.controller;

import com.example.task_management_app.TaskManagementAppApplication;
import com.example.task_management_app.dto.TaskPriorityDTO;
import com.example.task_management_app.model.TaskPriority;
import com.example.task_management_app.model.User;
import com.example.task_management_app.repository.TaskPriorityRepository;
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
class TaskPriorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskPriority testTaskPriority;
    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        taskPriorityRepository.deleteAll();
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

        // Create test task priority
        testTaskPriority = new TaskPriority();
        testTaskPriority.setName("High");
        testTaskPriority.setValue(3);
        testTaskPriority.setDescription("High priority task");
        testTaskPriority.setColor("#FF0000");
        testTaskPriority.setDisplayOrder(1);
        testTaskPriority = taskPriorityRepository.save(testTaskPriority);
    }

    @Test
    @WithMockUser
    void getAllTaskPriorities_ShouldReturnAllPriorities() throws Exception {
        // Create additional test priority
        TaskPriority anotherPriority = new TaskPriority();
        anotherPriority.setName("Low");
        anotherPriority.setValue(1);
        anotherPriority.setDescription("Low priority task");
        anotherPriority.setColor("#00FF00");
        anotherPriority.setDisplayOrder(3);
        taskPriorityRepository.save(anotherPriority);

        mockMvc.perform(get("/api/task-priorities"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("High")))
                .andExpect(jsonPath("$[0].value", is(3)))
                .andExpect(jsonPath("$[0].description", is("High priority task")))
                .andExpect(jsonPath("$[0].color", is("#FF0000")))
                .andExpect(jsonPath("$[0].displayOrder", is(1)))
                .andExpect(jsonPath("$[1].name", is("Low")))
                .andExpect(jsonPath("$[1].value", is(1)))
                .andExpect(jsonPath("$[1].description", is("Low priority task")))
                .andExpect(jsonPath("$[1].color", is("#00FF00")))
                .andExpect(jsonPath("$[1].displayOrder", is(3)));
    }

    @Test
    @WithMockUser
    void getTaskPriorityById_ShouldReturnPriority_WhenPriorityExists() throws Exception {
        mockMvc.perform(get("/api/task-priorities/{id}", testTaskPriority.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskPriority.getId())))
                .andExpect(jsonPath("$.name", is("High")))
                .andExpect(jsonPath("$.value", is(3)))
                .andExpect(jsonPath("$.description", is("High priority task")))
                .andExpect(jsonPath("$.color", is("#FF0000")))
                .andExpect(jsonPath("$.displayOrder", is(1)));
    }

    @Test
    @WithMockUser
    void getTaskPriorityById_ShouldReturnNotFound_WhenPriorityDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/task-priorities/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getTaskPriorityByName_ShouldReturnPriority_WhenPriorityExists() throws Exception {
        mockMvc.perform(get("/api/task-priorities/name/{name}", "High"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskPriority.getId())))
                .andExpect(jsonPath("$.name", is("High")))
                .andExpect(jsonPath("$.value", is(3)))
                .andExpect(jsonPath("$.description", is("High priority task")))
                .andExpect(jsonPath("$.color", is("#FF0000")))
                .andExpect(jsonPath("$.displayOrder", is(1)));
    }

    @Test
    @WithMockUser
    void getTaskPriorityByName_ShouldReturnNotFound_WhenPriorityDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/task-priorities/name/{name}", "NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getTaskPriorityByValue_ShouldReturnPriority_WhenPriorityExists() throws Exception {
        mockMvc.perform(get("/api/task-priorities/value/{value}", 3))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskPriority.getId())))
                .andExpect(jsonPath("$.name", is("High")))
                .andExpect(jsonPath("$.value", is(3)))
                .andExpect(jsonPath("$.description", is("High priority task")))
                .andExpect(jsonPath("$.color", is("#FF0000")))
                .andExpect(jsonPath("$.displayOrder", is(1)));
    }

    @Test
    @WithMockUser
    void getTaskPriorityByValue_ShouldReturnNotFound_WhenPriorityDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/task-priorities/value/{value}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTaskPriority_ShouldCreatePriority_WhenValidDataProvided() throws Exception {
        TaskPriorityDTO newTaskPriorityDTO = new TaskPriorityDTO();
        newTaskPriorityDTO.setName("Medium");
        newTaskPriorityDTO.setValue(2);
        newTaskPriorityDTO.setDescription("Medium priority task");
        newTaskPriorityDTO.setColor("#FFFF00");
        newTaskPriorityDTO.setDisplayOrder(2);

        String jsonContent = objectMapper.writeValueAsString(newTaskPriorityDTO);

        mockMvc.perform(post("/api/task-priorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Medium")))
                .andExpect(jsonPath("$.value", is(2)))
                .andExpect(jsonPath("$.description", is("Medium priority task")))
                .andExpect(jsonPath("$.color", is("#FFFF00")))
                .andExpect(jsonPath("$.displayOrder", is(2)))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTaskPriority_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        TaskPriorityDTO newTaskPriorityDTO = new TaskPriorityDTO();
        newTaskPriorityDTO.setName("Medium");
        newTaskPriorityDTO.setValue(2);
        newTaskPriorityDTO.setDescription("Medium priority task");
        newTaskPriorityDTO.setColor("#FFFF00");
        newTaskPriorityDTO.setDisplayOrder(2);

        String jsonContent = objectMapper.writeValueAsString(newTaskPriorityDTO);

        mockMvc.perform(post("/api/task-priorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTaskPriority_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        TaskPriorityDTO newTaskPriorityDTO = new TaskPriorityDTO();
        newTaskPriorityDTO.setName(""); // Empty name
        newTaskPriorityDTO.setValue(2);
        newTaskPriorityDTO.setDescription("Medium priority task");
        newTaskPriorityDTO.setColor("#FFFF00");
        newTaskPriorityDTO.setDisplayOrder(2);

        String jsonContent = objectMapper.writeValueAsString(newTaskPriorityDTO);

        mockMvc.perform(post("/api/task-priorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTaskPriority_ShouldReturnBadRequest_WhenValueIsNull() throws Exception {
        TaskPriorityDTO newTaskPriorityDTO = new TaskPriorityDTO();
        newTaskPriorityDTO.setName("Medium");
        newTaskPriorityDTO.setValue(null); // Null value
        newTaskPriorityDTO.setDescription("Medium priority task");
        newTaskPriorityDTO.setColor("#FFFF00");
        newTaskPriorityDTO.setDisplayOrder(2);

        String jsonContent = objectMapper.writeValueAsString(newTaskPriorityDTO);

        mockMvc.perform(post("/api/task-priorities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTaskPriority_ShouldUpdatePriority_WhenValidDataProvided() throws Exception {
        TaskPriorityDTO updateTaskPriorityDTO = new TaskPriorityDTO();
        updateTaskPriorityDTO.setName("Updated Priority");
        updateTaskPriorityDTO.setValue(5);
        updateTaskPriorityDTO.setDescription("Updated description");
        updateTaskPriorityDTO.setColor("#FF00FF");
        updateTaskPriorityDTO.setDisplayOrder(0);

        String jsonContent = objectMapper.writeValueAsString(updateTaskPriorityDTO);

        mockMvc.perform(put("/api/task-priorities/{id}", testTaskPriority.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTaskPriority.getId())))
                .andExpect(jsonPath("$.name", is("Updated Priority")))
                .andExpect(jsonPath("$.value", is(5)))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.color", is("#FF00FF")))
                .andExpect(jsonPath("$.displayOrder", is(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateTaskPriority_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        TaskPriorityDTO updateTaskPriorityDTO = new TaskPriorityDTO();
        updateTaskPriorityDTO.setName("Updated Priority");
        updateTaskPriorityDTO.setValue(5);
        updateTaskPriorityDTO.setDescription("Updated description");
        updateTaskPriorityDTO.setColor("#FF00FF");
        updateTaskPriorityDTO.setDisplayOrder(0);

        String jsonContent = objectMapper.writeValueAsString(updateTaskPriorityDTO);

        mockMvc.perform(put("/api/task-priorities/{id}", testTaskPriority.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTaskPriority_ShouldReturnNotFound_WhenPriorityDoesNotExist() throws Exception {
        TaskPriorityDTO updateTaskPriorityDTO = new TaskPriorityDTO();
        updateTaskPriorityDTO.setName("Updated Priority");
        updateTaskPriorityDTO.setValue(5);
        updateTaskPriorityDTO.setDescription("Updated description");
        updateTaskPriorityDTO.setColor("#FF00FF");
        updateTaskPriorityDTO.setDisplayOrder(0);

        String jsonContent = objectMapper.writeValueAsString(updateTaskPriorityDTO);

        mockMvc.perform(put("/api/task-priorities/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTaskPriority_ShouldDeletePriority_WhenPriorityExists() throws Exception {
        mockMvc.perform(delete("/api/task-priorities/{id}", testTaskPriority.getId()))
                .andExpect(status().isNoContent());

        // Verify priority was deleted
        mockMvc.perform(get("/api/task-priorities/{id}", testTaskPriority.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteTaskPriority_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(delete("/api/task-priorities/{id}", testTaskPriority.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTaskPriority_ShouldReturnNotFound_WhenPriorityDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/task-priorities/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
