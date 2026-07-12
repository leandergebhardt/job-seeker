<template>
  <div class="view">
    <div class="view-header">
      <div>
        <h2 class="view-title">Applications</h2>
        <p class="view-sub">{{ filtered.length }} of {{ apps.length }} total</p>
      </div>
      <button class="btn-refresh" :disabled="loading" @click="load">
        {{ loading ? '…' : '↺ Refresh' }}
      </button>
    </div>

    <!-- Status filter tabs -->
    <div class="filter-row">
      <button
        v-for="f in FILTERS"
        :key="f.value"
        class="filter-btn"
        :class="{ active: activeFilter === f.value }"
        @click="activeFilter = f.value"
      >
        {{ f.label }}
        <span class="filter-count">{{ counts[f.value] ?? apps.length }}</span>
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="skeleton-list">
      <div v-for="n in 4" :key="n" class="skeleton" />
    </div>

    <!-- Empty -->
    <div v-else-if="filtered.length === 0" class="empty-state">
      <div class="empty-icon">◻</div>
      <p v-if="apps.length === 0">No applications yet.<br/>Find a job and click Apply to start tracking.</p>
      <p v-else>No applications with status <strong>{{ activeFilter }}</strong>.</p>
    </div>

    <!-- Table -->
    <div v-else class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th>Position</th>
            <th>Company</th>
            <th>Status</th>
            <th>Applied</th>
            <th>Notes</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="app in filtered"
            :key="app.id"
            class="table-row"
          >
            <td class="td-title">{{ app.jobTitle }}</td>
            <td class="td-company">{{ app.company }}</td>
            <td class="td-status">
              <select
                class="status-select"
                :style="statusStyle(app.status)"
                :value="app.status"
                @change="updateStatus(app, $event.target.value)"
              >
                <option v-for="s in STATUSES" :key="s" :value="s">{{ s }}</option>
              </select>
            </td>
            <td class="td-date">{{ formatDate(app.appliedAt) }}</td>
            <td class="td-notes">{{ app.notes || '—' }}</td>
            <td class="td-actions">
              <button class="btn-delete" title="Delete" @click="remove(app)">✕</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { appsApi } from '../api.js'

const showToast = inject('toast')

const STATUSES = ['APPLIED', 'INTERVIEWING', 'OFFER', 'REJECTED', 'WITHDRAWN']

const FILTERS = [
  { label: 'All',         value: 'ALL' },
  { label: 'Applied',     value: 'APPLIED' },
  { label: 'Interviewing',value: 'INTERVIEWING' },
  { label: 'Offer',       value: 'OFFER' },
  { label: 'Rejected',    value: 'REJECTED' },
  { label: 'Withdrawn',   value: 'WITHDRAWN' },
]

const STATUS_STYLE = {
  APPLIED:      { color: 'var(--accent)',  background: 'var(--accent-bg)' },
  INTERVIEWING: { color: 'var(--amber)',   background: 'var(--amber-bg)' },
  OFFER:        { color: 'var(--green)',   background: 'var(--green-bg)' },
  REJECTED:     { color: 'var(--red)',     background: 'var(--red-bg)' },
  WITHDRAWN:    { color: 'var(--muted)',   background: 'var(--faint)' },
}

const apps         = ref([])
const loading      = ref(false)
const activeFilter = ref('ALL')

const filtered = computed(() =>
  activeFilter.value === 'ALL'
    ? apps.value
    : apps.value.filter(a => a.status === activeFilter.value)
)

const counts = computed(() => {
  const c = {}
  for (const s of STATUSES) c[s] = apps.value.filter(a => a.status === s).length
  return c
})

function statusStyle(s) { return STATUS_STYLE[s] ?? {} }

function formatDate(d) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' })
}

async function load() {
  loading.value = true
  try {
    apps.value = await appsApi.list()
  } catch (e) {
    showToast(e.message, 'error')
  } finally {
    loading.value = false
  }
}

async function updateStatus(app, status) {
  try {
    const updated = await appsApi.update(app.id, { status })
    const idx = apps.value.findIndex(a => a.id === app.id)
    if (idx !== -1) apps.value[idx] = updated
    showToast('Status updated')
  } catch (e) {
    showToast(e.message, 'error')
  }
}

async function remove(app) {
  try {
    await appsApi.remove(app.id)
    apps.value = apps.value.filter(a => a.id !== app.id)
    showToast('Application deleted')
  } catch (e) {
    showToast(e.message, 'error')
  }
}

onMounted(load)
</script>

<style scoped>
.view {
  padding: 28px 32px;
}

.view-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}

.view-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
}

.view-sub {
  font-size: 13px;
  color: var(--muted);
  margin-top: 3px;
  font-family: ui-monospace, monospace;
}

.btn-refresh {
  padding: 6px 12px;
  background: var(--faint);
  color: var(--muted);
  font-size: 13px;
  border-radius: var(--radius-sm);
}
.btn-refresh:hover { color: var(--text); }

/* Filter row */
.filter-row {
  display: flex;
  gap: 4px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  background: transparent;
  color: var(--muted);
  border-radius: var(--radius-sm);
  font-size: 13px;
  transition: background var(--ease), color var(--ease);
}

.filter-btn:hover { color: var(--text); background: var(--faint); }

.filter-btn.active {
  color: var(--text);
  background: var(--raised);
  border: 1px solid var(--border-focus);
}

.filter-count {
  font-size: 11px;
  color: var(--muted);
  background: var(--faint);
  padding: 1px 6px;
  border-radius: 10px;
  font-family: ui-monospace, monospace;
}

/* Table */
.table-wrap {
  overflow-x: auto;
  border: 1px solid var(--border);
  border-radius: var(--radius);
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

thead tr {
  border-bottom: 1px solid var(--border);
}

th {
  padding: 11px 14px;
  text-align: left;
  font-size: 11px;
  font-weight: 500;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  background: var(--surface);
  white-space: nowrap;
}

.table-row {
  border-bottom: 1px solid var(--border);
  transition: background var(--ease);
}

.table-row:last-child { border-bottom: none; }
.table-row:hover { background: var(--raised); }

td { padding: 12px 14px; vertical-align: middle; }

.td-title   { color: var(--text); font-weight: 500; min-width: 180px; }
.td-company { color: var(--muted); white-space: nowrap; }
.td-date    { color: var(--muted); white-space: nowrap; font-family: ui-monospace, monospace; font-size: 12px; }
.td-notes   { color: var(--muted); max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.status-select {
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 20px;
  border: none;
  cursor: pointer;
}

.td-actions { text-align: right; }

.btn-delete {
  background: transparent;
  color: var(--faint);
  font-size: 12px;
  padding: 4px 7px;
  border-radius: var(--radius-sm);
}

.btn-delete:hover { color: var(--red); background: var(--red-bg); }

/* Skeleton */
.skeleton-list { display: flex; flex-direction: column; gap: 8px; }

.skeleton {
  height: 44px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  animation: pulse 1.4s ease infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.4; }
}

/* Empty */
.empty-state {
  text-align: center;
  padding: 80px 24px;
  color: var(--muted);
}

.empty-icon {
  font-size: 28px;
  margin-bottom: 14px;
  opacity: 0.35;
}

.empty-state p { font-size: 14px; line-height: 1.7; }
.empty-state strong { color: var(--text); font-weight: 500; }
</style>
