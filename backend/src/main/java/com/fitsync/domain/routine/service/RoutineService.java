package com.fitsync.domain.routine.service;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.domain.routine.dto.RoutineCreateRequestDto;
import com.fitsync.domain.routine.dto.RoutineCreateResponseDto;
import com.fitsync.domain.routine.dto.RoutineDetailResponseDto;
import com.fitsync.domain.routine.entity.Routine;
import com.fitsync.domain.routine.entity.RoutineExercise;
import com.fitsync.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;

    // 루틴 생성하기
    @Transactional
    public RoutineCreateResponseDto createRoutine(RoutineCreateRequestDto requestDto) {

        Routine routine = requestDto.toEntity();
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
