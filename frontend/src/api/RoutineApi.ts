// frontend\src\api\RoutineApi.ts

import { RoutineCreateRequestDto, RoutineCreateResponseDto, RoutineDetailResponseDto, RoutineUpdateRequestDto } from "../types/domain/routine";
import apiClient from "./apiClient";

const RoutineApi = {

  createRoutine(requestDto : RoutineCreateRequestDto): Promise<RoutineCreateResponseDto> {
    return apiClient.post('/api/routine', requestDto)
      .then(response => response.data);
  },

  getRoutine(id : number) : Promise<RoutineDetailResponseDto> {
    return apiClient.get(`/api/routine/${id}`)
      .then(response => response.data);
  },

  updateRoutine(id : number, requestDto : RoutineUpdateRequestDto) : Promise<RoutineDetailResponseDto> {
    return apiClient.put(`/api/routine/${id}`, requestDto)
      .then(response => response.data);
  },
}

export default RoutineApi