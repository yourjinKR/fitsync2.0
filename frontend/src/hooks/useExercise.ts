import { ExerciseIsHiddenBatchUpdateRequest } from './../types/domain/exercise/update';
import ExerciseApi from '../api/ExerciseApi';
import { Pageable } from '../types/api';
import { ExerciseCreateRequest, ExerciseDetailResponse, ExerciseSimpleResponse, ExerciseUpdateRequest } from '../types/domain/exercise';
import { useCallback, useEffect, useState } from "react";

const INIT_PAGE: Pageable = { page: 0, size: 30, sort: "id,desc" };
const EMPTY_LIST: ExerciseSimpleResponse[] = [];
const EMPTY_DETAIL: ExerciseDetailResponse | null = null;
const EMPTY_CREATE_FORM : ExerciseCreateRequest | null = null;
const EMPTY_UPDATE_FORM: ExerciseUpdateRequest | null = null;
const EMPTY_BATCH: ExerciseIsHiddenBatchUpdateRequest = {
  activate: { exerciseIds: [] },
  deactivate: { exerciseIds: [] },
};

const useExercise = () => {
  const [exerciseListPage, setExerciseListPage] = useState<Pageable>(INIT_PAGE);
  const [exerciseList, setExerciseList] = useState<ExerciseSimpleResponse[]>(EMPTY_LIST);
  const [selectedExercise, setSelectedExercise] = useState<ExerciseDetailResponse | null>(EMPTY_DETAIL);
  const [exerciseCreateForm, setExerciseCreateForm] = useState<ExerciseCreateRequest | null>(EMPTY_CREATE_FORM);
  const [exerciseUpdateForm, setExerciseUpdateForm] = useState<ExerciseUpdateRequest | null>(EMPTY_UPDATE_FORM);
  const [selectedIdList, setSelectedIdList] = useState<ExerciseIsHiddenBatchUpdateRequest>(EMPTY_BATCH);

  // 운동정보 불러오기
  const fetchExerciseList = useCallback(async () => {
    try {
      const response = await ExerciseApi.getAllExercises(exerciseListPage); 
      setExerciseList(response.content);
    } catch (error) {
      console.error("운동정보를 불러오지 못했습니다. error :" , error);
    }
  },[exerciseListPage]);
  
  // 다음 페이지 세팅
  const nextExercisePage = () => {
    setExerciseListPage(prev => ({...prev, page : prev.page += 1}));
  };

  /* 
  dependencies에 fetchExerciseList를 추가
  그렇게 되면 fetchExerciseList는 랜더링마다 함수 인스턴스가 새로 생기는 관계로
  useEffect가 불필요하게 반복적으로 발생할 수 있음
  */
  useEffect(() => {
    fetchExerciseList();
  },[fetchExerciseList, exerciseListPage]);

  // 페이징 초기화
  const clearExercisePage = () => {
    setExerciseListPage(INIT_PAGE);
  }

  // 특정 운동 상세정보 조회
  const loadExerciseDetailInfo = async(exerciseId:number) => {
    const response = await ExerciseApi.getExerciseById(exerciseId);
    setSelectedExercise(response);
  };

  // 선택된 운동 초기화
  const initSelectedExercise = () => {
    setSelectedExercise(EMPTY_DETAIL);
  };

  // 운동 정보 생성
  const submitExerciseCreate = async () => {
    if (!selectedExercise) return;

    const request = buildExerciseCreateForm(selectedExercise);
    const response = await ExerciseApi.createExercise(request);
    setSelectedExercise(response);
  };

  // 운동 상세정보 -> 운동 생성 form
  const buildExerciseCreateForm = (selectedExercise:ExerciseDetailResponse) => {
    if (!selectedExercise) return;

    const detail = selectedExercise;
    const instructions = detail.instructions.map(inst => {
      return {stepOrder: inst.stepOrder, description: inst.description};
    });

    const createForm:ExerciseCreateRequest = {
      name : detail.name,
      category : detail.category,
      description : detail.description,
      isHidden : detail.isHidden,
      instructions,
      metricRequirement : detail.metricRequirement
    };

    return createForm;
  }

  // 운동 정보 수정
  const submitExerciseUpdate = async () => {
    if (!selectedExercise) return;

    const request = buildUpdateExerciseForm(selectedExercise);
    const response = await ExerciseApi.updateExercise(selectedExercise.id, request);

    setSelectedExercise(response);
  };

  // 운동 상세정보 -> 운동 수정 form
  const buildUpdateExerciseForm = (request:ExerciseDetailResponse) => {
    if (!request) return;

    const detail = request;
    const instructions = detail.instructions.map(inst => {
      return {id : inst.id, stepOrder: inst.stepOrder, description: inst.description};
    });

    const updateForm:ExerciseUpdateRequest = {
      name : detail.name,
      category : detail.category,
      description : detail.description,
      isHidden : detail.isHidden,
      instructions,
      metricRequirement : detail.metricRequirement
    };

    return updateForm;
  };

  // 운동정보 활성화/비활성화 업데이트
  const updateExerciseState = async (): Promise<boolean> => {
    try {
      const { activate, deactivate } = selectedIdList;
      const a = activate.exerciseIds.length > 0;
      const d = deactivate.exerciseIds.length > 0;

      if (a && d) {
        await ExerciseApi.updateActivationStates(selectedIdList);
      } else if (a) {
        await ExerciseApi.activateExercises(activate);
      } else if (d) {
        await ExerciseApi.inactivateExercises(deactivate);
      } else {
        return false;
      }

      clearExercisePage();
      return true;

    } catch (error) {
      console.error(error);
      return false;
    }
  }


  // 운동정보 -> 루틴으로 저장
  // 사용자가 운동 리스트에서 운동 선택
  // 해당 운동들을 참고하여 routineCreateRequest를 만듬

  return {
    exerciseListPage,
    setExerciseListPage,

    exerciseList,
    setExerciseList,

    /**
     * 운동정보 불러오기 요청
     */
    fetchExerciseList,
    nextExercisePage,
    clearExercisePage,

    selectedExercise,
    setSelectedExercise,
    initSelectedExercise,
    /**
     * 운동 상세정보 요청
     */
    loadExerciseDetailInfo,

    exerciseCreateForm,
    setExerciseCreateForm,
    /**
     * 운동정보 신규 추가 form 빌드 함수
     */
    buildExerciseCreateForm,
    /**
     * 운동정보 신규 추가 요청
     */
    submitExerciseCreate,
    
    exerciseUpdateForm,
    setExerciseUpdateForm,
    /**
     * 운동정보 수정 form 빌드 함수
     */
    buildUpdateExerciseForm,
    /**
     * 운동정보 수정 요청
     */
    submitExerciseUpdate,

    selectedIdList,
    setSelectedIdList,
    /**
     * 운동정보 배치 수정 요청
     */
    updateExerciseState
  }
};
export default useExercise;
