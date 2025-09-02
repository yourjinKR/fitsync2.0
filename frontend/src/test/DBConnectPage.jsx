import React, { useState } from 'react';
import axios from 'axios';


const DBConnectPage = () => {
  const initUser = {name : '', age : 0, gender : 'male'};

  const [newUser, setNewUser] = useState(initUser);
  const [userList, setUserList] = useState([{}]);

  const testGetAll = async () => {
    const response = await axios.get("/api/test/all");
    console.log(response.data);
    setUserList(response.data);
  }

  const testRegister = async () => {
    console.log(newUser);
    

    if (newUser.name === '' || newUser.name === null) {
      alert("이름 입력");
      return;
    }
    if (newUser.age < 0) {
      alert("나이 입력");
      return;
    }
    if (newUser.gender === '' && newUser.gender === null) {
      alert("성별 입력");
      return;
    }

    try {
      const response = await axios.post("/api/test", newUser);
      console.log(response.data);

      setNewUser(initUser);

    } catch (error) {
      console.log(error);
    } finally {
      testGetAll();
    }
  }

  const handleNewUser = (e) => {
    const {name, value} = e.target;
    setNewUser({ ...newUser, [name]: value });
  }


  return (
    <div>
      <h2>DB 연결 테스트 입니다</h2>
      <form action="">
        <label htmlFor="name">이름</label>
        <input
          type='text'
          name='name'
          value={newUser.name}
          onChange={handleNewUser}></input><br />

        <label htmlFor="age">나이</label>
        <input
          type='number'
          name='age'
          value={newUser.age}
          onChange={handleNewUser}></input><br />
        
        <label>성별</label>
        <label>
          <input
            type="radio"
            name="gender" // 그룹으로 묶기 위해 name을 동일하게 설정
            value="male"
            // newUser.gender 값이 'male'일 때 체크됨
            checked={newUser.gender === 'male'}
            onChange={handleNewUser}
          /> 남자
        </label>
        <label>
          <input
            type="radio"
            name="gender"
            value="female"
            // newUser.gender 값이 'female'일 때 체크됨
            checked={newUser.gender === 'female'}
            onChange={handleNewUser}
          /> 여자
        </label>
        <br />

        <br />
        <button type='button' onClick={testRegister}>유저 추가</button><br />
      </form>

      <button onClick={testGetAll}>갱신</button>
      <h3>유저 리스트</h3>
      {
        userList.map((user, index) => {
          return(
            <div key={index}>이름 : {user.name}, 나이 : {user.age}, 성별 : {user.gender === 'male' ? '남자' : '여자'}</div>
          )
        })
      }
    </div>
  );
};

export default DBConnectPage;