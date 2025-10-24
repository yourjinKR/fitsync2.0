import { JSONObject } from "../../common";

export interface WorkoutCreateRequest {
  title: string;
  routineSnapshot: JSONObject;
  memo: string;
  ownerId: number;
  writerId: number;
  workoutExercises: WorkoutCreateRequest.WorkoutExerciseRequest[];
}

export namespace WorkoutCreateRequest {
  export interface WorkoutExerciseRequest {
    exerciseId : number;
    exericseName : string;
    memo : string;
    workoutSets : WorkoutSetRequest[];
  }

  export interface WorkoutSetRequest{
  weightKg : number;
  reps : number;
  distanceMeter : number;
  durationSecond : number;
  }
}
