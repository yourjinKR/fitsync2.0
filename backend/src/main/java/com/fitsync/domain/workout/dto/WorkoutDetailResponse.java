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
public class WorkoutDetailResponse {

    private Long id;
    private String title;
    private Map<String, Object> routineSnapshot;
    private String memo;
    private OffsetDateTime createdAt;

    private userResponse owner;
    private userResponse writer;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class userResponse {
        private Long id;
        private String name;
    }

    private List<WorkoutExerciseResponse> workoutExercises;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkoutExerciseResponse {
        private Long id;
        private Long exerciseId;
        private String exerciseName;
        private String memo;

        private List<WorkoutSetResponse> workoutSets;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkoutSetResponse {
        private Long id;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;
    }
}
