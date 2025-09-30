package com.fitsync.domain.user.controller;

import com.fitsync.domain.user.dto.UserInfoResponseDto;
import com.fitsync.domain.user.service.UserService;
import com.fitsync.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService usersService;

    /**
     * 현재 로그인된 사용자의 ID(PK)를 반환하는 API
     */
    @GetMapping("/me/id")
    public ResponseEntity<Long> getMyId(@AuthenticationPrincipal String email) {
        // @AuthenticationPrincipal 어노테이션이 SecurityContext에 저장된 Principal(현재는 email)을 바로 주입해줍니다.
        User user = usersService.findByEmail(email);
        return ResponseEntity.ok(user.getId());
    }

    /**
     * 현재 로그인된 사용자의 전체 정보를 반환하는 API
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(@AuthenticationPrincipal String email) {
        User user = usersService.findByEmail(email);
        // User 엔티티를 직접 노출하기보다 필요한 정보만 담은 DTO로 변환하여 반환하는 것이 안전합니다.
        return ResponseEntity.ok(new UserInfoResponseDto(user.getId(), user.getEmail(), user.getName()));
    }

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
