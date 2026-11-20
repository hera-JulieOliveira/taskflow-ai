import { useState } from 'react'
import { api } from '../api/client'

export default function FeedbackButtons({ feature }) {
  const [sent, setSent] = useState(false)
  const [error, setError] = useState('')

  async function send(helpful) {
    try {
      setError('')
      await api.aiFeedback({ feature, helpful })
      setSent(true)
    } catch (e) {
      setError(e.message)
    }
  }

  if (sent) return <p className="muted">Obrigada pelo feedback!</p>

  return (
    <div className="feedback">
      <span className="muted">Esta resposta foi útil?</span>
      <button onClick={() => send(true)} aria-label="Resposta útil">👍</button>
      <button onClick={() => send(false)} aria-label="Resposta não útil">👎</button>
      {error && <span className="error" role="alert">{error}</span>}
    </div>
  )
}
