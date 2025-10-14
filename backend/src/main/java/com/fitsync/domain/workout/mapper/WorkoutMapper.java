package com.fitsync.domain.workout.mapper;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.routine.dto.RoutineDetailResponse;
import com.fitsync.domain.user.entity.User;
import com.fitsync.domain.workout.dto.WorkoutCreateRequest;
import com.fitsync.domain.workout.dto.WorkoutDetailResponse;
import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.entity.WorkoutExercise;
import com.fitsync.domain.workout.entity.WorkoutSet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutMapper {

    // create
    public Workout toEntity(WorkoutCreateRequest dto) {
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

    public WorkoutExercise toEntity(WorkoutCreateRequest.WorkoutExerciseRequest dto) {
        return WorkoutExercise.builder()
//                .id()
//                .workout()
//                .exercise()
                .exerciseName(dto.getExerciseName())
                .memo(dto.getMemo())
//                .workoutSets()
                .build();
    }

    public WorkoutSet toEntity(WorkoutCreateRequest.WorkoutSetRequest dto) {
        return WorkoutSet.builder()
//                .id()
                .weightKg(dto.getWeightKg())
                .reps(dto.getReps())
                .distanceMeter(dto.getDistanceMeter())
                .durationSecond(dto.getDurationSecond())
                .build();
    }

    // create : routineDTO -> workoutDTO
    public WorkoutCreateRequest toWorkoutDto(RoutineDetailResponse dto) {
        return null;
    }




    // read detail
    public WorkoutDetailResponse toDetailDto(Workout workout) {

        User owner = workout.getOwner();
        User writer = workout.getOwner();

        List<WorkoutExercise> workoutExercises = workout.getWorkoutExercises();
        List<WorkoutDetailResponse.WorkoutExerciseResponse> workoutExerciseDtoList = workoutExercises.stream()
                .map(this::toExerciseDto)
                .toList();

        return WorkoutDetailResponse.builder()
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

    public WorkoutDetailResponse.userResponse toUserSimpleDto(User user) {
        return new WorkoutDetailResponse.userResponse(user.getId(), user.getName());
    }

    public WorkoutDetailResponse.WorkoutExerciseResponse toExerciseDto(WorkoutExercise workoutExercise) {
        Exercise innerExercise = workoutExercise.getExercise();

        List<WorkoutSet> workoutSets = workoutExercise.getWorkoutSets();
        List<WorkoutDetailResponse.WorkoutSetResponse> workoutSetDtoList = workoutSets.stream()
                .map(this::toSetDto)
                .toList();

        return WorkoutDetailResponse.WorkoutExerciseResponse.builder()
                .id(workoutExercise.getId())
                .exerciseId(innerExercise.getId())
                .exerciseName(workoutExercise.getExerciseName())
                .memo(workoutExercise.getMemo())
                .workoutSets(workoutSetDtoList)
                .build();
    }

    public WorkoutDetailResponse.WorkoutSetResponse toSetDto(WorkoutSet workoutSet) {

        return WorkoutDetailResponse.WorkoutSetResponse.builder()
                .id(workoutSet.getId())
                .weightKg(workoutSet.getWeightKg())
                .reps(workoutSet.getReps())
                .durationSecond(workoutSet.getDurationSecond())
                .distanceMeter(workoutSet.getDistanceMeter())
                .build();
    }
}
