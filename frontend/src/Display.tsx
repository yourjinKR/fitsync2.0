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

const Display = () => {
  return (
    <div>
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
        </Route>

        {/* 로그인을 안한 사용자만 접근하는 페이지 */}
        <Route element={<PublicRoute/>}>
          <Route path={"/login"} element={<LoginPage/>}/>
        </Route>
      </Routes>
    </div>
  );
};

export default Display;