const baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
export const GOOGLE_URL = `${baseURL}/oauth2/authorization/google`;

export const handleGoogleLogin = () => {
  // window.location.href 대신 replace를 사용합니다.
  // 이렇게 하면 브라우저 방문 기록에 현재 페이지(/login)가 남지 않고,
  // 구글 로그인 페이지로 "대체"됩니다.
  window.location.replace(GOOGLE_URL);
};