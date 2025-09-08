// 1) 모든 import는 최상단
import type { ISODate, ISODateTime, Nullable } from '../common';

// 2) 그 다음 상수/타입 선언
export const SOCIAL_PROVIDERS = ['GOOGLE', 'KAKAO', 'NAVER'] as const;
export type SocialProvider = (typeof SOCIAL_PROVIDERS)[number];

export const USER_STATUS = ['ACTIVE', 'INACTIVE', 'SUSPENDED'] as const;
export type UserStatus = (typeof USER_STATUS)[number];

export const USER_TYPE = ['MEMBER', 'TRAINER'] as const;
export type UserType = (typeof USER_TYPE)[number];

export const GENDERS = ['MALE', 'FEMALE', 'OTHER'] as const;
export type Gender = (typeof GENDERS)[number];

export interface User {
  id: number;
  email: string;
  name: string;
  socialProvider: SocialProvider;
  status: UserStatus;
  type: UserType;
  birthDate: ISODate;
  gender: Gender;
  intro: Nullable<string>;
  bio: Nullable<string>;
  purpose: Nullable<string>;
  disease: Nullable<string>;
  activityArea: Nullable<string>;
  gymId: Nullable<number>;
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
  hidden: boolean;
}

export interface UserCreateRequest {
  email: string;
  name: string;
  socialProvider: SocialProvider;
  type: UserType;
  birthDate?: ISODate;
  gender?: Gender;
  intro?: string | null;
}

export type UserUpdateRequest = Partial<Omit<User, 'id' | 'createdAt' | 'updatedAt'>>;

