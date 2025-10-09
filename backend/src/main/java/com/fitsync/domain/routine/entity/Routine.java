package com.fitsync.domain.routine.entity;

import com.fitsync.domain.routine.dto.RoutineSimpleRequest;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

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
    @OrderBy("displayOrder ASC, id ASC")
    private List<RoutineExercise> routineExercises = new ArrayList<>();

    // 자식 데이터 추가 메소드
    public void addExercise(RoutineExercise exercise) {
        this.routineExercises.add(exercise);
        exercise.setRoutine(this);
    }

    // 자신의 루틴 직접 작성
    public void forMe(User user) {
        this.owner = user;
        this.writer = user;
    }

    // PUT UPDATE
    public void updateBasic(String name, Integer displayOrder, String memo) {
        if (name != null) this.name = name;
        if (displayOrder != null) this.displayOrder = displayOrder;
        if (memo != null) this.memo = memo;
    }

    // PATCH UPDATE
    public void updateBasic(RoutineSimpleRequest requestDto) {
        if (name != null) this.name = requestDto.getName();
        if (displayOrder != null) this.displayOrder = requestDto.getDisplayOrder();
        if (memo != null) this.memo = requestDto.getMemo();
    }

    public void rename(String name) {
        this.name = name;
    }

    public void reorder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public void reMemo(String memo) {
        this.memo = memo;
    }
}

