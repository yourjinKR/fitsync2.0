package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseUpdateRequestDto {

    private String name;
    private String category;
    private String description;
    private boolean isHidden;

    private List<InstructionRequestDto> instructions;

    @Getter
    @NoArgsConstructor
    public static class InstructionRequestDto {
        private Integer stepOrder;
        private String description;
    }
}
