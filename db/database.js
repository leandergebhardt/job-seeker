import { DatabaseSync } from 'node:sqlite'
import { mkdirSync } from 'fs'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'

const __dirname = dirname(fileURLToPath(import.meta.url))
const dataDir   = join(__dirname, '../data')
mkdirSync(dataDir, { recursive: true })

const db = new DatabaseSync(join(dataDir, 'jobseeker.db'))

db.exec('PRAGMA journal_mode = WAL')
db.exec('PRAGMA foreign_keys = ON')

db.exec(`
  CREATE TABLE IF NOT EXISTS jobs (
    id          TEXT PRIMARY KEY,
    external_id TEXT UNIQUE,
    title       TEXT NOT NULL,
    company     TEXT,
    location    TEXT,
    description TEXT,
    url         TEXT,
    date_posted TEXT,
    saved       INTEGER NOT NULL DEFAULT 0,
    created_at  TEXT    NOT NULL DEFAULT (datetime('now'))
  );

  CREATE TABLE IF NOT EXISTS applications (
    id           TEXT PRIMARY KEY,
    job_id       TEXT NOT NULL,
    status       TEXT NOT NULL DEFAULT 'APPLIED',
    notes        TEXT,
    resume_url   TEXT,
    cover_letter TEXT,
    applied_at   TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at   TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE
  );
`)

export default db
