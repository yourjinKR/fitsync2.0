package com.fitsync.domain.exercise.dto;

import com.fitsync.domain.exercise.entity.MetricRequirement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// TODO : 이미지 URL 추가 (추후에 진행)
@Getter
@NoArgsConstructor
public class ExerciseCreateRequest {
    
    // id 필요없음
    private String name;
    private String category;
    private String description;
    private boolean isHidden;

    private List<InstructionRequest> instructions;
    private MetricRequest metricRequirement;

    // 운동 설명
    @Getter
    @NoArgsConstructor
    public static class InstructionRequest {
        private Integer stepOrder;
        private String description;
    }

    // 운동 입력 가능 여부
    @Getter
    @NoArgsConstructor
    public static class MetricRequest {
        private MetricRequirement weightKgStatus;
        private MetricRequirement repsStatus;
        private MetricRequirement distanceMeterStatus;
        private MetricRequirement durationSecondStatus;
    }
}
