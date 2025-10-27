import React from 'react';
import useExercise from '../hooks/useExercise';
import ExerciseCard from '../components/exercise/ExerciseCard';

const ExerciseListPage = () => {
  const { exerciseList } = useExercise();

  return (
    <div>
      {exerciseList.map(exercise => {
        return <ExerciseCard exercise={exercise}/>
      })}
    </div>
  );
};

export default ExerciseListPage;