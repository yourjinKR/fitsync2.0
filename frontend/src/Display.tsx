import React from 'react';
import { Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import AdminMain from './pages/AdminMain';

const Display = () => {
  return (
    <div>
      <Routes>
        <Route path="/" element={<Home/>}/>
      </Routes>
      <Routes>
        <Route path="/admin" element={<AdminMain/>}/>
      </Routes>
    </div>
  );
};

export default Display;