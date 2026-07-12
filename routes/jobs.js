import { Router }     from 'express'
import { randomUUID } from 'crypto'
import db              from '../db/database.js'
import { fetchJobs }   from '../services/jobApi.js'

const router = Router()

// Normalise a DB row to the JSON shape the frontend expects
function toResponse(row) {
  return {
    id:          row.id,
    externalId:  row.external_id,
    title:       row.title,
    company:     row.company,
    location:    row.location,
    description: row.description,
    url:         row.url,
    datePosted:  row.date_posted,
    saved:       Boolean(row.saved),
    createdAt:   row.created_at,
  }
}

// GET /api/jobs/search — /search and /saved must come BEFORE /:id
router.get('/search', async (req, res) => {
  const {
    title    = 'Software Engineer',
    location = 'Germany',
    limit    = 10,
    offset   = 0,
  } = req.query

  try {
    const external = await fetchJobs({ title, location, limit: Number(limit), offset: Number(offset) })

    // Bulk-upsert: insert new jobs, skip known ones by external_id
    const upsert = db.prepare(`
      INSERT INTO jobs (id, external_id, title, company, location, description, url, date_posted)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
      ON CONFLICT (external_id) DO NOTHING
    `)

    db.exec('BEGIN')
    try {
      for (const j of external) {
        upsert.run(
          randomUUID(),
          j.id             ?? null,
          j.title          ?? null,
          j.organization   ?? null,
          j.locations_raw  ?? null,
          j.description_text ?? null,
          j.url            ?? null,
          j.date_posted    ?? null,
        )
      }
      db.exec('COMMIT')
    } catch (e) {
      db.exec('ROLLBACK')
      throw e
    }

    // Return from DB so `saved` flags are accurate
    const externalIds = external.map(j => j.id).filter(Boolean)
    if (externalIds.length === 0) return res.json([])

    const placeholders = externalIds.map(() => '?').join(',')
    const rows = db
      .prepare(`SELECT * FROM jobs WHERE external_id IN (${placeholders})`)
      .all(...externalIds)

    res.json(rows.map(toResponse))
  } catch (err) {
    console.error('[/api/jobs/search]', err.message)
    res.status(500).json({ detail: err.message })
  }
})

// GET /api/jobs/saved
router.get('/saved', (_req, res) => {
  const rows = db.prepare('SELECT * FROM jobs WHERE saved = 1 ORDER BY created_at DESC').all()
  res.json(rows.map(toResponse))
})

// GET /api/jobs/:id
router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM jobs WHERE id = ?').get(req.params.id)
  if (!row) return res.status(404).json({ detail: 'Job not found' })
  res.json(toResponse(row))
})

// POST /api/jobs/:id/save
router.post('/:id/save', (req, res) => {
  const row = db.prepare('SELECT * FROM jobs WHERE id = ?').get(req.params.id)
  if (!row) return res.status(404).json({ detail: 'Job not found' })
  db.prepare('UPDATE jobs SET saved = 1 WHERE id = ?').run(req.params.id)
  res.json(toResponse({ ...row, saved: 1 }))
})

// DELETE /api/jobs/:id/save
router.delete('/:id/save', (req, res) => {
  const row = db.prepare('SELECT * FROM jobs WHERE id = ?').get(req.params.id)
  if (!row) return res.status(404).json({ detail: 'Job not found' })
  db.prepare('UPDATE jobs SET saved = 0 WHERE id = ?').run(req.params.id)
  res.json(toResponse({ ...row, saved: 0 }))
})

// DELETE /api/jobs/:id
router.delete('/:id', (req, res) => {
  const info = db.prepare('DELETE FROM jobs WHERE id = ?').run(req.params.id)
  if (info.changes === 0) return res.status(404).json({ detail: 'Job not found' })
  res.status(204).end()
})

export default router
