import { ExerciseIsHiddenBatchUpdateRequest } from './../types/domain/exercise/update';
import ExerciseApi from '../api/ExerciseApi';
import { Pageable } from '../types/api';
import { ExerciseDetailResponse, ExerciseSimpleResponse, ExerciseUpdateRequest } from '../types/domain/exercise';
import { useState } from "react";

const INIT_PAGE: Pageable = { page: 0, size: 100, sort: "id.desc" };
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
  const fetchExerciseList = async () => {
    const response = await ExerciseApi.getAllExercises(exerciseListPage); 
    setExerciseList(response.content);
  };
  
  // 다음 페이지 세팅
  const nextExercisePage = () => {
    setExerciseListPage({...exerciseListPage, page : exerciseListPage.page++});
  };

  // 페이징 초기화
  const clearExercisePage = () => {
    setExerciseListPage(INIT_PAGE);
  }
  
  //운동 정보 다음 페이지 불러오기 (무한 스크롤)
  const loadMoreExerciseList = async () => {
    nextExercisePage();
    const response = await ExerciseApi.getAllExercises(exerciseListPage);
    setExerciseList([...exerciseList, ...response.content]);
  };

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
    loadMoreExerciseList,

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
