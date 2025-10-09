package com.fitsync.domain.workout.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDetailResponseDto {

    private Long id;
    private String title;
    private Map<String, Object> routineSnapshot;
    private String memo;
    private OffsetDateTime createdAt;

    private userSimpleDto owner;
    private userSimpleDto writer;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class userSimpleDto {
        private Long id;
        private String name;
    }

    private List<WorkoutExerciseResponseDto> workoutExercises;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkoutExerciseResponseDto {
        private Long id;
        private Long exerciseId;
        private String exerciseName;
        private String memo;

        private List<WorkoutSetResponseDto> workoutSets;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkoutSetResponseDto {
        private Long id;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;
    }
}
