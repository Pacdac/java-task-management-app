package com.example.task_management_app.controller;

import com.example.task_management_app.TaskManagementAppApplication;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.task_management_app.model.User;
import com.example.task_management_app.repository.UserRepository;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = { TaskManagementAppApplication.class })
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository repository;

    @Test
    @WithMockUser(roles = "USER")
    public void getAllUsers_WhenUsersExist_ReturnsUsersList() throws Exception {

        User testUser = new User();
        testUser.setUsername("integrationTestUser");
        testUser.setEmail("integration@test.com");
        testUser.setPasswordHash("hashedPassword123");
        testUser.setFirstName("Integration");
        testUser.setLastName("Test");
        testUser.setRole("USER");
        repository.save(testUser);

        mvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("integrationTestUser"))
                .andExpect(jsonPath("$[0].email").value("integration@test.com"))
                .andExpect(jsonPath("$[0].firstName").value("Integration"))
                .andExpect(jsonPath("$[0].lastName").value("Test"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[0].password").doesNotExist()); // Password should not be in response
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAllUsers_WhenNoUsersExist_ReturnsEmptyList() throws Exception {

        mvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void getAllUsers_WhenNotAuthenticated_ReturnsUnauthorized() throws Exception {

        mvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}