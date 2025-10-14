package com.fitsync.domain.exercise.entity;

import com.fitsync.domain.exercise.mapper.InstructionSetting;
import com.fitsync.domain.exercise.mapper.MetricRequirementSetting;
import com.fitsync.global.error.exception.ResourceConflictException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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
    }

    public void removeInstruction(ExerciseInstruction instruction) {
        this.instructions.remove(instruction);
        instruction.setExercise(null);
    }

    // 해당 운동이 어떤 값들을 지닐 수 있는지 (중량, 횟수, 거리, 시간)
    @Builder.Default
    @OneToOne(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ExerciseMetricRequirement metricRequirement = new ExerciseMetricRequirement();

    public void setRequirement(ExerciseMetricRequirement metricRequirement) {
        this.metricRequirement = metricRequirement;
        metricRequirement.setExercise(this);
    }

    // 루틴 업데이트 메소드

    public void rename(String name) { this.name = name; }
    public void recategorize(String category)  { this.category = category; }
    public void reDescription(String description) { this.description = description; }

    public void hide() {
        this.isHidden = true;
    }
    public void show() {
        this.isHidden = false;
    }

    public void updateMetricRequirement(MetricRequirementSetting metricRequirement) {
        this.metricRequirement.update(metricRequirement);
    }

    public void syncInstructions(List<InstructionSetting> settings) {
        if (settings == null) return;

        Map<Long, ExerciseInstruction> existingById = this.instructions.stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(ExerciseInstruction::getId, Function.identity()));

        // 중복 검증
        Set<Long> seen = new HashSet<>();
        for (InstructionSetting s : settings) {
            if (s.id() != null && !seen.add(s.id())) {
                throw new ResourceConflictException("중복된 instruction의 고유 ID가 존재합니다. id=" + s.id());
            }
        }

        // 다음 상태를 구성
        List<ExerciseInstruction> next = new ArrayList<>(settings.size());
        for (InstructionSetting s : settings) {
            if (s.id() == null) {
                // 신규 추가
                ExerciseInstruction created = ExerciseInstruction.builder()
                        .stepOrder(s.stepOrder())
                        .description(s.description())
                        .build();
                this.addInstruction(created);
                next.add(created);
            } else {
                // 수정 또는 잘못된 id
                ExerciseInstruction target = existingById.remove(s.id()); // 있으면 제거되어 '잔여=삭제대상' 계산됨
                if (target == null) {
                    // 정책 선택: 예외/무시/신규로 간주
                    throw new IllegalArgumentException("해당 운동과 일치하지 않는 설명입니다. id=" + s.id());
                }
                // 소속 검증(안전)
                if (target.getExercise() != this) {
                    throw new IllegalStateException("헤당 instruction 객체는 다른 운동에 속해 있습니다. id=" + s.id());
                }
                target.update(s);
                next.add(target);
            }
        }

        // 잔여 객체들 삭제
        existingById.values().forEach(this::removeInstruction);

        // displayOrder 정합
        next.sort(Comparator.comparing(ExerciseInstruction::getStepOrder, Comparator.nullsLast(Integer::compareTo)));
        for (int i = 0; i < next.size(); i++) {
            next.get(i).reorder(i + 1); // 도메인 규칙상 연속 정수로 맞추고 싶다면
        }

        // 컬렉션 교체(변경 감지 보장)
        this.instructions.clear();
        this.instructions.addAll(next);
    }
}
