import apiClient from "./apiClient";

const ExerciseApi = {
  findExercise : async (exerciseId : number) => await apiClient.get(`/api/exercise/${exerciseId}`),
};

export default ExerciseApi;