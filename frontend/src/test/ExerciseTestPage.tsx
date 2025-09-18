import styled from 'styled-components';
import ExerciseApi from '../api/ExerciseApi';
import { ExerciseRequestDto, InstructionCreateDto, ExerciseSimpleResponseDto, ExerciseDetailResponseDto } from '../types/domain/exercise';
import { Page } from '../types/api'; // 'api' 폴더에 Page 타입이 있다고 가정
import { ApiError } from '../types/error';
import { useCallback, useEffect, useState } from 'react';

// --- 폼 데이터 초기 상태 정의 ---
const INITIAL_FORM_STATE = {
  name: '새로운 운동',
  category: '가슴',
  description: '운동에 대한 간단한 요약 설명입니다.',
  isHidden: false,
};
const INITIAL_INSTRUCTIONS_STATE: InstructionCreateDto[] = [
  { stepOrder: 1, description: '1단계 설명' },
];

// ==============================================================================
// === Styled Components (스타일 정의) ===========================================
// ==============================================================================
const PageWrapper = styled.div`
  max-width: var(--container-max);
  margin: 0 auto;
  padding: var(--space-8) var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
`;
const Header = styled.header`
  text-align: center;
  margin-bottom: var(--space-4);
  h1 { font-size: var(--fs-2xl); color: var(--text-1); }
`;
const SectionCard = styled.div`
  background: var(--bg-elev-1);
  border: 1px solid var(--border-1);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-1);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
`;
const StyledTable = styled.table`
  width: 100%;
  border-collapse: collapse;
  text-align: center;
  font-size: var(--fs-sm);
`;
const Th = styled.th`
  background: var(--bg-elev-2);
  padding: var(--space-3);
  border-bottom: 2px solid var(--border-2);
  color: var(--text-2);
`;
const Td = styled.td<{ $isHidden?: boolean }>`
  padding: var(--space-3);
  border-bottom: 1px solid var(--border-1);
  color: ${({ $isHidden }) => ($isHidden ? 'var(--text-3)' : 'var(--text-1)')};
  background-color: ${({ $isHidden }) => ($isHidden ? 'oklch(23% 0.02 27 / 0.5)' : 'transparent')};
  transition: background-color 0.2s ease;
`;
const PaginationControls = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--space-4);
  margin-top: var(--space-4);
`;
const ActionGroup = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  align-items: center;
`;
const Form = styled.form` display: flex; flex-direction: column; gap: var(--space-4); `;
const FormGroup = styled.div` display: flex; flex-direction: column; gap: var(--space-2); `;
const Label = styled.label` font-size: var(--fs-sm); color: var(--text-2); font-weight: 500; `;
const StyledInput = styled.input` width: 100%; `;
const StyledTextarea = styled.textarea` width: 100%; min-height: 80px; resize: vertical; `;
const InstructionItem = styled.div` display: flex; align-items: center; gap: var(--space-2); `;
const Button = styled.button` /* GlobalStyle에서 상속 */ `;

