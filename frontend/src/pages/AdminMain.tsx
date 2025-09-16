// AdminMain.tsx
import React, { useEffect, useState } from 'react';
import UserApi from '../api/UserApi';
import { User } from '../types/domain/users';
import ExerciseApi from '../api/ExerciseApi';

const AdminMain = () => {
  const [loading, setLoading] = useState(true);
  const [users, setUsers] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);

  const fetchUsers = async () => {
    try {
      const { data } = await UserApi.findAllUsers();
      console.log('[findAllUsers raw data]', data);

      // ✅ 백엔드 응답 형태가 배열이 아닐 수 있으니 안전하게 추출
      //   예) Spring Data Page: { content: [...], totalElements: ... }
      //   예) 래핑: { data: [...] } 또는 { users: [...] }
      const arr =
        (Array.isArray(data) && data) ||
        data?.content ||
        data?.users ||
        data?.data ||
        [];

      if (!Array.isArray(arr)) {
        throw new Error('응답에 사용자 배열이 없습니다.');
      }

      setUsers(arr);
    } catch (err) {
      console.error(err);
      const errorMessage = err instanceof Error ? err.message : '사용자 목록 조회 실패';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div style={{ color: 'red' }}>에러: {error}</div>;

  // ✅ 키 안전하게 선택 (id가 없을 수도 있으니 인덱스 fallback)
  const getKey = (u: User, i: number) => (u.id ?? u.email ?? u.id ?? i).toString();
  const getName = (u: User) => u.name ?? '(이름 없음)';

  const testPage = {
    page: 1,
    size: 20,
  }

  return (
    <div>
      <h1>관리자 페이지</h1>

      <button onClick={() => ExerciseApi.getExerciseById(2).then(response => console.log(response))}>특정 운동 정보 확인</button>
      <button onClick={() => ExerciseApi.getAllExercises(testPage).then(response => console.log(response))}>운동 정보 리스트</button>

      <h2>유저 수: {users.length}</h2>

      <ul>
        {users.map((u, i) => (
          <li key={getKey(u, i)}>
            {getName(u)} {u.email ? `(${u.email})` : ''}
          </li>
        ))}
      </ul>

      <h3>원본 데이터 확인 (디버깅용)</h3>
      <pre style={{ background: '#111', color: '#0f0', padding: 12 }}>
        {JSON.stringify(users, null, 2)}
      </pre>
    </div>
  );
};

export default AdminMain;