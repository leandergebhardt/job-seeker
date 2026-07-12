import 'dotenv/config'
import express from 'express'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'
import jobsRouter        from './routes/jobs.js'
import applicationsRouter from './routes/applications.js'

const __dirname = dirname(fileURLToPath(import.meta.url))
const app  = express()
const PORT = process.env.PORT ?? 3000

app.use(express.json())

// ── API routes ─────────────────────────────────────────────
app.use('/api/jobs',         jobsRouter)
app.use('/api/applications', applicationsRouter)

// ── Serve Vue frontend (production only) ───────────────────
// In development Vite handles the frontend on :5173 and
// proxies /api calls here. In production `npm run build`
// writes the bundle to dist/ which Express serves directly.
if (process.env.NODE_ENV === 'production') {
  const dist = join(__dirname, 'dist')
  app.use(express.static(dist))
  app.get('*', (_req, res) => res.sendFile(join(dist, 'index.html')))
}

app.listen(PORT, () => {
  console.log(`[api] http://localhost:${PORT}/api/jobs/search`)
  if (process.env.NODE_ENV !== 'production') {
    console.log('[vue] run  npm run dev  to start Vite on :5173')
  }
})
