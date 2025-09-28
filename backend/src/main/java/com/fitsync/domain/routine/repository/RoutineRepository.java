package com.fitsync.domain.routine.repository;

import com.fitsync.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    @Query("SELECT r FROM Routine r JOIN FETCH r.routineExercises re JOIN FETCH re.sets JOIN FETCH re.exercise WHERE r.id = :id")
    public Optional<Routine> findRoutineDetailsById(@Param("id") Long id);
}
