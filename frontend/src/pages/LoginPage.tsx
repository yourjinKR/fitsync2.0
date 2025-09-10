import React from 'react';
import { handleGoogleLogin } from '../contexts/AuthURL';

const LoginPage = () => {

  return (
    <div>
      <h1>FitSync에 로그인</h1>
      <button onClick={handleGoogleLogin}>Google 계정으로 로그인</button>
    </div>
  );
};

export default LoginPage;