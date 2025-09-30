import { Nullable } from './../common';
// frontend\src\types\domain\routine.ts

// 루틴 생성 요청
export interface RoutineCreateRequestDto {
  ownerId : number;
  writerId : number;
  name : string;
  displayOrder : number;
  memo : string;

  exercises : RoutineExerciseDto1[]
}

interface RoutineExerciseDto1 {
  displayOrder : number;
  memo : string;

  exerciseId : number;

  sets : RoutineSetDto[];
}

interface RoutineSetDto {
  id : Nullable<number>;
  displayOrder : number;
  weightKg : number;
  reps : number;
  distanceMeter : number;
  durationSecond : number;
}

// 루틴 생성 응답
export interface RoutineCreateResponseDto {
  id : number;
}

// 루틴 리스트 조회 응답
export interface RoutineSimpleResponseDto {
  id : number;
  ownerId : number;
  writerId : number;
  name : string;
  displayOrder : number;
  memo : string;
  createdAt : string; // ISO 8601 문자열
  updatedAt : string;
}

// 특정 루틴 조회 응답
export interface RoutineDetailResponseDto {
  id : number;
  ownerId : number;
  writerId : number;
  name : string;
  displayOrder : number;
  memo : string;

  exercises : RoutineExerciseDto2[];
}

interface RoutineExerciseDto2 {
  id : number;
  displayOrder : number;
  memo : string;

  exercise : ExerciseSummaryDto;

  sets : RoutineSetDto[];
}

interface ExerciseSummaryDto {
  id : number;
  name : string;
}

// 루틴 수정 DTO
export interface RoutineUpdateRequestDto {
  id : number;
  name : string;
  displayOrder : number;
  memo : string;

  routineExercises : RoutineExerciseDto3[]
}

interface RoutineExerciseDto3 {
  id : Nullable<number>;
  exerciseId : number;
  displayOrder : number;
  memo : string;

  sets : RoutineSetDto[];
}

// 루틴 정렬 수정 DTO
export interface RoutineSimpleRequestDto {
  id : number;
  name : string;
  displayOrder : number;
  memo : string;
}


