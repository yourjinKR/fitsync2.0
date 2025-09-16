package com.fitsync.domain.exercise.controller;

import com.fitsync.domain.exercise.dto.ExerciseResponseDto;
import com.fitsync.domain.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseResponseDto> getExercise(@PathVariable("exerciseId") Long exerciseId) {

        ExerciseResponseDto responseDto = exerciseService.getExercise(exerciseId);

        if (responseDto == null) {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
