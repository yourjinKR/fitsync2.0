package com.fitsync.domain.exercise.service;

import com.fitsync.domain.exercise.dto.ExerciseDto;
import com.fitsync.domain.exercise.dto.ExerciseResponseDto;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseInstructionRepository;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseInstructionRepository exerciseInstructionRepository;


    /**
     * id값에 일치하는 특정 운동 정보를 가져오는 메소드
     * @param exerciseId 운동 정보 PK
     * @return ExerciseResponseDto
     */
    public ExerciseResponseDto getExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID와 일치하는 운동 정보를 찾지 못했습니다. exerciseId : " + exerciseId));

        return new ExerciseResponseDto(exercise);
    }
}
