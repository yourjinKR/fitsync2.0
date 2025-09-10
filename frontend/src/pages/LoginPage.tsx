import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { GOOGLE_URL, KAKAO_URL } from '../api/AuthApi';

const LoginPage: React.FC = () => {
    const { setAccessToken } = useAuth();
    const navigate = useNavigate();
    
    useEffect(() => {
        // 팝업 창으로부터 메시지를 받기 위한 이벤트 리스너를 등록합니다.
        const handleMessage = (event: MessageEvent) => {
            // 메시지의 출처와 타입을 확인하여 보안을 강화할 수 있습니다.
            // if (event.origin !== 'http://localhost:8080') return;

            if (event.data.type === 'loginSuccess' && event.data.accessToken) {
                console.log('팝업으로부터 accessToken을 받았습니다:', event.data.accessToken);
                // 전달받은 accessToken을 전역 상태에 저장합니다.
                setAccessToken(event.data.accessToken);
                // 로그인이 완료되었으므로 메인 페이지로 이동합니다.
                navigate('/', { replace: true });
            }
        };

        window.addEventListener('message', handleMessage);

        // 컴포넌트가 언마운트될 때 이벤트 리스너를 정리합니다.
        return () => {
            window.removeEventListener('message', handleMessage);
        };
    }, [setAccessToken, navigate]);

    // 소셜 로그인 버튼 클릭 시 팝업 창을 여는 함수
    const openLoginPopup = (url: string) => {
        const width = 600;
        const height = 700;
        const left = window.screen.width / 2 - width / 2;
        const top = window.screen.height / 2 - height / 2;
        window.open(url, 'loginPopup', `width=${width},height=${height},top=${top},left=${left}`);
    };

    return (
        <div>
            <h1>로그인</h1>
            <button onClick={() => openLoginPopup(GOOGLE_URL)}>구글로 로그인</button>
            <button onClick={() => openLoginPopup(KAKAO_URL)}>카카오로 로그인</button>
        </div>
    );
};

export default LoginPage;
