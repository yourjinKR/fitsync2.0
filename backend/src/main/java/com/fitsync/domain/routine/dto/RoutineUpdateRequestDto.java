package com.fitsync.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineUpdateRequestDto {

    // 기본정보
    private Long id;
    private String name;
    private Integer displayOrder;
    private String memo;

    // 루틴 내 운동정보
    private List<RoutineExerciseDto> routineExercises;

    @Getter
    @NoArgsConstructor
    public static class RoutineExerciseDto {
        private Long id;
        private Long exerciseId;
        private Integer displayOrder;
        private String memo;

        // 세트 정보
        private List<RoutineSetDto> sets;
    }

    @Getter
    @NoArgsConstructor
    public static class RoutineSetDto {
        private Long id;
        private Integer displayOrder;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;
    }
}
