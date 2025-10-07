package com.fitsync.domain.exercise.mapper;

import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import com.fitsync.domain.exercise.entity.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ExerciseMapper {

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
        exercise.getMetricRequirement()
                .update(setting);
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
