import React, { createContext, useState, useContext, ReactNode, useEffect, useMemo } from 'react';
import apiClient, { setAuthFunctions } from '../api/apiClient';

// --- (추가) 쿠키 값을 읽기 위한 헬퍼 함수 ---
const getCookie = (name: string): string | null => {
  const cookies = document.cookie.split(';');
  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i].trim();
    if (cookie.startsWith(name + '=')) {
      return cookie.substring(name.length + 1);
    }
  }
  return null;
};

interface AuthContextType {
  accessToken: string | null;
  setAccessToken: (token: string | null) => void;
  isLoggedIn: boolean;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  // (수정) useState의 초기값으로 쿠키를 직접 읽어옵니다.
  const [accessToken, setAccessToken] = useState<string | null>(() => getCookie('accessToken'));
  const [isLoading, setIsLoading] = useState(true); // silentRefresh를 위해 로딩 상태 유지
  const isLoggedIn = !!accessToken;

  const logout = async () => {
    try {
      await apiClient.post('/api/auth/logout');
    } catch (error) {
      console.error('로그아웃 요청 실패:', error);
    } finally {
      setAccessToken(null);
    }
  };

  const authContextValue = useMemo(() => ({
    accessToken,
    setAccessToken,
    isLoggedIn,
    logout,
  }), [accessToken, isLoggedIn]);

  useEffect(() => {
    setAuthFunctions(
      () => accessToken,
      setAccessToken,
      logout
    );
  }, [accessToken]);


  useEffect(() => {
    const silentRefresh = async () => {
      // 앱 시작 시 쿠키에 accessToken이 이미 있으면 굳이 재발급 요청을 보낼 필요가 없습니다.
      if (accessToken) {
        setIsLoading(false);
        return;
      }

      try {
        const { data } = await apiClient.post('/api/auth/refresh');
        setAccessToken(data.accessToken);
        console.log('자동 로그인 성공!');
      } catch (error) {
        console.log('자동 로그인 실패. 리프레시 토큰이 없거나 만료되었습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    silentRefresh();
  }, []); // 이 useEffect는 처음 한 번만 실행됩니다.


  if (isLoading) {
    return <div>애플리케이션 로딩 중...</div>;
  }

  return (
    <AuthContext.Provider value={authContextValue}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth는 AuthProvider 안에서 사용해야 합니다.');
  }
  return context;
};
