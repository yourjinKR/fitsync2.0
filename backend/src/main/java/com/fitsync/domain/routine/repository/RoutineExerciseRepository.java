package com.fitsync.domain.routine.repository;

import com.fitsync.domain.routine.entity.RoutineExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, Long> {

}
