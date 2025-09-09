import React from 'react';
import UserApi from '../api/UserApi';
import { useAuth } from '../contexts/AuthContext';

const Home : React.FC = () => {
  const { isLoggedIn, accessToken, logout } = useAuth();  
  return (
    <div>
      <h1>메인 페이지</h1>
      {isLoggedIn ? (
        <div>
          <p>로그인에 성공했습니다!</p>
          <p style={{ wordBreak: 'break-all' }}>Access Token: {accessToken}</p>
          <button onClick={logout}>로그아웃</button> {/* 로그아웃 버튼 */}
          <div>URL 주소 : {process.env.REACT_APP_API_URL || ''}</div>
        </div>
      ) : (
        <a href="http://localhost:8080/oauth2/authorization/google">
          Google로 로그인하기
        </a>
      )}
    </div>
  );
};

export default Home;