import { MetricRequirement } from "./common";

export interface ExerciseUpdateRequest {
  name: string;
  category: string;
  description: string;
  isHidden: boolean;
  instructions: ExerciseUpdateRequest.InstructionRequest[];
  metricRequirement: ExerciseUpdateRequest.MetricRequest;
}

export namespace ExerciseUpdateRequest {
  export interface InstructionRequest {
    id: number;
    stepOrder: number;
    description: string;
  }

  export interface MetricRequest {
    weightKgStatus: MetricRequirement;
    repsStatus: MetricRequirement;
    distanceMeterStatus: MetricRequirement;
    durationSecondStatus: MetricRequirement;
  }
}

export interface ExerciseIsHiddenUpdateRequest {
  exerciseIds: number[];
}

export interface ExerciseIsHiddenBatchUpdateRequest{
  activate : ExerciseIsHiddenUpdateRequest;
  deactivate : ExerciseIsHiddenUpdateRequest;
}
