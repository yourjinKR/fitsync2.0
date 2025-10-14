package com.fitsync.domain.exercise.mapper;

import com.fitsync.domain.exercise.entity.MetricRequirement;

public record MetricRequirementSetting(
        MetricRequirement weightKgStatus,
        MetricRequirement repsStatus,
        MetricRequirement distanceMeterStatus,
        MetricRequirement durationSecondStatus
) {
}
