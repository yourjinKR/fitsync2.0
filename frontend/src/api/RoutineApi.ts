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

  async createRoutine(requestDto : RoutineCreateRequest): Promise<RoutineDetailResponse> {
    const response = await apiClient.post('/api/routine', requestDto);
    return response.data;
  },

  async getRoutineList(userId : number, params : Pageable): Promise<Page<RoutineSimpleResponse>> {
    const response = await apiClient.get(`/api/routine/user/${userId}`, { params });
    return response.data;
  },

  async getRoutine(id : number) : Promise<RoutineDetailResponse> {
    const response = await apiClient.get(`/api/routine/${id}`);
    return response.data;
  },

  async updateRoutine(id : number, requestDto : RoutineUpdateRequest) : Promise<RoutineDetailResponse> {
    const response = await apiClient.put(`/api/routine/${id}`, requestDto);
    return response.data;
  },
  
  async updateRoutineHeader(id : number, requestDto : RoutineSimpleRequest) : Promise<void> {
    const response = await apiClient.patch(`/api/routine/header/${id}`, requestDto);
    console.log(response);
    return response.data;
  },

  async sortRoutine(requestDto : RoutineSimpleRequest[]) : Promise<void> {
    const response = await apiClient.patch(`/api/routine/displayOrder`, requestDto);
    return response.data;
  },

  async deleteRoutine(id : number, requestDto : RoutineDeleteRequest) : Promise<void> {
    const response = await apiClient.delete(`/api/routine/${id}`, { data: requestDto }) // delete는 data를 붙여줘야 함. 왤까? 나중에 알아보자...
      ;
    return response.data;
  },
}

export default RoutineApi