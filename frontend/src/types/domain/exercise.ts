// src/types/domain/exercise.ts

import { Nullable } from "../common";

// 목록 조회 시 사용할 간단한 DTO
export interface ExerciseSimpleResponseDto {
  id: number;
  name: string;
  category: string;
  isHidden : boolean;
}

// 운동정보 상세보기 응답 DTO
export interface ExerciseDetailResponseDto {
  id: number;
  name: string;
  category: string;
  description: Nullable<string>;
  isHidden : boolean;
  createdAt: string; // OffsetDateTime은 string으로 받습니다.
  updatedAt : string;

  instructions: InstructionResponseDto[];
  metricRequirement : MetricRequireDto;
}

export interface InstructionResponseDto {
  id: number;
  stepOrder: number;
  description: string;
}

interface MetricRequireDto {
  weightKgStatus : MetricRequirement;
  repsStatus : MetricRequirement;
  distanceMeterStatus : MetricRequirement;
  durationSecondStatus : MetricRequirement;
}

export enum MetricRequirement {
  FORBIDDEN = 'FORBIDDEN',
  OPTIONAL = 'OPTIONAL',
  REQUIRED = 'REQUIRED'
}

// 재사용 인터페이스
interface ExerciseBasicRequestDto {
  name: string;
  category: string;
  description: Nullable<string>;
  isHidden : boolean;
}

// TODO : 이거 나중에 요청 DTO 수정해야될 수도 있음
// 운동정보 생성 요청 DTO
export interface ExerciseCreateRequestDto extends ExerciseBasicRequestDto {
  instructions: InstructionBasicDto[];
  metricRequirement : MetricRequireDto;
}

export interface InstructionBasicDto {
  stepOrder: number;
  description: string;
}

// 운동정보 업데이트
export interface ExerciseUpdateRequestDto extends ExerciseBasicRequestDto {
  instructions : InstructionUpdateDto[];
}

interface InstructionUpdateDto extends InstructionBasicDto {
  id : number;
}


// hidden값 수정 요청 DTO
export interface ExerciseHiddenUpdateDto {
  exerciseIds : number[];
}

