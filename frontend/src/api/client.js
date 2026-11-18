async function request(path, options = {}) {
  const response = await fetch(`/api${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })

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
    throw new Error(message)
  }

  if (response.status === 204) return null
  return response.json()
}

export const api = {
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
