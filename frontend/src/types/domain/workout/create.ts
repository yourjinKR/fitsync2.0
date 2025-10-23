import { JSONObject } from "../../common";

export interface WorkoutCreateRequestDto {
  title: string;
  routineSnapshot: JSONObject;
  memo: string;
  ownerId: number;
  writerId: number;
  workoutExercises: WorkoutCreateRequestDto.WorkoutExerciseRequest[];
}

export namespace WorkoutCreateRequestDto {
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
