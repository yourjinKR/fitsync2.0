package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import com.fitsync.domain.exercise.entity.ExerciseMetricRequirement;
import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class ExerciseDetailResponseDto {

    private Long id;
    private String name;
    private String category;
    private String description;
    private boolean isHidden;
    private OffsetDateTime createdAt;

    private List<InstructionResponseDto> instructions;
    private MetricResponseDto metricRequirement;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class InstructionResponseDto {
        private Long id;
        private Integer stepOrder;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MetricResponseDto {
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
