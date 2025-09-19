package com.fitsync.domain.exercise.controller;

import com.fitsync.domain.exercise.dto.*;
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
    public ResponseEntity<ExerciseDetailResponseDto> createExercise(@RequestBody ExerciseCreateRequestDto requestDto) {
        ExerciseDetailResponseDto createdExercise = exerciseService.createExercise(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise); // return 201
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

        ExerciseDetailResponseDto updatedExercise = exerciseService.updateExercise(exerciseId, requestDto);

        System.out.println("dto!!! : " + requestDto.toString());

        return ResponseEntity.ok(updatedExercise);
    }

    /**
     * 운동정보 비활성화 메소드 (삭제 기능처럼 사용, 다른 데이터들과 연관성 존재)
     * @param exerciseId pk
     * @return 별도의 응답 본문은 없음을 의미하는 204 No Content 반환
     */
    @PatchMapping("/{exerciseId}/deactivation")
    public ResponseEntity<Void> inactivateExercise(@PathVariable Long exerciseId) {
        exerciseService.inactivateExercise(exerciseId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 운동정보 활성화 메소드
     * @param exerciseId pk
     * @return 별도의 응답 본문은 없음을 의미하는 204 No Content 반환
     */
    @PatchMapping("/{exerciseId}/activation")
    public ResponseEntity<Void> activateExercise(@PathVariable Long exerciseId) {

        exerciseService.activateExercise(exerciseId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 여러 운동정보를 숨기는 메소드
     * @param requestDto <code>ExerciseIsHiddenUpdateRequestDto</code>
     * @return empty context
     */
    @PostMapping("/deactivations")
    public ResponseEntity<Void> inactivateExercise(@RequestBody ExerciseIsHiddenUpdateRequestDto requestDto) {

        exerciseService.inactivateExercises(requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 여러 운동정보를 숨기기 해제 메소드
     * @param requestDto <code>ExerciseIsHiddenUpdateRequestDto</code>
     * @return empty context
     */
    @PostMapping("/activations")
    public ResponseEntity<Void> activateExercise(@RequestBody ExerciseIsHiddenUpdateRequestDto requestDto) {

        exerciseService.activateExercises(requestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 운동정보 삭제 메소드
     */
    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> removeExercise(@PathVariable("exerciseId") Long exerciseId) {

        exerciseService.removeExercise(exerciseId);

        return ResponseEntity.ok().build();
    }
}
