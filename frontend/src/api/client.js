const TOKEN_KEY = 'taskflow_token'

export const auth = {
  getToken: () => localStorage.getItem(TOKEN_KEY),
  setToken: (token) => localStorage.setItem(TOKEN_KEY, token),
  clear: () => localStorage.removeItem(TOKEN_KEY),
}

let onUnauthorized = () => {}
export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json' }
  const token = auth.getToken()
  if (token) headers.Authorization = `Bearer ${token}`

  const response = await fetch(`/api${path}`, { ...options, headers })

  if (!response.ok) {
    let message = 'Algo deu errado. Tente novamente.'
    try {
      const body = await response.json()
      if (body.details) {
        message = Object.values(body.details).join(' ')
      } else if (body.message) {
        message = body.message
      }
    } catch {
      // resposta sem JSON: mantém a mensagem padrão
    }

    // Token inválido/expirado em rota protegida: volta para o login
    if (response.status === 401 && token) {
      onUnauthorized()
      message = 'Sua sessão expirou. Entre novamente.'
    }
    throw new Error(message)
  }

  if (response.status === 204) return null
  return response.json()
}

export const api = {
  register: (data) =>
    request('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  login: (data) =>
    request('/auth/login', { method: 'POST', body: JSON.stringify(data) }),

  listProjects: () => request('/projects'),
  createProject: (data) =>
    request('/projects', { method: 'POST', body: JSON.stringify(data) }),
  deleteProject: (id) => request(`/projects/${id}`, { method: 'DELETE' }),

  listTasks: (projectId) => request(`/projects/${projectId}/tasks`),
  createTask: (projectId, data) =>
    request(`/projects/${projectId}/tasks`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  updateTask: (id, data) =>
    request(`/tasks/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteTask: (id) => request(`/tasks/${id}`, { method: 'DELETE' }),
}
