package com.fitsync.domain.workout.entity;

import com.fitsync.domain.exercise.entity.Exercise;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "workout_exercises")
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "exercise_name", length = 120) // 스냅샷용
    private String exerciseName;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @OneToMany(mappedBy = "workoutExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkoutSet> workoutSets = new ArrayList<>();

    public void addSet(WorkoutSet set) {
        workoutSets.add(set);
        set.setWorkoutExercise(this);
    }

    public void selectExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}
