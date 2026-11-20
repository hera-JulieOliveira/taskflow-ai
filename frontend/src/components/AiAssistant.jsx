import { useState } from 'react'
import { api } from '../api/client'
import FeedbackButtons from './FeedbackButtons'

export default function AiAssistant({ title, description, onUseDescription, onUseSubtasks }) {
  const [loading, setLoading] = useState('')
  const [error, setError] = useState('')
  const [result, setResult] = useState(null)

  const hasTitle = title.trim().length > 0

  async function run(feature) {
    setLoading(feature)
    setError('')
    setResult(null)
    try {
      const payload = { title, description }
      if (feature === 'description') {
        const { text } = await api.aiDescription(payload)
        setResult({ feature, text })
      } else {
        const { items } = await api.aiSubtasks(payload)
        setResult({ feature, items })
      }
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading('')
    }
  }

  return (
    <div className="ai-panel">
      <strong>Assistente de IA</strong>
      <div className="actions">
        <button
          type="button"
          onClick={() => run('description')}
          disabled={!hasTitle || loading !== ''}
        >
          {loading === 'description' ? 'Gerando...' : 'Gerar descrição'}
        </button>
        <button
          type="button"
          onClick={() => run('subtasks')}
          disabled={!hasTitle || loading !== ''}
        >
          {loading === 'subtasks' ? 'Pensando...' : 'Sugerir subtarefas'}
        </button>
      </div>
      {!hasTitle && <p className="muted">Digite o título da tarefa para usar a IA.</p>}

      {error && <p className="error" role="alert">{error}</p>}

      {result?.feature === 'description' && (
        <div className="ai-result">
          <p>{result.text}</p>
          <button type="button" onClick={() => onUseDescription(result.text)}>
            Usar esta descrição
          </button>
          <FeedbackButtons feature="description" />
        </div>
      )}

      {result?.feature === 'subtasks' && (
        <div className="ai-result">
          <ul>
            {result.items.map((item, i) => <li key={i}>{item}</li>)}
          </ul>
          <button type="button" onClick={() => onUseSubtasks(result.items)}>
            Criar estas subtarefas
          </button>
          <FeedbackButtons feature="subtasks" />
        </div>
      )}

      <p className="muted small">Conteúdo gerado por IA. Revise antes de usar.</p>
    </div>
  )
}
