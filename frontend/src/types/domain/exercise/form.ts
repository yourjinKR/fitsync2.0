import { WithId } from "../../common";
import { MetricRequirement } from "./common";

export interface ExerciseFormResponse extends WithId {
  name: string;
  category: string;
  weightKgStatus: MetricRequirement;
  repsStatus: MetricRequirement;
  distanceMeterStatus: MetricRequirement;
  durationSecondStatus: MetricRequirement;
}
