package com.fitsync.domain.exercise.repository;

import com.fitsync.domain.exercise.dto.ExerciseSimpleResponseDto;
import com.fitsync.domain.exercise.entity.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {


    @Query("""
    select new com.fitsync.domain.exercise.dto.ExerciseSimpleResponseDto(e.id, e.name, e.category, e.isHidden)
            from  Exercise e
    """)
    Page<ExerciseSimpleResponseDto> findAllSimple(Pageable pageable);

    /**
     * 상세 조회 시 사용하는 메소드.
     * LEFT JOIN FETCH를 사용하여 Exercise와 연관된 instructions를 한 번에 가져옵니다.
     * @return Exercise (with instructions)
     */
    @Query("SELECT e FROM Exercise e LEFT JOIN FETCH e.instructions WHERE e.id = :id")
    public Optional<Exercise> findByIdWithInstructions(@Param("id") Long id);

    // 활성화된 특정 운동을 가져오기
    public Optional<Exercise> findByIdAndIsHiddenIsFalse(long id);

    // 해당 이름의 운동이 있는지 확인
    public boolean existsByName (String name);

    /**
     * JPQL을 사용하여 여러 운동의 isHidden 상태를 한 번에 변경
     * @param exerciseIds pk들
     * @param isHidden true or false 직접 지정
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true) // 이 쿼리가 INSERT, UPDATE, DELETE 등 상태를 변경하는 쿼리임을 알림
    @Query("UPDATE Exercise e SET e.isHidden = :isHidden WHERE e.id IN :exerciseIds")
    void updateHiddenStatusByIds(@Param("exerciseIds") List<Long> exerciseIds, @Param("isHidden") boolean isHidden);

}
