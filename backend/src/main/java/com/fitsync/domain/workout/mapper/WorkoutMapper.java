package com.fitsync.domain.workout.mapper;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.routine.dto.RoutineDetailResponse;
import com.fitsync.domain.user.entity.User;
import com.fitsync.domain.workout.dto.WorkoutCreateRequestDto;
import com.fitsync.domain.workout.dto.WorkoutDetailResponseDto;
import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.entity.WorkoutExercise;
import com.fitsync.domain.workout.entity.WorkoutSet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutMapper {

    // create
    public Workout toEntity(WorkoutCreateRequestDto dto) {
        return Workout.builder()
//                .id()
//                .owner()
//                .writer()
                .title(dto.getTitle())
                .routineSnapshot(dto.getRoutineSnapshot())
                .memo(dto.getMemo())
//                .workoutExercises()
                .build();
    }

    public WorkoutExercise toEntity(WorkoutCreateRequestDto.WorkoutExerciseRequestDto dto) {
        return WorkoutExercise.builder()
//                .id()
//                .workout()
//                .exercise()
                .exerciseName(dto.getExerciseName())
                .memo(dto.getMemo())
//                .workoutSets()
                .build();
    }

    public WorkoutSet toEntity(WorkoutCreateRequestDto.WorkoutSetRequestDto dto) {
        return WorkoutSet.builder()
//                .id()
                .weightKg(dto.getWeightKg())
                .reps(dto.getReps())
                .distanceMeter(dto.getDistanceMeter())
                .durationSecond(dto.getDurationSecond())
                .build();
    }

    // create : routineDTO -> workoutDTO
    public WorkoutCreateRequestDto toWorkoutDto(RoutineDetailResponse dto) {
        return null;
    }




    // read detail
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
