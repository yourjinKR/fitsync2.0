package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import lombok.Getter;

/**
 * 운동 리스트 조회에서 사용될 전용 DTO이다.
 * id, name, category
 */
@Getter
public class ExerciseSimpleResponseDto {

    private final Long id;
    private final String name;
    private final String category;

    // 엔티티 -> DTO
    public ExerciseSimpleResponseDto(Exercise exercise) {
        this.id = exercise.getId();
        this.name = exercise.getName();
        this.category = exercise.getCategory();
    }
}
