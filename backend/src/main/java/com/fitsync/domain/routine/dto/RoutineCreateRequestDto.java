package com.fitsync.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineCreateRequestDto {

    // 루틴 기본정보 (User 정보 제거)
    private String name;
    private Integer displayOrder;
    private String memo;

    // 루틴 내 운동정보
    private List<RoutineExerciseDto> exercises;

    @Getter
    @NoArgsConstructor
    public static class RoutineExerciseDto {
        private Long exerciseId;
        private Integer displayOrder;
        private String memo;
        private List<RoutineSetDto> sets;
    }

    @Getter
    @NoArgsConstructor
    public static class RoutineSetDto {
        private Integer displayOrder;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;
    }
}