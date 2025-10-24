// frontend\src\api\WorkoutApi.ts

import { Page, Pageable } from "../types/api";
import { WorkoutCreateRequest, WorkoutDetailResponse, WorkoutSimpleResponse, WorkoutUpdateRequest } from "../types/domain/workout";

import apiClient from "./apiClient";

const WorkoutApi = {

  createWorkout(requestDto : WorkoutCreateRequest): Promise<WorkoutDetailResponse> {
    return apiClient.post('/api/workout', requestDto)
      .then(response => response.data);
  },

  getMyWorkoutList(userId : number): Promise<WorkoutSimpleResponse[]> {
    return apiClient.get(`/api/workout/user/${userId}`)
    .then(response => response.data);
  },

  getWorkout(id : number) : Promise<WorkoutDetailResponse> {
    return apiClient.get(`/api/workout/${id}`)
      .then(response => response.data);
  },

  updateWorkout(id : number, requestDto : WorkoutUpdateRequest) : Promise<WorkoutDetailResponse> {
    return apiClient.put(`/api/workout/${id}`, requestDto)
      .then(response => response.data);
  },

  deleteWorkout(id : number) : Promise<void> {
    return apiClient.delete(`/api/workout/${id}`)
      .then(response => response.data);
  },
}

export default WorkoutApi;