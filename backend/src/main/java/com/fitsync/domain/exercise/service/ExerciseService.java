package com.fitsync.domain.exercise.service;

import com.fitsync.domain.exercise.dto.*;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import com.fitsync.domain.exercise.mapper.ExerciseMapper;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    /**
     * 새로운 운동 정보를 생성하는 메소드
     * @param requestDto <code>ExerciseCreateRequestDto</code> 생성 전용 DTO 사용
     * @return <code>ExerciseDetailResponseDto</code> 바로 해당 정보를 보여주기 위해 응답
     */
    @Transactional
    public ExerciseDetailResponse createExercise(ExerciseCreateRequest requestDto) {

        if (exerciseRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 운동이 존재합니다 :  " + requestDto.getName());
        }

        Exercise exercise = exerciseMapper.toEntity(requestDto);

        List<ExerciseInstruction> instructions = requestDto.getInstructions().stream()
                .map(exerciseMapper::toEntity)
                .toList();
        instructions.forEach(exercise::addInstruction);

        exercise.setRequirement(exerciseMapper.toEntity(requestDto.getMetricRequirement()));

        Exercise savedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toDto(savedExercise);
    }

    /**
     * 모든 운동 정보를 가져오는 메소드
     */
    public Page<ExerciseSimpleResponse> getAllExercises(Pageable pageable) {
        return exerciseRepository.findAllSimple(pageable);
    }

    /**
     * id값에 일치하는 특정 운동 정보를 가져오는 메소드
     * @param exerciseId 운동 정보 PK
     * @return <code>ExerciseResponseDto</code>
     */
    public ExerciseDetailResponse getExercise(Long exerciseId) {

        Exercise exercise = exerciseRepository.findByIdWithInstructions(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID와 일치하는 운동 정보를 찾지 못했습니다. exerciseId : " + exerciseId));

        return exerciseMapper.toDto(exercise);
    }

    /**
     * 운동 정보를 수정하는 메소드
     * @param exerciseId pk
     * @param requestDto <code>ExerciseUpdateRequestDto</code>
     * @return <code>ExerciseDetailResponseDto</code>
     */
    @Transactional
    public ExerciseDetailResponse updateExercise(Long exerciseId, ExerciseUpdateRequest requestDto) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID와 일치하는 운동 정보를 찾지 못했습니다. exerciseId : " + exerciseId));

        // dto, entity 분리하기
        exerciseMapper.applyUpdateFrom(exercise, requestDto);

        return exerciseMapper.toDto(exercise);
    }

    /**
     * 운동정보를 비활성화 하는 메소드
     * @param exerciseId pk
     */
    @Transactional
    public void inactivateExercise(Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID와 일치하는 운동 정보를 찾지 못했습니다. exerciseId : " + exerciseId));


        exercise.hide();
    }

    /**
     * 운동정보를 활성화 하는 메소드
     * @param exerciseId pk
     */
    @Transactional
    public void activateExercise(Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID와 일치하는 운동 정보를 찾지 못했습니다. exerciseId : " + exerciseId));

        exercise.show();
    }

    /**
     * 복수의 운동 정보를 비활성화 하는 메소드
     * @param requestDto <code>ExerciseIsHiddenUpdateRequestDto</code>
     */
    @Transactional
    public void deactivateExercises(ExerciseIsHiddenUpdateRequest requestDto) {

        exerciseRepository.updateHiddenStatusByIds(
                requestDto.getExerciseIds(),
                true
        );
    }

    /**
     * 복수의 운동 정보를 활성화 하는 메소드
     * @param requestDto <code>ExerciseIsHiddenUpdateRequestDto</code>
     */
    @Transactional
    public void activateExercises(ExerciseIsHiddenUpdateRequest requestDto) {

        exerciseRepository.updateHiddenStatusByIds(
                requestDto.getExerciseIds(),
                false
        );
    }

    /**
     * 운동정보 삭제하는 메소드
     * @param exerciseId pk
     */
    @Transactional
    public void removeExercise(Long exerciseId) {

        Exercise exercise = exerciseRepository.getReferenceById(exerciseId);

        exerciseRepository.deleteById(exercise.getId());
    }



}
