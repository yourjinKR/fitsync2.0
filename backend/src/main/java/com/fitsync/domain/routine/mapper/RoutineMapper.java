package com.fitsync.domain.routine.mapper;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.routine.dto.RoutineCreateRequestDto;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.entity.RoutineSet;
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
}
