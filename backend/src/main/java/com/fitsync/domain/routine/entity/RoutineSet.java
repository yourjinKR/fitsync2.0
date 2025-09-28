package com.fitsync.domain.routine.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routine_sets")
public class RoutineSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.PROTECTED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_exercise_id",  nullable = false)
    private RoutineExercise routineExercise;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "weight_kg")
    private BigDecimal weightKg;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "distance_m")
    private Integer distanceMeter;

    @Column(name = "duration_sec")
    private Integer durationSecond;

}
