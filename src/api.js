const BASE = import.meta.env.VITE_API_BASE_URL ?? ''

async function req(path, opts = {}) {
  let res
  try {
    res = await fetch(BASE + path, {
      ...opts,
      headers: { 'Content-Type': 'application/json', ...opts.headers },
    })
  } catch (error) {
    if (error.name === 'AbortError') throw error
    if (error instanceof TypeError) {
      throw new Error('Unable to connect to the server. Check your connection and try again.')
    }
    throw error
  }

  if (res.status === 204) return null

  const text = await res.text()
  let data
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      if (res.ok) throw new Error('The server returned an invalid response.')
    }
  } else if (res.ok) {
    throw new Error('The server returned an empty response.')
  }

  if (!res.ok) {
    throw new Error(data?.detail || data?.message || `Request failed (${res.status}).`)
  }
  return data
}

export const jobsApi = {
  search: (params, options) => req(`/api/jobs/search?${new URLSearchParams(params)}`, options),
  saved:  ()       => req('/api/jobs/saved'),
  save:   (id)     => req(`/api/jobs/${id}/save`, { method: 'POST' }),
  unsave: (id)     => req(`/api/jobs/${id}/save`, { method: 'DELETE' }),
  remove: (id)     => req(`/api/jobs/${id}`,      { method: 'DELETE' }),
}

export const appsApi = {
  list:   ()         => req('/api/applications'),
  create: (body)     => req('/api/applications',       { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => req(`/api/applications/${id}`, { method: 'PUT',  body: JSON.stringify(body) }),
  remove: (id)       => req(`/api/applications/${id}`, { method: 'DELETE' }),
}
