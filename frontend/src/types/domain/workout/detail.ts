import { JSONObject, WithId } from "../../common";

export interface WorkoutDetailResponse extends WithId {
  title: string;
  routineSnapshot : JSONObject;
  memo: string;
  createdAt : string;

  owner : WorkoutDetailResponse.userResponse;
  writer : WorkoutDetailResponse.userResponse;

  workoutExercises: WorkoutDetailResponse.WorkoutExerciseResponse[];
}

export namespace WorkoutDetailResponse {
  export interface userResponse extends WithId {
    name : string;
  }

  export interface WorkoutExerciseResponse extends WithId {
    exerciseId : number;
    exericseName : string;
    memo : string;
    workoutSets : WorkoutSetResponse[];
  }

  export interface WorkoutSetResponse extends WithId {
    weightKg : number;
    reps : number;
    distanceMeter : number;
    durationSecond : number;
  }
}