export interface ApiError extends Error {
  code?: string;
  status?: number;
  data?: unknown;
}

export class CustomError extends Error implements ApiError {
  constructor(
    message: string,
    public code?: string,
    public status?: number,
    public data?: unknown
  ) {
    super(message);
    this.name = 'CustomError';
  }
}