package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import lombok.Getter;

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
    private final List<InstructionInfo> instructions;

    // 단계별 설명을 위한 내부 DTO
    @Getter
    public static class InstructionInfo {
        private final Long id;
        private final Integer stepOrder;
        private final String description;

        public InstructionInfo(ExerciseInstruction instruction) {
            this.id = instruction.getId();
            this.stepOrder = instruction.getStepOrder();
            this.description = instruction.getDescription();
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
                .map(InstructionInfo::new)
                .collect(Collectors.toList());
    }
}
