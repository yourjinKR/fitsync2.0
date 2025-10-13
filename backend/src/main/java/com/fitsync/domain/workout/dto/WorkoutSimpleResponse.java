package com.fitsync.domain.workout.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSimpleResponse {

    Long id;
    OffsetDateTime createdAt;

}
