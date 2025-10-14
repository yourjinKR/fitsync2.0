package com.fitsync.domain.workout.entity;

import com.fitsync.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "owner_id")
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "writer_id")
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private User writer;

    @Column(name = "title", length = 120)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "routine_snapshot", columnDefinition = "jsonb")
    private Map<String, Object> routineSnapshot;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkoutExercise> workoutExercises = new ArrayList<>();

    public void addExercise(WorkoutExercise exercise) {
        workoutExercises.add(exercise);
        exercise.setWorkout(this);
    }

    public void forMe(User user) {
        this.owner = user;
        this.writer = user;
    }
}
