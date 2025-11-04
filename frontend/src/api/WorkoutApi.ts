// frontend\src\api\WorkoutApi.ts

import { WorkoutCreateRequest, WorkoutDetailResponse, WorkoutSimpleResponse, WorkoutUpdateRequest } from "../types/domain/workout";

import apiClient from "./apiClient";

const WorkoutApi = {

  async createWorkout(requestDto : WorkoutCreateRequest): Promise<WorkoutDetailResponse> {
    const response = await apiClient.post('/api/workout', requestDto);
    return response.data;
  },

  async getMyWorkoutList(userId : number): Promise<WorkoutSimpleResponse[]> {
    const response = await apiClient.get(`/api/workout/user/${userId}`);
    return response.data;
  },

  async getWorkout(id : number) : Promise<WorkoutDetailResponse> {
    const response = await apiClient.get(`/api/workout/${id}`);
    return response.data;
  },

  async getMyWorkoutToday(userId : number) : Promise<WorkoutDetailResponse[]> {
    const response = await apiClient.get(`/api/workout/${userId}/today`);
    return response.data;
  },

  async updateWorkout(id : number, requestDto : WorkoutUpdateRequest) : Promise<WorkoutDetailResponse> {
    const response = await apiClient.put(`/api/workout/${id}`, requestDto);
    return response.data;
  },

  async deleteWorkout(id : number) : Promise<void> {
    const response = await apiClient.delete(`/api/workout/${id}`);
    return response.data;
  },
}

export default WorkoutApi;