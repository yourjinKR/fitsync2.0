import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

/**
 * [모바일 전용]
 * 소셜 로그인 성공 후, URL에 담겨온 AccessToken을 추출하여
 * 전역 상태를 업데이트하고 메인 페이지로 이동시키는 컴포넌트.
 */
const AuthCallBack: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { setAccessToken } = useAuth();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const accessToken = params.get('accessToken');

        if (accessToken) {
            setAccessToken(accessToken);
            console.log('로그인 성공 (모바일), URL에서 토큰을 추출하여 상태에 저장했습니다.');
            navigate('/', { replace: true });
        } else {
            // 이 페이지는 모바일 리다이렉션 시에만 사용되므로, 토큰이 없으면 실패입니다.
            console.error('로그인 실패 (모바일): URL에서 AccessToken을 찾을 수 없습니다.');
            navigate('/login', { replace: true });
        }
    }, [location, navigate, setAccessToken]);

    return <div>로그인 처리 중...</div>;
};

export default AuthCallBack;

