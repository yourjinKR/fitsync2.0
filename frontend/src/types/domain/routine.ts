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
  displayOrder : number;
  memo : string;

  exercise : ExerciseSummaryDto;

  sets : RoutineSetDto[];
}

interface ExerciseSummaryDto {
  id : number;
  name : string;
}

