import { useState } from 'react'
import ProjectList from './components/ProjectList'
import TaskList from './components/TaskList'
import './App.css'

export default function App() {
  const [selected, setSelected] = useState(null)

  return (
    <main className="container">
      <h1>TaskFlow AI</h1>
      {selected ? (
        <TaskList project={selected} onBack={() => setSelected(null)} />
      ) : (
        <ProjectList onSelect={setSelected} />
      )}
    </main>
  )
}
