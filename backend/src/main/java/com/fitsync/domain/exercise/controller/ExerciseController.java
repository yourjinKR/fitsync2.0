package com.fitsync.domain.exercise.controller;

import com.fitsync.domain.exercise.dto.ExerciseResponseDto;
import com.fitsync.domain.exercise.dto.ExerciseSimpleResponseDto;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * 모든 운동 정보를 페이지 단위로 조회
     * 예시 URL: /exercises?page=0&size=20&sort=name,asc
     */
    @GetMapping("/all")
    public ResponseEntity<Page<ExerciseSimpleResponseDto>> getAllExercises(
            // @PageableDefault를 통해 기본값을 설정 가능
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {

        Page<ExerciseSimpleResponseDto> exercisePage = exerciseService.getAllExercises(pageable);

        return ResponseEntity.ok(exercisePage);
    }
}
