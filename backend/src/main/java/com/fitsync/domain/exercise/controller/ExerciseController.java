package com.fitsync.domain.exercise.controller;

import com.fitsync.domain.exercise.dto.ExerciseResponseDto;
import com.fitsync.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseResponseDto> getExercise(@PathVariable("exerciseId") Long exerciseId) {
        // 예외 처리는 GlobalExceptionHandler가 처리해줌
        return ResponseEntity.ok(exerciseService.getExercise(exerciseId));
    }
}
