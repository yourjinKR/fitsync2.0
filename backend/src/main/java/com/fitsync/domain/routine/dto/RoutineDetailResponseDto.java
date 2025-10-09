package com.fitsync.domain.routine.dto;

import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineDetailResponseDto {
    private Long id;
    private Long ownerId;
    private Long writerId;
    private String name;
    private Integer displayOrder;
    private String memo;

    private List<RoutineExerciseDto> exercises;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoutineExerciseDto {
        private Long id;
        private Integer displayOrder;
        private String memo;

        private Long exerciseId;
        private String exerciseName;
        private String exerciseCategory;
        private MetricRequirement weightKgStatus;
        private MetricRequirement repsStatus;
        private MetricRequirement distanceMeterStatus;
        private MetricRequirement durationSecondStatus;

        private List<RoutineSetDto> sets;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoutineSetDto {
        private Long id;
        private Integer displayOrder;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;
    }
}
