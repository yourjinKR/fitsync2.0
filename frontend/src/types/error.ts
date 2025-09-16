// src/types/error.ts

/**
 * API 호출 시 발생하는 모든 에러를 표준화하기 위한 클래스입니다.
 * Axios의 복잡한 에러 객체 대신, 이 클래스를 사용하여 에러 정보를 일관되게 관리합니다.
 */
export class ApiError extends Error {
  // HTTP 상태 코드를 저장하는 필드입니다. (e.g., 404, 500)
  public readonly statusCode: number;

  /**
   * ApiError 객체를 생성합니다.
   * @param message - 에러 메시지 (백엔드에서 받은 메시지)
   * @param statusCode - HTTP 상태 코드
   */
  constructor(message: string, statusCode: number) {
    // 1. 부모 클래스인 'Error'의 생성자를 호출하여 'message' 속성을 설정합니다.
    super(message);

    // 2. 이 클래스 고유의 속성인 'statusCode'를 설정합니다.
    this.statusCode = statusCode;

    // 3. 에러 객체의 이름을 'ApiError'로 명확히 지정합니다.
    //    이렇게 하면 console.log(error) 시 어떤 종류의 에러인지 쉽게 알 수 있습니다.
    this.name = 'ApiError';
  }
}