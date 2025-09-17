package com.fitsync.domain.exercise.service;

import com.fitsync.domain.exercise.dto.ExerciseCreateRequestDto;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseInstructionRepository exerciseInstructionRepository;


    /**
     * 새로운 운동 정보를 생성하는 메소드
     * @param requestDto <code>ExerciseCreateRequestDto</code> 생성 전용 DTO 사용
     * @return <code>ExerciseDetailResponseDto</code> 바로 해당 정보를 보여주기 위해 응답
     */
    @Transactional
    public ExerciseDetailResponseDto createExercise(ExerciseCreateRequestDto requestDto) {

        if (exerciseRepository.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("이미 동일한 이름의 운동이 존재합니다 :  " + requestDto.getName());
        }

        Exercise exercise = requestDto.toEntity();
        Exercise savedExercise = exerciseRepository.save(exercise);

        return new ExerciseDetailResponseDto(savedExercise);
    }

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
