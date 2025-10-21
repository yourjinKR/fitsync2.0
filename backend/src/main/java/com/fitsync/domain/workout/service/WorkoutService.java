package com.fitsync.domain.workout.service;

import com.fitsync.domain.exercise.entity.Exercise;
import com.fitsync.domain.exercise.mapper.ExerciseMapper;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import com.fitsync.domain.user.entity.User;
import com.fitsync.domain.user.entity.UserType;
import com.fitsync.domain.workout.dto.WorkoutCreateRequest;
import com.fitsync.domain.workout.dto.WorkoutDetailResponse;
import com.fitsync.domain.workout.dto.WorkoutSimpleResponse;
import com.fitsync.domain.workout.dto.WorkoutUpdateRequest;
import com.fitsync.domain.workout.entity.Workout;
import com.fitsync.domain.workout.entity.WorkoutExercise;
import com.fitsync.domain.workout.entity.WorkoutSet;
import com.fitsync.domain.workout.mapper.WorkoutMapper;
import com.fitsync.domain.workout.repository.WorkoutRepository;
import com.fitsync.global.error.exception.ResourceNotFoundException;
import com.fitsync.global.error.exception.UnauthorizedAccessException;
import com.fitsync.global.util.LoginUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper;
    private final LoginUserProvider loginUserProvider;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    // create
    @Transactional
    public Long createWorkout(WorkoutCreateRequest requestDto) {
        Workout workout;

        User currentUser = loginUserProvider.getCurrentUser();
        if (!Objects.equals(currentUser.getId(), requestDto.getOwnerId())) {
            throw new UnauthorizedAccessException("운동을 기록할 권한이 없습니다. ");
        }
        else {
            workout = workoutMapper.toEntity(requestDto);
            workout.forMe(currentUser);
        }

        if (requestDto.getWorkoutExercises() != null) {
            for (WorkoutCreateRequest.WorkoutExerciseRequest exerciseDto : requestDto.getWorkoutExercises()) {
                Exercise exercise = exerciseRepository.getReferenceById(exerciseDto.getExerciseId());

                WorkoutExercise workoutExercise = workoutMapper.toEntity(exerciseDto);
                workoutExercise.selectExercise(exercise);

                if (exerciseDto.getWorkoutSets() != null) {
                    for (WorkoutCreateRequest.WorkoutSetRequest setDto : exerciseDto.getWorkoutSets()) {

                        WorkoutSet workoutSet = workoutMapper.toEntity(setDto);

                        workoutExercise.addSet(workoutSet);
                    }
                }

                workout.addExercise(workoutExercise);
            }
        }

        Workout savedWorkout = workoutRepository.save(workout);

        return savedWorkout.getId();
    }

    // read
    @Transactional
    public WorkoutDetailResponse getWorkoutById(Long id) {

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 운동기록을 찾지 못함, id : " + id));

        return workoutMapper.toDetailDto(workout);
    }

    // read simple list
    @Transactional
    public List<WorkoutSimpleResponse> getMyWorkoutList(Long userId) {

        return workoutRepository.findMyRoutineList(userId);
    }

    // read today
    @Transactional
    public List<WorkoutDetailResponse> getMyWorkoutToday(Long userId) {

        ZoneOffset userOffset = ZoneOffset.of("+09:00");
        LocalDate today = LocalDate.now(userOffset);

        OffsetDateTime startTime = today.atStartOfDay().atOffset(userOffset);
        OffsetDateTime endTime = today.plusDays(1).atStartOfDay().atOffset(userOffset);

        List<Workout> entities = workoutRepository.findWorkoutListToday(userId, startTime, endTime);

        return entities.stream()
                .map(workoutMapper::toDetailDto)
                .toList();
    }

    // update (제목 및 메모는 수정 가능)
    @Transactional
    public void updateWorkout(Long workoutId, WorkoutUpdateRequest requestDto) {

        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 운동기록을 찾지 못함, id : " + workoutId));

        workout.updateBasic(requestDto.getTitle(), requestDto.getMemo());
    }

    // delete (일반 사용자는 권한 없음)
    @Transactional
    public void deleteWorkout(Long workoutId) {
        User currUser = loginUserProvider.getCurrentUser();
        UserType userType = currUser.getType();

        if (!userType.equals(UserType.ADMIN)) {
            throw new UnauthorizedAccessException("운동을 삭제할 권한이 없습니다.");
        }

        Workout refWorkout = workoutRepository.getReferenceById(workoutId);
        workoutRepository.deleteById(refWorkout.getId());
    }
}

