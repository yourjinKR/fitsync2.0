package com.fitsync;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health") // Health Check를 위한 경로
    public String healthCheck() {
        return "ok";
    }
}