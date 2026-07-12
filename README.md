# job.seeker

A self-contained job-search and application-tracking app. Single Node.js process serves both the REST API and the Vue 3 frontend. Data is stored in a local SQLite database — no separate database server required.

---

## Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Vue 3 + Vite |
| Backend | Node.js + Express |
| Database | SQLite (`better-sqlite3`) |
| Jobs data | [Active Jobs DB](https://rapidapi.com/fantageek/api/active-jobs-db) via RapidAPI |

---

## Prerequisites

| Tool | Version |
|------|---------|
| Node.js | 18+ |
| npm | 9+ |

---

## Installation

```bash
git clone https://github.com/leandergebhardt/job-seeker.git
cd job-seeker
npm install
cp .env.example .env   # then add your RAPIDAPI_KEY
```

Get a free API key at [rapidapi.com → Active Jobs DB](https://rapidapi.com/fantageek/api/active-jobs-db).

---

## Running

### Development (two servers, hot reload)

```bash
npm run dev
```

| Server | URL | Role |
|--------|-----|------|
| Express | http://localhost:3000 | REST API |
| Vite | http://localhost:5173 | Frontend (proxies `/api` → Express) |

Open **http://localhost:5173** in your browser.

### Production (single server)

```bash
npm run build   # compiles Vue into dist/
npm start       # Express serves API + dist/ on :3000
```

Open **http://localhost:3000**.

---

## Features

**Search** — Enter a job title and location (defaults to Germany). Results are fetched live from RapidAPI and cached in the local database.

**Saved** — One-click save any job. Persisted across restarts.

**Applications** — Track every application with status, notes, resume URL, and cover letter. Update status inline; filter by stage.

---

## Project structure

```
job-seeker/
├── server.js              # Express entry — API + static serving
├── db/
│   └── database.js        # SQLite connection + schema creation
├── routes/
│   ├── jobs.js            # GET /api/jobs/search, /saved, /:id …
│   └── applications.js    # GET/POST/PUT/DELETE /api/applications …
├── services/
│   └── jobApi.js          # RapidAPI HTTP client
├── src/                   # Vue 3 source
│   ├── App.vue
│   ├── api.js
│   ├── main.js
│   ├── style.css
│   ├── components/
│   │   ├── JobCard.vue
│   │   └── ApplyModal.vue
│   └── views/
│       ├── SearchView.vue
│       ├── SavedView.vue
│       └── ApplicationsView.vue
├── public/
│   └── favicon.svg
├── data/                  # SQLite database (git-ignored)
│   └── jobseeker.db
├── .env.example
├── vite.config.js
└── index.html
```

---

## API reference

### Jobs

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/jobs/search?title=&location=&limit=&offset=` | Fetch from RapidAPI + cache locally |
| `GET` | `/api/jobs/saved` | List saved jobs |
| `GET` | `/api/jobs/:id` | Single job |
| `POST` | `/api/jobs/:id/save` | Mark as saved |
| `DELETE` | `/api/jobs/:id/save` | Unmark saved |
| `DELETE` | `/api/jobs/:id` | Delete job + its applications |

### Applications

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/applications` | All applications |
| `GET` | `/api/applications/:id` | Single application |
| `GET` | `/api/applications/job/:jobId` | Applications for a job |
| `POST` | `/api/applications` | Create application |
| `PUT` | `/api/applications/:id` | Update status / notes |
| `DELETE` | `/api/applications/:id` | Delete |

#### Application status values

`APPLIED` · `INTERVIEWING` · `OFFER` · `REJECTED` · `WITHDRAWN`

---

## Environment variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `RAPIDAPI_KEY` | Yes | — | API key from rapidapi.com |
| `PORT` | No | `3000` | Port Express listens on |
