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

  return (
    <main className="container">
      <header className="header">
        <h1>TaskFlow AI</h1>
        {authenticated && <button onClick={logout}>Sair</button>}
      </header>

      {!authenticated ? (
        <AuthForm onAuthenticated={() => setAuthenticated(true)} />
      ) : selected ? (
        <TaskList project={selected} onBack={() => setSelected(null)} />
      ) : (
        <ProjectList onSelect={setSelected} />
      )}
    </main>
  )
}
