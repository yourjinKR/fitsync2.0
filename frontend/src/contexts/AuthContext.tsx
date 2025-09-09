import React, { createContext, useState, useContext, ReactNode } from 'react';
/**
 * React의 Context API를 사용하여 
 * 로그인 상태와 accessToken을 전역적으로 관리
 */

// Context에서 관리할 상태의 타입 정의
interface AuthContextType {
  accessToken: string | null;
  setAccessToken: (token: string | null) => void;
  isLoggedIn: boolean;
}

// Context 객체 생성
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Context를 제공하는 Provider 컴포넌트
export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const isLoggedIn = !!accessToken;

  return (
    <AuthContext.Provider value={{ accessToken, setAccessToken, isLoggedIn }}>
      {children}
    </AuthContext.Provider>
  );
};

// Context를 쉽게 사용하기 위한 커스텀 훅
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};