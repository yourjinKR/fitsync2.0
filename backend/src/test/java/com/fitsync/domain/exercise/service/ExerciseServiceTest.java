package com.fitsync.domain.exercise.service;

import com.fitsync.domain.exercise.dto.ExerciseCreateRequestDto;
import com.fitsync.domain.exercise.dto.ExerciseDetailResponseDto;
import com.fitsync.domain.exercise.dto.ExerciseIsHiddenUpdateRequestDto;
import com.fitsync.domain.exercise.dto.ExerciseUpdateRequestDto;
import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    // 테스트용 임시 빌버
    private Exercise entity(Long id, String name, boolean hidden) {
        return Exercise.builder()
                .id(id)
                .name(name)
                .category("STRENGTH")
                .description("desc")
                .isHidden(hidden)
                .build();
    }

    @Test
    @DisplayName("createExercise: 이름 중복 시 IllegalArgumentException")
    void createExercise_duplicatedName() {
        Exercise exercise1 = entity(998L, "중복운동", false);
        Exercise exercise2 = entity(998L, "중복운동", false);
        exerciseRepository.save(exercise1);
        exerciseRepository.save(exercise2);
    }

    @Test
    @DisplayName("createExercise: 정상 생성 후 DTO 반환")
    void createExercise_ok() {
        Exercise exercise = entity(999L, "바벨 백스쿼트", false);
        System.out.println(exercise);
    }

    @Test
    @DisplayName("getAllExercises: 페이지 매핑 확인")
    void getAllExercises() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        Page<Exercise> page = new PageImpl<>(
                List.of(entity(1L, "A", false), entity(2L, "B", true)),
                pageable, 2
        );
        given(exerciseRepository.findAll(pageable)).willReturn(page);

        var result = exerciseService.getAllExercises(pageable);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("A");
        assertThat(result.getContent().get(1).isHidden()).isTrue();
    }

    @Test
    @DisplayName("getExercise: 존재하지 않으면 ResourceNotFoundException")
    void getExercise_notFound() {
        given(exerciseRepository.findByIdWithInstructions(10L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> exerciseService.getExercise(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateExercise: 엔티티 필드가 DTO 값으로 갱신된다")
    void updateExercise_ok() {
        Exercise ex = entity(5L, "Old", true);
        given(exerciseRepository.findById(5L)).willReturn(Optional.of(ex));

        ExerciseUpdateRequestDto dto = new ExerciseUpdateRequestDto();
        // 테스트 간편화를 위해 리플렉션으로 private 필드 세팅 (혹은 테스트 전용 세터를 DTO에 추가하세요)
        set(dto, "name", "New");
        set(dto, "category", "MOBILITY");
        set(dto, "description", "updated");
        set(dto, "isHidden", false);

        ExerciseDetailResponseDto res = exerciseService.updateExercise(5L, dto);

        assertThat(res.getName()).isEqualTo("New");
        assertThat(res.getCategory()).isEqualTo("MOBILITY");
        assertThat(res.isHidden()).isFalse();
    }

    @Test
    @DisplayName("inactivateExercise / activateExercise: 숨김 플래그 토글")
    void activate_inactivate() {
        Exercise ex = entity(7L, "Squat", false);
        given(exerciseRepository.findById(7L)).willReturn(Optional.of(ex));

        exerciseService.inactivateExercise(7L);
        assertThat(ex.isHidden()).isTrue();

        exerciseService.activateExercise(7L);
        assertThat(ex.isHidden()).isFalse();
    }

    @Test
    @DisplayName("inactivateExercises / activateExercises: 일괄 업데이트 쿼리 호출")
    void bulkActivateInactivate() {
        ExerciseIsHiddenUpdateRequestDto req = new ExerciseIsHiddenUpdateRequestDto();
        set(req, "exerciseIds", List.of(1L, 2L, 3L));

        exerciseService.inactivateExercises(req);
        then(exerciseRepository).should().updateHiddenStatusByIds(List.of(1L, 2L, 3L), true);

        exerciseService.activateExercises(req);
        then(exerciseRepository).should().updateHiddenStatusByIds(List.of(1L, 2L, 3L), false);
    }

    // --- 유틸: 테스트에서 private 필드 세팅 (간편용) ---
    private static void set(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
