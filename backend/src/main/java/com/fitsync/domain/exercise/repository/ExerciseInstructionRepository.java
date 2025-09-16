package com.fitsync.domain.exercise.repository;

import com.fitsync.domain.exercise.entity.ExerciseInstruction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseInstructionRepository extends JpaRepository<ExerciseInstruction, Long> {


}
