package com.fitsync.domain.exercise.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ExerciseIsHiddenUpdateRequest {

    private List<Long> exerciseIds;
}
