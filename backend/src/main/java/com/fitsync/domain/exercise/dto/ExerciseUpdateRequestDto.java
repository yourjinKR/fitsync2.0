package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Data
public class ExerciseUpdateRequestDto {

    private String name;
    private String category;
    private String description;
    private boolean isHidden;

    private List<InstructionRequestDto> instructions;

    @Getter
    @NoArgsConstructor
    public static class InstructionRequestDto {
        private Long id;
        private Integer stepOrder;
        private String description;
    }

    @JsonProperty("isHidden")
    public boolean isHidden() {
        return this.isHidden;
    }
}
