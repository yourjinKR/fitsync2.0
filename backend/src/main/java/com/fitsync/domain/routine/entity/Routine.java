package com.fitsync.domain.routine.entity;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import com.fitsync.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "routines")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User 엔티티와 직접 연관 관계를 맺습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // User 엔티티와 직접 연관 관계를 맺습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineExercise> routineExercises = new ArrayList<>();

    // 자식 데이터 추가 메소드
    public void addExercise(RoutineExercise exercise) {
        this.routineExercises.add(exercise);
        exercise.setRoutine(this);
    }

}

