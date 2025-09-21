package com.fitsync.domain.exercise.repository;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseInstructionRepository instructionRepository;

    private Exercise sampleExercise(String name, boolean hidden) {
        return Exercise.builder()
                .name(name)
                .category("STRENGTH")
                .description("desc")
                .isHidden(hidden)
                .build();
    }

    private ExerciseInstruction step(int order, String desc) {
        return ExerciseInstruction.builder()
                .stepOrder(order)
                .description(desc)
                .build();
    }

    @Test
    @DisplayName("findByIdWithInstructions: 지연로딩 없이 instruction을 함께 조회한다")
    void findByIdWithInstructions_joinFetch() {
        // given
        Exercise e = sampleExercise("Bench Press", false);
        e.addInstruction(step(1, "벤치에 눕는다"));
        e.addInstruction(step(2, "바를 내린다"));
        Exercise saved = exerciseRepository.save(e);

        // when
        Optional<Exercise> foundOpt = exerciseRepository.findByIdWithInstructions(saved.getId());

        // then
        assertThat(foundOpt).isPresent();
        Exercise found = foundOpt.get();
        assertThat(found.getInstructions()).hasSize(2);
        assertThat(found.getInstructions().get(0).getStepOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("existsByName: 동일 이름 존재 여부 확인")
    void existsByName() {
        exerciseRepository.save(sampleExercise("Squat", false));
        boolean exists = exerciseRepository.existsByName("Squat");
        boolean notExists = exerciseRepository.existsByName("Deadlift");
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("updateHiddenStatusByIds: 여러 ID의 isHidden 값을 일괄 업데이트한다")
    void updateHiddenStatusByIds() {
        Exercise e1 = exerciseRepository.save(sampleExercise("Curl", false));
        Exercise e2 = exerciseRepository.save(sampleExercise("Row", false));
        Exercise e3 = exerciseRepository.save(sampleExercise("Press", false));

        exerciseRepository.updateHiddenStatusByIds(List.of(e1.getId(), e3.getId()), true);

        assertThat(exerciseRepository.findById(e1.getId()).orElseThrow().isHidden()).isTrue();
        assertThat(exerciseRepository.findById(e2.getId()).orElseThrow().isHidden()).isFalse();
        assertThat(exerciseRepository.findById(e3.getId()).orElseThrow().isHidden()).isTrue();
    }

    @Test
    @DisplayName("orphanRemoval: 부모에서 instruction을 제거하면 자식이 삭제된다")
    void orphanRemoval() {
        Exercise e = sampleExercise("Pulldown", false);
        e.addInstruction(step(1, "그립 잡기"));
        e.addInstruction(step(2, "당기기"));
        Exercise saved = exerciseRepository.save(e);
        Long childId = saved.getInstructions().get(0).getId();

        // 부모에서 전부 제거
        saved.getInstructions().clear();
        exerciseRepository.flush();

        assertThat(instructionRepository.findById(childId)).isEmpty();
    }
}
