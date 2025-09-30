// src/pages/RoutineTestPage.tsx

import React, { useState, useEffect, useCallback } from 'react';
import styled from 'styled-components';
import ExerciseApi from '../api/ExerciseApi';
import RoutineApi from '../api/RoutineApi';
import { ExerciseSimpleResponseDto } from '../types/domain/exercise';
import {
  RoutineCreateRequestDto,
  RoutineCreateResponseDto,
  RoutineDetailResponseDto,
  RoutineUpdateRequestDto,
  RoutineSimpleResponseDto,
  RoutineDeleteRequestDto,
} from '../types/domain/routine';
import { ApiError } from '../types/error';

// dnd
import {
  DragDropContext,
  Droppable,
  Draggable,
  DropResult,
} from '@hello-pangea/dnd';
import { Pageable } from '../types/api';

/* ------------------------------------------------------------------ */
/* 1) 이 페이지에서만 쓰는 타입                                         */
/* ------------------------------------------------------------------ */
type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // zero-based page index
  size: number;
};

type BuildingExercise = {
  exerciseId: number;
  name: string; // UI 표시용
  sets: {
    displayOrder: number;
    weightKg: number;
    reps: number;
    distanceMeter: number;
    durationSecond: number;
  }[];
};

type EditingSet = {
  id: number | null; // RoutineSetDto.id
  displayOrder: number;
  weightKg: number;
  reps: number;
  distanceMeter: number;
  durationSecond: number;
};

type EditingExercise = {
  // routine_exercises.id
  routineExerciseId: number; // 조회 응답의 ex.id (신규는 0으로 표시)
  exerciseId: number;        // 실제 운동 id
  name: string;              // UI 표시용
  displayOrder: number;
  memo: string;
  sets: EditingSet[];
};

type EditingRoutine = {
  id: number;
  name: string;
  displayOrder: number;
  memo: string;
  routineExercises: EditingExercise[];
};

/* ------------------------------------------------------------------ */
/* 2) 초기 상태                                                        */
/* ------------------------------------------------------------------ */
const INITIAL_CREATE_STATE = {
  name: '나의 새로운 루틴',
  memo: '이 루틴은...',
  exercises: [] as BuildingExercise[],
};

const INITIAL_EDIT_STATE: EditingRoutine | null = null;

/* ------------------------------------------------------------------ */
/* 3) 유틸                                                             */
/* ------------------------------------------------------------------ */
const reorderWithDisplayOrder = <T extends { displayOrder: number }>(items: T[]) =>
  items.map((it, i) => ({ ...it, displayOrder: i + 1 }));

/* ------------------------------------------------------------------ */
/* 4) 스타일                                                            */
/* ------------------------------------------------------------------ */
const PageWrapper = styled.div`
  max-width: var(--container-max);
  margin: 0 auto;
  padding: var(--space-8) var(--space-4);
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-8);
  @media (min-width: 1024px) {
    grid-template-columns: 400px 1fr;
  }
`;
const Header = styled.header`
  grid-column: 1 / -1;
  text-align: center;
  h1 { font-size: var(--fs-2xl); color: var(--text-1); }
`;
const SectionCard = styled.section`
  background: var(--bg-elev-1);
  border: 1px solid var(--border-1);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-1);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
  align-self: start;
`;
const ExerciseList = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 400px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
`;
const ExerciseListItem = styled.li`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-3);
  border-radius: var(--radius-md);
  background: var(--bg-app);
  border: 1px solid var(--border-2);
`;
const Button = styled.button``;
const FormGroup = styled.div` display: flex; flex-direction: column; gap: var(--space-2); `;
const Label = styled.label` font-size: var(--fs-sm); color: var(--text-2); font-weight: 500; `;
const StyledInput = styled.input` width: 100%; `;
const StyledTextarea = styled.textarea` width: 100%; min-height: 60px; resize: vertical; `;
const RoutineBuilder = styled.div` display: flex; flex-direction: column; gap: var(--space-6); `;
const SelectedExerciseCard = styled.div`
  background: var(--bg-elev-2);
  padding: var(--space-4);
  border-radius: var(--radius-md);
  border-left: 4px solid var(--color-primary);
  transition: background .15s ease, box-shadow .15s ease;
`;
const SetInputGrid = styled.div`
  display: grid;
  grid-template-columns: auto 1fr 1fr auto;
  gap: var(--space-2);
  align-items: center;
  font-size: var(--fs-sm);
`;
const ActionGroup = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  align-items: center;
`;
const ResultDisplay = styled.div`
  background: var(--bg-elev-2);
  padding: var(--space-4);
  border-radius: var(--radius-md);
  font-size: var(--fs-sm);
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 500px;
  overflow-y: auto;
`;

