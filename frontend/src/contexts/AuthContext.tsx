import React, { createContext, useState, useContext, ReactNode, useEffect, useMemo, useCallback } from 'react';
import apiClient, { setAuthFunctions } from '../api/apiClient';

interface AuthContextType {
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
            await apiClient.post('/api/auth/logout');
        } catch (error) {
            console.error('로그아웃 요청 실패:', error);
        } finally {
            setAccessToken(null);
        }
    }, []);
    
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
                const response = await fetch(`${process.env.REACT_APP_API_URL || 'http://localhost:8080'}/api/auth/refresh`, {
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

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth는 AuthProvider 안에서 사용해야 합니다.');
    }
    return context;
};

