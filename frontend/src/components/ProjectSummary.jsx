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
      <button onClick={generate} disabled={loading}>
        {loading ? 'Resumindo...' : 'Resumir andamento com IA'}
      </button>
      {error && <p className="error" role="alert">{error}</p>}
      {text && (
        <div className="ai-result">
          <p>{text}</p>
          <FeedbackButtons feature="summary" />
        </div>
      )}
    </div>
  )
}
