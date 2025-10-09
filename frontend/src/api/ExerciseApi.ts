import { Page, Pageable } from "../types/api";
import { 
  ExerciseSimpleResponse,
  ExerciseCreateRequest,
  ExerciseDetailResponse,
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
  getAllExercises(params: Pageable): Promise<Page<ExerciseSimpleResponse>> {

    return apiClient.get<Page<ExerciseSimpleResponse>>('/api/exercise/all', { params })
      // .then(response => response.data);
      .then(response => {
        console.log(response.data);
        return response.data;
      })
  },

  /**
   * 특정 ID의 운동 상세 정보를 조회합니다.
   * @param exerciseId - id (pk)
   */
  getExerciseById(exerciseId: number): Promise<ExerciseDetailResponse> {
    return apiClient.get<ExerciseDetailResponse>(`/api/exercise/${exerciseId}`)
      .then(response => response.data);
  },

  /**
   * 운동 정보를 새로 생성합니다.
   * @param requestDto ExerciseRequestDto (types/domain/exercise.ts 참고)
   * @returns ExerciseDetailResponseDto (관리자가 운동을 생성하면 해당 운동 정보를 바로 보여줌)
   */
  createExercise(requestDto: ExerciseCreateRequest) : Promise<ExerciseDetailResponse> {
    return apiClient.post(`/api/exercise`, requestDto)
      .then(response => response.data);
  },

  /**
   * 운동 정보를 수정합니다
   * @param exerciseId ExerciseRequestDto (types/domain/exercise.ts 참고)
   * @param requestDto 
   * @returns 
   */
  updateExercise(exerciseId: number, requestDto: ExerciseCreateRequest) : Promise<ExerciseDetailResponse> {
    return apiClient.put(`/api/exercise/${exerciseId}`, requestDto)
      .then(response => response.data);
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
   * @returns 
   */
  activateExercise(exerciseId: number): Promise<void> {

    return apiClient.patch(`/api/exercise/${exerciseId}/activation`);
  },
  
  /**
   * 특정 운동들을 비활성화합니다.
   * @param exerciseIds id list (pk)
   * @returns 
   */
  inactivateExercises(exerciseIds: number[]): Promise<void> {

    return apiClient.post(`/api/exercise/deactivations`, {exerciseIds});
  },
  
  /**
   * 특정 운동들을 활성화합니다.
   * @param exerciseIds id list (pk)
   * @returns 
   */
  activateExercises(exerciseIds: number[]): Promise<void> {

    return apiClient.post(`/api/exercise/activations`, {exerciseIds});
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