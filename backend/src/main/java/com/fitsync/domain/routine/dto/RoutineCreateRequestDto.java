package com.fitsync.domain.routine.dto;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.entity.RoutineSet;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class RoutineCreateRequestDto {

    // 루틴 기본정보
    private Long ownerId;
    private Long writerId;
    private String name;
    private Integer displayOrder;
    private String memo;

    // 루틴 내 운동정보
    private List<RoutineExerciseDto> exercises;

    @Getter
    @NoArgsConstructor
    // 루틴 내 운동 정보 DTO
    public static class RoutineExerciseDto {
        private Long exerciseId; // 운동정보 id만 가져옴
        private Integer displayOrder;
        private String memo;

        // 운동 내 세트 정보
        private List<RoutineSetDto> sets;

        // DTO -> Entity
        public RoutineExercise toEntity() {

            RoutineExercise exercise = RoutineExercise.builder()
                    // exercise는 서비스에서 주입
                    .displayOrder(this.displayOrder)
                    .memo(this.memo)
                    .build();

            // 세트 정보 주입
            if (this.sets != null) {
                List<RoutineSet> setEntities = this.sets.stream()
                        .map(RoutineSetDto::toEntity)
                        .toList();
                setEntities.forEach(exercise::addSet);
            }

            return exercise;
        }
    }

    @Getter
    @NoArgsConstructor
    // 운동 내 세트 정보 DTO
    public static class RoutineSetDto {
        private Integer displayOrder;
        private BigDecimal weightKg;
        private Integer reps;
        private Integer distanceMeter;
        private Integer durationSecond;

        // DTO -> Entity
        private RoutineSet toEntity() {

            return RoutineSet.builder()
                    .displayOrder(this.displayOrder)
                    .weightKg(this.weightKg)
                    .reps(this.reps)
                    .distanceMeter(this.distanceMeter)
                    .durationSecond(this.durationSecond)
                    .build();
        }
    }

    // DTO -> entity
    public Routine toEntity() {

        Routine routine = Routine.builder()
                .ownerId(this.ownerId)
                .writerId(this.writerId)
                .name(this.name)
                .displayOrder(this.displayOrder)
                .memo(this.memo)
                .build();

        if (this.exercises != null) {
            List<RoutineExercise> exerciseEntities = this.exercises.stream()
                    .map(RoutineExerciseDto::toEntity)
                    .toList();
            exerciseEntities.forEach(routine::addExercise);
        }

        return routine;
    }
}
