package com.fitsync.domain.workout.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class WorkoutCreateRequestDto {

    private String title;
    private Map<String, Object> routineSnapshot;
    private String memo;

    private Long ownerId;
    private Long writerId;

    private List<WorkoutExerciseRequestDto> workoutExercises;

    @Getter
    @NoArgsConstructor
    public static class WorkoutExerciseRequestDto {
        private Long exerciseId;
        private String exerciseName;
        private String memo;

        private List<WorkoutSetRequestDto> workoutSets;
    }

    @Getter
    @NoArgsConstructor
    public static class WorkoutSetRequestDto {
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;
    }
}
