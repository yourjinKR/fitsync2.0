import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const PrivateRoute = () => {
  const { isLoggedIn } = useAuth();

  // 로그인되어 있지 않다면 login화면으로 이동
  return isLoggedIn ? <Outlet /> : <Navigate to={"/login"} />
};

export default PrivateRoute;