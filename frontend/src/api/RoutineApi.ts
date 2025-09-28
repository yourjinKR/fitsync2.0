// frontend\src\api\RoutineApi.ts

import { RoutineCreateRequestDto, RoutineCreateResponseDto, RoutineDetailResponseDto } from "../types/domain/routine";
import apiClient from "./apiClient";

const RoutineApi = {

  createRoutine(requestDto : RoutineCreateRequestDto): Promise<RoutineCreateResponseDto> {
    return apiClient.post('/api/routine', requestDto)
      .then(response => response.data);
  },

  getRoutine(id : number) : Promise<RoutineDetailResponseDto> {
    return apiClient.get(`/api/routine/${id}`)
      .then(response => response.data);
  }
}

export default RoutineApi