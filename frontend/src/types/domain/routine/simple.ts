import { WithId } from "../../common";

// src/types/api/routine/simple.ts
export interface RoutineSimpleResponse extends WithId {
  ownerId: number;
  writerId: number;
  name: string;
  displayOrder: number;
  memo: string;
  createdAt: string; // ISO 8601
  updatedAt: string;
}

export interface RoutineSimpleRequest extends WithId {
  name: string;
  displayOrder: number;
  memo: string;
}

