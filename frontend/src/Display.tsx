import React from 'react';
import TestPage from './test/DBConnectPage';
import { Route, Routes } from 'react-router-dom';

const Display = () => {
  return (
    <div>
      <h1>FitSync 2 페이지입니당</h1>
      <Routes>
        <Route path="/" element={<TestPage/>}/>
      </Routes>
    </div>
  );
};

export default Display;