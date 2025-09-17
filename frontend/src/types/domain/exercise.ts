// src/types/domain/exercise.ts

import { Nullable } from "../common";

// 목록 조회 시 사용할 간단한 DTO
export interface ExerciseSimpleResponseDto {
  id: number;
  name: string;
  category: string;
}

// 운동정보 상세보기 응답 DTO
export interface ExerciseDetailResponseDto {
  id: number;
  name: string;
  category: string;
  description: Nullable<string>;
  isHidden : boolean;
  createdAt: string; // OffsetDateTime은 string으로 받습니다.

  instructions: InstructionInfo[];
}

export interface InstructionInfo {
  id: number;
  stepOrder: number;
  description: string;
}

// 운동정보 생성 요청 DTO
export interface ExerciseCreateRequestDto {
  name: string;
  category: string;
  description: Nullable<string>;
  isHidden : boolean;

  instructions: InstructionCreateDto[];
}

export interface InstructionCreateDto {
  stepOrder: number;
  description: string;
}