// ==============================================================================
// === Component Logic (컴포넌트 로직) ==========================================
// ==============================================================================
const ExerciseTestPage = () => {
  const [exercisePage, setExercisePage] = useState<Page<ExerciseSimpleResponseDto> | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [targetId, setTargetId] = useState<number | null>(null);
  const [formData, setFormData] = useState(INITIAL_FORM_STATE);
  const [instructions, setInstructions] = useState<InstructionCreateDto[]>(INITIAL_INSTRUCTIONS_STATE);

  const fetchList = useCallback(async (page: number) => {
    try {
      const data = await ExerciseApi.getAllExercises({ page, size: 5, sort: 'id,desc' });
      setExercisePage(data);
    } catch (error) { console.error("❌ 전체 조회 실패:", error); }
  }, []);

  useEffect(() => { fetchList(currentPage); }, [currentPage, fetchList]);

  const handleCheckboxChange = (id: number, checked: boolean) => {
    setSelectedIds(prev => checked ? [...prev, id] : prev.filter(selectedId => selectedId !== id));
  };
  
  const handleBulkAction = async (action: 'activate' | 'inactivate') => {
    if (selectedIds.length === 0) return alert("먼저 항목을 선택해주세요.");
    const actionText = action === 'activate' ? '활성화' : '비활성화';
    if (!window.confirm(`선택된 ${selectedIds.length}개의 항목을 ${actionText} 하시겠습니까?`)) return;

    try {
      if (action === 'activate') {
        await ExerciseApi.activateExercises(selectedIds);
      } else {
        await ExerciseApi.inactivateExercises(selectedIds);
      }
      alert(`일괄 ${actionText} 처리가 완료되었습니다.`);
      setSelectedIds([]);
      fetchList(currentPage);
    } catch (error) { alert(`일괄 ${actionText} 처리에 실패했습니다.` + error); }
  };
  
  const handleLoad = async () => {
    const idToLoad = prompt("불러올 운동의 ID를 입력하세요:");
    if (!idToLoad) return;
    try {
      const id = parseInt(idToLoad, 10);
      const data = await ExerciseApi.getExerciseById(id);
      setTargetId(data.id);
      setFormData({ name: data.name, category: data.category, description: data.description || '', isHidden: data.isHidden });
      setInstructions(data.instructions.map(inst => ({ stepOrder: inst.stepOrder, description: inst.description })));
    } catch (err) {
      const error = err as ApiError;
      alert(`불러오기 실패: ${error.message} (상태 코드: ${error})`);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    const inputValue = type === 'checkbox' ? (e.target as HTMLInputElement).checked : value;
    setFormData(prev => ({ ...prev, [name]: inputValue }));
  };

  const handleInstructionChange = (index: number, e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    const newInstructions = [...instructions];
    newInstructions[index] = { ...newInstructions[index], description: value };
    setInstructions(newInstructions);
  };

  const addInstruction = () => setInstructions([...instructions, { stepOrder: instructions.length + 1, description: '' }]);

  const removeInstruction = (index: number) => {
    const newInstructions = instructions.filter((_, i) => i !== index)
      .map((inst, i) => ({ ...inst, stepOrder: i + 1 }));
    setInstructions(newInstructions);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const requestDto: ExerciseRequestDto = { ...formData, instructions };
    try {
      let response: ExerciseDetailResponseDto;
      if (targetId) {
        response = await ExerciseApi.updateExercise(targetId, requestDto);
        alert('운동이 성공적으로 수정되었습니다!');
      } else {
        response = await ExerciseApi.createExercise(requestDto);
        alert('운동이 성공적으로 생성되었습니다!' + response);
      }
      resetForm();
      fetchList(currentPage);
    } catch (error) { alert('작업에 실패했습니다.' + error); }
  };
  
  const resetForm = () => {
    setTargetId(null);
    setFormData(INITIAL_FORM_STATE);
    setInstructions(INITIAL_INSTRUCTIONS_STATE);
  };

  return (
    <PageWrapper>
      <Header><h1>Exercise 관리 테스트</h1></Header>
      
      <SectionCard>
        <h2 className="fs-lg">운동 목록</h2>
        <ActionGroup>
            <Button className="btn-primary" onClick={() => handleBulkAction('activate')}>선택 활성화</Button>
            <Button className="btn-ghost" onClick={() => handleBulkAction('inactivate')}>선택 비활성화</Button>
            <span>({selectedIds.length}개 선택됨)</span>
        </ActionGroup>
        <StyledTable>
          <thead>
            <tr>
              <Th><input type="checkbox" onChange={(e) => {
                if(e.target.checked) {
                    setSelectedIds(exercisePage?.content.map(ex => ex.id) || []);
                } else {
                    setSelectedIds([]);
                }
              }} /></Th>
              <Th>ID</Th><Th>이름</Th><Th>카테고리</Th><Th>상태</Th>
            </tr>
          </thead>
          <tbody>
            {exercisePage?.content.map(ex => (
              <tr key={ex.id}>
                <Td><input type="checkbox" checked={selectedIds.includes(ex.id)} onChange={(e) => handleCheckboxChange(ex.id, e.target.checked)} /></Td>
                <Td $isHidden={ex.isHidden}>{ex.id}</Td>
                <Td $isHidden={ex.isHidden}>{ex.name}</Td>
                <Td $isHidden={ex.isHidden}>{ex.category}</Td>
                <Td $isHidden={ex.isHidden}>{ex.isHidden ? '비활성' : '활성'}</Td>
              </tr>
            ))}
          </tbody>
        </StyledTable>
        <PaginationControls>
          <Button onClick={() => setCurrentPage(p => p - 1)} disabled={exercisePage?.first}>이전</Button>
          <span>페이지 {exercisePage ? exercisePage.number + 1 : 0} / {exercisePage?.totalPages}</span>
          <Button onClick={() => setCurrentPage(p => p + 1)} disabled={exercisePage?.last}>다음</Button>
        </PaginationControls>
      </SectionCard>

      <SectionCard>
        <h2 className="fs-lg">개별 기능 테스트</h2>
        <ActionGroup>
          <Button onClick={handleLoad}>ID로 불러오기 (수정용)</Button>
        </ActionGroup>
      </SectionCard>

      <SectionCard>
        <Form onSubmit={handleSubmit}>
          <h2 className="fs-lg">{targetId ? `운동 #${targetId} 수정` : '새로운 운동 생성'}</h2>
          
          <FormGroup>
            <Label htmlFor="name">이름</Label>
            <StyledInput type="text" id="name" name="name" value={formData.name} onChange={handleInputChange} required />
          </FormGroup>
          
          <FormGroup>
            <Label htmlFor="category">카테고리</Label>
            <StyledInput type="text" id="category" name="category" value={formData.category} onChange={handleInputChange} required />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="description">요약 설명</Label>
            <StyledTextarea id="description" name="description" value={formData.description || ''} onChange={handleInputChange} />
          </FormGroup>

          <FormGroup>
            <Label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
              <StyledInput type="checkbox" name="isHidden" checked={formData.isHidden} onChange={handleInputChange} style={{width: 'auto', marginRight: '8px'}} />
              비활성화 (숨김 처리)
            </Label>
          </FormGroup>

          <FormGroup>
            <Label>단계별 설명</Label>
            {instructions.map((inst, index) => (
              <InstructionItem key={index}>
                <span>{inst.stepOrder}.</span>
                <StyledInput type="text" value={inst.description} onChange={(e) => handleInstructionChange(index, e)} required />
                <Button type="button" onClick={() => removeInstruction(index)}>-</Button>
              </InstructionItem>
            ))}
            <Button type="button" onClick={addInstruction} className="btn-ghost">설명 추가 (+)</Button>
          </FormGroup>

          <ActionGroup style={{ marginTop: 'var(--space-4)' }}>
            <Button type="submit" className="btn-primary">{targetId ? '수정 완료' : '생성하기'}</Button>
            <Button type="button" onClick={resetForm} className="btn-ghost">폼 초기화</Button>
          </ActionGroup>
        </Form>
      </SectionCard>
    </PageWrapper>
  );
};

export default ExerciseTestPage;

