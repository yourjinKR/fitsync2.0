package com.fitsync.domain.exercise.service;

import com.fitsync.domain.exercise.dto.ExerciseDto;
import com.fitsync.domain.exercise.dto.ExerciseResponseDto;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseInstructionRepository;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseInstructionRepository exerciseInstructionRepository;


    /**
     * id값에 일치하는 특정 운동 정보를 가져오는 메소드
     */
    public ExerciseResponseDto getExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId).orElse(null);

        if (exercise == null) {return null;}

        return new ExerciseResponseDto(exercise);
    }
}
