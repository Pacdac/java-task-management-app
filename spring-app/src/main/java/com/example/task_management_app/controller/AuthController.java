package com.example.task_management_app.controller;

import com.example.task_management_app.dto.AuthRequestDTO;
import com.example.task_management_app.dto.AuthResponseDTO;
import com.example.task_management_app.dto.RegistrationDTO;
import com.example.task_management_app.dto.UserDTO;
import com.example.task_management_app.security.JwtUtil;
import com.example.task_management_app.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegistrationDTO registrationDTO) {
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

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponseDTO(token, request.getUsername()));
    }
}
