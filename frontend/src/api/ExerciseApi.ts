import { Page } from "../types/api";
import { ExerciseDetailResponseDto, ExerciseSimpleResponseDto } from "../types/domain/exercise";
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
};

export default ExerciseApi;