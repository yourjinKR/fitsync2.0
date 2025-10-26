import { WithId } from "../../common";
import { MetricRequirement } from "../exercise/index";

// src/types/api/routine/detail.ts
export interface RoutineDetailResponse extends WithId {
  ownerId: number;
  writerId: number;
  name: string;
  displayOrder: number;
  memo: string;
  exercises: RoutineDetailResponse.RoutineExerciseResponse[];
}

export namespace RoutineDetailResponse {
  export interface RoutineExerciseResponse extends WithId {
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

  export interface RoutineSetResponse extends WithId {
    displayOrder: number;
    weightKg: number;
    reps: number;
    distanceMeter: number;
    durationSecond: number;
  }
}
