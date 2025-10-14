import { MetricRequirement } from "../exercise/index";

// src/types/api/routine/detail.ts
export interface RoutineDetailResponse {
  id: number;
  ownerId: number;
  writerId: number;
  name: string;
  displayOrder: number;
  memo: string;
  exercises: RoutineDetailResponse.RoutineExerciseResponse[];
}

export namespace RoutineDetailResponse {
  export interface RoutineExerciseResponse {
    id: number;
    displayOrder: number;
    memo: string;

    exerciseId: number;
    exerciseName: string;
    exerciseCategory: string;

    weightKgStatus: MetricRequirement;
    repsStatus: MetricRequirement;
    distanceMeterStatus: MetricRequirement;
    durationSecondStatus: MetricRequirement;

    sets: RoutineDetailResponse.RoutineSetResponse[];
  }

  export interface RoutineSetResponse {
    id: number;
    displayOrder: number;
    weightKg: number;
    reps: number;
    distanceMeter: number;
    durationSecond: number;
  }
}
