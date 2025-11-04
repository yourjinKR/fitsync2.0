import { WithId } from "../../common";

export interface ExerciseSimpleResponse extends WithId {
  name: string;
  category: string;
  isHidden: boolean;
}
