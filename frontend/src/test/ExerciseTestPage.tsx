import React, { useState, useEffect, useCallback } from 'react';
import styled from 'styled-components';
import ExerciseApi from '../api/ExerciseApi';
import RoutineApi from '../api/RoutineApi';
import { ExerciseSimpleResponseDto } from '../types/domain/exercise';
// --- 타입 임포트 수정 ---
import { 
  RoutineCreateRequestDto, 
  RoutineCreateResponseDto, 
  RoutineDetailResponseDto 
} from '../types/domain/routine';
import { ApiError } from '../types/error';

// --- 타입 정의: 페이지 내부에서 사용하는 '만들고 있는 루틴'의 타입 ---
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

// --- 초기 상태 정의 ---
const INITIAL_ROUTINE_STATE = {
  name: '나의 새로운 루틴',
  memo: '이 루틴은...',
  exercises: [] as BuildingExercise[],
};

// ==============================================================================
// === Styled Components (스타일 정의 - 이전과 동일) ===============================
// ==============================================================================
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

// ==============================================================================
// === Component Logic (컴포넌트 로직) ==========================================
// ==============================================================================
const RoutineTestPage = () => {
  const [allExercises, setAllExercises] = useState<ExerciseSimpleResponseDto[]>([]);
  const [myRoutine, setMyRoutine] = useState(INITIAL_ROUTINE_STATE);
  // --- 상태 타입 수정 ---
  const [createdRoutineInfo, setCreatedRoutineInfo] = useState<RoutineCreateResponseDto | null>(null);
  const [viewRoutineId, setViewRoutineId] = useState('');
  const [detailedRoutine, setDetailedRoutine] = useState<RoutineDetailResponseDto | null>(null);

  // 1. 전체 운동 목록 불러오기 (로직 동일)
  const fetchAllExercises = useCallback(async () => {
    try {
      const data = await ExerciseApi.getAllExercises({ page: 0, size: 100, sort: 'name,asc' });
      setAllExercises(data.content);
    } catch (error) { console.error("❌ 전체 운동 조회 실패:", error); }
  }, []);

  useEffect(() => { fetchAllExercises(); }, [fetchAllExercises]);

  // 2. 루틴 생성기 관련 핸들러 (로직 동일)
  const handleAddToRoutine = (exercise: ExerciseSimpleResponseDto) => {
    setMyRoutine(prev => ({
      ...prev,
      exercises: [...prev.exercises, {
        exerciseId: exercise.id,
        name: exercise.name,
        sets: [{ displayOrder: 1, weightKg: 60, reps: 10, distanceMeter: 0, durationSecond: 0 }],
      }],
    }));
  };
  
  const handleRoutineInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setMyRoutine(prev => ({ ...prev, [name]: value }));
  };
  
  const handleSetChange = (exIndex: number, setIndex: number, e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const newExercises = [...myRoutine.exercises];
    newExercises[exIndex].sets[setIndex] = {
      ...newExercises[exIndex].sets[setIndex],
      [name]: Number(value)
    };
    setMyRoutine(prev => ({ ...prev, exercises: newExercises }));
  };

  const addSet = (exIndex: number) => {
    const newExercises = [...myRoutine.exercises];
    const newOrder = newExercises[exIndex].sets.length + 1;
    newExercises[exIndex].sets.push({ displayOrder: newOrder, weightKg: 0, reps: 0, distanceMeter: 0, durationSecond: 0 });
    setMyRoutine(prev => ({ ...prev, exercises: newExercises }));
  };

  const removeSet = (exIndex: number, setIndex: number) => {
    const newExercises = [...myRoutine.exercises];
    newExercises[exIndex].sets.splice(setIndex, 1);
    newExercises[exIndex].sets.forEach((set, i) => set.displayOrder = i + 1);
    setMyRoutine(prev => ({ ...prev, exercises: newExercises }));
  };
  
  const removeExerciseFromRoutine = (exIndex: number) => {
    const newExercises = myRoutine.exercises.filter((_, i) => i !== exIndex);
    setMyRoutine(prev => ({ ...prev, exercises: newExercises }));
  };
  
  // 3. 루틴 생성 API 호출
  const handleCreateRoutine = async () => {
    if (!myRoutine.name) return alert('루틴 이름을 입력해주세요.');
    if (myRoutine.exercises.length === 0) return alert('운동을 하나 이상 추가해주세요.');

    // --- DTO 타입 수정 ---
    const requestDto: RoutineCreateRequestDto = {
      ownerId: 1, // 테스트용 하드코딩
      writerId: 1, // 테스트용 하드코딩
      name: myRoutine.name,
      memo: myRoutine.memo,
      displayOrder: 0, // 미사용
      exercises: myRoutine.exercises.map((ex, index) => ({
        exerciseId: ex.exerciseId,
        displayOrder: index + 1,
        memo: '', // 미사용
        sets: ex.sets,
      })),
    };

    try {
      const response = await RoutineApi.createRoutine(requestDto);
      setCreatedRoutineInfo(response);
      alert(`루틴 생성 성공! ID: ${response.id}`);
      setMyRoutine(INITIAL_ROUTINE_STATE);
    } catch (err) {
      const error = err as ApiError;
      alert(`루틴 생성 실패: ${error.message}`);
    }
  };

  // 4. 루틴 조회 API 호출 (로직 동일)
  const handleViewRoutine = async () => {
    if (!viewRoutineId) return alert('조회할 루틴 ID를 입력하세요.');
    try {
      const id = parseInt(viewRoutineId, 10);
      const data = await RoutineApi.getRoutine(id);
      setDetailedRoutine(data);
    } catch (err) {
      const error = err as ApiError;
      alert(`루틴 조회 실패: ${error.message}`);
      setDetailedRoutine(null);
    }
  };

  // 5. 렌더링 (로직 동일)
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
                  <Button 
                    onClick={() => handleAddToRoutine(ex)}
                    disabled={myRoutine.exercises.some(e => e.exerciseId === ex.id)}
                    className="btn-primary"
                    style={{padding: '4px 8px', fontSize: 'var(--fs-xs)'}}
                  >
                    추가
                  </Button>
                </ExerciseListItem>
              ))}
            </ExerciseList>
          </SectionCard>
          
          <SectionCard style={{marginTop: 'var(--space-8)'}}>
            <h2 className="fs-lg">루틴 상세 조회</h2>
            <FormGroup>
              <Label htmlFor="viewId">루틴 ID</Label>
              <StyledInput id="viewId" type="number" value={viewRoutineId} onChange={e => setViewRoutineId(e.target.value)} placeholder="조회할 ID 입력"/>
            </FormGroup>
            <Button onClick={handleViewRoutine} className="btn-primary">조회하기</Button>
            {detailedRoutine && (
              <ResultDisplay>
                <h3>{detailedRoutine.name} (ID: {detailedRoutine.id})</h3>
                <p>메모: {detailedRoutine.memo || '없음'}</p>
                <hr />
                {detailedRoutine.exercises.map((ex, i) => (
                  <div key={i} style={{marginBottom: '12px'}}>
                    <strong>{ex.displayOrder}. {ex.exercise.name}</strong>
                    {ex.sets.map((set, j) => (
                      <div key={j} style={{paddingLeft: '16px', fontSize: '1.3rem'}}>
                        - {set.displayOrder}세트: {set.weightKg}kg / {set.reps}회
                      </div>
                    ))}
                  </div>
                ))}
              </ResultDisplay>
            )}
          </SectionCard>
        </aside>

        {/* === Column 2: 루틴 생성기 === */}
        <main>
          <SectionCard>
            <h2 className="fs-lg">루틴 생성기</h2>
            <RoutineBuilder>
              <FormGroup>
                <Label htmlFor="name">루틴 이름</Label>
                <StyledInput id="name" name="name" value={myRoutine.name} onChange={handleRoutineInputChange} />
              </FormGroup>
              <FormGroup>
                <Label htmlFor="memo">루틴 메모</Label>
                <StyledTextarea id="memo" name="memo" value={myRoutine.memo} onChange={handleRoutineInputChange} />
              </FormGroup>
              
              <hr style={{border: 0, borderTop: `1px solid var(--border-1)`}}/>

              {myRoutine.exercises.length === 0 && <p style={{color: 'var(--text-3)', textAlign: 'center'}}>왼쪽 목록에서 운동을 추가해주세요.</p>}
              
              {myRoutine.exercises.map((ex, exIndex) => (
                <SelectedExerciseCard key={ex.exerciseId}>
                  <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px'}}>
                    <h3 className="fs-md">{exIndex + 1}. {ex.name}</h3>
                    <Button onClick={() => removeExerciseFromRoutine(exIndex)} style={{background: 'var(--danger)', color: 'white'}}>운동 제외</Button>
                  </div>
                  {ex.sets.map((set, setIndex) => (
                    <SetInputGrid key={setIndex}>
                      <span>{set.displayOrder}세트</span>
                      <StyledInput type="number" name="weightKg" value={set.weightKg} onChange={e => handleSetChange(exIndex, setIndex, e)} placeholder="무게(kg)" />
                      <StyledInput type="number" name="reps" value={set.reps} onChange={e => handleSetChange(exIndex, setIndex, e)} placeholder="횟수" />
                      <Button onClick={() => removeSet(exIndex, setIndex)} style={{fontSize: 'var(--fs-lg)'}}>-</Button>
                    </SetInputGrid>
                  ))}
                  <Button onClick={() => addSet(exIndex)} className="btn-ghost" style={{width: '100%', marginTop: '12px'}}>세트 추가 (+)</Button>
                </SelectedExerciseCard>
              ))}

            </RoutineBuilder>
            <ActionGroup style={{marginTop: 'var(--space-4)'}}>
              <Button onClick={handleCreateRoutine} className="btn-primary" style={{width: '100%', padding: '16px'}}>루틴 생성하기</Button>
            </ActionGroup>

            {createdRoutineInfo && (
              <ResultDisplay>
                <strong>✅ 생성 성공!</strong>
                <p>생성된 루틴 ID: {createdRoutineInfo.id}</p>
                <p>위 ID를 왼쪽 조회 폼에 입력하여 확인할 수 있습니다.</p>
              </ResultDisplay>
            )}
          </SectionCard>
        </main>
      </PageWrapper>
    </>
  );
};

export default RoutineTestPage;