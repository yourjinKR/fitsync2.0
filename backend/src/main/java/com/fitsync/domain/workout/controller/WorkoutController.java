package com.fitsync.domain.workout.controller;

import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.service.WorkoutService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> getWorkoutById(@PathVariable String id) {

        return null;
    }

    @PostMapping
    public ResponseEntity<?> createWorkout(@RequestBody Workout workout) {

        return null;
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
