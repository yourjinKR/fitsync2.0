package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import com.fitsync.domain.exercise.entity.ExerciseMetricRequirement;
import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO : 이미지 URL 추가 (추후에 진행)
@Getter
@NoArgsConstructor
public class ExerciseCreateRequestDto {
    
    // id 필요없음
    private String name;
    private String category;
    private String description;
    private boolean isHidden;

    private List<InstructionRequestDto> instructions;
    private MetricRequestDto metricRequirement;

    // 운동 설명
    @Getter
    @NoArgsConstructor
    public static class InstructionRequestDto {
        private Integer stepOrder;
        private String description;

        public ExerciseInstruction toEntity() {
            return ExerciseInstruction.builder()
                    .stepOrder(this.stepOrder)
                    .description(this.description)
                    .build();
        }
    }

    // 운동 입력 가능 여부
    @Getter
    @NoArgsConstructor
    public static class MetricRequestDto {
        private MetricRequirement weightKgStatus;
        private MetricRequirement repsStatus;
        private MetricRequirement distanceMeterStatus;
        private MetricRequirement durationSecondStatus;

        public ExerciseMetricRequirement toEntity() {
            return ExerciseMetricRequirement.builder()
                    .weightKgStatus(this.weightKgStatus)
                    .repsStatus(this.repsStatus)
                    .distanceMeterStatus(this.distanceMeterStatus)
                    .durationSecondStatus(this.durationSecondStatus)
                    .build();
        }
    }

    public Exercise toEntity() {
        Exercise exercise = Exercise.builder()
                .name(this.name)
                .category(this.category)
                .description(this.description)
                .isHidden(this.isHidden)
                .build();

        // instruction DTO 리스트를 엔티티 리스트로 변환하고, Exercise 엔티티와 연관관계를 맺어줍니다
        if (this.instructions != null) {
            List<ExerciseInstruction> instructionEntities = this.instructions.stream()
                    .map(InstructionRequestDto::toEntity)
                    .toList();
            instructionEntities.forEach(exercise::addInstruction);
        }

        if (this.metricRequirement != null) {
            exercise.setRequirement(this.metricRequirement.toEntity());
        }

        return exercise;
    }


}
