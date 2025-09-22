package com.fitsync.domain.exercise.entity;

import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

//    @Lob // 왜 사용하지 않는가?
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
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


    /**
     * 운동정보 비활성화 메소드 (삭제처럼 사용)
     */
    public void hide() {
        this.isHidden = true;
    }

    /**
     * 운동정보 활성화 메소드
     */
    public void show() {
        this.isHidden = false;
    }


























}
