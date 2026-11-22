import { randomUUID } from 'node:crypto'
import { fetchJobs } from '../../services/jobApi.js'

export const handler = async (event) => {
  if (event.httpMethod !== 'GET') {
    return {
      statusCode: 405,
      headers: { Allow: 'GET', 'Content-Type': 'application/json' },
      body: JSON.stringify({ detail: 'Method not allowed' }),
    }
  }

  const params = new URL(event.rawUrl).searchParams
  const title = params.get('title') || 'Software Engineer'
  const location = params.get('location') || 'Germany'
  const limit = Number(params.get('limit') || 10)
  const offset = Number(params.get('offset') || 0)

  try {
    const jobs = await fetchJobs({ title, location, limit, offset })
    return {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(jobs.map((job) => ({
        id: randomUUID(),
        externalId: job.id ?? null,
        title: job.title ?? null,
        company: job.organization ?? null,
        location: job.locations_raw ?? null,
        description: job.description_text ?? null,
        url: job.url ?? null,
        datePosted: job.date_posted ?? null,
        saved: false,
        createdAt: null,
      }))),
    }
  } catch (error) {
    console.error('[/api/jobs/search]', error.message)
    return {
      statusCode: 500,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ detail: error.message }),
    }
  }
}
