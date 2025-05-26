package com.example.task_management_app.service;

import com.example.task_management_app.dto.UserDTO;
import com.example.task_management_app.exception.ResourceNotFoundException;
import com.example.task_management_app.model.User;
import com.example.task_management_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        // Setup test User entity
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword123");
        testUser.setRole("ROLE_USER");

        // Setup test UserDTO
        testUserDTO = new UserDTO();
        testUserDTO.setId(1);
        testUserDTO.setUsername("testuser");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setRole("ROLE_USER");

        lenient().when(passwordEncoder.encode(any())).thenReturn("hashedpassword123");
    }

    @Test
    void getAllUsers_ReturnsListOfUserDTOs() {

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUserById_WhenUserExists_ReturnsUserDTO() {

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserById(1);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 99");
    }

    @Test
    void createUser_WithValidData_ReturnsCreatedUserDTO() {

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUsername("newuser");
        newUserDTO.setEmail("new@example.com");
        newUserDTO.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = userService.createUser(newUserDTO);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void updateUser_WithValidData_ReturnsUpdatedUserDTO() {

        UserDTO updateDTO = new UserDTO();
        updateDTO.setEmail("updated@example.com");
        updateDTO.setRole("ROLE_ADMIN");

        User updatedUser = new User();
        updatedUser.setId(1);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setRole("ROLE_ADMIN");
        updatedUser.setPasswordHash("hashedpassword123");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO result = userService.updateUser(1, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {

        UserDTO updateDTO = new UserDTO();
        updateDTO.setEmail("updated@example.com");
        updateDTO.setRole("ROLE_ADMIN");

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99, updateDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 99");
    }

    @Test
    void deleteUser_WhenUserExists_DeletesSuccessfully() {

        when(userRepository.existsById(1)).thenReturn(true);

        userService.deleteUser(1);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {

        when(userRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 99");
    }

    @Test
    void getUserByUsername_WhenUserExists_ReturnsUserDTO() {

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserByUsername("testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUserByUsername_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with username: nonexistent");
    }
}
