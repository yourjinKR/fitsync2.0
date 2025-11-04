import { Page, Pageable } from "../types/api";
import { 
  ExerciseSimpleResponse,
  ExerciseCreateRequest,
  ExerciseDetailResponse,
  ExerciseIsHiddenBatchUpdateRequest,
  ExerciseIsHiddenUpdateRequest,
} from "../types/domain/exercise/index";

import apiClient from "./apiClient";

// 페이지네이션 요청을 위한 파라미터 타입
// interface Pageable {
//   page: number;
//   size: number;
//   sort?: string; // 예: 'name,asc'
// }

const ExerciseApi = {
  /**
   * 모든 운동 정보를 페이지 단위로 조회합니다.
   * @param params - 페이지, 사이즈, 정렬 정보
   */
  async getAllExercises(params: Pageable): Promise<Page<ExerciseSimpleResponse>> {

    const response = await apiClient.get<Page<ExerciseSimpleResponse>>('/api/exercise/all', { params });
    return response.data;
  },

  /**
   * 특정 ID의 운동 상세 정보를 조회합니다.
   * @param exerciseId - id (pk)
   */
  async getExerciseById(exerciseId: number): Promise<ExerciseDetailResponse> {
    const response = await apiClient.get<ExerciseDetailResponse>(`/api/exercise/${exerciseId}`);
    return response.data;
  },

  /**
   * 운동 정보를 새로 생성합니다.
   * @param requestDto ExerciseRequestDto (types/domain/exercise.ts 참고)
   * @returns ExerciseDetailResponseDto (관리자가 운동을 생성하면 해당 운동 정보를 바로 보여줌)
   */
  async createExercise(requestDto: ExerciseCreateRequest) : Promise<ExerciseDetailResponse> {
    const response = await apiClient.post(`/api/exercise`, requestDto);
    return response.data;
  },

  /**
   * 운동 정보를 수정합니다
   * @param exerciseId ExerciseRequestDto (types/domain/exercise.ts 참고)
   * @param requestDto 
   * @returns 
   */
  async updateExercise(exerciseId: number, requestDto: ExerciseCreateRequest) : Promise<ExerciseDetailResponse> {
    const response = await apiClient.put(`/api/exercise/${exerciseId}`, requestDto);
    return response.data;
  },

  /**
   * 특정 ID의 운동 정보를 삭제(숨김 처리)합니다.
   * @param exerciseId - id (pk)
   */
  inactivateExercise(exerciseId: number): Promise<void> {
    return apiClient.patch(`/api/exercise/${exerciseId}/deactivation`);
  },

  /**
   * 특정 ID의 운동 정보를 활성화합니다.
   * @param exerciseId id (pk)
   * @returns void
   */
  activateExercise(exerciseId: number): Promise<void> {
    return apiClient.patch(`/api/exercise/${exerciseId}/activation`);
  },
  
  /**
   * 특정 운동들을 비활성화합니다.
   * @param exerciseIds id list (pk)
   * @returns void
   */
  inactivateExercises(exerciseIds: ExerciseIsHiddenUpdateRequest): Promise<void> {
    return apiClient.post(`/api/exercise/deactivate`, {exerciseIds});
  },
  
  /**
   * 특정 운동들을 활성화합니다.
   * @param exerciseIds id list (pk)
   * @returns void
   */
  activateExercises(exerciseIds: ExerciseIsHiddenUpdateRequest): Promise<void> {
    return apiClient.post(`/api/exercise/activate`, {exerciseIds});
  },

  /**
   * 특정 운동들을 활성화/비활성화합니다.
   * @param requestDto ExerciseIsHiddenBatchUpdateRequest
   * @returns void
   */
  updateActivationStates(requestDto: ExerciseIsHiddenBatchUpdateRequest): Promise<void> {
    return apiClient.patch(`/api/exercise/activation-states`, {requestDto});
  },

  /**
   * 특정 운동을 삭제합니다
   * @param exerciseId id (pk)
   * @returns 
   */
  removeExercise(exerciseId : number) : Promise<void> {

    return apiClient.delete(`/api/exercise/${exerciseId}`);
  }

  
};

export default ExerciseApi;