// src/types/api/routine/create.ts
export interface RoutineCreateRequest {
  name: string;
  displayOrder: number;
  memo: string;
  exercises: RoutineCreateRequest.RoutineExerciseRequest[];
}

export namespace RoutineCreateRequest {
  export interface RoutineExerciseRequest {
    exerciseId: number;
    displayOrder: number;
    memo: string;
    sets: RoutineCreateRequest.RoutineSetRequest[];
  }

  export interface RoutineSetRequest {
    displayOrder: number;
    weightKg: number; // BigDecimal → number (필요시 string)
    reps: number;
    distanceMeter: number;
    durationSecond: number;
  }
}

export interface RoutineCreateResponse {
  id: number;
}
