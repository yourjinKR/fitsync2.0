import useExercise from '../hooks/useExercise';

const ExerciseHookTestPage = () => {
  const {
    exerciseList,
    selectedExercise,
    loadExerciseDetailInfo
  } = useExercise();

  return (
    <div>
      <div>
        <h1>운동리스트</h1>
        {exerciseList.map((exercise, i) => (
          <div key={i} onClick={() => loadExerciseDetailInfo(exercise.id)}>{exercise.name}</div>
        ))}
      </div>
      {selectedExercise && (
        <div>
          <h1>운동 상세보기</h1>
          <h3>운동명</h3>
          <div>{selectedExercise.name}</div>
        </div>
      )}
    </div>
  );
};

export default ExerciseHookTestPage;