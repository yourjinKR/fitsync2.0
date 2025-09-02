package com.fitsync.controller;


import com.fitsync.domain.Test;
import com.fitsync.repository.TestRepository;
import com.fitsync.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final TestService service;

    @GetMapping("all")
    public ResponseEntity<List<Test>> getAllTests() {
        List<Test> tests = service.findAll();
        return ResponseEntity.ok(tests);
    }

    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test savedTest = service.save(test);
        return null;
    }
}
