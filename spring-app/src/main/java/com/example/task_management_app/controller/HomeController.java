package com.example.task_management_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Task Management App API");
        response.put("status", "running");
        return response;
    }
    
    @GetMapping("/api/status")
    public Map<String, String> status() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "operational");
        status.put("version", "1.0.0");
        status.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return status;
    }
}
