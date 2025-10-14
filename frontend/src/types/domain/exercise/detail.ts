import { MetricRequirement } from "./common";

export interface ExerciseDetailResponse {
  id: number;
  name: string;
  category: string;
  description: string;
  isHidden: boolean;
  createdAt: string; // OffsetDateTime → ISO 8601 문자열
  instructions: ExerciseDetailResponse.InstructionResponse[];
  metricRequirement: ExerciseDetailResponse.MetricResponse;
}

export namespace ExerciseDetailResponse {
  export interface InstructionResponse {
    id: number;
    stepOrder: number;
    description: string;
  }

  export interface MetricResponse {
    weightKgStatus: MetricRequirement;
    repsStatus: MetricRequirement;
    distanceMeterStatus: MetricRequirement;
    durationSecondStatus: MetricRequirement;
  }
}
