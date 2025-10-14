package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExerciseSimpleResponse {

    private Long id;
    private String name;
    private String category;
    private boolean isHidden;

    @JsonProperty("isHidden")
    public boolean isHidden() {
        return this.isHidden;
    }
}
