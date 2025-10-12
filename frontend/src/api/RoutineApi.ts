// frontend\src\api\RoutineApi.ts

import { Page, Pageable } from "../types/api";
import { 
  RoutineDetailResponse, 
  RoutineCreateRequest, 
  RoutineDeleteRequest, 
  RoutineSimpleRequest, 
  RoutineSimpleResponse, 
  RoutineUpdateRequest } from "../types/domain/routine/index";
import apiClient from "./apiClient";

const RoutineApi = {

  createRoutine(requestDto : RoutineCreateRequest): Promise<RoutineDetailResponse> {
    return apiClient.post('/api/routine', requestDto)
      .then(response => response.data);
  },

  getRoutineList(userId : number, params : Pageable): Promise<Page<RoutineSimpleResponse>> {
    return apiClient.get(`/api/routine/user/${userId}`, { params })
    .then(response => response.data);
  },

  getRoutine(id : number) : Promise<RoutineDetailResponse> {
    return apiClient.get(`/api/routine/${id}`)
      .then(response => response.data);
  },

  updateRoutine(id : number, requestDto : RoutineUpdateRequest) : Promise<RoutineDetailResponse> {
    return apiClient.put(`/api/routine/${id}`, requestDto)
      .then(response => response.data);
  },
  
  updateRoutineHeader(id : number, requestDto : RoutineSimpleRequest) : Promise<void> {
    return apiClient.patch(`/api/routine/header/${id}`, requestDto)
      .then(response => {
        console.log(response);
        return response.data;
      });
  },

  sortRoutine(requestDto : RoutineSimpleRequest[]) : Promise<void> {
    return apiClient.patch(`/api/routine/displayOrder`, requestDto)
      .then(response => response.data);
  },

  deleteRoutine(id : number, requestDto : RoutineDeleteRequest) : Promise<void> {
    return apiClient.delete(`/api/routine/${id}`, {data : requestDto}) // delete는 data를 붙여줘야 함. 왤까? 나중에 알아보자...
      .then(response => response.data);
  },
}

export default RoutineApi