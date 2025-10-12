import { useState } from "react";
import { RoutineCreateRequest, RoutineDetailResponse, RoutineSimpleResponse, RoutineUpdateRequest } from "../types/domain/routine";
import RoutineApi from "../api/RoutineApi";
import { Pageable } from "../types/api";

const useRoutine = () => {
  const [routineList, setRoutineList] = useState<RoutineSimpleResponse[]>();
  const [selectedRoutine, setSelectedRoutine] = useState<RoutineDetailResponse>();
  const [routineCreateForm, setRoutineCreateForm] = useState<RoutineCreateRequest>();
  const [routineUpdateForm, setRoutineUpdateForm] = useState<RoutineUpdateRequest>();

  const fetchRoutineList = async (userId:number, pageable:Pageable) => {
    const response = await RoutineApi.getRoutineList(userId, pageable);
    setRoutineList(response.content);
  };

  const loadRoutineDetailInfo = async (id:number) => {
    const response = await RoutineApi.getRoutine(id);
    setSelectedRoutine(response);
  }

  const submitRoutineCreate = async () => {
    const request = buildRoutineCreateForm(selectedRoutine);
    const response = await RoutineApi.createRoutine(request);
    setSelectedRoutine(response);
  }

  const buildRoutineCreateForm = (request:RoutineDetailResponse) => {
    const detail = request;

    const parsedExercises:RoutineCreateRequest.RoutineExerciseRequest[] = detail.exercises.map((exercise) => {
      const sets = exercise.sets.map((set) => {
        const {id: _id, ...newSet} = set;
        return newSet;
      });

      return {
        exerciseId : exercise.exerciseId,
        displayOrder : exercise.displayOrder,
        memo : exercise.memo,
        sets,
      };
    })
    
    const createForm:RoutineCreateRequest = {
      name : detail.name,
      memo : detail.memo,
      displayOrder : detail.displayOrder,
      exercises : parsedExercises,
    }
    
    return createForm;
  }

  const submitRoutineUpdate = async () => {
    if (!selectedRoutine) return;

    const request = buildRoutineUpdateForm(selectedRoutine);
    const response = await RoutineApi.updateRoutine(selectedRoutine.id, request);

    setSelectedRoutine(response);
  }

  const buildRoutineUpdateForm = (request:RoutineDetailResponse) => {
    if (!request) return;

    const detail = request;

    const parsedExercises:RoutineUpdateRequest.RoutineExerciseRequest[] = detail.exercises.map((exercise) => {
      return {
        id : exercise.id, 
        exerciseId : exercise.exerciseId,
        memo : exercise.memo,
        displayOrder : exercise.displayOrder,
        sets : exercise.sets,
      }
    });

    const updateForm:RoutineUpdateRequest = {
      id : detail.id,
      name : detail.name,
      memo : detail.memo,
      displayOrder : detail.displayOrder,
      routineExercises : parsedExercises,
    };

    return updateForm;
  }


  return {
    routineList,
    setRoutineList,
    /**
     * 루틴 리스트 요청
     */
    fetchRoutineList,

    selectedRoutine,
    setSelectedRoutine,
    /**
     * 특정 루틴 정보 불러오기
     */
    loadRoutineDetailInfo,

    routineCreateForm,
    setRoutineCreateForm,
    /**
     * 루틴 생성 form 만들기
     */
    buildRoutineCreateForm,
    /**
     * 루틴 생성 요청
     */
    submitRoutineCreate,

    routineUpdateForm,
    setRoutineUpdateForm,
    /**
     * 루틴 수정 form 만들기
     */
    buildRoutineUpdateForm,
    /**
     * 루틴 수정 요청
     */
    submitRoutineUpdate,

  };
}
export default useRoutine;