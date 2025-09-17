package com.fitsync.domain.exercise.repository;

import com.fitsync.domain.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    // 활성화된 특정 운동을 가져오기
    public Optional<Exercise> findByIdAndIsHiddenIsFalse(long id);

    // 해당 이름의 운동이 있는지 확인
    public boolean existsByName (String name);
}
