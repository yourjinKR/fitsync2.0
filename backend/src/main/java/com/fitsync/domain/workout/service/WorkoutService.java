package com.fitsync.domain.workout.service;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.domain.user.entity.User;
import com.fitsync.domain.workout.dto.WorkoutCreateRequestDto;
import com.fitsync.domain.workout.dto.WorkoutDetailResponseDto;
import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.entity.WorkoutExercise;
import com.fitsync.domain.workout.entity.WorkoutSet;
import com.fitsync.domain.workout.mapper.WorkoutMapper;
import com.fitsync.domain.workout.repository.WorkoutRepository;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import com.fitsync.global.error.exception.UnauthorizedAccessException;
import com.fitsync.global.util.LoginUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper;
    private final LoginUserProvider loginUserProvider;
    private final ExerciseRepository exerciseRepository;

    // create
    public Long createWorkout(WorkoutCreateRequestDto requestDto) {
        Workout workout;

        User currentUser = loginUserProvider.getCurrentUser();
        if (!Objects.equals(currentUser.getId(), requestDto.getOwnerId())) {
            throw new UnauthorizedAccessException("운동을 기록할 권한이 없습니다. ");
        }
        else {
            workout = workoutMapper.toEntity(requestDto);
            workout.forMe(currentUser);
        }

        if (requestDto.getWorkoutExercises() != null) {
            for (WorkoutCreateRequestDto.WorkoutExerciseRequestDto exerciseDto : requestDto.getWorkoutExercises()) {
                Exercise exercise = exerciseRepository.findById(exerciseDto.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("운동 정보를 찾을 수 없습니다: " + exerciseDto.getExerciseId()));

                WorkoutExercise workoutExercise = workoutMapper.toEntity(exerciseDto);
                workoutExercise.selectExercise(exercise);

                if (exerciseDto.getWorkoutSets() != null) {
                    for (WorkoutCreateRequestDto.WorkoutSetRequestDto setDto : exerciseDto.getWorkoutSets()) {

                        WorkoutSet workoutSet = workoutMapper.toEntity(setDto);

                        workoutExercise.addSet(workoutSet);
                    }
                }

                workout.addExercise(workoutExercise);
            }
        }

        Workout savedWorkout = workoutRepository.save(workout);

        return savedWorkout.getId();
    }

    // read
    public WorkoutDetailResponseDto getWorkoutById(Long id) {

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 운동기록을 찾지 못함, id : " + id));

        return workoutMapper.toDetailDto(workout);
    }

    // update (메모는 수정 가능)

    // delete (일반 사용자는 권한 없음)

}

