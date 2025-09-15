export const BACKEND_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

export const GOOGLE_URL = `${BACKEND_URL}/oauth2/authorization/google`;
export const NAVER_URL = `${BACKEND_URL}/oauth2/authorization/naver`;
export const KAKAO_URL = `${BACKEND_URL}/oauth2/authorization/kakao`;