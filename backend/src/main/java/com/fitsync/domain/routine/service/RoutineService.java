package com.fitsync.domain.routine.service;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.domain.routine.dto.RoutineCreateRequestDto;
import com.fitsync.domain.routine.dto.RoutineCreateResponseDto;
import com.fitsync.domain.routine.dto.RoutineDetailResponseDto;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.entity.RoutineSet;
import com.fitsync.domain.routine.repository.RoutineRepository;
import com.fitsync.domain.user.entity.User;
import com.fitsync.global.util.LoginUserProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final LoginUserProvider loginUserProvider;

    // 루틴 생성하기
    @Transactional
    public RoutineCreateResponseDto createRoutine(RoutineCreateRequestDto requestDto) {

        // 1. 현재 사용자 조회
        User currentUser = loginUserProvider.getCurrentUser();

        // 2. 최상위 Routine 엔티티 생성
        Routine routine = Routine.builder()
                .owner(currentUser)
                .writer(currentUser)
                .name(requestDto.getName())
                .displayOrder(requestDto.getDisplayOrder())
                .memo(requestDto.getMemo())
                .build();

        // 3. DTO의 운동 목록을 순회하며 RoutineExercise 엔티티 생성 및 연결
        if (requestDto.getExercises() != null) {
            for (RoutineCreateRequestDto.RoutineExerciseDto exerciseDto : requestDto.getExercises()) {

                // 3-1. exerciseId로 Exercise 엔티티 조회
                Exercise exercise = exerciseRepository.findById(exerciseDto.getExerciseId())
                        .orElseThrow(() -> new EntityNotFoundException("운동 정보를 찾을 수 없습니다: " + exerciseDto.getExerciseId()));

                // 3-2. RoutineExercise 엔티티 생성
                RoutineExercise routineExercise = RoutineExercise.builder()
                        .exercise(exercise) // ⬅️ 조회한 Exercise 엔티티 설정
                        .displayOrder(exerciseDto.getDisplayOrder())
                        .memo(exerciseDto.getMemo())
                        .build();

                // 3-3. DTO의 세트 목록을 순회하며 RoutineSet 엔티티 생성 및 연결
                if (exerciseDto.getSets() != null) {
                    for (RoutineCreateRequestDto.RoutineSetDto setDto : exerciseDto.getSets()) {
                        RoutineSet routineSet = RoutineSet.builder()
                                .displayOrder(setDto.getDisplayOrder())
                                .weightKg(setDto.getWeightKg())
                                .reps(setDto.getReps())
                                .distanceMeter(setDto.getDistanceMeter())
                                .durationSecond(setDto.getDurationSecond())
                                // ... 나머지 세트 정보 ...
                                .build();
                        routineExercise.addSet(routineSet); // RoutineExercise에 세트 추가
                    }
                }
                routine.addExercise(routineExercise); // Routine에 RoutineExercise 추가
            }
        }

        // 4. 최상위 엔티티인 Routine 저장 (Cascade 옵션으로 자식 엔티티들도 함께 저장됨)
        Routine savedRoutine = routineRepository.save(routine);

        return new RoutineCreateResponseDto(savedRoutine.getId());
    }

    // 루틴 확인
    @Transactional(readOnly = true)
    public RoutineDetailResponseDto getRoutine(Long id) {

        Routine routine = routineRepository.findRoutineDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴을 찾지 못함 : " + id));

        return new RoutineDetailResponseDto(routine);
    }
}
