import { Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import AdminMain from './pages/AdminMain';
import PrivateRoute from './components/PrivateRoute';
import PublicRoute from './components/PublicRoute';
import LoginPage from './pages/LoginPage';
import ErrorPage from './pages/ErrorPage';
import AuthCallBack from './pages/AuthCallback';
import RoutineTestPage from './test/RoutineTestPage';
import ExerciseTestPage from './test/ExerciseTestPage';
import WorkoutTestPage from './test/WorkoutTestPage';
import ExerciseHookTestPage from './test/ExerciseHookTestPage';
import styled from 'styled-components';

const DisplayWrapper = styled.div`
  max-width: 750px;
  width: 100%;
  margin: 0 auto;
  position: relative;
  height: 100vh;
  overflow: hidden;
  background: var(--bg-primary);
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
`;

const DisplayInnner = styled.div`
  position: relative;
  overflow: auto;
  height: calc( 100vh - 150px );
  background: var(--bg-primary);
  margin-top : 65px;
`;

const Display = () => {
  return (
    <DisplayWrapper>
      <DisplayInnner>
        <Routes>
          {/* 로그인 여부가 상관 없는 페이지 */}
          <Route path="/" element={<Home/>}/>
          <Route path="/auth/callback" element={<AuthCallBack />} />
          <Route path="/auth/error" element={<ErrorPage />}/>
          
          {/* 로그인한 사용자만 접근하는 페이지 */}
          <Route element={<PrivateRoute/>}>
            <Route path="/admin" element={<AdminMain/>}/>
            <Route path='/test/exercise' element={<ExerciseTestPage/>}/>
            <Route path='/test/routine' element={<RoutineTestPage/>}/>
            <Route path='/test/workout' element={<WorkoutTestPage/>}/>
            <Route path='/test/hook' element={<ExerciseHookTestPage/>}/>
          </Route>

          {/* 로그인을 안한 사용자만 접근하는 페이지 */}
          <Route element={<PublicRoute/>}>
            <Route path={"/login"} element={<LoginPage/>}/>
          </Route>
        </Routes>
      </DisplayInnner>
    </DisplayWrapper>
  );
};

export default Display;