// src/types/domain/exercise.ts

// 목록 조회 시 사용할 간단한 DTO 타입
export interface ExerciseSimpleResponseDto {
  id: number;
  name: string;
  category: string;
}

// (기존의 상세 조회 DTO도 여기에 함께 관리하면 좋습니다)
export interface ExerciseDetailResponseDto {
  id: number;
  name: string;
  category: string;
  description: string | null;
  createdAt: string; // OffsetDateTime은 string으로 받습니다.
  instructions: InstructionInfo[];
}

export interface InstructionInfo {
  id: number;
  stepOrder: number;
  description: string;
}