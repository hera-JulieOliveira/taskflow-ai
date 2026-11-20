import { useEffect, useState } from 'react'
import { api } from '../api/client'
import AiAssistant from './AiAssistant'
import ProjectSummary from './ProjectSummary'

const STATUS_LABELS = {
  TODO: 'A fazer',
  IN_PROGRESS: 'Em andamento',
  DONE: 'Concluída',
}

export default function TaskList({ project, onBack }) {
  const [tasks, setTasks] = useState([])
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function load() {
    try {
      setError('')
      setTasks(await api.listTasks(project.id))
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [project.id])

  async function handleSubmit(event) {
    event.preventDefault()
    try {
      setError('')
      await api.createTask(project.id, { title, description })
      setTitle('')
      setDescription('')
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  async function createSubtasks(items) {
    try {
      setError('')
      for (const item of items) {
        await api.createTask(project.id, { title: item.slice(0, 150) })
      }
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  async function handleStatusChange(task, status) {
    try {
      setError('')
      await api.updateTask(task.id, {
        title: task.title,
        description: task.description,
        status,
      })
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  async function handleDelete(id) {
    try {
      setError('')
      await api.deleteTask(id)
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <section>
      <button onClick={onBack}>← Voltar</button>
      <h2>{project.name}</h2>
      {project.description && <p className="muted">{project.description}</p>}

      <ProjectSummary projectId={project.id} />

      <form onSubmit={handleSubmit} className="form">
        <input
          placeholder="Título da tarefa"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          aria-label="Título da tarefa"
        />
        <input
          placeholder="Descrição (opcional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          aria-label="Descrição da tarefa"
        />
        <button type="submit">Adicionar tarefa</button>
      </form>

      <AiAssistant
        title={title}
        description={description}
        onUseDescription={setDescription}
        onUseSubtasks={createSubtasks}
      />

      {error && <p className="error" role="alert">{error}</p>}
      {loading && <p>Carregando...</p>}
      {!loading && tasks.length === 0 && (
        <p className="muted">Nenhuma tarefa neste projeto.</p>
      )}

      <ul className="list">
        {tasks.map((t) => (
          <li key={t.id} className="card">
            <div>
              <strong>{t.title}</strong>
              {t.description && <p className="muted">{t.description}</p>}
            </div>
            <div className="actions">
              <select
                value={t.status}
                onChange={(e) => handleStatusChange(t, e.target.value)}
                aria-label={`Status da tarefa ${t.title}`}
              >
                {Object.entries(STATUS_LABELS).map(([value, label]) => (
                  <option key={value} value={value}>{label}</option>
                ))}
              </select>
              <button className="danger" onClick={() => handleDelete(t.id)}>
                Apagar
              </button>
            </div>
          </li>
        ))}
      </ul>
    </section>
  )
}
