const BASE = import.meta.env.VITE_API_BASE_URL ?? ''

async function req(path, opts = {}) {
  const res = await fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...opts,
  })
  if (res.status === 204) return null
  const data = await res.json()
  if (!res.ok) throw new Error(data?.detail || data?.message || 'Request failed')
  return data
}

export const jobsApi = {
  search: (params) => req(`/api/jobs/search?${new URLSearchParams(params)}`),
  saved:  ()       => req('/api/jobs/saved'),
  save:   (id)     => req(`/api/jobs/${id}/save`, { method: 'POST' }),
  unsave: (id)     => req(`/api/jobs/${id}/save`, { method: 'DELETE' }),
  remove: (id)     => req(`/api/jobs/${id}`,      { method: 'DELETE' }),
}

export const appsApi = {
  list:   ()         => req('/api/applications'),
  create: (body)     => req('/api/applications',      { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => req(`/api/applications/${id}`, { method: 'PUT',  body: JSON.stringify(body) }),
  remove: (id)       => req(`/api/applications/${id}`, { method: 'DELETE' }),
}
