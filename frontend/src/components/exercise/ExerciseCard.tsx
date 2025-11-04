import React from 'react';

const ExerciseCard = ({exercise}) => {
  return (
    <div>
      <h3>{exercise.name}</h3>
      <p>{exercise.category}</p>
    </div>
  );
};

export default ExerciseCard;