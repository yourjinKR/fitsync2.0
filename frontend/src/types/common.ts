// src/types/common.ts

export type ISODateTime = string; // "2025-09-07T10:06:14.439089Z"
export type ISODate = string;     // "1995-05-10"
export type Nullable<T> = T | null;

// Spring의 Page 객체에 대한 표준 인터페이스
// 제네릭(<T>)을 사용하여 어떤 타입의 목록이든 재사용할 수 있습니다.
export interface Page<T> {
  content: T[];          // 현재 페이지의 데이터 목록
  totalPages: number;    // 전체 페이지 수
  totalElements: number; // 전체 데이터 수
  number: number;        // 현재 페이지 번호 (0부터 시작)
  size: number;          // 페이지당 데이터 수
  first: boolean;        // 첫 페이지 여부
  last: boolean;         // 마지막 페이지 여부
}

// JSON, JSONB
export type JSONPrimitive = string | number | boolean | null;
export type JSONValue = JSONPrimitive | JSONObject | JSONArray;
export interface JSONObject { [key: string]: JSONValue; }
export type JSONArray = JSONValue[];