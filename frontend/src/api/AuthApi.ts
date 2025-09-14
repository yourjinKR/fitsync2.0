export const backendURL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

export const GOOGLE_URL = `${backendURL}/oauth2/authorization/google`;
export const NAVER_URL = `${backendURL}/oauth2/authorization/naver`;
export const KAKAO_URL = `${backendURL}/oauth2/authorization/kakao`;