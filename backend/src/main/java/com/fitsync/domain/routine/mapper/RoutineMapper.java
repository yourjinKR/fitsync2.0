package com.fitsync.domain.routine.mapper;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseMetricRequirement;
import com.fitsync.domain.routine.dto.RoutineCreateRequest;
import com.fitsync.domain.routine.dto.RoutineDetailResponse;
import com.fitsync.domain.routine.dto.RoutineUpdateRequest;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.entity.RoutineSet;
import com.fitsync.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RoutineMapper {

    // create
    public Routine toEntity(RoutineCreateRequest dto) {
        return Routine.builder()
                .name(dto.getName())
                .displayOrder(dto.getDisplayOrder())
                .memo(dto.getMemo())
                .build();
    }

    public RoutineExercise toEntity(RoutineCreateRequest.RoutineExerciseRequest dto) {
        return com.fitsync.domain.routine.entity.RoutineExercise.builder()
                .displayOrder(dto.getDisplayOrder())
                .memo(dto.getMemo())
                .build();
    }

    public RoutineSet toEntity(RoutineCreateRequest.RoutineSetRequest dto) {
        return com.fitsync.domain.routine.entity.RoutineSet.builder()
                .displayOrder(dto.getDisplayOrder())
                .weightKg(dto.getWeightKg())
                .reps(dto.getReps())
                .distanceMeter(dto.getDistanceMeter())
                .durationSecond(dto.getDurationSecond())
                .build();
    }

    // read detail
    public RoutineDetailResponse toDto(Routine routine) {
        User owner = routine.getOwner();
        User writer = routine.getWriter();

        return RoutineDetailResponse.builder()
                .id(routine.getId())
                .ownerId(owner.getId())
                .writerId(writer.getId())
                .name(routine.getName())
                .displayOrder(routine.getDisplayOrder())
                .memo(routine.getMemo())
                .exercises(routine.getRoutineExercises().stream().map(this::toDto).toList())
                .build();
    }

    public RoutineDetailResponse.RoutineExerciseResponse toDto(RoutineExercise rExercise) {
        Exercise  exercise = rExercise.getExercise();
        ExerciseMetricRequirement metric = exercise.getMetricRequirement();

        return RoutineDetailResponse.RoutineExerciseResponse.builder()
                .id(rExercise.getId())
                .displayOrder(rExercise.getDisplayOrder())
                .memo(rExercise.getMemo())
                .exerciseId(exercise.getId())
                .exerciseName(exercise.getName())
                .exerciseCategory(exercise.getCategory())
                .weightKgStatus(metric.getWeightKgStatus())
                .repsStatus(metric.getRepsStatus())
                .distanceMeterStatus(metric.getDistanceMeterStatus())
                .durationSecondStatus(metric.getDurationSecondStatus())
                .sets(rExercise.getSets().stream().map(this::toDto).toList())
                .build();
    }

    public RoutineDetailResponse.RoutineSetResponse toDto(RoutineSet set) {
        return RoutineDetailResponse.RoutineSetResponse.builder()
                .id(set.getId())
                .displayOrder(set.getDisplayOrder())
                .weightKg(set.getWeightKg())
                .reps(set.getReps())
                .distanceMeter(set.getDistanceMeter())
                .durationSecond(set.getDurationSecond())
                .build();
    }

    // update
    public Routine toEntity(RoutineUpdateRequest dto) {
        return Routine.builder()
                .id(dto.getId())
//                .owner()
//                .writer()
                .name(dto.getName())
                .displayOrder(dto.getDisplayOrder())
                .memo(dto.getMemo())
//                .routineExercises(dto.getRoutineExercises())
                .build();
    }

    public RoutineExercise toEntity(RoutineUpdateRequest.RoutineExerciseRequest dto) {
        return com.fitsync.domain.routine.entity.RoutineExercise.builder()
                .id(dto.getId())
//                .routine()
//                .exercise()
                .displayOrder( dto.getDisplayOrder())
                .memo(dto.getMemo())
//                .sets()
                .build();
    }

    public RoutineSet toEntity(RoutineUpdateRequest.RoutineSetRequest dto) {
        return com.fitsync.domain.routine.entity.RoutineSet.builder()
                .id(dto.getId())
//                .routineExercise()
                .displayOrder( dto.getDisplayOrder())
                .weightKg(dto.getWeightKg())
                .reps(dto.getReps())
                .distanceMeter(dto.getDistanceMeter())
                .durationSecond(dto.getDurationSecond())
                .build();
    }
}
