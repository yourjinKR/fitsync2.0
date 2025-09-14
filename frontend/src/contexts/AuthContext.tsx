import React, { createContext, useState, ReactNode, useEffect, useMemo, useCallback } from 'react';
import { setAuthFunctions } from '../api/apiClient';
import { backendURL } from '../api/AuthApi';

export interface AuthContextType {
    accessToken: string | null;
    isLoggedIn: boolean;
    isLoading: boolean;
    setAccessToken: (token: string | null) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const isLoggedIn = !!accessToken;

    const logout = useCallback(async () => {
        try {
            // 백엔드에 로그아웃 요청을 보내고 refreshToken 쿠키를 제거
            await fetch(`${backendURL}/api/auth/logout`, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                }
            });
            
            // 프론트엔드의 상태와 저장소 초기화
            setAccessToken(null);
            
            // 브라우저 저장소 초기화
            localStorage.removeItem('accessToken');
            sessionStorage.clear();
            
            // 모든 쿠키 제거 시도
            document.cookie.split(";").forEach(function(c) { 
                document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
            });
            
        } catch (error) {
            console.error('로그아웃 요청 실패:', error);
            // 에러가 발생하더라도 프론트엔드 상태는 초기화
            setAccessToken(null);
            localStorage.removeItem('accessToken');
        }
    }, [accessToken]);
    
    useEffect(() => {
        setAuthFunctions(() => accessToken, setAccessToken, logout);
    }, [accessToken, logout]);

    // 앱이 처음 로드될 때 실행되는 '자동 로그인' 로직
    useEffect(() => {
        let mounted = true;
        
        const refreshAccessToken = async () => {
            // 이미 accessToken이 있으면 새로운 토큰을 요청하지 않음
            if (accessToken) {
                setIsLoading(false);
                return;
            }

            try {
                const response = await fetch(`${backendURL}/api/auth/refresh`, {
                    method: 'POST',
                    credentials: 'include',  // 쿠키 포함
                });
                
                if (!response.ok) {
                    throw new Error('토큰 갱신 실패');
                }

                const data = await response.json();
                
                if (mounted && data.accessToken) {
                    setAccessToken(data.accessToken);
                    console.log('자동 로그인 성공!');
                }
            } catch (error) {
                if (mounted) {
                    console.log('자동 로그인 실패:', error);
                    setAccessToken(null);  // 확실하게 null로 설정
                }
            } finally {
                if (mounted) {
                    setIsLoading(false);
                }
            }
        };

        refreshAccessToken();

        return () => {
            mounted = false;
        };
    }, [accessToken]);

    const authContextValue = useMemo(() => ({
        accessToken, isLoggedIn, isLoading, setAccessToken, logout,
    }), [accessToken, isLoggedIn, isLoading, logout]);
    
    if (isLoading) {    
        return <div>애플리케이션 로딩 중</div>;
    }

    return (
        <AuthContext.Provider value={authContextValue}>
            {children}
        </AuthContext.Provider>
    );
};

// AuthContext의 실제 훅은 hooks/useAuth.ts로 이동했습니다.
// 이 파일에서는 AuthProvider만 내보냅니다.
export { AuthContext };
