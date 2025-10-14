package com.fitsync.domain.workout.repository;

import com.fitsync.domain.workout.dto.WorkoutSimpleResponse;
import com.fitsync.domain.workout.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @Query("""
        SELECT new com.fitsync.domain.workout.dto.WorkoutSimpleResponse
            (w.id, w.createdAt)
        FROM Workout w
        JOIN w.owner o
        WHERE (o.id = :ownerId)
        ORDER BY w.createdAt DESC
    """)
    List<WorkoutSimpleResponse> findMyRoutineList(Long ownerId);

    @Query("""
        SELECT w FROM Workout w
        LEFT JOIN FETCH w.workoutExercises we
        LEFT JOIN FETCH we.exercise e
        LEFT JOIN FETCH we.workoutSets ws
        WHERE w.owner.id = :ownerId
        AND w.createdAt >= :startTime
        AND w.createdAt < :endTime
        ORDER BY w.createdAt DESC
    """)
    List<Workout> findWorkoutListToday(Long ownerId, OffsetDateTime startTime, OffsetDateTime endTime);
}
