package com.fitsync.domain.workout.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "workout_sets")
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_exercise_id")
    private WorkoutExercise workoutExercise;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "distance_m")
    private Integer distanceM;

    @Column(name = "duration_sec")
    private Integer durationSec;

    public void attachTo(WorkoutExercise workoutExercise) {
        this.workoutExercise = workoutExercise;
        if (workoutExercise != null && !workoutExercise.getWorkoutSets().contains(this)) {
            workoutExercise.getWorkoutSets().add(this);
        }
    }
}
