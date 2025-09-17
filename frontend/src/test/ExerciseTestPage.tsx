import React, { useState } from 'react';
import ExerciseApi from '../api/ExerciseApi';
import { InstructionCreateDto } from '../types/domain/exercise';
import { Nullable } from '../types/common';

const ExerciseTestPage = () => {
  // --- 1. 상태(State) 관리 ---
  // 기본 운동 정보를 관리하는 상태
  const [exerciseData, setExerciseData] = useState({
    name: '새로운 운동',
    category: '가슴',
    description: '운동에 대한 간단한 요약 설명입니다.',
    isHidden : false
  });

  // 동적인 단계별 설명을 관리하는 상태
  const [instructions, setInstructions] = useState<InstructionCreateDto[]>([
    { stepOrder: 1, description: '1단계 설명' },
    { stepOrder: 2, description: '2단계 설명' },
  ]);

  const [targetId, setTargetId] = useState<Nullable<number>>(null);

  // --- 2. 이벤트 핸들러 함수 ---
  // 기본 정보 입력 필드 변경 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setExerciseData(prev => ({ ...prev, [name]: value }));
  };

  // 단계별 설명 입력 필드 변경 핸들러
  const handleInstructionChange = (index: number, e: React.ChangeEvent<HTMLInputElement>) => {
    const { value } = e.target;
    const newInstructions = [...instructions];
    newInstructions[index] = { ...newInstructions[index], description: value };
    setInstructions(newInstructions);
  };

  // 단계별 설명 추가 핸들러
  const addInstruction = () => {
    setInstructions([
      ...instructions,
      { stepOrder: instructions.length + 1, description: '' },
    ]);
  };

  // 단계별 설명 삭제 핸들러
  const removeInstruction = (index: number) => {
    const newInstructions = instructions.filter((_, i) => i !== index);
    // 삭제 후 stepOrder 재정렬
    const reorderedInstructions = newInstructions.map((inst, i) => ({
      ...inst,
      stepOrder: i + 1,
    }));
    setInstructions(reorderedInstructions);
  };

  const handleLoad = async () => {
    const idToLoad = prompt("수정할 운동의 ID를 입력하세요:");
    if (!idToLoad) return;

    try {
      const id = parseInt(idToLoad, 10);
      const data = await ExerciseApi.getExerciseById(id);
      console.log('✅ 불러오기 성공:', data);
      
      // 불러온 데이터로 폼 상태를 업데이트
      setTargetId(data.id);
      setExerciseData({
        name: data.name,
        category: data.category,
        description: data.description || '',
        isHidden: data.isHidden,
      });
      // 불러온 instruction 데이터로 상태 업데이트 (id는 제외)
      setInstructions(data.instructions.map(inst => ({
        stepOrder: inst.stepOrder,
        description: inst.description,
      })));

    } catch (error) {
      console.error('❌ 불러오기 실패:', error);
      alert('운동 정보 불러오기에 실패했습니다.');
    }
  };

  // 폼 제출 (API 호출) 핸들러
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    const requestDto = { ...exerciseData, instructions };

    try {
      if (targetId) { // ID가 있으면 '수정'
        console.log('API 수정 요청:', targetId, requestDto);
        const response = await ExerciseApi.updateExercise(targetId, requestDto);
        console.log('✅ 수정 성공:', response);
        alert('운동이 성공적으로 수정되었습니다!');
        setTargetId(null);

      } else { // ID가 없으면 '생성'
        console.log('API 생성 요청:', requestDto);
        const response = await ExerciseApi.createExercise(requestDto);
        console.log('✅ 생성 성공:', response);
        alert('운동이 성공적으로 생성되었습니다!');
      }
    } catch (error) {
      console.error('❌ 작업 실패:', error);
      alert('작업에 실패했습니다.');
    }
  };
  

  // --- 3. UI (JSX) ---
  return (
    <div style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
      <h1>Exercise 테스트</h1>
      <div>
        <button onClick={() => ExerciseApi.getExerciseById(2).then(response => console.log(response))}>ID 2번 운동 정보 확인</button>
        <button onClick={() => ExerciseApi.getAllExercises({ page: 0, size: 10 }).then(response => console.log(response))}>운동 정보 리스트</button>
      </div>
      <hr />

      <div style={{ border: '2px solid blue', padding: '10px' }}>
        <h3>운동 수정 테스트</h3>
        <button type-="button" onClick={handleLoad}>수정할 운동 불러오기</button>
        <p>수정 대상 ID: {targetId || '없음'}</p>
      </div>
      
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <h2>{targetId ? `운동 #${targetId} 수정` : '운동 생성'}</h2>
        
        <div>
          <label>운동 이름: </label>
          <input type="text" name="name" value={exerciseData.name} onChange={handleInputChange} required />
        </div>

        <div>
          <label>카테고리: </label>
          <input type="text" name="category" value={exerciseData.category} onChange={handleInputChange} required />
        </div>

        <div>
          <label>요약 설명: </label>
          <textarea name="description" value={exerciseData.description} onChange={handleInputChange} />
        </div>

        <div>
          <h3>단계별 설명</h3>
          {instructions.map((inst, index) => (
            <div key={index} style={{ marginBottom: '5px' }}>
              <span>{inst.stepOrder}. </span>
              <input
                type="text"
                value={inst.description}
                onChange={(e) => handleInstructionChange(index, e)}
                style={{ width: '300px' }}
                required
              />
              <button type="button" onClick={() => removeInstruction(index)} style={{ marginLeft: '10px' }}>삭제</button>
            </div>
          ))}
          <button type="button" onClick={addInstruction}>설명 추가</button>
        </div>

        <button type="submit">{targetId ? '수정하기' : '생성하기'}</button>
      </form>
    </div>
  );
};

export default ExerciseTestPage;