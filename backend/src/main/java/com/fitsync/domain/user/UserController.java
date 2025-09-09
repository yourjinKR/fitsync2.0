package com.fitsync.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user") // 이 컨트롤러의 기본 URL 경로를 '/api/users'로 설정합니다.
@RequiredArgsConstructor
public class UserController {

    private final UserService usersService;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = usersService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = usersService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }
}
