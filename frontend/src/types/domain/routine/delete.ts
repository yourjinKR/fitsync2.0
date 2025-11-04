import { WithId } from "../../common";

// src/types/api/routine/delete.ts
export interface RoutineDeleteRequest extends WithId {
  ownerId: number;
}

