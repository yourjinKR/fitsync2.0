package com.fitsync.domain.exercise.mapper;

import com.fitsync.domain.exercise.dto.ExerciseCreateRequest;
import com.fitsync.domain.exercise.dto.ExerciseDetailResponse;
import com.fitsync.domain.exercise.dto.ExerciseUpdateRequest;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import com.fitsync.domain.exercise.entity.ExerciseMetricRequirement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExerciseMapper {

    // create
    public Exercise toEntity(ExerciseCreateRequest dto) {
        return Exercise.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .isHidden(dto.isHidden())
                .build();
    }

    public ExerciseInstruction toEntity(ExerciseCreateRequest.InstructionRequest dto) {
        return ExerciseInstruction.builder()
                .description(dto.getDescription())
                .stepOrder(dto.getStepOrder())
                .build();
    }

    public ExerciseMetricRequirement toEntity(ExerciseCreateRequest.MetricRequest dto) {
        return ExerciseMetricRequirement.builder()
                .weightKgStatus(dto.getWeightKgStatus())
                .repsStatus(dto.getRepsStatus())
                .distanceMeterStatus(dto.getDistanceMeterStatus())
                .durationSecondStatus(dto.getDurationSecondStatus())
                .build();
    }


    // read detail
    public ExerciseDetailResponse toDto(Exercise entity) {
        return ExerciseDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .isHidden(entity.isHidden())
                .createdAt(entity.getCreatedAt())
                .instructions(entity.getInstructions().stream().map(this::toDto).toList())
                .metricRequirement(toDto(entity.getMetricRequirement()))
                .build();
    }

    public ExerciseDetailResponse.InstructionResponse toDto(ExerciseInstruction entity) {
        return ExerciseDetailResponse.InstructionResponse.builder()
                .id(entity.getId())
                .stepOrder(entity.getStepOrder())
                .description(entity.getDescription())
                .build();
    }

    public ExerciseDetailResponse.MetricResponse toDto(ExerciseMetricRequirement entity) {
        return ExerciseDetailResponse.MetricResponse.builder()
                .weightKgStatus(entity.getWeightKgStatus())
                .repsStatus(entity.getRepsStatus())
                .distanceMeterStatus(entity.getDistanceMeterStatus())
                .durationSecondStatus(entity.getDurationSecondStatus())
                .build();
    }


    // update
    public void applyUpdateFrom(Exercise exercise, ExerciseUpdateRequest dto) {

        exercise.rename(dto.getName());
        exercise.recategorize(dto.getCategory());
        exercise.reDescription(dto.getDescription());
        if (dto.isHidden()) exercise.show(); else exercise.hide();


        List<InstructionSetting> instructionSettings = dto.getInstructions().stream()
                .map(this::toInstructionSetting)
                .toList();
        exercise.syncInstructions(instructionSettings);


        MetricRequirementSetting setting = this.toMetricRequirement(dto.getMetricRequirement());
        exercise.updateMetricRequirement(setting);
    }

    public MetricRequirementSetting toMetricRequirement(ExerciseUpdateRequest.MetricRequest dto) {
        return new MetricRequirementSetting(
                dto.getWeightKgStatus(),
                dto.getRepsStatus(),
                dto.getDistanceMeterStatus(),
                dto.getDurationSecondStatus());
    }

    public InstructionSetting toInstructionSetting(ExerciseUpdateRequest.InstructionRequest dto) {
        return new InstructionSetting(
                dto.getId(),
                dto.getStepOrder(),
                dto.getDescription()
        );
    }
}
