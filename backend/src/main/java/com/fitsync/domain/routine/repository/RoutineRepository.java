package com.fitsync.domain.routine.repository;

import com.fitsync.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    @Query("""
     SELECT DISTINCT r
     FROM Routine r
     LEFT JOIN FETCH r.routineExercises re
     LEFT JOIN FETCH re.exercise e
     WHERE r.id = :id
    """)
    Optional<Routine> findRoutineDetailsById(@Param("id") Long id);
}
