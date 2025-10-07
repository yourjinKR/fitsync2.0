// ============================
// DTOs for Request (Client -> Server)
// ============================

import { RoutineDetailResponseDto } from "./routine";

// Omit을 사용해 Response 타입으로부터 생성
type WorkoutSetRequestDto = Omit<WorkoutSetResponseDto, 'id'>;

// Omit과 &를 사용해 Response 타입으로부터 생성
type WorkoutExerciseRequestDto = Omit<WorkoutExerciseResponseDto, 'id' | 'workoutSets'> & {
  workoutSets: WorkoutSetRequestDto[];
};

// 루틴 스냅샷 객체의 형태를 구체적으로 정의
interface RoutineSnapshotDetail {
  sets: number;
  reps: number;
  // ... 기타 필요한 속성들
}

export interface WorkoutCreateRequestDto {
  title: string;
  routineSnapshot: RoutineDetailResponseDto; // routine detail DTO를 그대로 재사용
  memo: string;
  ownerId: number; // 오타 수정
  writerId: number;
  workoutExercises: WorkoutExerciseRequestDto[];
}


// ============================
// DTOs for Response (Server -> Client)
// ============================

interface SimpleUserDto {
  id: number;
  name: string;
}

interface WorkoutSetResponseDto {
  id: number;
  weightKg: number;
  reps: number;
  distanceMeter: number;
  durationSecond: number;
}

interface WorkoutExerciseResponseDto {
  id: number;
  exerciseId: number;
  exerciseName: string;
  memo: string;
  workoutSets: WorkoutSetResponseDto[];
}

export interface WorkoutDetailResponseDto {
  id: number;
  title: string;
  routineSnapshot: { [key: string]: RoutineSnapshotDetail }; // Map 대신 객체 + 인덱스 시그니처 사용
  memo: string;
  createdAt: string; // 서버에서 받은 후 new Date(createdAt)으로 변환해서 사용하는 것을 추천
  owner: SimpleUserDto;
  writer: SimpleUserDto;
  workoutExercises: WorkoutExerciseResponseDto[];
}