/* ------------------------------------------------------------------ */
/* 5) 컴포넌트                                                         */
/* ------------------------------------------------------------------ */
const RoutineTestPage = () => {
  const [allExercises, setAllExercises] = useState<ExerciseSimpleResponseDto[]>([]);

  // 생성용 상태
  const [createState, setCreateState] = useState(INITIAL_CREATE_STATE);
  const [createdRoutineInfo, setCreatedRoutineInfo] = useState<RoutineCreateResponseDto | null>(null);

  // 조회/수정용 상태
  const [viewRoutineId, setViewRoutineId] = useState('');
  const [viewedRoutine, setViewedRoutine] = useState<RoutineDetailResponseDto | null>(null);
  const [editState, setEditState] = useState<EditingRoutine | null>(INITIAL_EDIT_STATE);

  // 루틴 리스트(사용자)
  const [userIdForList, setUserIdForList] = useState('3'); // 테스트용 default
  const [routinePage, setRoutinePage] = useState<Page<RoutineSimpleResponseDto> | null>(null);
  const [pageParams, setPageParams] = useState({ page: 0, size: 20, sort: 'displayOrder,asc' });
  const [isLoadingList, setIsLoadingList] = useState(false);

  /* ------------------ 전체 운동 목록 ------------------ */
  const fetchAllExercises = useCallback(async () => {
    try {
      const data = await ExerciseApi.getAllExercises({ page: 0, size: 100, sort: 'name,asc' });
      setAllExercises(data.content);
    } catch (error) {
      console.error('❌ 전체 운동 조회 실패:', error);
    }
  }, []);

  useEffect(() => { fetchAllExercises(); }, [fetchAllExercises]);

  /* ------------------ 루틴 리스트 조회 ------------------ */
  const fetchRoutineList = useCallback(async () => {
    try {
      setIsLoadingList(true);
      const uid = parseInt(userIdForList, 10);
      const data = await RoutineApi.getRoutineList(uid, pageParams as Pageable);
      setRoutinePage(data);
    } catch (e) {
      console.error('❌ 루틴 리스트 조회 실패:', e);
      alert('루틴 리스트 조회에 실패했습니다.');
    } finally {
      setIsLoadingList(false);
    }
  }, [userIdForList, pageParams]);

  useEffect(() => { fetchRoutineList(); }, [fetchRoutineList]);

  // 루틴 리스트 drag end (routine 간 displayOrder 정렬)
  const onRoutineDragEnd = (result: DropResult) => {
    if (!routinePage) return;
    const { destination, source } = result;
    if (!destination || destination.index === source.index) return;

    const newContent = Array.from(routinePage.content);
    const [removed] = newContent.splice(source.index, 1);
    newContent.splice(destination.index, 0, removed);

    const normalized = reorderWithDisplayOrder(newContent);
    setRoutinePage({ ...routinePage, content: normalized });
  };

  // 루틴 리스트의 displayOrder를 서버에 저장 (개별 PUT)
  const saveRoutineOrder = async () => {
    if (!routinePage) return;
    try {
      for (const r of routinePage.content) {
        // 1) 상세 조회
        const detail = await RoutineApi.getRoutine(r.id);
        // 2) 리스트에서 조정한 displayOrder 반영, 내부는 유지
        const dto: RoutineUpdateRequestDto = {
          id: detail.id,
          name: detail.name,
          displayOrder: r.displayOrder ?? 0,
          memo: detail.memo ?? '',
          routineExercises: detail.exercises
            .sort((a, b) => a.displayOrder - b.displayOrder)
            .map(ex => ({
              id: ex.id,
              exerciseId: ex.exercise.id,
              displayOrder: ex.displayOrder,
              memo: ex.memo ?? '',
              sets: ex.sets
                .sort((s1, s2) => s1.displayOrder - s2.displayOrder)
                .map(s => ({
                  id: s.id ?? null,
                  displayOrder: s.displayOrder,
                  weightKg: s.weightKg,
                  reps: s.reps,
                  distanceMeter: s.distanceMeter,
                  durationSecond: s.durationSecond,
                })),
            })),
        };
        await RoutineApi.updateRoutine(detail.id, dto);
      }
      alert('루틴 displayOrder가 저장되었습니다.');
    } catch (e) {
      console.error('❌ 루틴 순서 저장 실패:', e);
      alert('루틴 순서 저장 중 오류가 발생했습니다.');
    }
  };

  /* ------------------ 생성기 핸들러 ------------------ */
  const handleAddToCreate = (exercise: ExerciseSimpleResponseDto) => {
    setCreateState(prev => ({
      ...prev,
      exercises: [
        ...prev.exercises,
        {
          exerciseId: exercise.id,
          name: exercise.name,
          sets: [{ displayOrder: 1, weightKg: 60, reps: 10, distanceMeter: 0, durationSecond: 0 }],
        },
      ],
    }));
  };

  const handleCreateInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setCreateState(prev => ({ ...prev, [name]: value }));
  };

  type SetEditableField = 'weightKg' | 'reps' | 'distanceMeter' | 'durationSecond';

  const handleCreateSetChange =
    (exIndex: number, setIndex: number, field: SetEditableField) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const valueNum = Number(e.target.value);
      setCreateState(prev => {
        const exercises = [...prev.exercises];
        const targetSet = { ...exercises[exIndex].sets[setIndex], [field]: valueNum };
        exercises[exIndex].sets = [
          ...exercises[exIndex].sets.slice(0, setIndex),
          targetSet,
          ...exercises[exIndex].sets.slice(setIndex + 1),
        ];
        return { ...prev, exercises };
      });
    };

  const addCreateSet = (exIndex: number) => {
    const newExercises = [...createState.exercises];
    const newOrder = newExercises[exIndex].sets.length + 1;
    newExercises[exIndex].sets.push({ displayOrder: newOrder, weightKg: 0, reps: 0, distanceMeter: 0, durationSecond: 0 });
    setCreateState(prev => ({ ...prev, exercises: newExercises }));
  };

  const removeCreateSet = (exIndex: number, setIndex: number) => {
    const newExercises = [...createState.exercises];
    newExercises[exIndex].sets.splice(setIndex, 1);
    newExercises[exIndex].sets.forEach((s, i) => (s.displayOrder = i + 1));
    setCreateState(prev => ({ ...prev, exercises: newExercises }));
  };

  const removeCreateExercise = (exIndex: number) => {
    const newExercises = createState.exercises.filter((_, i) => i !== exIndex);
    setCreateState(prev => ({ ...prev, exercises: newExercises }));
  };

  const handleCreateRoutine = async () => {
    if (!createState.name) return alert('루틴 이름을 입력해주세요.');
    if (createState.exercises.length === 0) return alert('운동을 하나 이상 추가해주세요.');

    const requestDto: RoutineCreateRequestDto = {
      ownerId: 1, // 테스트용
      writerId: 1, // 테스트용
      name: createState.name,
      memo: createState.memo,
      displayOrder: 0,
      exercises: createState.exercises.map((ex, index) => ({
        exerciseId: ex.exerciseId,
        displayOrder: index + 1,
        memo: '',
        sets: ex.sets.map(set => ({
          id: null,
          displayOrder: set.displayOrder,
          weightKg: set.weightKg,
          reps: set.reps,
          distanceMeter: set.distanceMeter,
          durationSecond: set.durationSecond,
        })),
      })),
    };

    try {
      const response = await RoutineApi.createRoutine(requestDto);
      setCreatedRoutineInfo(response);
      alert(`루틴 생성 성공! ID: ${response.id}`);
      setCreateState(INITIAL_CREATE_STATE);
    } catch (err) {
      const error = err as ApiError;
      alert(`루틴 생성 실패: ${error.message}`);
    }
  };

  /* ------------------ 조회 & 편집 상태로 변환 ------------------ */
  const handleViewRoutine = async () => {
    if (!viewRoutineId) return alert('조회할 루틴 ID를 입력하세요.');
    try {
      const id = parseInt(viewRoutineId, 10);
      const data = await RoutineApi.getRoutine(id);
      setViewedRoutine(data);
    } catch (err) {
      const error = err as ApiError;
      alert(`루틴 조회 실패: ${error.message}`);
      setViewedRoutine(null);
    }
  };

  // 조회된 루틴을 편집 모드로 불러오기
  const loadToEdit = () => {
    if (!viewedRoutine) return;
    const mapped: EditingRoutine = {
      id: viewedRoutine.id,
      name: viewedRoutine.name,
      displayOrder: viewedRoutine.displayOrder ?? 0,
      memo: viewedRoutine.memo ?? '',
      routineExercises: viewedRoutine.exercises
        .sort((a, b) => a.displayOrder - b.displayOrder)
        .map((ex, idx) => ({
          routineExerciseId: ex.id,
          exerciseId: ex.exercise.id,
          name: ex.exercise.name,
          displayOrder: idx + 1,
          memo: ex.memo ?? '',
          sets: ex.sets
            .sort((s1, s2) => s1.displayOrder - s2.displayOrder)
            .map(s => ({
              id: s.id ?? null,
              displayOrder: s.displayOrder,
              weightKg: s.weightKg,
              reps: s.reps,
              distanceMeter: s.distanceMeter,
              durationSecond: s.durationSecond,
            })),
        })),
    };
    setEditState(mapped);
    alert('편집 모드로 불러왔습니다.');
  };

  /* ------------------ 편집기 핸들러 ------------------ */
  const handleEditHeaderChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    if (!editState) return;
    const { name, value } = e.target;
    setEditState({ ...editState, [name]: value } as EditingRoutine);
  };

  const addExerciseToEdit = (exercise: ExerciseSimpleResponseDto) => {
    if (!editState) {
      alert('왼쪽에서 루틴을 조회 후, "편집 모드로 불러오기"를 먼저 눌러주세요.');
      return;
    }
    const nextOrder = editState.routineExercises.length + 1;

    const newExercise: EditingExercise = {
      routineExerciseId: 0, // 신규 표시용(서버 전송 시 id:null)
      exerciseId: exercise.id,
      name: exercise.name,
      displayOrder: nextOrder,
      memo: '',
      sets: [{ id: null, displayOrder: 1, weightKg: 0, reps: 0, distanceMeter: 0, durationSecond: 0 }],
    };

    setEditState(prev => prev ? { ...prev, routineExercises: [...prev.routineExercises, newExercise] } : prev);
  };


  const handleEditSetChange =
    (exIndex: number, setIndex: number, field: SetEditableField) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      if (!editState) return;
      const valueNum = Number(e.target.value);
      const newExercises = [...editState.routineExercises];
      const sets = [...newExercises[exIndex].sets];
      sets[setIndex] = { ...sets[setIndex], [field]: valueNum };
      newExercises[exIndex] = { ...newExercises[exIndex], sets };
      setEditState({ ...editState, routineExercises: newExercises });
    };

  const addEditSet = (exIndex: number) => {
    if (!editState) return;
    const newExercises = [...editState.routineExercises];
    const nextOrder = newExercises[exIndex].sets.length + 1;
    newExercises[exIndex].sets.push({
      id: null,
      displayOrder: nextOrder,
      weightKg: 0,
      reps: 0,
      distanceMeter: 0,
      durationSecond: 0,
    });
    setEditState({ ...editState, routineExercises: newExercises });
  };

  const removeEditSet = (exIndex: number, setIndex: number) => {
    if (!editState) return;
    const newExercises = [...editState.routineExercises];
    newExercises[exIndex].sets.splice(setIndex, 1);
    newExercises[exIndex].sets.forEach((s, i) => (s.displayOrder = i + 1));
    setEditState({ ...editState, routineExercises: newExercises });
  };

  const removeEditExercise = (exIndex: number) => {
    if (!editState) return;
    const newExercises = editState.routineExercises.filter((_, i) => i !== exIndex);
    setEditState({ ...editState, routineExercises: reorderWithDisplayOrder(newExercises) });
  };

  const handleUpdateRoutine = async () => {
    if (!editState) return alert('편집할 루틴이 없습니다.');
    if (!editState.name) return alert('루틴 이름을 입력해주세요.');
    if (editState.routineExercises.length === 0) return alert('최소 1개의 운동이 필요합니다.');

    const dto: RoutineUpdateRequestDto = {
      id: editState.id,
      name: editState.name,
      displayOrder: editState.displayOrder ?? 0,
      memo: editState.memo ?? '',
      routineExercises: editState.routineExercises.map(ex => ({
        id: ex.routineExerciseId === 0 ? null : ex.routineExerciseId,
        exerciseId: ex.exerciseId,
        displayOrder: ex.displayOrder,
        memo: ex.memo ?? '',
        sets: ex.sets.map(s => ({
          id: s.id,
          displayOrder: s.displayOrder,
          weightKg: s.weightKg,
          reps: s.reps,
          distanceMeter: s.distanceMeter,
          durationSecond: s.durationSecond,
        })),
      })),
    };

    try {
      const updated = await RoutineApi.updateRoutine(editState.id, dto);
      setViewedRoutine(updated);
      alert('루틴 수정 성공!');
    } catch (err) {
      const error = err as ApiError;
      alert(`루틴 수정 실패: ${error.message}`);
    }
  };

  /* ------------------ 편집기: 드래그 정렬 ------------------ */
  // 운동 카드 드래그 종료
  const onEditExerciseDragEnd = (result: DropResult) => {
    if (!editState) return;
    const { destination, source } = result;
    if (!destination || destination.droppableId !== 'edit-exercise-list' || destination.index === source.index) return;

    const newExercises = Array.from(editState.routineExercises);
    const [removed] = newExercises.splice(source.index, 1);
    newExercises.splice(destination.index, 0, removed);

    setEditState({ ...editState, routineExercises: reorderWithDisplayOrder(newExercises) });
  };

  // 세트 드래그 종료(운동별로 별도 컨텍스트를 씀)
  const onEditSetDragEnd = (exIndex: number) => (result: DropResult) => {
    if (!editState) return;
    const { destination, source } = result;
    if (!destination || destination.index === source.index) return;

    const exercises = [...editState.routineExercises];
    const sets = Array.from(exercises[exIndex].sets);
    const [removed] = sets.splice(source.index, 1);
    sets.splice(destination.index, 0, removed);

    exercises[exIndex] = { ...exercises[exIndex], sets: reorderWithDisplayOrder(sets) };
    setEditState({ ...editState, routineExercises: exercises });
  };

  // ----------------- 삭제: 리스트 항목에서 사용 -----------------
  const deleteRoutineFromList = async (r: RoutineSimpleResponseDto) => {
    if (!confirm(`정말로 삭제할까요?\n[${r.name}] (ID: ${r.id})`)) return;
    try {
      const req: RoutineDeleteRequestDto = { id: r.id, ownerId: r.ownerId };
      await RoutineApi.deleteRoutine(r.id, req);

      // 리스트에서 제거 + displayOrder 재정렬
      setRoutinePage(prev => {
        if (!prev) return prev;
        const next = prev.content.filter(item => item.id !== r.id);
        return { ...prev, content: reorderWithDisplayOrder(next) };
      });

      // 현재 상세/편집 중이던 루틴이면 비워주기
      if (String(r.id) === viewRoutineId) {
        setViewRoutineId('');
        setViewedRoutine(null);
        setEditState(null);
      }

      alert('삭제 완료되었습니다.');
    } catch (e) {
      console.error('❌ 루틴 삭제 실패:', e);
      alert('루틴 삭제 중 오류가 발생했습니다.');
    }
  };

  // ----------------- 삭제: 상세/편집 화면에서 사용 -----------------
  const deleteViewedRoutine = async () => {
    if (!viewedRoutine) return;
    if (!confirm(`정말로 삭제할까요?\n[${viewedRoutine.name}] (ID: ${viewedRoutine.id})`)) return;
    try {
      const req: RoutineDeleteRequestDto = { id: viewedRoutine.id, ownerId: viewedRoutine.owner.id };
      console.log("data check : ", viewedRoutine);
      await RoutineApi.deleteRoutine(viewedRoutine.id, req);

      // 리스트 최신화 시도
      await fetchRoutineList();

      // 상세/편집 상태 초기화
      setViewRoutineId('');
      setViewedRoutine(null);
      setEditState(null);

      alert('삭제 완료되었습니다.');
    } catch (e) {
      console.error('❌ 루틴 삭제 실패:', e);
      alert('루틴 삭제 중 오류가 발생했습니다.');
    }
  };

  /* ------------------ 렌더링 ------------------ */
  return (
    <>
      <Header><h1>루틴 관리 테스트</h1></Header>
      {/* TODO : 추후 삭제 필요 */}
      <button onClick={
        () => {
          RoutineApi.updateRoutineHeader(11, {id : 11, name : "직접 수정 테스트123", displayOrder : 1, memo : "무야호!!!"})
        }
      }>
        테스트
      </button>
      <PageWrapper>
        {/* === Column 1: 운동 목록 / 사용자 루틴 리스트 / 루틴 조회 === */}
        <aside>

          {/* 사용자 루틴 리스트 + 드래그 정렬 */}
          <SectionCard>
            <h2 className="fs-lg">사용자 루틴 리스트</h2>
            <FormGroup>
              <Label htmlFor="uid">User ID</Label>
              <StyledInput id="uid" type="number" value={userIdForList} onChange={e => setUserIdForList(e.target.value)} />
            </FormGroup>
            <ActionGroup>
              <Button onClick={fetchRoutineList} className="btn-ghost">다시 불러오기</Button>
            </ActionGroup>

            {isLoadingList && <p style={{ color: 'var(--text-3)' }}>불러오는 중…</p>}

            {routinePage && (
              <>
                <DragDropContext onDragEnd={onRoutineDragEnd}>
                  <Droppable droppableId="routine-list" type="ROUTINE">
                    {(dp) => (
                      <div ref={dp.innerRef} {...dp.droppableProps} style={{ display:'flex', flexDirection:'column', gap:8 }}>
                        {routinePage.content.map((r, index) => (
                          <Draggable key={r.id.toString()} draggableId={r.id.toString()} index={index}>
                            {(drag, snap) => (
                              <div
                                ref={drag.innerRef}
                                {...drag.draggableProps}
                                {...drag.dragHandleProps}
                                className="card"
                                style={{
                                  padding:'12px',
                                  borderLeft:'4px solid var(--color-primary)',
                                  boxShadow: snap.isDragging ? 'var(--shadow-2)' : 'var(--shadow-1)',
                                  ...drag.draggableProps.style,
                                }}
                              >
                                <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center' }}>
                                  <div>
                                    <strong>{r.displayOrder || index + 1}. {r.name}</strong>
                                    <div className="fs-sm" style={{ color:'var(--text-3)' }}>
                                      ID: {r.id} / updated: {new Date(r.updatedAt).toLocaleString()}
                                    </div>
                                  </div>
                                  <div style={{ display:'flex', gap:8 }}>
                                    <Button onClick={() => setViewRoutineId(String(r.id))} className="btn-ghost">상세 조회</Button>
                                    <Button onClick={loadToEdit} disabled={String(r.id) !== viewRoutineId} className="btn-primary">편집 모드</Button>
                                    <Button onClick={() => deleteRoutineFromList(r)} style={{ background: 'var(--danger)', color: '#fff' }} title="이 루틴을 삭제합니다">삭제</Button>
                                  </div>
                                </div>
                              </div>
                            )}
                          </Draggable>
                        ))}
                        {dp.placeholder}
                      </div>
                    )}
                  </Droppable>
                </DragDropContext>

                {/* 간단 페이지네이션 */}
                <ActionGroup style={{ marginTop: 12 }}>
                  <Button
                    disabled={routinePage.number <= 0}
                    onClick={() => setPageParams(p => ({ ...p, page: p.page - 1 }))}
                  >이전</Button>
                  <span className="fs-sm">페이지 {routinePage.number + 1} / {routinePage.totalPages}</span>
                  <Button
                    disabled={routinePage.number + 1 >= routinePage.totalPages}
                    onClick={() => setPageParams(p => ({ ...p, page: p.page + 1 }))}
                  >다음</Button>
                </ActionGroup>

                {/* 루틴 순서 저장 */}
                <ActionGroup>
                  <Button onClick={saveRoutineOrder} className="btn-primary" style={{ width:'100%' }}>
                    루틴 displayOrder 저장
                  </Button>
                </ActionGroup>
              </>
            )}
          </SectionCard>

          {/* 전체 운동 목록 */}
          <SectionCard>
            <h2 className="fs-lg">전체 운동 목록</h2>
            <ExerciseList>
              {allExercises.map(ex => (
                <ExerciseListItem key={ex.id}>
                  <span>{ex.name} <small>({ex.category})</small></span>
                  <div style={{ display: 'flex', gap: 8 }}>
                    {/* 생성용 추가 */}
                    <Button
                      onClick={() => handleAddToCreate(ex)}
                      disabled={createState.exercises.some(e => e.exerciseId === ex.id)}
                      className="btn-primary"
                      style={{ padding: '4px 8px', fontSize: 'var(--fs-xs)' }}
                    >
                      생성용 추가
                    </Button>
                    {/* 편집에 추가 */}
                    <Button
                      onClick={() => addExerciseToEdit(ex)}
                      disabled={!editState || (editState?.routineExercises.some(e => e.exerciseId === ex.id))}
                      className="btn-ghost"
                      style={{ padding: '4px 8px', fontSize: 'var(--fs-xs)' }}
                      title={editState ? '현재 편집 중인 루틴에 이 운동을 추가합니다' : '편집 모드가 아닙니다'}
                    >
                      편집에 추가
                    </Button>
                  </div>
                </ExerciseListItem>
              ))}
            </ExerciseList>
          </SectionCard>

          {/* 루틴 상세 조회 */}
          <SectionCard style={{ marginTop: 'var(--space-8)' }}>
            <h2 className="fs-lg">루틴 상세 조회</h2>
            <FormGroup>
              <Label htmlFor="viewId">루틴 ID</Label>
              <StyledInput
                id="viewId"
                type="number"
                value={viewRoutineId}
                onChange={e => setViewRoutineId(e.target.value)}
                placeholder="조회할 ID 입력"
              />
            </FormGroup>
            <ActionGroup>
              <Button onClick={handleViewRoutine} className="btn-primary">조회하기</Button>
              <Button onClick={loadToEdit} disabled={!viewedRoutine} className="btn-ghost">
                편집 모드로 불러오기
              </Button>
            </ActionGroup>

            {viewedRoutine && (
              <>
                <ResultDisplay>
                  <h3>{viewedRoutine.name} (ID: {viewedRoutine.id})</h3>
                  <p>메모: {viewedRoutine.memo || '없음'}</p>
                  <hr />
                  {viewedRoutine.exercises.map((ex, i) => (
                    <div key={i} style={{ marginBottom: '12px' }}>
                      <strong>{ex.displayOrder}. {ex.exercise.name}</strong>
                      {ex.sets.map((set, j) => (
                        <div key={j} style={{ paddingLeft: '16px', fontSize: '1.3rem' }}>
                          - {set.displayOrder}세트: {set.weightKg}kg / {set.reps}회
                        </div>
                      ))}
                    </div>
                  ))}
                </ResultDisplay>
                <ActionGroup>
                  <Button onClick={deleteViewedRoutine} style={{ background: 'var(--danger)', color: '#fff', width: '100%' }}>
                    이 루틴 삭제
                  </Button>
                </ActionGroup>
              </>
            )}
          </SectionCard>
        </aside>

        {/* === Column 2: 생성기 + 편집기 === */}
        <main>
          {/* 생성기 */}
          <SectionCard>
            <h2 className="fs-lg">루틴 생성기</h2>
            <RoutineBuilder>
              <FormGroup>
                <Label htmlFor="createName">루틴 이름</Label>
                <StyledInput id="createName" name="name" value={createState.name} onChange={handleCreateInputChange} />
              </FormGroup>
              <FormGroup>
                <Label htmlFor="createMemo">루틴 메모</Label>
                <StyledTextarea id="createMemo" name="memo" value={createState.memo} onChange={handleCreateInputChange} />
              </FormGroup>

              <hr style={{ border: 0, borderTop: `1px solid var(--border-1)` }} />

              {createState.exercises.length === 0 && (
                <p style={{ color: 'var(--text-3)', textAlign: 'center' }}>왼쪽 목록에서 운동을 추가해주세요.</p>
              )}

              {createState.exercises.map((ex, exIndex) => (
                <SelectedExerciseCard key={ex.exerciseId}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                    <h3 className="fs-md">{exIndex + 1}. {ex.name}</h3>
                    <Button onClick={() => removeCreateExercise(exIndex)} style={{ background: 'var(--danger)', color: 'white' }}>운동 제외</Button>
                  </div>
                  {ex.sets.map((set, setIndex) => (
                    <SetInputGrid key={setIndex}>
                      <span>{set.displayOrder}세트</span>
                      <StyledInput
                        type="number"
                        value={set.weightKg}
                        onChange={handleCreateSetChange(exIndex, setIndex, 'weightKg')}
                        placeholder="무게(kg)"
                      />
                      <StyledInput
                        type="number"
                        value={set.reps}
                        onChange={handleCreateSetChange(exIndex, setIndex, 'reps')}
                        placeholder="횟수"
                      />
                      <Button onClick={() => removeCreateSet(exIndex, setIndex)} style={{ fontSize: 'var(--fs-lg)' }}>-</Button>
                    </SetInputGrid>
                  ))}
                  <Button onClick={() => addCreateSet(exIndex)} className="btn-ghost" style={{ width: '100%', marginTop: '12px' }}>세트 추가 (+)</Button>
                </SelectedExerciseCard>
              ))}
            </RoutineBuilder>

            <ActionGroup style={{ marginTop: 'var(--space-4)' }}>
              <Button onClick={handleCreateRoutine} className="btn-primary" style={{ width: '100%', padding: '16px' }}>루틴 생성하기</Button>
            </ActionGroup>

            {createdRoutineInfo && (
              <ResultDisplay>
                <strong>✅ 생성 성공!</strong>
                <p>생성된 루틴 ID: {createdRoutineInfo.id}</p>
                <p>왼쪽 조회 폼에 입력하여 확인할 수 있습니다.</p>
              </ResultDisplay>
            )}
          </SectionCard>

          {/* 편집기 (운동/세트 드래그 정렬) */}
          <SectionCard>
            <h2 className="fs-lg">루틴 수정기</h2>
            {!editState && <p style={{ color: 'var(--text-3)' }}>왼쪽에서 루틴을 조회한 뒤, “편집 모드로 불러오기”를 클릭하세요.</p>}
            {editState && (
              <>
                <RoutineBuilder>
                  <FormGroup>
                    <Label htmlFor="editName">루틴 이름</Label>
                    <StyledInput id="editName" name="name" value={editState.name} onChange={handleEditHeaderChange} />
                  </FormGroup>
                  <FormGroup>
                    <Label htmlFor="editMemo">루틴 메모</Label>
                    <StyledTextarea id="editMemo" name="memo" value={editState.memo} onChange={handleEditHeaderChange} />
                  </FormGroup>

                  <hr style={{ border: 0, borderTop: `1px solid var(--border-1)` }} />

                  {editState.routineExercises.length === 0 && (
                    <p style={{ color: 'var(--text-3)', textAlign: 'center' }}>현재 편집 중인 운동이 없습니다.</p>
                  )}

                  {/* 운동 카드 드래그 컨텍스트 */}
                  <DragDropContext onDragEnd={onEditExerciseDragEnd}>
                    <Droppable droppableId="edit-exercise-list" type="EXERCISE">
                      {(dp) => (
                        <div ref={dp.innerRef} {...dp.droppableProps} style={{ display:'flex', flexDirection:'column', gap:12 }}>
                          {editState.routineExercises.map((ex, exIndex) => (
                            <Draggable
                              key={`${ex.routineExerciseId}-${ex.exerciseId}`}
                              draggableId={`${ex.routineExerciseId}-${ex.exerciseId}`}
                              index={exIndex}
                            >
                              {(drag, snap) => (
                                <SelectedExerciseCard
                                  ref={drag.innerRef}
                                  {...drag.draggableProps}
                                  style={{
                                    ...drag.draggableProps.style,
                                    boxShadow: snap.isDragging ? 'var(--shadow-2)' : 'var(--shadow-1)',
                                  }}
                                >
                                  {/* 드래그 핸들: 제목 바 전체 */}
                                  <div
                                    {...drag.dragHandleProps}
                                    style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12, cursor: 'grab' }}
                                  >
                                    <h3 className="fs-md">
                                      {ex.displayOrder}. {ex.name}
                                      {ex.routineExerciseId === 0 && <small style={{ marginLeft: 8, color: 'var(--text-3)' }}>(신규)</small>}
                                    </h3>
                                    <Button onClick={() => removeEditExercise(exIndex)} style={{ background: 'var(--danger)', color: 'white' }}>
                                      운동 제거
                                    </Button>
                                  </div>

                                  {/* 세트 드래그 컨텍스트 (운동별로 분리) */}
                                  <DragDropContext onDragEnd={onEditSetDragEnd(exIndex)}>
                                    <Droppable droppableId={`set-list-${exIndex}`} type={`SET-${exIndex}`}>
                                      {(dpSet) => (
                                        <div ref={dpSet.innerRef} {...dpSet.droppableProps}>
                                          {ex.sets.map((set, setIndex) => (
                                            <Draggable
                                              key={`${set.id ?? 'new'}-${setIndex}`}
                                              draggableId={`set-${exIndex}-${set.id ?? 'new'}-${setIndex}`}
                                              index={setIndex}
                                            >
                                              {(dragSet, snapSet) => (
                                                <div
                                                  ref={dragSet.innerRef}
                                                  {...dragSet.draggableProps}
                                                  style={{
                                                    ...dragSet.draggableProps.style,
                                                    border: '1px dashed var(--border-2)',
                                                    padding: '8px',
                                                    marginBottom: '8px',
                                                    borderRadius: '8px',
                                                    boxShadow: snapSet.isDragging ? 'var(--shadow-2)' : undefined,
                                                  }}
                                                >
                                                  {/* 핸들 */}
                                                  <div {...dragSet.dragHandleProps} style={{ cursor:'grab', opacity:.8, marginBottom:8 }}>
                                                    ⋮⋮
                                                  </div>

                                                  <SetInputGrid>
                                                    <span>{set.displayOrder}세트</span>
                                                    <StyledInput
                                                      type="number"
                                                      value={set.weightKg}
                                                      onChange={handleEditSetChange(exIndex, setIndex, 'weightKg')}
                                                      placeholder="무게(kg)"
                                                    />
                                                    <StyledInput
                                                      type="number"
                                                      value={set.reps}
                                                      onChange={handleEditSetChange(exIndex, setIndex, 'reps')}
                                                      placeholder="횟수"
                                                    />
                                                    <Button onClick={() => removeEditSet(exIndex, setIndex)} style={{ fontSize:'var(--fs-lg)' }}>-</Button>
                                                  </SetInputGrid>
                                                </div>
                                              )}
                                            </Draggable>
                                          ))}
                                          {dpSet.placeholder}
                                        </div>
                                      )}
                                    </Droppable>
                                  </DragDropContext>

                                  <Button onClick={() => addEditSet(exIndex)} className="btn-ghost" style={{ width: '100%', marginTop: '12px' }}>
                                    세트 추가 (+)
                                  </Button>
                                </SelectedExerciseCard>
                              )}
                            </Draggable>
                          ))}
                          {dp.placeholder}
                        </div>
                      )}
                    </Droppable>
                  </DragDropContext>
                </RoutineBuilder>

                <ActionGroup style={{ marginTop: 'var(--space-4)' }}>
                  <Button onClick={handleUpdateRoutine} className="btn-primary" style={{ width: '100%', padding: '16px' }}>
                    루틴 수정 저장하기
                  </Button>
                </ActionGroup>
              </>
            )}
          </SectionCard>
        </main>
      </PageWrapper>
    </>
  );
};

export default RoutineTestPage;
