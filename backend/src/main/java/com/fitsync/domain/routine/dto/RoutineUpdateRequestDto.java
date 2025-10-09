package com.fitsync.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineUpdateRequestDto {
    private Long id;
    private String name;
    private Integer displayOrder;
    private String memo;

    private List<RoutineExerciseDto> routineExercises;

    @Getter
    @NoArgsConstructor
    public static class RoutineExerciseDto {
        private Long id; // Nullable
        private Long exerciseId;
        private Integer displayOrder;
        private String memo;

        private List<RoutineSetDto> sets;
    }

    @Getter
    @NoArgsConstructor
    public static class RoutineSetDto {
        private Long id; // Nullable
        private Integer displayOrder;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;
    }
}
