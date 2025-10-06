package com.fitsync.domain.workout.controller;

import com.fitsync.domain.workout.dto.WorkoutCreateRequestDto;
import com.fitsync.domain.workout.dto.WorkoutDetailResponseDto;
import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout")
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<List<?>> getMyWorkoutList() {

        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDetailResponseDto> getWorkoutById(@PathVariable Long id) {

        WorkoutDetailResponseDto responseDto = workoutService.getWorkoutById(id);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<?> createWorkout(@RequestBody WorkoutCreateRequestDto requestDto) {

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
