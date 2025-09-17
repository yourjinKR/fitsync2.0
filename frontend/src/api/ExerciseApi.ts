import { Page } from "../types/api";
import { ExerciseRequestDto, ExerciseDetailResponseDto, ExerciseSimpleResponseDto } from "../types/domain/exercise";
import apiClient from "./apiClient";

// 페이지네이션 요청을 위한 파라미터 타입
interface Pageable {
  page: number;
  size: number;
  sort?: string; // 예: 'name,asc'
}

const ExerciseApi = {
  /**
   * 모든 운동 정보를 페이지 단위로 조회합니다.
   * @param params - 페이지, 사이즈, 정렬 정보
   */
  getAllExercises(params: Pageable): Promise<Page<ExerciseSimpleResponseDto>> {

    return apiClient.get<Page<ExerciseSimpleResponseDto>>('/api/exercise/all', { params })
      .then(response => response.data);
  },

  /**
   * 특정 ID의 운동 상세 정보를 조회합니다.
   * @param exerciseId - 조회할 운동의 ID
   */
  getExerciseById(exerciseId: number): Promise<ExerciseDetailResponseDto> {
    return apiClient.get<ExerciseDetailResponseDto>(`/api/exercise/${exerciseId}`)
      .then(response => response.data);
  },

  /**
   * 운동 정보를 새로 생성합니다.
   * @param requestDto ExerciseRequestDto (types/domain/exercise.ts 참고)
   * @returns ExerciseDetailResponseDto (관리자가 운동을 생성하면 해당 운동 정보를 바로 보여줌)
   */
  createExercise(requestDto: ExerciseRequestDto) : Promise<ExerciseDetailResponseDto> {
    return apiClient.post(`/api/exercise`, requestDto)
      .then(response => response.data);
  },

  /**
   * 운동 정보를 수정합니다
   * @param exerciseId ExerciseRequestDto (types/domain/exercise.ts 참고)
   * @param requestDto 
   * @returns 
   */
  updateExercise(exerciseId: number, requestDto: ExerciseRequestDto) : Promise<ExerciseDetailResponseDto> {
    return apiClient.put(`/api/exercise/${exerciseId}`, requestDto)
      .then(response => response.data);
  },
};

export default ExerciseApi;