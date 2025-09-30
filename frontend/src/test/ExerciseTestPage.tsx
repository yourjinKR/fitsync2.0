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
} from '../types/domain/routine';
import { ApiError } from '../types/error';

/* ------------------------------------------------------------------ */
/* 1) 이 페이지에서만 쓰는 편집용 타입                                 */
/* ------------------------------------------------------------------ */
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
  routineExerciseId: number; // 조회 응답의 ex.id
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
/* 3) 스타일들 (기존 유지)                                             */
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
/* 4) 컴포넌트                                                         */
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
      routineExerciseId: 0,            // 신규 항목 표시용(클라이언트 내부 식별). 서버 전송 시 id:null 처리
      exerciseId: exercise.id,
      name: exercise.name,
      displayOrder: nextOrder,
      memo: '',
      sets: [
        { id: null, displayOrder: 1, weightKg: 0, reps: 0, distanceMeter: 0, durationSecond: 0 },
      ],
    };

    setEditState(prev => prev
      ? { ...prev, routineExercises: [...prev.routineExercises, newExercise] }
      : prev
    );
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
    newExercises.forEach((ex, i) => (ex.displayOrder = i + 1));
    setEditState({ ...editState, routineExercises: newExercises });
  };

  const handleUpdateRoutine = async () => {
    if (!editState) return alert('편집할 루틴이 없습니다.');
    if (!editState.name) return alert('루틴 이름을 입력해주세요.');
    if (editState.routineExercises.length === 0) return alert('최소 1개의 운동이 필요합니다.');

    // 권장 타입 수정(Nullable<number>)을 적용했다면 아래처럼 간단히:
    const dto: RoutineUpdateRequestDto = {
      id: editState.id,
      name: editState.name,
      displayOrder: editState.displayOrder ?? 0,
      memo: editState.memo ?? '',
      routineExercises: editState.routineExercises.map(ex => ({
        // 신규: routineExerciseId === 0 → id:null
        id: ex.routineExerciseId === 0 ? null : ex.routineExerciseId,
        exerciseId: ex.exerciseId,
        displayOrder: ex.displayOrder,
        memo: ex.memo ?? '',
        sets: ex.sets.map(s => ({
          id: s.id, // null이면 신규
          displayOrder: s.displayOrder,
          weightKg: s.weightKg,
          reps: s.reps,
          distanceMeter: s.distanceMeter,
          durationSecond: s.durationSecond,
        })),
      })),
    };

    /* 만약 타입을 못 바꾼다면(여전히 id: number) — 조건부 속성 주입 우회안:
    const dto = {
      id: editState.id,
      name: editState.name,
      displayOrder: editState.displayOrder ?? 0,
      memo: editState.memo ?? '',
      routineExercises: editState.routineExercises.map(ex => {
        const base = {
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
        };
        return ex.routineExerciseId === 0
          ? (base as any)                  // id를 아예 빼서 전송(백엔드에서 허용되는 경우에만)
          : ({ id: ex.routineExerciseId, ...base } as any);
      }),
    } as unknown as RoutineUpdateRequestDto;
    */
    try {
      const updated = await RoutineApi.updateRoutine(editState.id, dto);
      setViewedRoutine(updated);
      alert('루틴 수정 성공!');
    } catch (err) {
      const error = err as ApiError;
      alert(`루틴 수정 실패: ${error.message}`);
    }
  };


  /* ------------------ 렌더링 ------------------ */
  return (
    <>
      <Header><h1>루틴 관리 테스트</h1></Header>
      <PageWrapper>
        {/* === Column 1: 운동 목록 & 루틴 조회 === */}
        <aside>
          <SectionCard>
            <h2 className="fs-lg">전체 운동 목록</h2>
            <ExerciseList>
              {allExercises.map(ex => (
                <ExerciseListItem key={ex.id}>
                  <span>{ex.name} <small>({ex.category})</small></span>
                  <div style={{ display: 'flex', gap: 8 }}>
                    {/* 기존: 생성용 추가 */}
                    <Button
                      onClick={() => handleAddToCreate(ex)}
                      disabled={createState.exercises.some(e => e.exerciseId === ex.id)}
                      className="btn-primary"
                      style={{ padding: '4px 8px', fontSize: 'var(--fs-xs)' }}
                    >
                      생성용 추가
                    </Button>

                    {/* 신규: 편집에 추가 (편집 모드일 때만 활성화) */}
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
              <Button
                onClick={loadToEdit}
                disabled={!viewedRoutine}
                className="btn-ghost"
              >
                편집 모드로 불러오기
              </Button>
            </ActionGroup>

            {viewedRoutine && (
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

          {/* 편집기 */}
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

                  {editState.routineExercises.map((ex, exIndex) => (
                    <SelectedExerciseCard key={`${ex.routineExerciseId}-${ex.exerciseId}`}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                        <h3 className="fs-md">{ex.displayOrder}. {ex.name}</h3>
                        <Button onClick={() => removeEditExercise(exIndex)} style={{ background: 'var(--danger)', color: 'white' }}>운동 제거</Button>
                      </div>
                      {ex.sets.map((set, setIndex) => (
                        <SetInputGrid key={`${set.id ?? 'new'}-${setIndex}`}>
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
                          <Button onClick={() => removeEditSet(exIndex, setIndex)} style={{ fontSize: 'var(--fs-lg)' }}>-</Button>
                        </SetInputGrid>
                      ))}
                      <Button onClick={() => addEditSet(exIndex)} className="btn-ghost" style={{ width: '100%', marginTop: '12px' }}>세트 추가 (+)</Button>
                    </SelectedExerciseCard>
                  ))}
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
