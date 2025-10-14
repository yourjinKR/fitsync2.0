package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseUpdateRequest {

    private String name;
    private String category;
    private String description;
    private boolean isHidden;

    private List<InstructionRequest> instructions;
    private MetricRequest metricRequirement;

    @Getter
    public static class InstructionRequest {
        private Long id;
        private Integer stepOrder;
        private String description;
    }

    @Getter
    @Builder
    public static class MetricRequest {
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
