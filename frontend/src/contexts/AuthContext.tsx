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
        if (accessToken) {
            setIsLoading(false);
            return;
        }
        const silentRefresh = async () => {
            try {
                const { data } = await apiClient.post('/api/auth/refresh');
                setAccessToken(data.accessToken);
                console.log('자동 로그인 성공!');
            } catch (error) {
                console.log('자동 로그인 실패. 유효한 리프레시 토큰이 없습니다.');
            } finally {
                setIsLoading(false);
            }
        };
        silentRefresh();
    }, [accessToken]);

    const authContextValue = useMemo(() => ({
        accessToken, isLoggedIn, isLoading, setAccessToken, logout,
    }), [accessToken, isLoggedIn, isLoading, logout]);
    
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

