import { useEffect, useState } from 'react'
import { api } from '../api/client'

export default function ProjectList({ onSelect }) {
  const [projects, setProjects] = useState([])
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function load() {
    try {
      setError('')
      setProjects(await api.listProjects())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  async function handleSubmit(event) {
    event.preventDefault()
    try {
      setError('')
      await api.createProject({ name, description })
      setName('')
      setDescription('')
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  async function handleDelete(id) {
    if (!window.confirm('Apagar este projeto e todas as suas tarefas?')) return
    try {
      setError('')
      await api.deleteProject(id)
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <section>
      <h2>Projetos</h2>

      <form onSubmit={handleSubmit} className="form">
        <input
          placeholder="Nome do projeto"
          value={name}
          onChange={(e) => setName(e.target.value)}
          aria-label="Nome do projeto"
        />
        <input
          placeholder="Descrição (opcional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          aria-label="Descrição do projeto"
        />
        <button type="submit">Criar projeto</button>
      </form>

      {error && <p className="error" role="alert">{error}</p>}
      {loading && <p>Carregando...</p>}
      {!loading && projects.length === 0 && (
        <p className="muted">Nenhum projeto ainda. Crie o primeiro acima.</p>
      )}

      <ul className="list">
        {projects.map((p) => (
          <li key={p.id} className="card">
            <div>
              <strong>{p.name}</strong>
              {p.description && <p className="muted">{p.description}</p>}
            </div>
            <div className="actions">
              <button onClick={() => onSelect(p)}>Abrir</button>
              <button className="danger" onClick={() => handleDelete(p.id)}>
                Apagar
              </button>
            </div>
          </li>
        ))}
      </ul>
    </section>
  )
}
