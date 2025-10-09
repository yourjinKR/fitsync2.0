// src/types/api/routine/simple.ts
export interface RoutineSimpleResponse {
  id: number;
  ownerId: number;
  writerId: number;
  name: string;
  displayOrder: number;
  memo: string;
  createdAt: string; // ISO 8601
  updatedAt: string;
}

export interface RoutineSimpleRequest {
  id: number;
  name: string;
  displayOrder: number;
  memo: string;
}

