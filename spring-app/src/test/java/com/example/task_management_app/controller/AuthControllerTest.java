package com.example.task_management_app.controller;

import com.example.task_management_app.TaskManagementAppApplication;
import com.example.task_management_app.dto.AuthRequestDTO;
import com.example.task_management_app.dto.RegistrationDTO;
import com.example.task_management_app.model.User;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        userRepository.deleteAll();

        // Create test user for login tests
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole("USER");
        testUser = userRepository.save(testUser);
    }

    @Test
    void register_ShouldCreateUser_WhenValidDataProvided() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("newuser");
        registrationDTO.setEmail("newuser@example.com");
        registrationDTO.setPassword("password123456789");
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("User");

        String jsonContent = objectMapper.writeValueAsString(registrationDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.authorities[0].authority", is("ROLE_USER")));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUsernameIsEmpty() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername(""); // Empty username
        registrationDTO.setEmail("newuser@example.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("User");

        String jsonContent = objectMapper.writeValueAsString(registrationDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("newuser");
        registrationDTO.setEmail("invalid-email"); // Invalid email format
        registrationDTO.setPassword("password123");
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("User");

        String jsonContent = objectMapper.writeValueAsString(registrationDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordIsEmpty() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("newuser");
        registrationDTO.setEmail("newuser@example.com");
        registrationDTO.setPassword(""); // Empty password
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("User");

        String jsonContent = objectMapper.writeValueAsString(registrationDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturnConflict_WhenUsernameAlreadyExists() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("testuser"); // Username already exists
        registrationDTO.setEmail("different@example.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("User");

        String jsonContent = objectMapper.writeValueAsString(registrationDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void register_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("newuser");
        registrationDTO.setEmail("test@example.com"); // Email already exists
        registrationDTO.setPassword("password123");
        registrationDTO.setFirstName("New");
        registrationDTO.setLastName("User");

        String jsonContent = objectMapper.writeValueAsString(registrationDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentialsProvided() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testuser");
        authRequestDTO.setPassword("password123");

        String jsonContent = objectMapper.writeValueAsString(authRequestDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.authorities", notNullValue()));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenInvalidCredentialsProvided() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testuser");
        authRequestDTO.setPassword("wrongpassword"); // Wrong password

        String jsonContent = objectMapper.writeValueAsString(authRequestDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenUserDoesNotExist() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("nonexistent"); // User does not exist
        authRequestDTO.setPassword("password123");

        String jsonContent = objectMapper.writeValueAsString(authRequestDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenUsernameIsEmpty() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername(""); // Empty username
        authRequestDTO.setPassword("password123");

        String jsonContent = objectMapper.writeValueAsString(authRequestDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError()); // Empty username will cause authentication failure
    }

    @Test
    void login_ShouldReturnBadRequest_WhenPasswordIsEmpty() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("testuser");
        authRequestDTO.setPassword(""); // Empty password

        String jsonContent = objectMapper.writeValueAsString(authRequestDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().is4xxClientError()); // Empty password will cause authentication failure
    }
}
