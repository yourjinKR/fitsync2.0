// src/types/api/routine/update.ts
export interface RoutineUpdateRequest {
  id: number;
  name: string;
  displayOrder: number;
  memo: string;
  routineExercises: RoutineUpdateRequest.RoutineExerciseRequest[];
}

export namespace RoutineUpdateRequest {
  export interface RoutineExerciseRequest {
    id: number | null;      // Nullable
    exerciseId: number;
    displayOrder: number;
    memo: string;
    sets: RoutineUpdateRequest.RoutineSetRequest[];
  }

  export interface RoutineSetRequest {
    id: number | null;      // Nullable
    displayOrder: number;
    weightKg: number;       // BigDecimal → number
    reps: number;
    distanceMeter: number;
    durationSecond: number;
  }
}
