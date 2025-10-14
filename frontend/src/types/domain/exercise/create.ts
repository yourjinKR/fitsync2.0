import { MetricRequirement } from "./common";

export interface ExerciseCreateRequest {
  name: string;
  category: string;
  description: string;
  isHidden: boolean;
  instructions: ExerciseCreateRequest.InstructionRequest[];
  metricRequirement: ExerciseCreateRequest.MetricRequest;
}

export namespace ExerciseCreateRequest {
  export interface InstructionRequest {
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
