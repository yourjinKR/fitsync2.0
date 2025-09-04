import React, { useState, useEffect } from 'react';
import axios from 'axios';
import styled from 'styled-components';

// API 기본 URL 설정
const API_URL = "/api/test";

// Styled Components
const Container = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  font-family: sans-serif;
`;

const Title = styled.h1`
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  color: #1f2937;
`;

const SubTitle = styled.h2`
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #374151;
`;

const FormContainer = styled.div`
  background-color: white;
  padding: 1.5rem;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
`;

const InputGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: 1rem;
  
  @media (min-width: 768px) {
    grid-template-columns: 1fr 1fr;
  }
`;

const Input = styled.input`
  padding: 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.25rem;
  width: 100%;
`;

const TextArea = styled.textarea`
  padding: 0.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.25rem;
  width: 100%;
  height : 200px;
  resize : none;
`;

const Button = styled.button`
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  transition: background-color 0.2s;
  color: white;
  
  ${props => props.primary && `
    background-color: #3b82f6;
    &:hover {
      background-color: #2563eb;
    }
  `}
  
  ${props => props.secondary && `
    background-color: #6b7280;
    &:hover {
      background-color: #4b5563;
    }
  `}
  
  ${props => props.danger && `
    background-color: #ef4444;
    &:hover {
      background-color: #dc2626;
    }
  `}
  
  ${props => props.success && `
    background-color: #10b981;
    &:hover {
      background-color: #059669;
    }
  `}
`;

const UserList = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const UserItem = styled.li`
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const UserInfo = styled.div`
  flex: 1;
`;

const UserName = styled.p`
  font-weight: bold;
  font-size: 1.125rem;
`;

const UserDetail = styled.p`
  font-size: 0.875rem;
  color: #6b7280;
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 0.5rem;
`;

const GenderGroup = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
  
  span {
    color: #6b7280;
  }
`;

const CheckboxLabel = styled.label`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

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
    <Container>
      <Title>사용자 관리</Title>
      
      {/* 사용자 추가/수정 폼 */}
      <FormContainer>
        <SubTitle>
          {editingId ? '사용자 정보 수정' : '새로운 사용자 추가'}
        </SubTitle>
        <form onSubmit={handleSubmit}>
          <InputGrid>
            <Input type="text" name="name" value={formData.name} onChange={handleInputChange} placeholder="이름 (필수)" required />
            <Input type="email" name="email" value={formData.email} onChange={handleInputChange} placeholder="이메일 (필수)" required />
            <Input type="password" name="password" value={formData.password} onChange={handleInputChange} placeholder="비밀번호 (필수)" required />
            <Input type="date" name="birthDate" value={formData.birthDate} onChange={handleInputChange} />
            <Input type="tel" name="phoneNumber" value={formData.phoneNumber} onChange={handleInputChange} placeholder="전화번호" />
            <Input type="text" name="address" value={formData.address} onChange={handleInputChange} placeholder="주소" />
            <Input type="text" name="profileImageUrl" value={formData.profileImageUrl} onChange={handleInputChange} placeholder="프로필 이미지 URL" />
            <GenderGroup>
              <span>성별:</span>
              <label><input type="radio" name="gender" value="male" checked={formData.gender === 'male'} onChange={handleInputChange} /> 남자</label>
              <label><input type="radio" name="gender" value="female" checked={formData.gender === 'female'} onChange={handleInputChange} /> 여자</label>
            </GenderGroup>
          </InputGrid>
          <TextArea name="bio" value={formData.bio} onChange={handleInputChange} placeholder="자기소개" />
          <CheckboxLabel>
            <input type="checkbox" name="agreedToTerms" checked={formData.agreedToTerms} onChange={handleInputChange} />
            이용약관에 동의합니다.
          </CheckboxLabel>
          <ButtonGroup>
            <Button type="submit" primary>
              {editingId ? '수정하기' : '추가하기'}
            </Button>
            {editingId && (
              <Button type="button" onClick={resetForm} secondary>
                취소
              </Button>
            )}
          </ButtonGroup>
        </form>
      </FormContainer>

      {/* 사용자 목록 */}
      <div>
        <SubTitle>사용자 목록</SubTitle>
        <FormContainer>
          <UserList>
            {users.map((user) => (
              <UserItem key={user.id}>
                <UserInfo>
                  <UserName>{user.name} ({user.email})</UserName>
                  <UserDetail>생년월일: {user.birthDate ? user.birthDate.split('T')[0] : 'N/A'}</UserDetail>
                  <UserDetail>연락처: {user.phoneNumber || 'N/A'}</UserDetail>
                </UserInfo>
                <ButtonGroup>
                  <Button onClick={() => handleEdit(user)} success>수정</Button>
                  <Button onClick={() => handleDelete(user.id)} danger>삭제</Button>
                </ButtonGroup>
              </UserItem>
            ))}
          </UserList>
        </FormContainer>
      </div>
    </Container>
  );
};

export default TestPage;
