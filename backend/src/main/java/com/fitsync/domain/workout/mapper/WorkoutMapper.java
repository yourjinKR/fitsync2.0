package com.fitsync.domain.workout.mapper;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.user.entity.User;
import com.fitsync.domain.workout.dto.WorkoutDetailResponseDto;
import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.entity.WorkoutExercise;
import com.fitsync.domain.workout.entity.WorkoutSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkoutMapper {

    public WorkoutDetailResponseDto toDetailDto(Workout workout) {

        User owner = workout.getOwner();
        User writer = workout.getOwner();

        List<WorkoutExercise> workoutExercises = workout.getWorkoutExercises();
        List<WorkoutDetailResponseDto.WorkoutExerciseResponseDto> workoutExerciseDtoList = workoutExercises.stream()
                .map(this::toExerciseDto)
                .toList();

        return WorkoutDetailResponseDto.builder()
                .id(workout.getId())
                .title(workout.getTitle())
                .routineSnapshot(workout.getRoutineSnapshot())
                .memo(workout.getMemo())
                .createdAt(workout.getCreatedAt())
                .owner(toUserSimpleDto(owner))
                .owner(toUserSimpleDto(writer))
                .workoutExercises(workoutExerciseDtoList)
                .build();
    }

    public WorkoutDetailResponseDto.userSimpleDto toUserSimpleDto(User user) {
        return new WorkoutDetailResponseDto.userSimpleDto(user.getId(), user.getName());
    }

    public WorkoutDetailResponseDto.WorkoutExerciseResponseDto toExerciseDto(WorkoutExercise workoutExercise) {
        Exercise innerExercise = workoutExercise.getExercise();

        List<WorkoutSet> workoutSets = workoutExercise.getWorkoutSets();
        List<WorkoutDetailResponseDto.WorkoutSetResponseDto> workoutSetDtoList = workoutSets.stream()
                .map(this::toSetDto)
                .toList();

        return WorkoutDetailResponseDto.WorkoutExerciseResponseDto.builder()
                .id(workoutExercise.getId())
                .exerciseId(innerExercise.getId())
                .exerciseName(workoutExercise.getExerciseName())
                .memo(workoutExercise.getMemo())
                .workoutSets(workoutSetDtoList)
                .build();
    }

    public WorkoutDetailResponseDto.WorkoutSetResponseDto toSetDto(WorkoutSet workoutSet) {

        return WorkoutDetailResponseDto.WorkoutSetResponseDto.builder()
                .id(workoutSet.getId())
                .weightKg(workoutSet.getWeightKg())
                .reps(workoutSet.getReps())
                .durationSecond(workoutSet.getDurationSecond())
                .distanceMeter(workoutSet.getDistanceMeter())
                .build();
    }
}
