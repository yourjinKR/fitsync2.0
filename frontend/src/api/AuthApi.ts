const baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const GOOGLE_URL = `${baseURL}/oauth2/authorization/google`;
export const NAVER_URL = `${baseURL}/oauth2/authorization/naver`;
export const KAKAO_URL = `${baseURL}/oauth2/authorization/kakao`;