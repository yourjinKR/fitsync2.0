package com.fitsync.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoutineDeleteRequestDto {
    private Long id;
    private Long ownerId;
}
