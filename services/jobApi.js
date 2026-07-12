const RAPIDAPI_HOST = 'active-jobs-db.p.rapidapi.com'
const RAPIDAPI_BASE = `https://${RAPIDAPI_HOST}`

/**
 * Fetches active job listings from active-jobs-db.p.rapidapi.com.
 * Mirrors the query params used by the original Spring WebClient.
 */
export async function fetchJobs({ title, location, limit = 10, offset = 0 }) {
  const params = new URLSearchParams({
    time_frame:         '24h',
    limit:              String(limit),
    offset:             String(offset),
    description_format: 'text',
    title:              `"${title}"`,
    location,
  })

  const res = await fetch(`${RAPIDAPI_BASE}/active-ats?${params}`, {
    headers: {
      'Content-Type':   'application/json',
      'x-rapidapi-host': RAPIDAPI_HOST,
      'x-rapidapi-key':  process.env.RAPIDAPI_KEY ?? '',
    },
  })

  if (!res.ok) {
    const body = await res.text()
    throw new Error(`RapidAPI ${res.status}: ${body}`)
  }

  return res.json()
}
