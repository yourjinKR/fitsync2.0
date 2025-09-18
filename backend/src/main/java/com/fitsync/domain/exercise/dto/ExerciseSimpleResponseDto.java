package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private final boolean isHidden;

    // 엔티티 -> DTO
    public ExerciseSimpleResponseDto(Exercise exercise) {
        this.id = exercise.getId();
        this.name = exercise.getName();
        this.category = exercise.getCategory();
        this.isHidden = exercise.isHidden();
    }

    //  Lombok이 생성하는 isHidden() 메소드 대신,
    //  직접 getter를 만들고 @JsonProperty를 붙여줍니다.
    @JsonProperty("isHidden")
    public boolean isHidden() {
        return this.isHidden;
    }
}
