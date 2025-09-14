import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const PublicRoute = () => {
  const { isLoggedIn } = useAuth();

  // 비로그인일때만 보여줘야하는 페이지를 컨트롤
  // 문제방지
  return !isLoggedIn ? <Outlet /> : <Navigate to="/" />;
};

export default PublicRoute;