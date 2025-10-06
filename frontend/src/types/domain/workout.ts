
// 운동기록 생성 요청 DTO
export interface WorkoutCreateRequestDto {
  title : string;
  routineSnapshot : Map<string, object>;
  memo : string;
  onwerId : number;
  writerId : number;
  workoutExercises : WorkoutExerciseRequestDto[];
};

interface WorkoutExerciseRequestDto {
  exerciseId : number;
  exerciseName : string;
  memo : string;
  workoutSets : WorkoutSetRequestDto[]; 
}

interface WorkoutSetRequestDto {
  weightKg : number;
  reps : number;
  distanceMeter : number;
  durationSecond : number;
}

// 특정 운동기록 조회 요청 DTO
export interface WorkoutDetailResponseDto {
  id : number;
  title : string;
  routineSnapshot : Map<string, object>;
  memo : string;
  createdAt : string;
  owner : simpleUserDto;
  writer : simpleUserDto;
  workoutExercises : WorkoutExerciseResponseDto[];
}

interface simpleUserDto {
  id : number;
  name : string;
}

interface WorkoutExerciseResponseDto {
  id : number;
  exerciseId : number;
  exerciseName : string;
  memo : string;
  workoutSets : WorkoutSetResponseDto[]; 
}

interface WorkoutSetResponseDto {
  id : number;
  weightKg : number;
  reps : number;
  distanceMeter : number;
  durationSecond : number;
}
