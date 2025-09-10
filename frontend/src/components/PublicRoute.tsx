import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Navigate, Outlet } from 'react-router-dom';

const PublicRoute = () => {
  const { isLoggedIn } = useAuth();

  // 비로그인일때만 보여줘야하는 페이지를 컨트롤
  // 문제방지
  return !isLoggedIn ? <Outlet /> : <Navigate to="/" />;
};

export default PublicRoute;