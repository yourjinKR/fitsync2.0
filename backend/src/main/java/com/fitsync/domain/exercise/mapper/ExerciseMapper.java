package com.fitsync.domain.exercise.mapper;

import com.fitsync.domain.exercise.dto.ExerciseCreateRequestDto;
import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import com.fitsync.domain.exercise.entity.ExerciseMetricRequirement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExerciseMapper {

    // create
    public Exercise toEntity(ExerciseCreateRequestDto dto) {

        List<ExerciseInstruction> instructions = dto.getInstructions().stream()
                .map(this::toEntity)
                .toList();

        return Exercise.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .isHidden(dto.isHidden())
                .instructions(instructions)
                .metricRequirement(toEntity(dto.getMetricRequirement()))
                .build();
    }

    public ExerciseInstruction toEntity(ExerciseCreateRequestDto.InstructionRequestDto dto) {
        return ExerciseInstruction.builder()
                .description(dto.getDescription())
                .stepOrder(dto.getStepOrder())
                .build();
    }

    public ExerciseMetricRequirement toEntity(ExerciseCreateRequestDto.MetricRequestDto dto) {
        return ExerciseMetricRequirement.builder()
                .weightKgStatus(dto.getWeightKgStatus())
                .repsStatus(dto.getRepsStatus())
                .distanceMeterStatus(dto.getDistanceMeterStatus())
                .durationSecondStatus(dto.getDurationSecondStatus())
                .build();
    }


    // update
    public void applyUpdateFrom(Exercise exercise, ExerciseUpdateRequestDto dto) {

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

    public MetricRequirementSetting toMetricRequirement(ExerciseUpdateRequestDto.MetricRequestDto dto) {
        return new MetricRequirementSetting(
                dto.getWeightKgStatus(),
                dto.getRepsStatus(),
                dto.getDistanceMeterStatus(),
                dto.getDurationSecondStatus());
    }

    public InstructionSetting toInstructionSetting(ExerciseUpdateRequestDto.InstructionRequestDto dto) {
        return new InstructionSetting(
                dto.getId(),
                dto.getStepOrder(),
                dto.getDescription()
        );
    }
}
