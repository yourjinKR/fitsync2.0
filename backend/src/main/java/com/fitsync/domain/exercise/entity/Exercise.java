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

    public void addInstruction(ExerciseInstruction instruction) {
        this.instructions.add(instruction);
        instruction.setExercise(this);
        this.updatedAt = OffsetDateTime.now();
    }

    public void removeInstruction(ExerciseInstruction instruction) {
        this.instructions.remove(instruction);
        instruction.setExercise(null);
        this.updatedAt = OffsetDateTime.now();
    }

    // 해당 운동이 어떤 값들을 지닐 수 있는지 (중량, 횟수, 거리, 시간)
    @Builder.Default
    @OneToOne(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private ExerciseMetricRequirement metricRequirement = new ExerciseMetricRequirement();

    public void setRequirement(ExerciseMetricRequirement metricRequirement) {
        this.metricRequirement = metricRequirement;
        metricRequirement.setExercise(this);
    }

    public void update(ExerciseUpdateRequestDto dto) {

        this.name = dto.getName();
        this.category = dto.getCategory();
        this.description = dto.getDescription();
        this.isHidden = dto.isHidden();

        this.instructions.clear();
        Long metricId = this.metricRequirement.getId();

        if (dto.getInstructions() != null) {
            dto.getInstructions().forEach(inst -> {
                ExerciseInstruction instruction = ExerciseInstruction.builder()
                        .stepOrder(inst.getStepOrder())
                        .description(inst.getDescription())
                        .build();
                this.addInstruction(instruction);
            });
        }

        if (dto.getMetricRequirement() != null) {
            ExerciseUpdateRequestDto.MetricRequestDto metricDto = dto.getMetricRequirement();

            ExerciseMetricRequirement newMetric = ExerciseMetricRequirement.builder()
                    .id(metricId)
                    .weightKgStatus(metricDto.getWeightKgStatus())
                    .repsStatus(metricDto.getRepsStatus())
                    .distanceMeterStatus(metricDto.getDistanceMeterStatus())
                    .durationSecondStatus(metricDto.getDurationSecondStatus())
                    .build();

            this.setRequirement(newMetric);
        }
    }


    public void hide() {
        this.isHidden = true;
    }

    public void show() {
        this.isHidden = false;
    }
}
