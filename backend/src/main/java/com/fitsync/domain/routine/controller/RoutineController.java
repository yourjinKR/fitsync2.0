package com.fitsync.domain.routine.controller;

import com.fitsync.domain.routine.dto.RoutineCreateRequestDto;
import com.fitsync.domain.routine.dto.RoutineCreateResponseDto;
import com.fitsync.domain.routine.dto.RoutineDetailResponseDto;
import com.fitsync.domain.routine.dto.RoutineUpdateRequestDto;
import com.fitsync.domain.routine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping
    public ResponseEntity<RoutineCreateResponseDto> createRoutine(@RequestBody RoutineCreateRequestDto requestDto) {

        RoutineCreateResponseDto responseDto =  routineService.createRoutine(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoutineDetailResponseDto> getRoutine(@PathVariable("id") Long id) {

        RoutineDetailResponseDto result = routineService.getRoutine(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoutineDetailResponseDto> updateRoutine(@PathVariable("id") Long id, @RequestBody RoutineUpdateRequestDto requestDto) {

        RoutineDetailResponseDto result = routineService.updateRoutine(id, requestDto);

        return ResponseEntity.ok(result);
    }
}
