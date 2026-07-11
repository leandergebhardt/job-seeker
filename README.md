# Job Seeker — Backend

A Spring Boot REST API for searching, saving, and tracking job applications. Fetches live listings from the [Active Jobs DB](https://rapidapi.com/fantageek/api/active-jobs-db) via RapidAPI and persists them locally.

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.8+ |
| Git | any recent |

For production only: PostgreSQL 15+.

---

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/leandergebhardt/job-seeker.git
cd job-seeker
```

### 2. Create your `.env` file

```bash
cp .env.example .env
```

Open `.env` and fill in your RapidAPI key:

```env
RAPIDAPI_KEY=your_rapidapi_key_here
```

> `.env` is listed in `.gitignore` and will never be committed.  
> Get a free API key at [rapidapi.com](https://rapidapi.com/fantageek/api/active-jobs-db).

### 3. Run the application

```bash
mvn spring-boot:run
```

The server starts on **http://localhost:8080**.

---

## Database

By default the app uses an **in-memory H2 database** — no setup required. Data resets when the server stops.

The H2 web console is available at **http://localhost:8080/h2-console** while the app is running.

| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:jobseekerdb` |
| Username | `sa` |
| Password | *(leave blank)* |

### Switching to PostgreSQL (production)

1. Add the remaining variables to your `.env`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=jobseeker
DB_USER=postgres
DB_PASSWORD=your_db_password_here
```

2. Create the database:

```bash
psql -U postgres -c "CREATE DATABASE jobseeker;"
```

3. Start with the production profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## API Reference

### Jobs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/jobs/search` | Fetch live jobs from RapidAPI and store them |
| `GET` | `/api/jobs/saved` | List all saved jobs |
| `GET` | `/api/jobs/{id}` | Get a single job by ID |
| `POST` | `/api/jobs/{id}/save` | Mark a job as saved |
| `DELETE` | `/api/jobs/{id}/save` | Unmark a saved job |
| `DELETE` | `/api/jobs/{id}` | Delete a job and its applications |

#### Search parameters

| Param | Default | Example |
|-------|---------|---------|
| `title` | `Software Engineer` | `Data Engineer` |
| `location` | `"United States" OR "United Kingdom"` | `Germany` |
| `limit` | `10` | `25` |
| `offset` | `0` | `10` |

**Example request:**

```bash
curl "http://localhost:8080/api/jobs/search?title=Data%20Engineer&location=Germany&limit=5"
```

---

### Applications

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/applications` | List all applications |
| `GET` | `/api/applications/{id}` | Get a single application |
| `GET` | `/api/applications/job/{jobId}` | Get all applications for a job |
| `POST` | `/api/applications` | Create a new application |
| `PUT` | `/api/applications/{id}` | Update status, notes, etc. |
| `DELETE` | `/api/applications/{id}` | Delete an application |

#### Application status values

`APPLIED` · `INTERVIEWING` · `OFFER` · `REJECTED` · `WITHDRAWN`

**Example — create an application:**

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "jobId": "your-job-uuid-here",
    "status": "APPLIED",
    "notes": "Applied via company website",
    "coverLetter": "Dear Hiring Manager..."
  }'
```

**Example — update status:**

```bash
curl -X PUT http://localhost:8080/api/applications/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "status": "INTERVIEWING",
    "notes": "Phone screen scheduled for Friday"
  }'
```

---

## Project Structure

```
src/main/java/com/jobseeker/
├── config/
│   ├── DotenvConfig.java        # Loads .env into system properties
│   └── WebClientConfig.java     # WebClient bean with RapidAPI headers
├── controller/
│   ├── JobController.java       # /api/jobs endpoints
│   └── ApplicationController.java  # /api/applications endpoints
├── dto/
│   ├── ExternalJobDto.java      # Maps RapidAPI response
│   ├── JobResponse.java         # Job response to frontend
│   ├── ApplicationRequest.java  # Create/update application body
│   └── ApplicationResponse.java # Application response to frontend
├── entity/
│   ├── Job.java                 # Job JPA entity
│   ├── JobApplication.java      # Application JPA entity
│   └── ApplicationStatus.java   # Status enum
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── JobNotFoundException.java
│   └── ApplicationNotFoundException.java
├── repository/
│   ├── JobRepository.java
│   └── ApplicationRepository.java
└── service/
    ├── JobApiClient.java        # Calls RapidAPI via WebClient
    ├── JobService.java          # Fetch, save, and manage jobs
    └── ApplicationService.java  # CRUD for applications
```

---

## Running Tests

```bash
mvn test
```

Tests use an isolated H2 database and a stub API key — no real RapidAPI calls are made.

---

## Environment Variables Reference

| Variable | Required | Description |
|----------|----------|-------------|
| `RAPIDAPI_KEY` | Yes | Your RapidAPI key |
| `DB_HOST` | Prod only | PostgreSQL host |
| `DB_PORT` | Prod only | PostgreSQL port (default `5432`) |
| `DB_NAME` | Prod only | Database name |
| `DB_USER` | Prod only | Database username |
| `DB_PASSWORD` | Prod only | Database password |