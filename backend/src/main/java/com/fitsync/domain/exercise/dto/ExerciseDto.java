package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseDto {

    private String name;
    private String category;
    private String description;

    private List<Instruction> instructions;

    @Getter
    @NoArgsConstructor
    public static class Instruction {
        private Integer stepOrder;
        private String description;
    }

    public Exercise toEntity(ExerciseDto dto) {
        return Exercise.builder()
                .name(dto.name)
                .category(dto.category)
                .description(dto.description)
                .build();
    }
}
