const baseURL = import.meta.env.BACKEND_URL || 'http://localhost:8080';

export const GOOGLE_URL = `${baseURL}/oauth2/authorization/google`;
export const NAVER_URL = `${baseURL}/oauth2/authorization/naver`;
export const KAKAO_URL = `${baseURL}/oauth2/authorization/kakao`;