package com.fitsync.domain.exercise.dto;

import lombok.Getter;

@Getter
public class ExerciseIsHiddenBatchUpdateRequest {
    private ExerciseIsHiddenUpdateRequest activate;
    private ExerciseIsHiddenUpdateRequest deactivate;
}
