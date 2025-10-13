package com.fitsync.domain.workout.controller;

import com.fitsync.domain.workout.dto.WorkoutCreateRequest;
import com.fitsync.domain.workout.dto.WorkoutDetailResponse;
import com.fitsync.domain.workout.dto.WorkoutSimpleResponse;
import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout")
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping("/my/{id}")
    public ResponseEntity<List<WorkoutSimpleResponse>> getMyWorkoutList(@PathVariable Long userId) {

        List<WorkoutSimpleResponse> responseDtos = workoutService.getMyWorkoutList(userId);

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDetailResponse> getWorkoutById(@PathVariable Long id) {

        WorkoutDetailResponse responseDto = workoutService.getWorkoutById(id);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<Long> createWorkout(@RequestBody WorkoutCreateRequest requestDto) {

        Long id = workoutService.createWorkout(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PatchMapping("/{id}/memo")
    public ResponseEntity<?> updateWorkoutMemo(@PathVariable String id, @RequestBody Workout workout) {

        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkout(@PathVariable String id) {

        return null;
    }
}
