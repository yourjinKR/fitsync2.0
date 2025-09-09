import axios, { AxiosInstance } from 'axios';

// 환경 변수에서 API의 기본 URL을 가져옵니다.
const baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// baseURL과 쿠키 전송 옵션이 설정된 axios 인스턴스를 생성합니다.
const apiClient : AxiosInstance = axios.create({
  baseURL: baseURL,
  withCredentials : true // cross-origin 요청 시 쿠키를 포함시키기 위한 설정
});

// AuthContext로부터 주입받을 함수들을 위한 참조 변수들입니다.
// 초기값은 아무 동작도 하지 않는 함수로 설정합니다.
let getAccessToken: () => string | null = () => null;
let setAccessToken: (token: string | null) => void = () => {};
let logout: () => void = () => {};

/**
 * AuthContext가 자신의 상태 관리 함수들을 apiClient에 주입하기 위해 호출하는 함수입니다.
 * @param getAccessTokenFunc - AccessToken을 반환하는 함수
 * @param setAccessTokenFunc - AccessToken을 상태에 저장하는 함수
 * @param logoutFunc - 로그아웃을 처리하는 함수
 */
export const setAuthFunctions = (
  getAccessTokenFunc: () => string | null,
  setAccessTokenFunc: (token: string | null) => void,
  logoutFunc: () => void
) => {
  getAccessToken = getAccessTokenFunc;
  setAccessToken = setAccessTokenFunc;
  logout = logoutFunc;
};

// 1. 요청 인터셉터 (Request Interceptor)
//    - 모든 API 요청이 서버로 전송되기 직전에 가로채어 특정 작업을 수행합니다.
apiClient.interceptors.request.use(
  (config) => {
    // AuthContext로부터 주입받은 함수를 통해 현재 AccessToken을 가져옵니다.
    const accessToken = getAccessToken();
    
    // 토큰이 존재하면, 요청 헤더에 Authorization 헤더를 추가합니다.
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 2. 응답 인터셉터 (Response Interceptor)
//    - 서버로부터 응답을 받은 직후에 특정 작업을 수행합니다.
apiClient.interceptors.response.use(
  (response) => response, // 성공적인 응답은 그대로 반환합니다.
  async (error) => {
    const originalRequest = error.config;

    // AccessToken 만료로 인한 401 에러이고, 아직 재시도를 안 했다면
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // 무한 재발급 요청을 방지하기 위한 플래그

      try {
        // RefreshToken을 사용하여 새로운 AccessToken을 요청합니다.
        const { data } = await apiClient.post('/api/auth/refresh');
        const newAccessToken = data.accessToken;

        // AuthContext의 setAccessToken 함수를 호출하여 상태를 업데이트합니다.
        setAccessToken(newAccessToken);

        // 원래 요청의 헤더에 새로운 AccessToken을 설정하여 다시 보냅니다.
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return apiClient(originalRequest);
        
      } catch (refreshError) {
        // RefreshToken조차 만료되었다면, 로그아웃 처리합니다.
        logout();
        return Promise.reject(refreshError);
      }
    }
    // 그 외 다른 에러는 그대로 반환합니다.
    return Promise.reject(error);
  }
);

export default apiClient;

