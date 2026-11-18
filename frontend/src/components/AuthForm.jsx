import { useState } from 'react'
import { api, auth } from '../api/client'

export default function AuthForm({ onAuthenticated }) {
  const [mode, setMode] = useState('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const action = mode === 'login' ? api.login : api.register
      const { token } = await action({ email, password })
      auth.setToken(token)
      onAuthenticated()
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <section>
      <h2>{mode === 'login' ? 'Entrar' : 'Criar conta'}</h2>

      <form onSubmit={handleSubmit} className="form">
        <input
          type="email"
          placeholder="E-mail"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          aria-label="E-mail"
          autoComplete="email"
        />
        <input
          type="password"
          placeholder="Senha (mínimo 8 caracteres)"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          aria-label="Senha"
          autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Aguarde...' : mode === 'login' ? 'Entrar' : 'Cadastrar'}
        </button>
      </form>

      {error && <p className="error" role="alert">{error}</p>}

      <p className="muted">
        {mode === 'login' ? 'Ainda não tem conta? ' : 'Já tem conta? '}
        <button
          type="button"
          className="link"
          onClick={() => {
            setMode(mode === 'login' ? 'register' : 'login')
            setError('')
          }}
        >
          {mode === 'login' ? 'Cadastre-se' : 'Entrar'}
        </button>
      </p>
    </section>
  )
}
