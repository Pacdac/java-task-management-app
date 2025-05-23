package com.example.task_management_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/api/status")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Task Management App API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }

}
