package com.fitsync.domain.exercise.entity;

import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean isHidden;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseInstruction> instructions = new ArrayList<>();

    // 자식 데이터 추가 메소드
    public void addInstruction(ExerciseInstruction instruction) {
        this.instructions.add(instruction);
        instruction.setExercise(this);
    }

    /**
     * 특정 운동정보를 수정하는 메소드<br>
     * <p>
     *  해당 로직 내에 id가 없음에도 불구하고
     *  특정 운동을 수정할 수 있는 이유는<br>
     *  사전에 findById()메소드를 통해 id를 찾고
     *  해당 엔티티를 수정하기 때문이다.<br>
     *  추가로 자식 요소들은 삭제 후 다시 생성함.
     * </p>
     * @param dto ExerciseUpdateRequestDto
     */
    public void update(ExerciseUpdateRequestDto dto) {

        this.name = dto.getName();
        this.category = dto.getCategory();
        this.description = dto.getDescription();
        this.isHidden = dto.isHidden();

        this.instructions.clear();

        if (dto.getInstructions() != null) {
            dto.getInstructions().forEach(inst -> {
                ExerciseInstruction instruction = ExerciseInstruction.builder()
                        .stepOrder(inst.getStepOrder())
                        .description(inst.getDescription())
                        .build();
                this.addInstruction(instruction);
            });

        }
    }



























}
