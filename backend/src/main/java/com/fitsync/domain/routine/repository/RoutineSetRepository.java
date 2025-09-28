package com.fitsync.domain.routine.repository;

import com.fitsync.domain.routine.entity.RoutineSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineSetRepository extends JpaRepository<RoutineSet, Long> {

}
