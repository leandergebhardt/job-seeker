import { Router }     from 'express'
import { randomUUID } from 'crypto'
import db              from '../db/database.js'

const router = Router()

const VALID_STATUSES = new Set(['APPLIED', 'INTERVIEWING', 'OFFER', 'REJECTED', 'WITHDRAWN'])

function toResponse(app) {
  const job = db.prepare('SELECT title, company FROM jobs WHERE id = ?').get(app.job_id)
  return {
    id:          app.id,
    jobId:       app.job_id,
    jobTitle:    job?.title   ?? null,
    company:     job?.company ?? null,
    status:      app.status,
    notes:       app.notes,
    resumeUrl:   app.resume_url,
    coverLetter: app.cover_letter,
    appliedAt:   app.applied_at,
    updatedAt:   app.updated_at,
  }
}

// GET /api/applications
router.get('/', (_req, res) => {
  const rows = db.prepare('SELECT * FROM applications ORDER BY applied_at DESC').all()
  res.json(rows.map(toResponse))
})

// GET /api/applications/job/:jobId — must come BEFORE /:id
router.get('/job/:jobId', (req, res) => {
  const rows = db
    .prepare('SELECT * FROM applications WHERE job_id = ? ORDER BY applied_at DESC')
    .all(req.params.jobId)
  res.json(rows.map(toResponse))
})

// GET /api/applications/:id
router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM applications WHERE id = ?').get(req.params.id)
  if (!row) return res.status(404).json({ detail: 'Application not found' })
  res.json(toResponse(row))
})

// POST /api/applications
router.post('/', (req, res) => {
  const { jobId, status = 'APPLIED', notes, resumeUrl, coverLetter } = req.body
  if (!jobId) return res.status(400).json({ detail: 'jobId is required' })
  if (!VALID_STATUSES.has(status)) {
    return res.status(400).json({ detail: `status must be one of: ${[...VALID_STATUSES].join(', ')}` })
  }
  const job = db.prepare('SELECT id FROM jobs WHERE id = ?').get(jobId)
  if (!job) return res.status(404).json({ detail: 'Job not found' })

  const id = randomUUID()
  db.prepare(`
    INSERT INTO applications (id, job_id, status, notes, resume_url, cover_letter)
    VALUES (?, ?, ?, ?, ?, ?)
  `).run(id, jobId, status, notes ?? null, resumeUrl ?? null, coverLetter ?? null)

  res.status(201).json(toResponse(db.prepare('SELECT * FROM applications WHERE id = ?').get(id)))
})

// PUT /api/applications/:id
router.put('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM applications WHERE id = ?').get(req.params.id)
  if (!row) return res.status(404).json({ detail: 'Application not found' })

  const { status, notes, resumeUrl, coverLetter } = req.body
  if (status && !VALID_STATUSES.has(status)) {
    return res.status(400).json({ detail: `status must be one of: ${[...VALID_STATUSES].join(', ')}` })
  }

  db.prepare(`
    UPDATE applications
    SET status = ?, notes = ?, resume_url = ?, cover_letter = ?, updated_at = datetime('now')
    WHERE id = ?
  `).run(
    status      ?? row.status,
    notes       ?? row.notes,
    resumeUrl   ?? row.resume_url,
    coverLetter ?? row.cover_letter,
    req.params.id,
  )

  res.json(toResponse(db.prepare('SELECT * FROM applications WHERE id = ?').get(req.params.id)))
})

// DELETE /api/applications/:id
router.delete('/:id', (req, res) => {
  const info = db.prepare('DELETE FROM applications WHERE id = ?').run(req.params.id)
  if (info.changes === 0) return res.status(404).json({ detail: 'Application not found' })
  res.status(204).end()
})

export default router
