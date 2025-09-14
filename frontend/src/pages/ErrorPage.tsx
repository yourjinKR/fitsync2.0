import React from 'react';
import { useLocation, Navigate } from 'react-router-dom';

const ErrorPage = () => {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const hasError = params.get("error") === "true";

  if (!hasError) {
    return <Navigate to="/" replace />;
  }

  return (
    <div>
      <h1>에러 페이지 입니다.</h1>
      <h2>예기치 못한 상황!</h2>
      <a href="/">메인으로</a>
    </div>
  );
};

export default ErrorPage;