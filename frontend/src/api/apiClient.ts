// 1. axios 라이브러리를 가져옵니다.
import axios from 'axios';

// 2. 환경 변수에서 API의 기본 URL을 가져옵니다. 없으면 빈 문자열을 사용합니다.
const baseURL = process.env.REACT_APP_API_URL || '';

// 3. baseURL이 미리 설정된 axios 인스턴스를 생성합니다.
const apiClient = axios.create({
  baseURL: baseURL,
  // 여기에 모든 API 요청에 공통으로 들어갈 헤더 등을 설정할 수 있습니다.
  // headers: { 'Content-Type': 'application/json' },
});

// 응답 인터셉터 추가
apiClient.interceptors.response.use(
  (response) => {
    console.log('[API Response]', response); // 응답 전체 출력
    return response; // 반드시 반환해야 함
  },
  (error) => {
    console.error('[API Error]', error.response || error.message);
    return Promise.reject(error); // 에러는 reject
  }
);

// 4. 생성한 인스턴스를 다른 파일에서 사용할 수 있도록 내보냅니다.
export default apiClient;