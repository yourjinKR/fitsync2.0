import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios';
import { BACKEND_URL } from './AuthApi';
import { ApiError } from '../types/error';

// 환경 변수에서 API의 기본 URL을 가져옵니다.

// baseURL과 쿠키 전송 옵션이 설정된 axios 인스턴스를 생성합니다.
const apiClient : AxiosInstance = axios.create({
  baseURL: BACKEND_URL,
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

// AxiosRequestConfig를 확장하여 우리가 사용할 커스텀 속성(_retry)을 타입에 추가합니다.
interface RetryableAxiosRequestConfig extends AxiosRequestConfig {
  _retry?: boolean;
}

// 2. 응답 인터셉터 (Response Interceptor)
//    - 서버로부터 응답을 받은 직후에 특정 작업을 수행합니다.
apiClient.interceptors.response.use(
  (response) => response, // 성공적인 응답은 그대로 반환합니다.
  async (error : AxiosError) => { // error 타입을 AxiosError로 명시하여 타입 
    
    // 1. error.config를 우리가 만든 커스텀 타입으로 지정합니다.
    const originalRequest: RetryableAxiosRequestConfig | undefined = error.config;

    // 2. originalRequest가 없는 경우 (매우 드문 케이스), 에러를 즉시 반환합니다.
    //    이렇게 하면 이 코드 블록 아래에서는 originalRequest가 항상 존재함을 타입스크립트가 인지합니다.
    if (!originalRequest) {
      return Promise.reject(error);
    }

    // AccessToken 만료로 인한 401 에러이고, 아직 재시도를 안 했다면
    if (error.response?.status === 401 && !originalRequest._retry) {
      // 토큰 재발급 요청 자체에 대해서는 재시도하지 않음
      if (originalRequest.url === '/api/auth/refresh') {
        logout();
        return Promise.reject(error);
      }

      originalRequest._retry = true; // 여기에 _retry 속성이 있다는 것을 타입스크립트가 인지합니다.

      try {
        const response = await fetch(`${BACKEND_URL}/api/auth/refresh`, {
          method: 'POST',
          credentials: 'include',
        });

        if (!response.ok) {
          throw new Error('토큰 갱신 실패');
        }

        const data = await response.json();
        const newAccessToken = data.accessToken;

        if (!newAccessToken) {
          throw new Error('새로운 액세스 토큰이 없습니다');
        }

        setAccessToken(newAccessToken);

        // 타입 명시로 headers가 존재함을 타입스크립트가 인지함.
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        }
        
        return apiClient(originalRequest);
        
      } catch (refreshError) {
        // RefreshToken조차 만료되었다면, 로그아웃 처리합니다.
        logout();
        return Promise.reject(refreshError);
      }
    }

    // --- 401 외 다른 모든 종류의 에러를 표준 ApiError로 변환하는 로직 (핵심!) ---

    // 1. 서버에서 응답을 받은 경우 (e.g., 404, 400, 500 등)
    if (error.response) {
      // HTTP 상태 코드를 가져옵니다.
      const statusCode = error.response.status;
      
      // 백엔드의 GlobalExceptionHandler가 body에 담아 보낸 에러 메시지를 가져옵니다.
      // 메시지가 없다면 기본 메시지를 사용합니다.
      const message = (error.response.data as string) || '알 수 없는 오류가 발생했습니다.';
      
      // 우리가 정의한 ApiError 형태로 에러 객체를 만들어서 반환(reject)합니다.
      return Promise.reject(new ApiError(message, statusCode));
    }

    // 2. 서버에서 응답을 받지 못한 경우 (e.g., 네트워크 오류, CORS 오류 등)
    //    이 경우 error.response가 존재하지 않습니다.
    return Promise.reject(new ApiError(error.message, 0)); // 상태 코드는 0 또는 임의의 값으로 설정

  }
);

export default apiClient;
