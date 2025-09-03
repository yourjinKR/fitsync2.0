import React, { useState, useEffect } from 'react';
import axios from 'axios';

// API 기본 URL 설정
const API_URL = "/api/test";

const TestPage = () => {
  // 폼 데이터를 위한 초기 상태
  const initialFormState = {
    name: '',
    email: '',
    password: '',
    birthDate: '',
    gender: 'male',
    phoneNumber: '',
    address: '',
    bio: '',
    profileImageUrl: '',
    agreedToTerms: false,
  };

  const [users, setUsers] = useState([]); // 사용자 목록 상태
  const [formData, setFormData] = useState(initialFormState); // 폼 입력 데이터 상태
  const [editingId, setEditingId] = useState(null); // 수정 중인 사용자의 ID를 저장. null이면 생성 모드.

  // 컴포넌트가 처음 렌더링될 때 사용자 목록을 가져옵니다.
  useEffect(() => {
    fetchUsers();
  }, []);

  // 백엔드 API로부터 모든 사용자 목록을 가져오는 함수
  const fetchUsers = async () => {
    try {
      const response = await axios.get(API_URL);
      setUsers(response.data);
    } catch (error) {
      console.error("사용자 목록을 불러오는 데 실패했습니다:", error);
    }
  };

  // 폼 입력값이 변경될 때 호출되는 핸들러
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    // 체크박스의 경우 checked 값을 사용하고, 나머지는 value 값을 사용합니다.
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value,
    });
  };

  // 폼 제출(사용자 생성 또는 수정) 시 호출되는 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault(); // 폼의 기본 제출 동작(새로고침)을 막습니다.

    if (!formData.name || !formData.email || !formData.password) {
      alert("이름, 이메일, 비밀번호는 필수 항목입니다.");
      return;
    }

    try {
      if (editingId) {
        // editingId가 있으면 수정 모드
        await axios.put(`${API_URL}/${editingId}`, formData);
        alert("사용자 정보가 성공적으로 수정되었습니다.");
      } else {
        // editingId가 없으면 생성 모드
        await axios.post(API_URL, formData);
        alert("새로운 사용자가 성공적으로 추가되었습니다.");
      }
      resetForm(); // 폼 초기화
      fetchUsers(); // 목록 갱신
    } catch (error) {
      console.error("데이터 저장에 실패했습니다:", error);
      alert("데이터 저장 중 오류가 발생했습니다.");
    }
  };

  // 수정 버튼 클릭 시 호출되는 핸들러
  const handleEdit = (user) => {
    setEditingId(user.id); // 수정 모드로 전환
    // 날짜 형식을 'YYYY-MM-DD'로 맞추고, null 값은 빈 문자열로 처리
    const birthDate = user.birthDate ? user.birthDate.split('T')[0] : '';
    setFormData({ ...user, birthDate });
  };
  
  // 삭제 버튼 클릭 시 호출되는 핸들러
  const handleDelete = async (id) => {
    if (window.confirm("정말로 이 사용자를 삭제하시겠습니까?")) {
      try {
        await axios.delete(`${API_URL}/${id}`);
        alert("사용자가 삭제되었습니다.");
        fetchUsers(); // 목록 갱신
      } catch (error) {
        console.error("사용자 삭제에 실패했습니다:", error);
        alert("사용자 삭제 중 오류가 발생했습니다.");
      }
    }
  };

  // 폼 상태를 초기화하는 함수
  const resetForm = () => {
    setEditingId(null);
    setFormData(initialFormState);
  };

  return (
    <div className="container mx-auto p-8 font-sans">
      <h1 className="text-3xl font-bold mb-6 text-gray-800">사용자 관리</h1>
      
      {/* 사용자 추가/수정 폼 */}
      <div className="bg-white p-6 rounded-lg shadow-md mb-8">
        <h2 className="text-2xl font-semibold mb-4 text-gray-700">
          {editingId ? '사용자 정보 수정' : '새로운 사용자 추가'}
        </h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* 입력 필드들을 grid로 배치 */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <input type="text" name="name" value={formData.name} onChange={handleInputChange} placeholder="이름 (필수)" className="p-2 border rounded" required />
            <input type="email" name="email" value={formData.email} onChange={handleInputChange} placeholder="이메일 (필수)" className="p-2 border rounded" required />
            <input type="password" name="password" value={formData.password} onChange={handleInputChange} placeholder="비밀번호 (필수)" className="p-2 border rounded" required />
            <input type="date" name="birthDate" value={formData.birthDate} onChange={handleInputChange} className="p-2 border rounded" />
            <input type="tel" name="phoneNumber" value={formData.phoneNumber} onChange={handleInputChange} placeholder="전화번호" className="p-2 border rounded" />
            <input type="text" name="address" value={formData.address} onChange={handleInputChange} placeholder="주소" className="p-2 border rounded" />
            <input type="text" name="profileImageUrl" value={formData.profileImageUrl} onChange={handleInputChange} placeholder="프로필 이미지 URL" className="p-2 border rounded" />
            {/* 성별 선택 라디오 버튼 */}
            <div className="flex items-center space-x-4">
              <span className="text-gray-600">성별:</span>
              <label><input type="radio" name="gender" value="male" checked={formData.gender === 'male'} onChange={handleInputChange} /> 남자</label>
              <label><input type="radio" name="gender" value="female" checked={formData.gender === 'female'} onChange={handleInputChange} /> 여자</label>
            </div>
          </div>
          <textarea name="bio" value={formData.bio} onChange={handleInputChange} placeholder="자기소개" className="p-2 border rounded w-full"></textarea>
          {/* 약관 동의 체크박스 */}
          <label className="flex items-center">
            <input type="checkbox" name="agreedToTerms" checked={formData.agreedToTerms} onChange={handleInputChange} className="mr-2" />
            이용약관에 동의합니다.
          </label>
          {/* 버튼 영역 */}
          <div className="flex space-x-2">
            <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-colors">
              {editingId ? '수정하기' : '추가하기'}
            </button>
            {editingId && (
              <button type="button" onClick={resetForm} className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600 transition-colors">
                취소
              </button>
            )}
          </div>
        </form>
      </div>

      {/* 사용자 목록 */}
      <div>
        <h2 className="text-2xl font-semibold mb-4 text-gray-700">사용자 목록</h2>
        <div className="bg-white p-6 rounded-lg shadow-md">
          <ul className="space-y-4">
            {users.map((user) => (
              <li key={user.id} className="border-b pb-4 flex justify-between items-center">
                <div>
                  <p className="font-bold text-lg">{user.name} ({user.email})</p>
                  <p className="text-sm text-gray-600">생년월일: {user.birthDate ? user.birthDate.split('T')[0] : 'N/A'}</p>
                  <p className="text-sm text-gray-600">연락처: {user.phoneNumber || 'N/A'}</p>
                </div>
                <div className="flex space-x-2">
                  <button onClick={() => handleEdit(user)} className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600 text-sm">수정</button>
                  <button onClick={() => handleDelete(user.id)} className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm">삭제</button>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default TestPage;
