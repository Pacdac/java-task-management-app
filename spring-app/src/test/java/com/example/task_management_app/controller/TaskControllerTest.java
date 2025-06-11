package com.example.task_management_app.controller;

import com.example.task_management_app.TaskManagementAppApplication;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = { TaskManagementAppApplication.class })
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskCategoryRepository taskCategoryRepository;
    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    private User testUser;
    private TaskStatus testStatus;
    private TaskCategory testCategory;
    private TaskPriority testPriority;
    private Task testTask;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole("USER");
        testUser = userRepository.save(testUser);

        testStatus = new TaskStatus();
        testStatus.setName("To Do");
        testStatus.setDescription("Task to be done");
        testStatus.setColor("#FF0000");
        testStatus = taskStatusRepository.save(testStatus);

        testCategory = new TaskCategory();
        testCategory.setName("Work");
        testCategory.setDescription("Work related tasks");
        testCategory.setColor("#00FF00");
        testCategory = taskCategoryRepository.save(testCategory);

        testPriority = new TaskPriority();
        testPriority.setName("High");
        testPriority.setValue(3);
        testPriority.setDescription("High priority");
        testPriority.setColor("#0000FF");
        testPriority.setDisplayOrder(1);
        testPriority = taskPriorityRepository.save(testPriority);

        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test task description");
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setPriority(testPriority);
        testTask.setUser(testUser);
        testTask.setStatus(testStatus);
        testTask.setCategory(testCategory);
        testTask = taskRepository.save(testTask);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllTasks_WhenTasksExist_ReturnsTasksList() throws Exception {
        mvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].description").value("Test task description"))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].statusName").value("To Do"))
                .andExpect(jsonPath("$[0].categoryName").value("Work"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getTaskById_WhenTaskExists_ReturnsTask() throws Exception {
        mvc.perform(get("/api/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test task description"))
                .andExpect(jsonPath("$.userId").value(testUser.getId()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getTaskById_WhenTaskDoesNotExist_ReturnsNotFound() throws Exception {
        mvc.perform(get("/api/tasks/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getTasksByUserId_WhenTasksExist_ReturnsUserTasks() throws Exception {
        mvc.perform(get("/api/tasks/user/{userId}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].userId").value(testUser.getId()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createTask_WithValidData_ReturnsCreatedTask() throws Exception {
        String taskJson = """
                {
                    "title": "New Task",
                    "description": "New task description",
                    "dueDate": "2025-06-01",
                    "priority": 2,
                    "userId": %d,
                    "statusId": %d,
                    "categoryId": %d
                }
                """.formatted(testUser.getId(), testStatus.getId(), testCategory.getId());
        mvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New task description"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateTask_WithValidData_ReturnsUpdatedTask() throws Exception {
        String updatedTaskJson = """
                {
                    "title": "Updated Task",
                    "description": "Updated task description",
                    "dueDate": "2025-06-15",
                    "priority": 1,
                    "userId": %d,
                    "statusId": %d,
                    "categoryId": %d
                }
                """.formatted(testUser.getId(), testStatus.getId(), testCategory.getId());

        mvc.perform(put("/api/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedTaskJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated task description"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteTask_WhenTaskExists_ReturnsNoContent() throws Exception {
        mvc.perform(delete("/api/tasks/{id}", testTask.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void searchTasks_WithKeyword_ReturnsMatchingTasks() throws Exception {
        mvc.perform(get("/api/tasks/search")
                .param("keyword", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getOverdueTasks_ReturnsOverdueTasks() throws Exception {

        Task overdueTask = new Task();
        overdueTask.setTitle("Overdue Task");
        overdueTask.setDescription("This task is overdue");
        overdueTask.setDueDate(LocalDate.now().minusDays(1));
        overdueTask.setPriority(testPriority);
        overdueTask.setUser(testUser);
        overdueTask.setStatus(testStatus);
        overdueTask.setCategory(testCategory);
        taskRepository.save(overdueTask);

        mvc.perform(get("/api/tasks/overdue/{userId}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Overdue Task"));
    }

    @Test
    public void getAllTasks_WhenNotAuthenticated_ReturnsForbidden() throws Exception {
        mvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
