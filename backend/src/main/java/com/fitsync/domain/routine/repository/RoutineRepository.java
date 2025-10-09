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

    // TODO : 해당쿼리문에 대한 이슈, MultipleBagFetchException과 @EntityGraph를 사용했을때 터지는 이유 이해하기
    @Query("""
     SELECT DISTINCT r
     FROM Routine r
     LEFT JOIN FETCH r.routineExercises re
     LEFT JOIN FETCH re.exercise e
     WHERE r.id = :id
    """)
    Optional<Routine> findRoutineDetailsById(@Param("id") Long id);

    // dto mapping jpql문은 응답 DTO가 단일 DTO(List나 중첩 클래스가 없는)일때 사용을 권장
    @Query("""
       select new com.fitsync.domain.routine.dto.RoutineSimpleResponseDto(
           r.id, o.id, w.id, r.name, r.displayOrder, r.memo, r.createdAt, r.updatedAt
       )
       from Routine r
       join r.owner o
       join r.writer w
       where (:ownerId is null or o.id = :ownerId)
           order by r.displayOrder
    """)
    Page<RoutineSimpleResponseDto> findMyRoutineList(
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
}
