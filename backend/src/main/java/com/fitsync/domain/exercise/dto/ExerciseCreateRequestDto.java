package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
/**
 * 관리자가 운동을 추가할때 사용하는 dto이다.
 * TODO : 이미지 URL 추가 (추후에 진행)
 */
public class ExerciseCreateRequestDto {
    
    // id 필요없음
    private String name;
    private String category;
    private String description;
    private boolean isHidden;

    private List<InstructionRequestDto> instructions;

    @Getter
    @NoArgsConstructor
    public static class InstructionRequestDto {
        // id 필요없음
        private Integer stepOrder;
        private String description;

        // Instruction DTO를 Instruction 엔티티로 변환
        public ExerciseInstruction toEntity() {
            return ExerciseInstruction.builder()
                    .stepOrder(this.stepOrder)
                    .description(this.description)
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

        return exercise;
    }


}
