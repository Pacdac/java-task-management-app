package com.example.task_management_app.controller;

import com.example.task_management_app.dto.AuthRequestDTO;
import com.example.task_management_app.dto.AuthResponseDTO;
import com.example.task_management_app.dto.RegistrationDTO;
import com.example.task_management_app.dto.UserDTO;
import com.example.task_management_app.exception.AuthenticationFailedException;
import com.example.task_management_app.exception.InvalidAuthenticationDataException;
import com.example.task_management_app.security.JwtUtil;
import com.example.task_management_app.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtUtil jwtUtil,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistrationDTO registrationDTO) {

        try {
            // Additional validation
            validateRegistrationData(registrationDTO);

            // Convert registration DTO to user DTO
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(registrationDTO.getUsername());
            userDTO.setEmail(registrationDTO.getEmail());
            userDTO.setPassword(registrationDTO.getPassword());
            userDTO.setFirstName(registrationDTO.getFirstName());
            userDTO.setLastName(registrationDTO.getLastName());
            userDTO.setRole("USER"); // Default role for new registrations

            // Create user using the existing service
            UserDTO createdUser = userService.createUser(userDTO);

            // Get a JWT token for the newly created user
            UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new AuthResponseDTO(token, registrationDTO.getUsername(), userDetails.getAuthorities()));

        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {

        try {
            validateLoginData(request);

            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponseDTO(token, request.getUsername(), userDetails.getAuthorities()));

        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid credentials provided");
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationFailedException("User not found");
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Authentication failed");
        } catch (Exception e) {
            throw e; // Re-throw to let GlobalExceptionHandler handle it
        }
    }

    /**
     * Validate registration data beyond standard validation annotations
     */
    private void validateRegistrationData(RegistrationDTO registrationDTO) {
        if (!StringUtils.hasText(registrationDTO.getUsername())) {
            throw new InvalidAuthenticationDataException("Username cannot be empty");
        }

        if (!StringUtils.hasText(registrationDTO.getPassword())) {
            throw new InvalidAuthenticationDataException("Password cannot be empty");
        }

        if (registrationDTO.getPassword().length() < 12) {
            throw new InvalidAuthenticationDataException("Password must be at least 12 characters long");
        }

        // Username validation
        if (registrationDTO.getUsername().length() < 3) {
            throw new InvalidAuthenticationDataException("Username must be at least 3 characters long");
        }

        if (!registrationDTO.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidAuthenticationDataException("Username can only contain letters, numbers, and underscores");
        }
    }

    /**
     * Validate login data beyond standard validation annotations
     */
    private void validateLoginData(AuthRequestDTO request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new InvalidAuthenticationDataException("Username cannot be empty");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new InvalidAuthenticationDataException("Password cannot be empty");
        }
    }
}
