import React from 'react';
import './App.css';
import Display from './Display';
import { BrowserRouter } from 'react-router-dom';

function App() {
  return (
    <BrowserRouter>
      <Display/>
    </BrowserRouter>
  );
}

export default App;
