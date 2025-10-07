package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.Getter;

@Getter
public class ExerciseFormResponseDto {

    private Long id;
    private String name;
    private String category;

    private MetricRequirement weightKgStatus;
    private MetricRequirement repsStatus;
    private MetricRequirement distanceMeterStatus;
    private MetricRequirement durationSecondStatus;

}
