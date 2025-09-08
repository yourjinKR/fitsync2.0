import React from 'react';
import UserApi from '../api/UserApi';

const Home = () => {
  return (
    <div>
      <h1>Fitsync 2 홈임.</h1>
      <button onClick={() => UserApi.findAllUsers}>클릭</button>
    </div>
  );
};

export default Home;