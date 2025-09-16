package com.fitsync.domain.exercise.service;

import com.fitsync.domain.exercise.repository.ExerciseInstructionRepository;
import com.fitsync.domain.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseInstructionRepository exerciseInstructionRepository;

}
