package com.fitsync.domain.exercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseIsHiddenUpdateRequestDto {

    private List<Long> exerciseIds;
}
