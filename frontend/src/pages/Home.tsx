import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

const Home : React.FC = () => {
  const { isLoggedIn, accessToken, logout } = useAuth();  
  const navi = useNavigate();
  const prjName = process.env.REACT_APP_PROJECT_NAME;

  return (
    <div>
      <h1>{prjName}메인 페이지</h1>
      {isLoggedIn ? (
        <div>
          <p>로그인에 성공했습니다!</p>
          <p style={{ wordBreak: 'break-all' }}>Access Token: {accessToken}</p>
          <button onClick={logout}>로그아웃</button> {/* 로그아웃 버튼 */}
          <div>URL 주소 : {process.env.REACT_APP_API_URL || 'http://localhost:8080'}</div>
        </div>
      ) : (
        <button onClick={() => navi("/login")}>
          로그인 페이지
        </button>
      )}
    </div>
  );
};

export default Home;