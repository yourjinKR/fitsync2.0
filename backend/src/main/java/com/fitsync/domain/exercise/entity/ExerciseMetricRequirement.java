package com.fitsync.domain.exercise.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "exercise_metric_requirements")
public class ExerciseMetricRequirement {

    @Id
    @Column(name = "exercise_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_kg_status")
    private MetricRequirement weightKgStatus =  MetricRequirement.FORBIDDEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "reps_status")
    private MetricRequirement repsStatus =  MetricRequirement.FORBIDDEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "distance_m_status")
    private MetricRequirement distanceMeterStatus =  MetricRequirement.FORBIDDEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_sec_status")
    private MetricRequirement durationSecondStatus =  MetricRequirement.FORBIDDEN;

}
