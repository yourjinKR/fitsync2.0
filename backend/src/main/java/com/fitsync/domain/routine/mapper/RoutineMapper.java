package com.fitsync.domain.routine.mapper;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseMetricRequirement;
import com.fitsync.domain.routine.dto.RoutineCreateRequestDto;
import com.fitsync.domain.routine.dto.RoutineDetailResponseDto;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.entity.RoutineSet;
import com.fitsync.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RoutineMapper {

    // create
    public Routine toEntity(RoutineCreateRequestDto dto) {
        return Routine.builder()
                .name(dto.getName())
                .displayOrder(dto.getDisplayOrder())
                .memo(dto.getMemo())
                .build();
    }

    public RoutineExercise toEntity(RoutineCreateRequestDto.RoutineExerciseDto dto) {
        return RoutineExercise.builder()
                .displayOrder(dto.getDisplayOrder())
                .memo(dto.getMemo())
                .build();
    }

    public RoutineSet toEntity(RoutineCreateRequestDto.RoutineSetDto dto) {
        return RoutineSet.builder()
                .displayOrder(dto.getDisplayOrder())
                .weightKg(dto.getWeightKg())
                .reps(dto.getReps())
                .distanceMeter(dto.getDistanceMeter())
                .durationSecond(dto.getDurationSecond())
                .build();
    }

    // read detail
    public RoutineDetailResponseDto toDto(Routine routine) {
        User owner = routine.getOwner();
        User writer = routine.getWriter();

        return RoutineDetailResponseDto.builder()
                .id(routine.getId())
                .ownerId(owner.getId())
                .writerId(writer.getId())
                .name(routine.getName())
                .displayOrder(routine.getDisplayOrder())
                .memo(routine.getMemo())
                .exercises(routine.getRoutineExercises().stream().map(this::toDto).toList())
                .build();
    }

    public RoutineDetailResponseDto.RoutineExerciseDto toDto(RoutineExercise rExercise) {
        Exercise  exercise = rExercise.getExercise();
        ExerciseMetricRequirement metric = exercise.getMetricRequirement();

        return RoutineDetailResponseDto.RoutineExerciseDto.builder()
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

    public RoutineDetailResponseDto.RoutineSetDto toDto(RoutineSet set) {
        return RoutineDetailResponseDto.RoutineSetDto.builder()
                .id(set.getId())
                .displayOrder(set.getDisplayOrder())
                .weightKg(set.getWeightKg())
                .reps(set.getReps())
                .distanceMeter(set.getDistanceMeter())
                .durationSecond(set.getDurationSecond())
                .build();
    }
}
