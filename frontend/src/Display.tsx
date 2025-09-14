import React from 'react';
import { Route, Routes } from 'react-router-dom';
import Home from './pages/Home';

const Display = () => {
  return (
    <div>
      <Routes>
        {/* 로그인 여부가 상관 없는 페이지 */}
        <Route path="/" element={<Home/>}/>
      </Routes>
    </div>
  );
};

export default Display;