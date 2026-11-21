import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import AiAssistant from './AiAssistant'
import ProjectSummary from './ProjectSummary'
import Modal from './Modal'

const COLUMNS = [
  { status: 'TODO', label: 'A fazer', dot: 'dot-todo' },
  { status: 'IN_PROGRESS', label: 'Em andamento', dot: 'dot-progress' },
  { status: 'DONE', label: 'Concluída', dot: 'dot-done' },
]

export default function TaskList({ project, onBack }) {
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [creating, setCreating] = useState(false)
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [formError, setFormError] = useState('')

  const load = useCallback(async () => {
    try {
      setError('')
      setTasks(await api.listTasks(project.id))
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [project.id])

  useEffect(() => {
    let active = true
    api.listTasks(project.id)
      .then((data) => active && setTasks(data))
      .catch((e) => active && setError(e.message))
      .finally(() => active && setLoading(false))
    return () => { active = false }
  }, [project.id])

  function closeModal() {
    setCreating(false)
    setTitle('')
    setDescription('')
    setFormError('')
  }

  async function handleSubmit(event) {
    event.preventDefault()
    try {
      setFormError('')
      await api.createTask(project.id, { title, description })
      closeModal()
      await load()
    } catch (e) {
      setFormError(e.message)
    }
  }

  async function createSubtasks(items) {
    try {
      setFormError('')
      for (const item of items) {
        await api.createTask(project.id, { title: item.slice(0, 150) })
      }
      await load()
    } catch (e) {
      setFormError(e.message)
    }
  }

  async function move(task, status) {
    try {
      setError('')
      await api.updateTask(task.id, { title: task.title, description: task.description, status })
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  async function remove(id) {
    try {
      setError('')
      await api.deleteTask(id)
      await load()
    } catch (e) {
      setError(e.message)
    }
  }

  const done = tasks.filter((t) => t.status === 'DONE').length
  const percent = tasks.length ? Math.round((done / tasks.length) * 100) : 0

  return (
    <>
      <button className="breadcrumb" onClick={onBack}>← Todos os projetos</button>

      <div className="page-header">
        <div>
          <h2 className="page-title">{project.name}</h2>
          {project.description && <p className="page-subtitle">{project.description}</p>}
        </div>
        <button className="btn btn-primary" onClick={() => setCreating(true)}>
          + Nova tarefa
        </button>
      </div>

      {tasks.length > 0 && (
        <div style={{ marginBottom: 24 }}>
          <div className="progress-label">
            <span>Progresso</span>
            <span>{done} de {tasks.length} concluídas ({percent}%)</span>
          </div>
          <div className="progress"><span style={{ width: `${percent}%` }} /></div>
        </div>
      )}

      <div className="layout-2">
        <ProjectSummary projectId={project.id} />
      </div>

      {error && <div className="alert" role="alert" style={{ marginBottom: 16 }}>{error}</div>}

      {loading ? (
        <div className="board">
          <div className="skeleton" />
          <div className="skeleton" />
          <div className="skeleton" />
        </div>
      ) : (
        <div className="board">
          {COLUMNS.map((col, index) => {
            const items = tasks.filter((t) => t.status === col.status)
            return (
              <section key={col.status} className="column" aria-label={col.label}>
                <div className="column-header">
                  <span><span className={`dot ${col.dot}`} />{col.label}</span>
                  <span className="badge">{items.length}</span>
                </div>

                {items.length === 0 && <p className="column-empty">Nada por aqui</p>}

                {items.map((t) => (
                  <article key={t.id} className="task-card">
                    <h4>{t.title}</h4>
                    {t.description && <p>{t.description}</p>}
                    <div className="task-actions">
                      <div className="task-moves">
                        {index > 0 && (
                          <button
                            className="btn btn-sm"
                            onClick={() => move(t, COLUMNS[index - 1].status)}
                            aria-label={`Mover para ${COLUMNS[index - 1].label}`}
                          >
                            ←
                          </button>
                        )}
                        {index < COLUMNS.length - 1 && (
                          <button
                            className="btn btn-sm"
                            onClick={() => move(t, COLUMNS[index + 1].status)}
                            aria-label={`Mover para ${COLUMNS[index + 1].label}`}
                          >
                            →
                          </button>
                        )}
                      </div>
                      <button
                        className="btn btn-danger-ghost btn-sm"
                        onClick={() => remove(t.id)}
                        aria-label={`Apagar tarefa ${t.title}`}
                      >
                        Apagar
                      </button>
                    </div>
                  </article>
                ))}
              </section>
            )
          })}
        </div>
      )}

      {creating && (
        <Modal title="Nova tarefa" onClose={closeModal}>
          <form onSubmit={handleSubmit} className="form-stack">
            <div className="field">
              <label htmlFor="t-title">Título</label>
              <input
                id="t-title"
                className="input"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Ex.: Configurar pipeline de CI"
                autoFocus
              />
            </div>
            <div className="field">
              <label htmlFor="t-desc">Descrição (opcional)</label>
              <textarea
                id="t-desc"
                className="input"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Detalhes da tarefa"
              />
            </div>

            <AiAssistant
              title={title}
              description={description}
              onUseDescription={setDescription}
              onUseSubtasks={async (items) => {
                await createSubtasks(items)
                closeModal()
              }}
            />

            {formError && <div className="alert" role="alert">{formError}</div>}
            <div className="modal-actions">
              <button type="button" className="btn" onClick={closeModal}>Cancelar</button>
              <button type="submit" className="btn btn-primary">Criar tarefa</button>
            </div>
          </form>
        </Modal>
      )}
    </>
  )
}
