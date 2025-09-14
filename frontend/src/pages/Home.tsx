import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const Home : React.FC = () => {
  const { isLoggedIn, accessToken, logout } = useAuth();  
  const navi = useNavigate();
  const prjName = "fitsync";

  return (
    <div>
      <h1>{prjName} 메인 페이지</h1>
      {isLoggedIn ? (
        <div>
          <p>로그인에 성공했습니다!</p>
          <p style={{ wordBreak: 'break-all' }}>Access Token: {accessToken}</p>
          <button onClick={logout}>로그아웃</button> {/* 로그아웃 버튼 */}
          <div>URL 주소 : {import.meta.env.BACKEND_URL || 'http://localhost:8080'}</div>
        </div>
      ) : (
        <div>
          <div>URL 주소 : {import.meta.env.BACKEND_URL || 'http://localhost:8080'}</div>
          <button onClick={() => navi("/login")}>
            로그인 페이지
          </button>
        </div>
      )}
    </div>
  );
};

export default Home;