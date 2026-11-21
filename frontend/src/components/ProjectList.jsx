import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import Modal from './Modal'
import EmptyState from './EmptyState'

export default function ProjectList({ onSelect }) {
  const [projects, setProjects] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [creating, setCreating] = useState(false)
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [formError, setFormError] = useState('')

  const load = useCallback(async () => {
    try {
      setError('')
      setProjects(await api.listProjects())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    let active = true
    api.listProjects()
      .then((data) => active && setProjects(data))
      .catch((e) => active && setError(e.message))
      .finally(() => active && setLoading(false))
    return () => { active = false }
  }, [])

  function closeModal() {
    setCreating(false)
    setName('')
    setDescription('')
    setFormError('')
  }

  async function handleSubmit(event) {
    event.preventDefault()
    try {
      setFormError('')
      await api.createProject({ name, description })
      closeModal()
      await load()
    } catch (e) {
      setFormError(e.message)
    }
  }

  async function handleDelete(project) {
    if (!window.confirm(`Apagar "${project.name}" e todas as suas tarefas?`)) return
    try {
      setError('')
      await api.deleteProject(project.id)
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  return (
    <>
      <div className="page-header">
        <div>
          <h2 className="page-title">Seus projetos</h2>
          <p className="page-subtitle">Organize o trabalho e deixe a IA ajudar no planejamento.</p>
        </div>
        <button className="btn btn-primary" onClick={() => setCreating(true)}>
          + Novo projeto
        </button>
      </div>

      {error && <div className="alert" role="alert">{error}</div>}

      {loading ? (
        <div className="grid">
          <div className="skeleton" />
          <div className="skeleton" />
          <div className="skeleton" />
        </div>
      ) : projects.length === 0 ? (
        <EmptyState
          icon="📋"
          title="Nenhum projeto ainda"
          text="Crie o primeiro projeto para começar a organizar suas tarefas."
        >
          <button className="btn btn-primary" onClick={() => setCreating(true)}>
            + Criar primeiro projeto
          </button>
        </EmptyState>
      ) : (
        <div className="grid">
          {projects.map((p) => (
            <article key={p.id} className="project-card">
              <div>
                <h3>{p.name}</h3>
                <p>{p.description || 'Sem descrição'}</p>
              </div>
              <div className="project-footer">
                <button className="btn btn-primary btn-sm" onClick={() => onSelect(p)}>
                  Abrir
                </button>
                <button
                  className="btn btn-danger-ghost btn-sm"
                  onClick={() => handleDelete(p)}
                  aria-label={`Apagar projeto ${p.name}`}
                >
                  Apagar
                </button>
              </div>
            </article>
          ))}
        </div>
      )}

      {creating && (
        <Modal title="Novo projeto" onClose={closeModal}>
          <form onSubmit={handleSubmit} className="form-stack">
            <div className="field">
              <label htmlFor="p-name">Nome</label>
              <input
                id="p-name"
                className="input"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Ex.: Lançamento do app"
                autoFocus
              />
            </div>
            <div className="field">
              <label htmlFor="p-desc">Descrição (opcional)</label>
              <textarea
                id="p-desc"
                className="input"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Do que se trata este projeto?"
              />
            </div>
            {formError && <div className="alert" role="alert">{formError}</div>}
            <div className="modal-actions">
              <button type="button" className="btn" onClick={closeModal}>Cancelar</button>
              <button type="submit" className="btn btn-primary">Criar projeto</button>
            </div>
          </form>
        </Modal>
      )}
    </>
  )
}
