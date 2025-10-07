import React, { useCallback, useEffect, useState } from 'react';
import { ExerciseCreateRequestDto, ExerciseDetailResponseDto, ExerciseSimpleResponseDto, MetricRequirement } from '../types/domain/exercise';
import ExerciseApi from '../api/ExerciseApi';

const ExerciseTestPage = () => {
  const init = {
    id: 0,
    name: '',
    category: '',
    description: null,
    isHidden: false,
    createdAt: '',
    updatedAt : '',
    instructions: [],
    metricRequirement: {
      weightKgStatus: MetricRequirement.FORBIDDEN,
      repsStatus: MetricRequirement.FORBIDDEN,
      distanceMeterStatus: MetricRequirement.FORBIDDEN,
      durationSecondStatus: MetricRequirement.FORBIDDEN,
    },
  };
  enum Mode {
    NEW, MODIFY
  };

  const [allExercises, setAllExercises] = useState<ExerciseSimpleResponseDto[]>([]);
  const [selectedExercise, setSelectedExercise] = useState<ExerciseCreateRequestDto & ExerciseDetailResponseDto>(init);
  // const [newExercise, setNewExercise] = useState<ExerciseCreateRequestDto>(init);
  const [mode, setMode] = useState<Mode>(Mode.NEW);

  const fetchAllExercises = useCallback(async () => {
    try {
      const data = await ExerciseApi.getAllExercises({ page: 0, size: 30, sort: 'id,desc' });
      setAllExercises(data.content);
    } catch (error) {
      console.error('❌ 전체 운동 조회 실패:', error);
    }
  }, []);

  useEffect(() => { fetchAllExercises(); }, [fetchAllExercises]);

  const loadExerciseDetailInfo = async (exerciseId : number) => {
    const response = await ExerciseApi.getExerciseById(exerciseId);
    console.log(response);
    
    setSelectedExercise(response);
    setMode(Mode.MODIFY);
  }

  const handleSelectedExercise = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, type, value, checked } = e.target;

    // checkbox는 boolean, 나머지는 string
    const raw: string | boolean = type === 'checkbox' ? checked : value;

    // 1) instructions.i.field  (예: "instructions.2.description")
    if (name.startsWith('instructions.')) {
      const [, idxStr, field] = name.split('.'); // ['instructions', '2', 'description']
      const index = Number(idxStr);

      setSelectedExercise(prev => {
        const next = prev.instructions.map((inst, i) =>
          i === index ? { ...inst, [field as 'description' | 'stepOrder']: raw } : inst
        );
        return { ...prev, instructions: next };
      });

      return;
    }

    // 2) metricRequirement.field  (예: "metricRequirement.repsStatus")
    if (name.startsWith('metricRequirement.')) {
      const key = name.split('.')[1] as keyof typeof selectedExercise.metricRequirement; // 'repsStatus' 등
      setSelectedExercise(prev => ({
        ...prev,
        metricRequirement: {
          ...prev.metricRequirement,
          [key]: raw as 'FORBIDDEN' | 'OPTIONAL' | 'REQUIRED',
        },
      }));
      return;
    }

    // 3) 최상위 필드 (name, category, description, isHidden 등)
    setSelectedExercise(prev => ({
      ...prev,
      [name]: raw,
    }));
  };

  // TODO : 깊은복사? 얕은복사? 알아보기
  const addInstruction = (e:React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();

    const instructions = selectedExercise.instructions;
    instructions.push({stepOrder : instructions.length, description : "운동 설명" + instructions.length});
    
    setSelectedExercise({...selectedExercise, });
  }
  
  const removeInstruction = (e:React.MouseEvent<HTMLButtonElement>, stepOrder:number) => {
    e.preventDefault();
    console.log(stepOrder);
    console.log(selectedExercise.instructions);
    
    let instructions = selectedExercise.instructions;
    instructions = instructions.filter((inst) => inst.stepOrder !== stepOrder);

    setSelectedExercise({...selectedExercise, instructions : instructions});
  }

  const createMode = () => {
    setMode(Mode.NEW);
    setSelectedExercise(init);
  }

  const handleExerciseSubmit = async (e:React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (mode === Mode.NEW) {
      const response = await ExerciseApi.createExercise(selectedExercise);
      console.log(response);
    } else {
      const response = await ExerciseApi.updateExercise(selectedExercise.id, selectedExercise);
      console.log(response);
    }
  }


  return (
    <div>
      <div>
        {mode === Mode.NEW ? (<h1>운동 생성</h1>) : (<h1>운동 수정</h1>) }
        {mode === Mode.MODIFY && (<button onClick={createMode}>운동 생성하기</button>)}
        <form>
          <h3>운동명</h3>
          <input type="text" name="name" value={selectedExercise.name} onChange={(e) => handleSelectedExercise(e)}/>
          <h3>카테고리</h3>
          <input type="text" name="category" value={selectedExercise.category} onChange={(e) => handleSelectedExercise(e)}/>
          <h3>간단설명</h3>
          <input type="text" name="description" value={selectedExercise.description} onChange={(e) => handleSelectedExercise(e)}/>
          <br/>
          
          <h3>운동설명</h3>
          {selectedExercise.instructions.map((inst, i) => (
            <div>
              <input type="text" name={`instructions.${i}.description`} value={inst.description} key={inst.stepOrder} onChange={(e) => handleSelectedExercise(e)}/>
              <button onClick={(e) => removeInstruction(e, inst.stepOrder)}>X</button>
            </div>
          ))} 
          <button onClick={addInstruction}>운동 설명 추가</button>

          <h3>운동 세트값 권한</h3>

          <div>
            <div>중량</div>
            <select
              name="metricRequirement.weightKgStatus"
              value={selectedExercise.metricRequirement.weightKgStatus}
              onChange={handleSelectedExercise}
            >
              <option value="FORBIDDEN">금지</option>
              <option value="OPTIONAL">선택</option>
              <option value="REQUIRED">필수</option>
            </select>

            <div>횟수</div>
            <select
              name="metricRequirement.repsStatus"
              value={selectedExercise.metricRequirement.repsStatus}
              onChange={handleSelectedExercise}
            >
              <option value="FORBIDDEN">금지</option>
              <option value="OPTIONAL">선택</option>
              <option value="REQUIRED">필수</option>
            </select>

            <div>시간</div>
            <select
              name="metricRequirement.durationSecondStatus"
              value={selectedExercise.metricRequirement.durationSecondStatus}
              onChange={handleSelectedExercise}
            >
              <option value="FORBIDDEN">금지</option>
              <option value="OPTIONAL">선택</option>
              <option value="REQUIRED">필수</option>
            </select>

            <div>거리</div>
            <select
              name="metricRequirement.distanceMeterStatus"
              value={selectedExercise.metricRequirement.distanceMeterStatus}
              onChange={handleSelectedExercise}
            >
              <option value={"FORBIDDEN"}>금지</option>
              <option value={"OPTIONAL"}>선택</option>
              <option value={"REQUIRED"}>필수</option>s
            </select>
          </div>

          <button onClick={handleExerciseSubmit}>수정 혹은 생성 버튼임</button>
        </form>
      </div>

      <h1>운동 리스트</h1>
      {allExercises.map(ex => (
        <div key={ex.id} onClick={() => loadExerciseDetailInfo(ex.id)}>
          {ex.name}
        </div>
      ))}
    </div>
  );
};

export default ExerciseTestPage;