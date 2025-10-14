package com.fitsync.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineSimpleResponse {

    private Long id;
    private Long ownerId;
    private Long writerId;
    private String name;
    private Integer displayOrder;
    private String memo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}
