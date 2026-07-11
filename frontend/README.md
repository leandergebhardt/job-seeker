# Job Seeker — Frontend

Minimal dark-themed Vue 3 interface for searching, saving, and tracking job applications. Connects to the [Spring Boot backend](../README.md) over a local proxy — no CORS configuration needed.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Node.js | 18+ |
| npm | 9+ |
| Job Seeker backend | running on port 8080 |

---

## Installation

### 1. Navigate to the frontend directory

```bash
cd frontend
```

### 2. Install dependencies

```bash
npm install
```

### 3. Start the dev server

```bash
npm run dev
```

The app is available at **http://localhost:5173**.  
All `/api/...` requests are proxied to the backend at `http://localhost:8080` — start the backend first.

---

## Starting the full stack

Open two terminals:

```bash
# Terminal 1 — backend (from repo root)
mvn spring-boot:run

# Terminal 2 — frontend
cd frontend && npm run dev
```

---

## Building for production

```bash
npm run build
```

Output is written to `frontend/dist/`. Serve it with any static host (Nginx, Vercel, Netlify) and point `VITE_API_BASE_URL` at your deployed backend.

---

## Configuration

Copy `.env.example` to `.env` and edit if needed:

```bash
cp .env.example .env
```

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_BASE_URL` | *(empty)* | Backend base URL. Leave blank in local dev — Vite's proxy handles it. Set to `https://api.example.com` in production. |

---

## Features

### Search
- Enter a job title and location (defaults to **Germany**)
- Choose how many results to return (10 / 25 / 50)
- Results are fetched from the RapidAPI Active Jobs DB via the backend and stored locally
- Save any job with one click; open the Apply modal to start tracking an application

### Saved
- Lists all jobs you've saved across sessions
- Unsaving removes it from the list immediately
- Apply modal available from every card

### Applications
- Table view of every recorded application
- Filter by status: All · Applied · Interviewing · Offer · Rejected · Withdrawn
- Change status inline — auto-saves on selection
- Delete any application row

---

## Project structure

```
frontend/
├── .env.example
├── index.html
├── package.json
├── vite.config.js          # Dev server + /api proxy
└── src/
    ├── main.js             # App entry point
    ├── style.css           # Global tokens, reset, typography
    ├── api.js              # Typed fetch wrappers for backend endpoints
    ├── App.vue             # Shell: tab nav + global toast system
    ├── views/
    │   ├── SearchView.vue       # Job search + results
    │   ├── SavedView.vue        # Saved jobs list
    │   └── ApplicationsView.vue # Application tracker table
    └── components/
        ├── JobCard.vue          # Reusable job card with save/apply actions
        └── ApplyModal.vue       # Modal form for recording an application
```

---

## API endpoints used

| Method | Path | Used in |
|--------|------|---------|
| `GET` | `/api/jobs/search` | SearchView |
| `GET` | `/api/jobs/saved` | SavedView |
| `POST` | `/api/jobs/{id}/save` | JobCard |
| `DELETE` | `/api/jobs/{id}/save` | JobCard |
| `GET` | `/api/applications` | ApplicationsView |
| `POST` | `/api/applications` | ApplyModal |
| `PUT` | `/api/applications/{id}` | ApplicationsView |
| `DELETE` | `/api/applications/{id}` | ApplicationsView |

Full backend API reference is in the [root README](../README.md).