import React from 'react';
import './App.css';
import Display from './Display';
import { BrowserRouter } from 'react-router-dom';
import GlobalStyle from './styles/GlobalStyle';

function App() {
  return (
    <BrowserRouter>
      <GlobalStyle/>
      <Display/>
    </BrowserRouter>
  );
}

export default App;
