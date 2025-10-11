import { ExerciseIsHiddenBatchUpdateRequest } from './../types/domain/exercise/update';
import ExerciseApi from '../api/ExerciseApi';
import { Pageable } from '../types/api';
import { ExerciseDetailResponse, ExerciseSimpleResponse, ExerciseUpdateRequest } from '../types/domain/exercise';
import { useCallback, useEffect, useState } from "react";

const INIT_PAGE: Pageable = { page: 0, size: 30, sort: "id,desc" };
const EMPTY_LIST: ExerciseSimpleResponse[] = [];
const EMPTY_DETAIL: ExerciseDetailResponse | null = null;
const EMPTY_UPDATE_FORM: ExerciseUpdateRequest | null = null;
const EMPTY_BATCH: ExerciseIsHiddenBatchUpdateRequest = {
  activate: { exerciseIds: [] },
  deactivate: { exerciseIds: [] },
};
// 상태 불변성 위반: exerciseListPage.page++ 는 기존 state를 직접 변경합니다. 반드시 함수형 업데이트로 바꾸세요.
// 비동기-상태 경쟁 조건: nextExercisePage() 후 곧바로 ExerciseApi.getAllExercises(exerciseListPage) 를 호출하면 이전 페이지 값으로 API가 불립니다. 페이지 변경 → useEffect에서 fetch 트리거가 안전합니다.
// 초깃값 undefined: exerciseList, exerciseDetail, exerciseUpdateForm, selectedIdList 가 undefined일 수 있어 런타임 에러 위험이 있습니다. 타입 안전한 초깃값을 두세요.
// 로딩/에러/hasMore 누락: UI 제어를 위해 loading, error, hasMore가 필요합니다.
// 불필요 재렌더/함수 참조 안정성: 외부에 노출하는 함수는 useCallback으로 참조 안정성을 확보하세요.
// 여러 API 분기 로직: 활성/비활성 일괄 업데이트 분기는 명확한 가드와 early-return으로 간결하게.

const useExercise = () => {
  const [exerciseListPage, setExerciseListPage] = useState<Pageable>(INIT_PAGE);
  const [exerciseList, setExerciseList] = useState<ExerciseSimpleResponse[]>(EMPTY_LIST);
  const [exerciseDetail, setExerciseDetail] = useState<ExerciseDetailResponse>(EMPTY_DETAIL);
  const [exerciseUpdateForm, setExerciseUpdateForm] = useState<ExerciseUpdateRequest>(EMPTY_UPDATE_FORM);
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
    setExerciseListPage({...exerciseListPage, page : exerciseListPage.page++});
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
    setExerciseDetail(response);
  };

  // 운동 정보 수정
  const updateExerciseInfo = async () => {
    const response = await ExerciseApi.updateExercise(exerciseDetail.id, exerciseUpdateForm);
    setExerciseDetail(response);
  };

  // 운동정보 활성화/비활성화 업데이트
  const updateExerciseState = async () => {
    let response:void;
    if (selectedIdList.activate.exerciseIds.length && !selectedIdList.deactivate.exerciseIds.length) {
      response = await ExerciseApi.activateExercises(selectedIdList.activate);
    } 
    if (!selectedIdList.activate.exerciseIds.length && selectedIdList.deactivate.exerciseIds.length) {
      response = await ExerciseApi.inactivateExercises(selectedIdList.deactivate);
    }
    if (selectedIdList.activate.exerciseIds.length && selectedIdList.deactivate.exerciseIds.length) {
      response = await ExerciseApi.updateActivationStates(selectedIdList);
    }
    console.log(response);
    clearExercisePage();
    fetchExerciseList();
  }


  // 운동정보 -> 루틴으로 저장
  // 사용자가 운동 리스트에서 운동 선택
  // 해당 운동들을 참고하여 routineCreateRequest를 만듬

  return {
    exerciseListPage,
    setExerciseListPage,

    exerciseList,
    setExerciseList,

    fetchExerciseList,
    nextExercisePage,
    clearExercisePage,

    exerciseDetail,
    setExerciseDetail,
    loadExerciseDetailInfo,
    
    exerciseUpdateForm,
    setExerciseUpdateForm,
    updateExerciseInfo,

    selectedIdList,
    setSelectedIdList,
    updateExerciseState
  }
};
export default useExercise;
