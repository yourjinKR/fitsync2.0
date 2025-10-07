package com.fitsync.domain.exercise.entity;

import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import com.fitsync.domain.exercise.mapper.InstructionSetting;
import com.fitsync.domain.exercise.mapper.MetricRequirementSetting;
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

    public void applyMetric(ExerciseUpdateRequestDto.MetricRequestDto dto) {
        if (dto == null) return;

        if (this.metricRequirement == null) {
            this.metricRequirement = ExerciseMetricRequirement.createFor(this); // 팩토리
        }
        this.metricRequirement.applyFrom(dto); // 제자리 갱신
    }

    public void update(Long exerciseId, ExerciseUpdateRequestDto dto) {

        this.name = dto.getName();
        this.category = dto.getCategory();
        this.description = dto.getDescription();
        this.isHidden = dto.isHidden();

        this.instructions.clear();
        System.out.println("exerciseId !!!! : " + exerciseId);

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
            this.applyMetric(dto.getMetricRequirement());
        }
    }

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
        if (settings == null) return; // PATCH 의미: 미반영

        // 1) 기존 항목을 id 기준으로 맵핑 (null id는 제외)
        Map<Long, ExerciseInstruction> existingById = this.instructions.stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(ExerciseInstruction::getId, Function.identity()));

        // 2) 중복 id 방지/검증 (옵션)
        Set<Long> seen = new HashSet<>();
        for (InstructionSetting s : settings) {
            if (s.id() != null && !seen.add(s.id())) {
                throw new IllegalArgumentException("Duplicated instruction id: " + s.id());
            }
        }

        // 3) 다음 상태를 구성
        List<ExerciseInstruction> next = new ArrayList<>(settings.size());
        for (InstructionSetting s : settings) {
            if (s.id() == null) {
                // 신규 추가
                ExerciseInstruction created = ExerciseInstruction.builder()
                        .stepOrder(s.stepOrder())
                        .description(s.description())
                        .build();
                this.addInstruction(created); // 연관 양쪽 세팅
                next.add(created);
            } else {
                // 수정 또는 잘못된 id
                ExerciseInstruction target = existingById.remove(s.id()); // 있으면 제거되어 '잔여=삭제대상' 계산됨
                if (target == null) {
                    // 정책 선택: 예외/무시/신규로 간주
                    throw new IllegalArgumentException("Instruction not found in this Exercise: id=" + s.id());
                }
                // 소속 검증(안전)
                if (target.getExercise() != this) {
                    throw new IllegalStateException("Instruction belongs to a different Exercise: id=" + s.id());
                }
                target.update(s); // 엔티티 메서드로 부분 갱신
                next.add(target);
            }
        }

        // 4) 남은 항목은 VO에 없던 기존 것 → 삭제
        existingById.values().forEach(this::removeInstruction);

        // 5) 정렬 & displayOrder 정합(optional)
        next.sort(Comparator.comparing(ExerciseInstruction::getStepOrder, Comparator.nullsLast(Integer::compareTo)));
        for (int i = 0; i < next.size(); i++) {
            next.get(i).reorder(i + 1); // 도메인 규칙상 연속 정수로 맞추고 싶다면
        }

        // 6) 컬렉션 교체(변경 감지 보장)
        this.instructions.clear();
        this.instructions.addAll(next);
    }
}
