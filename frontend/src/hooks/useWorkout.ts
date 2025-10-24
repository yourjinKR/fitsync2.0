import { useState } from "react";
import { WorkoutDetailResponse, WorkoutSimpleResponse } from "../types/domain/workout";
import WorkoutApi from "../api/WorkoutApi";

const useWorkout = () => {
  const [myworkoutList, setMyWorkoutList] = useState<WorkoutSimpleResponse[]>();
  const [workoutToday, setWorkoutToday] = useState<WorkoutDetailResponse[]>();
  const [workoutDetail, setWorkoutDetail] = useState<WorkoutDetailResponse>();

  const fetchMyWorkoutList = async (userId:number) => {
    const response = await WorkoutApi.getMyWorkoutList(userId);
    setMyWorkoutList(response);
  };

  const fetchMyWorkoutToday = async (userId:number) => {
    const response = await WorkoutApi.getMyWorkoutToday(userId);
    setWorkoutToday(response);
  };

  const fetchWorkoutDetail = async (id:number) => {
    const response = await WorkoutApi.getWorkout(id);
    setWorkoutDetail(response);
  };

  return {
    myworkoutList,
    setMyWorkoutList,
    fetchMyWorkoutList,

    workoutToday,
    setWorkoutToday,
    fetchMyWorkoutToday,

    workoutDetail,
    setWorkoutDetail,
    fetchWorkoutDetail,
  }
}
export default useWorkout;