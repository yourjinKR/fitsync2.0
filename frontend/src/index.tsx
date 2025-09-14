import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App'
import GlobalStyle from './styles/GlobalStyle'
import { AuthProvider } from './contexts/AuthContext'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <GlobalStyle/>
    <AuthProvider>
      <App />
    </AuthProvider>
  </StrictMode>,
)
