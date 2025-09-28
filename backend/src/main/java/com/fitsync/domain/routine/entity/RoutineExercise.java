package com.fitsync.domain.routine.entity;

import com.fitsync.domain.exercise.entity.Exercise;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routine_exercises")
public class RoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.PROTECTED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    // 단방향 연관관계
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    // 1 : N
    @Builder.Default
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "routineExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineSet> sets = new ArrayList<>();

    public void addSet(RoutineSet routineSet) {
        this.sets.add(routineSet);
        routineSet.setRoutineExercise(this);
    }

}
