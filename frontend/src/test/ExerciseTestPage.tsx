import React, { useState } from 'react';
import ExerciseApi from '../api/ExerciseApi';
import { ExerciseCreateRequestDto, InstructionCreateDto } from '../types/domain/exercise';

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

  // 폼 제출 (API 호출) 핸들러
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); // 폼 기본 제출 동작 방지
    
    // API에 보낼 DTO 객체 생성
    const requestDto: ExerciseCreateRequestDto = {
      ...exerciseData,
      instructions: instructions,
    };

    try {
      console.log('API 요청 데이터:', requestDto);
      const response = await ExerciseApi.createExercise(requestDto);
      console.log('✅ 생성 성공:', response);
      alert('운동이 성공적으로 생성되었습니다!');
    } catch (error) {
      console.error('❌ 생성 실패:', error);
      alert('운동 생성에 실패했습니다.');
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
      
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <h2>운동 생성 테스트</h2>
        
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

        <button type="submit" style={{ marginTop: '20px', padding: '10px' }}>운동 생성하기</button>
      </form>
    </div>
  );
};

export default ExerciseTestPage;