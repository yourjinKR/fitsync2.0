package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import com.fitsync.domain.exercise.entity.ExerciseMetricRequirement;
import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ExerciseDetailResponseDto {

    private final Long id;
    private final String name;
    private final String category;
    private final String description;
    private final boolean isHidden;
    private final OffsetDateTime createdAt;

    private final List<InstructionResponseDto> instructions;
    private final MetricResponseDto metricRequirement;

    // 단계별 설명을 위한 내부 DTO
    @Getter
    public static class InstructionResponseDto {
        private final Long id;
        private final Integer stepOrder;
        private final String description;

        public InstructionResponseDto(ExerciseInstruction instruction) {
            this.id = instruction.getId();
            this.stepOrder = instruction.getStepOrder();
            this.description = instruction.getDescription();
        }
    }

    // 세트값 입력 허용을 위한 내부 DTO
    @Getter
    public static class MetricResponseDto {
        private MetricRequirement weightKgStatus;
        private MetricRequirement repsStatus;
        private MetricRequirement distanceMeterStatus;
        private MetricRequirement durationSecondStatus;

        public MetricResponseDto(ExerciseMetricRequirement metric) {
            if (metric != null) {
                this.weightKgStatus = metric.getWeightKgStatus();
                this.repsStatus = metric.getRepsStatus();
                this.distanceMeterStatus = metric.getDistanceMeterStatus();
                this.durationSecondStatus = metric.getDurationSecondStatus();
            }
        }
    }

    // 엔티티 -> DTO
    public ExerciseDetailResponseDto(Exercise exercise) {
        this.id = exercise.getId();
        this.name = exercise.getName();
        this.category = exercise.getCategory();
        this.description = exercise.getDescription();
        this.isHidden = exercise.isHidden();
        this.createdAt = exercise.getCreatedAt();

        this.instructions = exercise.getInstructions().stream()
                .map(InstructionResponseDto::new)
                .collect(Collectors.toList());

        this.metricRequirement = new MetricResponseDto(exercise.getMetricRequirement());
    }

    //  Lombok이 생성하는 isHidden() 메소드 대신,
    //  직접 getter를 만들고 @JsonProperty를 붙여줍니다.
    @JsonProperty("isHidden")
    public boolean isHidden() {
        return this.isHidden;
    }
}
