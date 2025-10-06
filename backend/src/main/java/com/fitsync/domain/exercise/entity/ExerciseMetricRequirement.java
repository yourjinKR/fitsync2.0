package com.fitsync.domain.exercise.entity;

import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exercise_metric_requirements")
public class ExerciseMetricRequirement {

    @Id
    @Column(name = "exercise_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "weight_kg_status")
    private MetricRequirement weightKgStatus =  MetricRequirement.FORBIDDEN;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "reps_status")
    private MetricRequirement repsStatus =  MetricRequirement.FORBIDDEN;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "distance_m_status")
    private MetricRequirement distanceMeterStatus =  MetricRequirement.FORBIDDEN;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_sec_status")
    private MetricRequirement durationSecondStatus =  MetricRequirement.FORBIDDEN;

    void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public static ExerciseMetricRequirement createFor(Exercise exercise) {
        ExerciseMetricRequirement exerciseMetricRequirement = new ExerciseMetricRequirement();
        exerciseMetricRequirement.setExercise(exercise);
        return exerciseMetricRequirement;
    }

    public void applyFrom(ExerciseUpdateRequestDto.MetricRequestDto requestDto) {
        this.setWeightKgStatus(requestDto.getWeightKgStatus());
        this.setRepsStatus(requestDto.getRepsStatus());
        this.setDistanceMeterStatus(requestDto.getDistanceMeterStatus());
        this.setDurationSecondStatus(requestDto.getDurationSecondStatus());
    }

}
