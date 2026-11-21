import { useEffect, useState } from 'react'
import AuthForm from './components/AuthForm'
import ProjectList from './components/ProjectList'
import TaskList from './components/TaskList'
import { auth, setUnauthorizedHandler } from './api/client'
import './App.css'

export default function App() {
  const [authenticated, setAuthenticated] = useState(Boolean(auth.getToken()))
  const [selected, setSelected] = useState(null)

  function logout() {
    auth.clear()
    setSelected(null)
    setAuthenticated(false)
  }

  useEffect(() => {
    setUnauthorizedHandler(logout)
  }, [])

  if (!authenticated) {
    return <AuthForm onAuthenticated={() => setAuthenticated(true)} />
  }

  return (
    <>
      <header className="topbar">
        <div className="topbar-inner">
          <button className="brand" onClick={() => setSelected(null)}>
            <span className="brand-logo" aria-hidden="true">✓</span>
            TaskFlow AI
          </button>
          <div className="user-box">
            <span className="user-email">{auth.getEmail()}</span>
            <button className="btn btn-sm" onClick={logout}>Sair</button>
          </div>
        </div>
      </header>

      <main className="page">
        {selected ? (
          <TaskList project={selected} onBack={() => setSelected(null)} />
        ) : (
          <ProjectList onSelect={setSelected} />
        )}
      </main>
    </>
  )
}
