package com.fitsync.domain.exercise.controller;

import com.fitsync.domain.exercise.dto.ExerciseCreateRequestDto;
import com.fitsync.domain.exercise.dto.ExerciseDetailResponseDto;
import com.fitsync.domain.exercise.dto.ExerciseSimpleResponseDto;
import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import com.fitsync.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * 새로운 운동생성
     * @return <code>ExerciseDetailResponseDto</code>
     */
    @PostMapping
    public ResponseEntity<ExerciseDetailResponseDto> createExercise(
            @RequestBody ExerciseCreateRequestDto requestDto
            ) {
        ExerciseDetailResponseDto createdExercise = exerciseService.createExercise(requestDto);

        // 201 return
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
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

    /**
     *  운동정보 상세보기
     * @param exerciseId pk
     */
    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseDetailResponseDto> getExercise(@PathVariable("exerciseId") Long exerciseId) {

        return ResponseEntity.ok(exerciseService.getExercise(exerciseId));
    }


    /**
     * 특정 운동 수정하기
     * @param exerciseId pk
     * @param requestDto 내부에 id는 없음
     */
    @PutMapping("/{exerciseId}")
    public ResponseEntity<ExerciseDetailResponseDto> updateExercise(@PathVariable Long exerciseId, @RequestBody ExerciseUpdateRequestDto requestDto) {

        System.out.println("id 확인 : " + exerciseId);
        System.out.println("dto 확인 : " + requestDto);

        ExerciseDetailResponseDto updatedExercise = exerciseService.updateExercise(exerciseId, requestDto);

        return ResponseEntity.ok(updatedExercise);
    }

}
