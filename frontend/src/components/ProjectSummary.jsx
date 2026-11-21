import { useState } from 'react'
import { api } from '../api/client'
import FeedbackButtons from './FeedbackButtons'

export default function ProjectSummary({ projectId }) {
  const [text, setText] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function generate() {
    setLoading(true)
    setError('')
    setText('')
    try {
      const response = await api.aiSummary(projectId)
      setText(response.text)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="ai-panel">
      <div className="task-actions">
        <div className="ai-title"><span aria-hidden="true">✨</span> Resumo com IA</div>
        <button className="btn btn-ai btn-sm" onClick={generate} disabled={loading}>
          {loading ? 'Resumindo...' : 'Resumir andamento'}
        </button>
      </div>
      {error && <div className="alert" role="alert">{error}</div>}
      {text && (
        <div className="ai-result">
          <p>{text}</p>
          <FeedbackButtons feature="summary" />
        </div>
      )}
    </div>
  )
}
