package com.fitsync.domain.exercise.service;

import com.fitsync.domain.exercise.dto.ExerciseDetailResponseDto;
import com.fitsync.domain.exercise.dto.ExerciseSimpleResponseDto;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseInstructionRepository;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseInstructionRepository exerciseInstructionRepository;


    /**
     * 모든 운동 정보를 가져오는 메소드
     */
    public Page<ExerciseSimpleResponseDto> getAllExercises(Pageable pageable) {

        Page<Exercise> exercisePage = exerciseRepository.findAll(pageable);

        return exercisePage.map(ExerciseSimpleResponseDto::new);
    }

    /**
     * id값에 일치하는 특정 운동 정보를 가져오는 메소드
     * @param exerciseId 운동 정보 PK
     * @return ExerciseResponseDto
     */
    public ExerciseDetailResponseDto getExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID와 일치하는 운동 정보를 찾지 못했습니다. exerciseId : " + exerciseId));

        return new ExerciseDetailResponseDto(exercise);
    }
}
