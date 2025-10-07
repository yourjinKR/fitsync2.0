// frontend\src\api\RoutineApi.ts

import { Page, Pageable } from "../types/api";
import { RoutineCreateRequestDto, RoutineDeleteRequestDto, RoutineDetailResponseDto, RoutineSimpleRequestDto, RoutineSimpleResponseDto, RoutineUpdateRequestDto } from "../types/domain/routine";
import apiClient from "./apiClient";

const RoutineApi = {

  createRoutine(requestDto : RoutineCreateRequestDto): Promise<number> {
    return apiClient.post('/api/routine', requestDto)
      .then(response => response.data);
  },

  getRoutineList(userId : number, params : Pageable): Promise<Page<RoutineSimpleResponseDto>> {
    return apiClient.get(`/api/routine/user/${userId}`, { params })
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
  
  updateRoutineHeader(id : number, requestDto : RoutineSimpleRequestDto) : Promise<void> {
    return apiClient.patch(`/api/routine/header/${id}`, requestDto)
      .then(response => {
        console.log(response);
        return response.data;
      });
  },

  sortRoutine(requestDto : RoutineSimpleRequestDto[]) : Promise<void> {
    return apiClient.patch(`/api/routine/displayOrder`, requestDto)
      .then(response => response.data);
  },

  deleteRoutine(id : number, requestDto : RoutineDeleteRequestDto) : Promise<void> {
    return apiClient.delete(`/api/routine/${id}`, {data : requestDto}) // delete는 data를 붙여줘야 함. 왤까? 나중에 알아보자...
      .then(response => response.data);
  },
}

export default RoutineApi