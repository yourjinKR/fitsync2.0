package com.fitsync.domain.routine.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.MetricRequirement;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.entity.RoutineSet;
import com.fitsync.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineDetailResponseDto {

    // 루틴 기본정보
    private Long id;
    private Long ownerId;
    private Long writerId;
    private String name;
    private Integer displayOrder;
    private String memo;

    // 루틴 내 운동정보
    private List<RoutineExerciseDto> exercises;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 루틴 내 운동 정보 DTO
    public static class RoutineExerciseDto {
        // 상세보기 응답 DTO에는 운동에 대한 정보가 필요함 (예: Exercise.name... 등등)
        private Long id;
        private Integer displayOrder;
        private String memo;

        private Long exerciseId;
        private String exerciseName;
        private String exerciseCategory;
        private MetricRequirement weightKgStatus;
        private MetricRequirement repsStatus;
        private MetricRequirement distanceMeterStatus;
        private MetricRequirement durationSecondStatus;

        // 운동 내 세트 정보
        private List<RoutineSetDto> sets;

        // 엔티티 -> DTO
        public RoutineExerciseDto(RoutineExercise routineExercise) {
            this.id = routineExercise.getId();
            this.displayOrder = routineExercise.getDisplayOrder();
            this.memo = routineExercise.getMemo();

            // 하위 요소도 변환
            this.sets = routineExercise.getSets().stream()
                    .map(RoutineSetDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @NoArgsConstructor
    // 운동 자체 정보
    public static class ExerciseSummaryDto {
        private Long id;
        private String name;
        private String category;

        // 루틴을 볼때 어떤게 값이 입력 불가인지 알 수 있어야 함
        private MetricRequirement weightKgStatus;
        private MetricRequirement repsStatus;
        private MetricRequirement distanceMeterStatus;
        private MetricRequirement durationSecondStatus;

        public ExerciseSummaryDto(Exercise exercise) {
            this.id = exercise.getId();
            this.name = exercise.getName();
            this.category = exercise.getCategory();

            this.weightKgStatus = exercise.getMetricRequirement().getWeightKgStatus();
            this.repsStatus = exercise.getMetricRequirement().getRepsStatus();
            this.distanceMeterStatus = exercise.getMetricRequirement().getDistanceMeterStatus();
            this.durationSecondStatus = exercise.getMetricRequirement().getDurationSecondStatus();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 운동 내 세트 정보 DTO
    public static class RoutineSetDto {
        private Long id;
        private Integer displayOrder;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;

        // 엔티티 -> DTO
        public RoutineSetDto(RoutineSet routineSet) {
            this.id = routineSet.getId();
            this.displayOrder = routineSet.getDisplayOrder();
            this.weightKg = routineSet.getWeightKg();
            this.reps = routineSet.getReps();
            this.distanceMeter = routineSet.getDistanceMeter();
            this.durationSecond = routineSet.getDurationSecond();
        }
    }

    // 생성자 엔티티 -> DTO
    public RoutineDetailResponseDto(Routine routine) {
        this.id = routine.getId();
        this.name = routine.getName();
        this.displayOrder = routine.getDisplayOrder();
        this.memo = routine.getMemo();

        // 하위 요소도 변환
        this.exercises = routine.getRoutineExercises().stream()
                .map(RoutineExerciseDto::new)
                .collect(Collectors.toList());
    }
}
