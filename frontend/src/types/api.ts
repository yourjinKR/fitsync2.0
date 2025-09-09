export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;        // 현재 페이지(0-base)
  size: number;          // 페이지 크기
  first: boolean;
  last: boolean;
};

export type ApiError = {
  status: number;
  message: string;
  details?: unknown;
};
