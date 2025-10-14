import { MetricRequirement } from "./common";

export interface ExerciseFormResponse {
  id: number;
  name: string;
  category: string;
  weightKgStatus: MetricRequirement;
  repsStatus: MetricRequirement;
  distanceMeterStatus: MetricRequirement;
  durationSecondStatus: MetricRequirement;
}
