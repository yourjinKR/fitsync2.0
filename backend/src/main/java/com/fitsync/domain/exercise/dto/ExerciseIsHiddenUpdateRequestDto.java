package com.fitsync.domain.exercise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseIsHiddenUpdateRequestDto {

    private List<Long> exerciseIds;
}
