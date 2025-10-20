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

    public Page<ExerciseSimpleResponse> getAllExercises(Pageable pageable) {
        return exerciseRepository.findAllSimple(pageable);
    }

    @Transactional
    public ExerciseDetailResponse getExercise(Long id) {

        Exercise exercise = selectExercise(id);

        return exerciseMapper.toDto(exercise);
    }

    @Transactional
    public ExerciseDetailResponse updateExercise(Long id, ExerciseUpdateRequest requestDto) {

        Exercise exercise = selectExercise(id);

        exerciseMapper.applyUpdateFrom(exercise, requestDto);

        return exerciseMapper.toDto(exercise);
    }

    @Transactional
    public void inactivateExercise(Long id) {

        Exercise exercise = selectExercise(id);

        exercise.hide();
    }

    @Transactional
    public void activateExercise(Long id) {

        Exercise exercise = selectExercise(id);

        exercise.show();
    }

    @Transactional
    public void deactivateExercises(ExerciseIsHiddenUpdateRequest requestDto) {

        exerciseRepository.updateHiddenStatusByIds(
                requestDto.getExerciseIds(),
                true
        );
    }

    @Transactional
    public void activateExercises(ExerciseIsHiddenUpdateRequest requestDto) {

        exerciseRepository.updateHiddenStatusByIds(
                requestDto.getExerciseIds(),
                false
        );
    }

    @Transactional
    public void removeExercise(Long exerciseId) {

        Exercise exercise = exerciseRepository.getReferenceById(exerciseId);

        exerciseRepository.deleteById(exercise.getId());
    }

    public Exercise selectExercise(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID와 일치하는 운동 정보를 찾지 못했습니다. exerciseId : " + id));
    }



}
