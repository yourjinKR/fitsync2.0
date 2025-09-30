package com.fitsync.domain.routine.service;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.domain.routine.dto.*;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.entity.RoutineSet;
import com.fitsync.domain.routine.repository.RoutineRepository;
import com.fitsync.domain.user.entity.User;
import com.fitsync.global.error.exception.BadRequestException;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import com.fitsync.global.error.exception.UnauthorizedAccessException;
import com.fitsync.global.util.LoginUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                        .orElseThrow(() -> new ResourceNotFoundException("운동 정보를 찾을 수 없습니다: " + exerciseDto.getExerciseId()));

                // 3-2. RoutineExercise 엔티티 생성
                RoutineExercise routineExercise = RoutineExercise.builder()
                        .exercise(exercise) // 조회한 Exercise 엔티티 설정
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

    // 사용자가 자신의 루틴 목록을 확인
    @Transactional
    public Page<RoutineSimpleResponseDto> getMyRoutineList(Long ownerId, Pageable  pageable) {

        return routineRepository.findMyRoutineList(ownerId, pageable);
    }

    // 루틴 확인
    @Transactional(readOnly = true)
    public RoutineDetailResponseDto getRoutine(Long id) {

        Routine routine = routineRepository.findRoutineDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 루틴을 찾지 못함 : " + id));

        return new RoutineDetailResponseDto(routine);
    }

    // 루틴 삭제
    @Transactional
    public void deleteRoutine(Long id, RoutineDeleteRequestDto requestDto) {

        if (!id.equals(requestDto.getId())) {
            throw new BadRequestException("id가 일치하지 않습니다.");
        }

        Routine routine = routineRepository.findRoutineDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 루틴을 찾지 못함 : " + id));

        User currentUser = loginUserProvider.getCurrentUser();

        if (currentUser.getId().equals(requestDto.getOwnerId())) {
            routineRepository.deleteById(id);
        } else {
            // 삭제 권한이 없는 에러 처리
            throw new UnauthorizedAccessException("해당 루틴을 삭제할 권한이 없습니다. ");
        }

    }

    // 루틴 기본정보 수정
    @Transactional
    public void updateRoutineHeader(Long id, RoutineSimpleRequestDto requestDto) {

        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 루틴을 찾지 못함 : " + id));

        routine.updateBasic(requestDto);
    }

    // 루틴 순서 정렬
    @Transactional
    public void sortRoutine(List<RoutineSimpleRequestDto> requestDtos) {

        // 각각의 id로 entity 조회 후 업데이트
        requestDtos.forEach(dto -> {
            Long id = dto.getId();
            Routine routine = routineRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("해당 루틴을 찾지 못함 : " + id));

            routine.updateBasic(dto);
        });

    }


    /**
     * 루틴 수정: 기본정보 + 운동목록 + 세트목록 동기화(추가/수정/삭제)
     * - requestDto.routineExercises 가 null 이면 컬렉션 동기화는 생략(기존 유지)
     * - 각 하위 컬렉션의 sets 가 null 이면 해당 세트는 유지(동기화 생략)
     */
    @Transactional
    public RoutineDetailResponseDto updateRoutine(Long id, RoutineUpdateRequestDto requestDto) {
        // 1) 루틴 + 운동까지 로딩 (세트는 @BatchSize 로 효율적 로딩)
        Routine routine = routineRepository.findRoutineDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 루틴을 찾지 못했습니다. id=" + id));

        // 2) 루틴 기본정보 업데이트 (null 값은 무시)
        routine.updateBasic(
                requestDto.getName(),
                requestDto.getDisplayOrder(),
                requestDto.getMemo()
        );

        // 3) 운동/세트 동기화 (요청이 있을 때만)
        if (requestDto.getRoutineExercises() != null) {
            syncRoutineExercises(routine, requestDto.getRoutineExercises());
        }

        // 4) 트랜잭션 종료 시 플러시 → 변경사항 반영
        return new RoutineDetailResponseDto(routine);
    }

    // ===== 내부 동기화 로직 =====

    private void syncRoutineExercises(Routine routine, List<RoutineUpdateRequestDto.RoutineExerciseDto> reqExercises) {
        // 기존 엔티티 맵핑(빠른 조회용)
        Map<Long, RoutineExercise> existingMap = routine.getRoutineExercises().stream()
                .filter(re -> re.getId() != null)
                .collect(Collectors.toMap(RoutineExercise::getId, Function.identity()));

        // 요청에 포함된 exerciseId를 모아 한 번에 로딩(성능 최적화)
        Set<Long> neededExerciseIds = reqExercises.stream()
                .map(RoutineUpdateRequestDto.RoutineExerciseDto::getExerciseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Exercise> exerciseRef = loadExercisesAsMap(neededExerciseIds);

        // DTO에 존재하는 id 모음(삭제 판단용)
        Set<Long> requestIds = reqExercises.stream()
                .map(RoutineUpdateRequestDto.RoutineExerciseDto::getId)
                .filter(Objects::nonNull) // id가 null이 아닌 데이터 (기존/수정)
                .collect(Collectors.toSet());

        // 3-1) 삭제: 엔티티에는 있으나 DTO에는 없는 항목 제거(orphanRemoval에 의해 삭제됨)
        routine.getRoutineExercises().removeIf(re ->
                re.getId() != null && !requestIds.contains(re.getId())
        );

        // 3-2) 추가/수정
        for (RoutineUpdateRequestDto.RoutineExerciseDto dto : reqExercises) {
            if (dto.getId() == null) {
                // 추가
                RoutineExercise newRe = buildRoutineExercise(dto, exerciseRef);
                routine.addExercise(newRe);

                // 세트 동기화(신규는 전부 추가)
                if (dto.getSets() != null) {
                    for (RoutineUpdateRequestDto.RoutineSetDto s : dto.getSets()) {
                        RoutineSet newSet = buildRoutineSet(s);
                        newRe.addSet(newSet);
                    }
                }
            } else {
                // 수정
                RoutineExercise target = existingMap.get(dto.getId());
                if (target == null) {
                    throw new ResourceNotFoundException("수정 대상 RoutineExercise 를 찾지 못했습니다. id=" + dto.getId());
                }

                Exercise newExercise = null;
                if (dto.getExerciseId() != null) {
                    newExercise = exerciseRef.get(dto.getExerciseId());
                    if (newExercise == null) {
                        throw new ResourceNotFoundException("운동 정보를 찾지 못했습니다. exerciseId=" + dto.getExerciseId());
                    }
                }

                target.updateBasic(
                        newExercise,                // null이면 기존 운동 유지
                        dto.getDisplayOrder(),
                        dto.getMemo()
                );

                // 세트 동기화(요청이 있을 때만)
                if (dto.getSets() != null) {
                    syncRoutineSets(target, dto.getSets());
                }
            }
        }
    }

    // TODO : 이해하기
    private void syncRoutineSets(RoutineExercise parent, List<RoutineUpdateRequestDto.RoutineSetDto> reqSets) {
        Map<Long, RoutineSet> existingMap = parent.getSets().stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(RoutineSet::getId, Function.identity()));

        Set<Long> reqIds = reqSets.stream()
                .map(RoutineUpdateRequestDto.RoutineSetDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 삭제
        parent.getSets().removeIf(s ->
                s.getId() != null && !reqIds.contains(s.getId())
        );

        // 추가/수정
        for (RoutineUpdateRequestDto.RoutineSetDto s : reqSets) {
            if (s.getId() == null) {
                RoutineSet newSet = buildRoutineSet(s);
                parent.addSet(newSet);
            } else {
                RoutineSet target = existingMap.get(s.getId());
                if (target == null) {
                    throw new ResourceNotFoundException("수정 대상 RoutineSet 을 찾지 못했습니다. id=" + s.getId());
                }
                target.updateBasic(
                        s.getDisplayOrder(),
                        s.getWeightKg(),
                        s.getReps(),
                        s.getDistanceMeter(),
                        s.getDurationSecond()
                );
            }
        }
    }

    // ===== 팩토리/헬퍼 =====

    // DTO -> Entity
    private RoutineExercise buildRoutineExercise(
            RoutineUpdateRequestDto.RoutineExerciseDto dto,
            Map<Long, Exercise> exerciseRef) {

        if (dto.getExerciseId() == null) {
            throw new ResourceNotFoundException("신규 RoutineExercise 생성 시 exerciseId 는 필수입니다.");
        }
        Exercise exercise = exerciseRef.get(dto.getExerciseId());
        if (exercise == null) {
            throw new ResourceNotFoundException("운동 정보를 찾지 못했습니다. exerciseId=" + dto.getExerciseId());
        }
        return RoutineExercise.builder()
                .exercise(exercise)
                .displayOrder(nullSafe(dto.getDisplayOrder(), 0))
                .memo(dto.getMemo())
                .build();
    }

    // DTO -> Entity
    private RoutineSet buildRoutineSet(RoutineUpdateRequestDto.RoutineSetDto s) {
        return RoutineSet.builder()
                .displayOrder(nullSafe(s.getDisplayOrder(), 0))
                .weightKg(s.getWeightKg())
                .reps(s.getReps())
                .distanceMeter(s.getDistanceMeter())
                .durationSecond(s.getDurationSecond())
                .build();
    }

    // ID 목록을 받고 Map으로 변환
    private Map<Long, Exercise> loadExercisesAsMap(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return exerciseRepository.findAllById(ids).stream()
                // identity는 i -> i와 동일, 가독성/일관성을 위해 사용
                .collect(Collectors.toMap(Exercise::getId, Function.identity()));
    }

    // 기본값 타입 강화
    private int nullSafe(Integer v, int defaultVal) {
        return v != null ? v : defaultVal;
    }
}
