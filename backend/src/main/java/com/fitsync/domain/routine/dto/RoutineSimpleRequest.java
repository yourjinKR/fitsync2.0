package com.fitsync.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoutineSimpleRequest {
    private Long id;
    private String name;
    private Integer displayOrder;
    private String memo;
}
