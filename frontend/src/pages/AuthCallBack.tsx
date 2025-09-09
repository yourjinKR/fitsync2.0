import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const AuthCallBack: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { setAccessToken } = useAuth();
  
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const accessToken = params.get('accessToken');

    if (accessToken) {
      setAccessToken(accessToken);

      console.log('로그인 성공, 토큰을 상태에 저장함');
      navigate('/', { replace: true });
    } else {
      console.error('로그인 실패, 토큰을 못찾음')
      navigate('/login', {replace : true});
    }
  }, [location, navigate, setAccessToken]);

  return (
    <div>
      로그인 처리 중...
    </div>
  );
};

export default AuthCallBack;