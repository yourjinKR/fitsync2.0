package com.fitsync.domain.routine.repository;

import com.fitsync.domain.routine.dto.RoutineSimpleResponseDto;
import com.fitsync.domain.routine.entity.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
       select new com.fitsync.domain.routine.dto.RoutineSimpleResponseDto(
           r.id, o.id, w.id, r.name, r.displayOrder, r.memo, r.createdAt, r.updatedAt
       )
       from Routine r
       join r.owner o
       join r.writer w
       where (:ownerId is null or o.id = :ownerId)
    """)
    Page<RoutineSimpleResponseDto> findMyRoutineList(
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
}
