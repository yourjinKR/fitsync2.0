package com.fitsync.domain.routine.controller;

import com.fitsync.domain.routine.dto.*;
import com.fitsync.domain.routine.service.RoutineService;
import com.fitsync.domain.user.entity.User;
import com.fitsync.global.util.LoginUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;
    private final LoginUserProvider loginUserProvider;

    // 루틴 생성
    @PostMapping
    public ResponseEntity<Long> createRoutine(@RequestBody RoutineCreateRequest requestDto) {

        Long id =  routineService.createRoutine(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // 사용자 루틴 목록
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<RoutineSimpleResponse>> getRoutineList(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {

        User currentUser = loginUserProvider.getCurrentUser();
        System.out.println("user !!!! " + currentUser.getId());
        System.out.println("user !!!! " + userId);

        // 현재 유저와 동일할 경우 나의 루틴을 조회
        if (userId.equals(currentUser.getId())) {
            Page<RoutineSimpleResponse> result = routineService.getMyRoutineList(userId, pageable);
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.notFound().build();
    }


    // 특정 루틴 상세보기
    @GetMapping("/{id}")
    public ResponseEntity<RoutineDetailResponse> getRoutine(@PathVariable("id") Long id) {

        RoutineDetailResponse result = routineService.getRoutine(id);

        return ResponseEntity.ok(result);
    }

    // 루틴 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<RoutineDetailResponse> updateRoutine(@PathVariable("id") Long id, @RequestBody RoutineUpdateRequest requestDto) {

        RoutineDetailResponse result = routineService.updateRoutine(id, requestDto);

        return ResponseEntity.ok(result);
    }

    // 루틴 기본 정보 수정 (이름, 메모)
    @PatchMapping("/header/{id}")
    public ResponseEntity<Void> updateRoutineHeader(@PathVariable("id") Long id, @RequestBody RoutineSimpleRequest requestDto) {

        routineService.updateRoutineHeader(id, requestDto);

        return ResponseEntity.ok().build();
    }

    // 루틴간 정렬 변경
    @PatchMapping("/displayOrder")
    public ResponseEntity<Void> sortRoutine(@RequestBody List<RoutineSimpleRequest> requestDtos) {

        routineService.sortRoutine(requestDtos);

        return ResponseEntity.ok().build();
    }

    // 루틴 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id, @RequestBody RoutineDeleteRequest requestDto) {

        routineService.deleteRoutine(id, requestDto);

        return ResponseEntity.ok().build();
    }
}
