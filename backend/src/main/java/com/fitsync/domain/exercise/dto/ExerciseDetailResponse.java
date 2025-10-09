package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ExerciseDetailResponse {

    private Long id;
    private String name;
    private String category;
    private String description;
    private boolean isHidden;
    private OffsetDateTime createdAt;

    private List<InstructionResponse> instructions;
    private MetricResponse metricRequirement;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class InstructionResponse {
        private Long id;
        private Integer stepOrder;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MetricResponse {
        private MetricRequirement weightKgStatus;
        private MetricRequirement repsStatus;
        private MetricRequirement distanceMeterStatus;
        private MetricRequirement durationSecondStatus;
    }

    @JsonProperty("isHidden")
    public boolean isHidden() {
        return this.isHidden;
    }
}
