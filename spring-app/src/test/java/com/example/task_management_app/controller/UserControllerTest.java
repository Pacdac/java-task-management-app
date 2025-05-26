package com.example.task_management_app.controller;

import com.example.task_management_app.config.TestSecurityConfig;
import com.example.task_management_app.dto.UserDTO;
import com.example.task_management_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
@Import(TestSecurityConfig.class)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        private UserDTO testUserDTO;

        @BeforeEach
        void setUp() {
                testUserDTO = new UserDTO();
                testUserDTO.setId(1);
                testUserDTO.setUsername("testuser");
                testUserDTO.setEmail("test@example.com");
                testUserDTO.setRole("ROLE_USER");
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_ReturnsListOfUsers() throws Exception {

                when(userService.getAllUsers())
                                .thenReturn(Arrays.asList(testUserDTO));

                mockMvc.perform(get("/api/users")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$[0].username").value("testuser"))
                                .andExpect(jsonPath("$[0].email").value("test@example.com"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_WhenUserExists_ReturnsUser() throws Exception {

                when(userService.getUserById(1))
                                .thenReturn(testUserDTO);

                mockMvc.perform(get("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getUserById_WhenUserDoesNotExist_ReturnsNotFound() throws Exception {

                when(userService.getUserById(99))
                                .thenReturn(null);

                mockMvc.perform(get("/api/users/99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createUser_WithValidData_ReturnsCreatedUser() throws Exception {

                UserDTO newUser = new UserDTO();
                newUser.setUsername("newuser");
                newUser.setEmail("new@example.com");
                newUser.setPassword("password");

                when(userService.createUser(any(UserDTO.class)))
                                .thenReturn(testUserDTO);

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUser)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateUser_WithValidData_ReturnsUpdatedUser() throws Exception {

                UserDTO updateDTO = new UserDTO();
                updateDTO.setEmail("updated@example.com");
                updateDTO.setRole("ROLE_ADMIN");

                UserDTO updatedUser = new UserDTO();
                updatedUser.setId(1);
                updatedUser.setUsername("testuser");
                updatedUser.setEmail("updated@example.com");
                updatedUser.setRole("ROLE_ADMIN");

                when(userService.updateUser(eq(1), any(UserDTO.class)))
                                .thenReturn(updatedUser);

                mockMvc.perform(put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("updated@example.com"))
                                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteUser_WhenUserExists_ReturnsNoContent() throws Exception {

                mockMvc.perform(delete("/api/users/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "USER")
        void getAllUsers_WithInsufficientRole_ReturnsForbidden() throws Exception {

                mockMvc.perform(get("/api/users"))
                                .andExpect(status().isForbidden());
        }
}
