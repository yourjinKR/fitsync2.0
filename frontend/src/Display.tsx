import React from 'react';
import { Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import AdminMain from './pages/AdminMain';
import AuthCallBack from './pages/AuthCallBack';

const Display = () => {
  return (
    <div>
      <Routes>
        <Route path="/" element={<Home/>}/>
        <Route path="/admin" element={<AdminMain/>}/>
        <Route path="/auth/callback" element={<AuthCallBack />} />
      </Routes>
    </div>
  );
};

export default Display;