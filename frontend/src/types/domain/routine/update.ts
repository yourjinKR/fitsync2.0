import { WithId } from "../../common";

// src/types/api/routine/update.ts
export interface RoutineUpdateRequest extends WithId {
  name: string;
  displayOrder: number;
  memo: string;
  routineExercises: RoutineUpdateRequest.RoutineExerciseRequest[];
}

export namespace RoutineUpdateRequest {
  export interface RoutineExerciseRequest {
    id: number | null;
    exerciseId: number;
    displayOrder: number;
    memo: string;
    sets: RoutineUpdateRequest.RoutineSetRequest[];
  }

  export interface RoutineSetRequest {
    id: number | null;
    displayOrder: number;
    weightKg: number;
    reps: number;
    distanceMeter: number;
    durationSecond: number;
  }
}
