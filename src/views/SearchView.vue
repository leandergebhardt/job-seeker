<template>
  <div class="view">
    <!-- Search bar -->
    <div class="search-bar">
      <div class="search-row">
        <div class="field-wrap">
          <span class="field-icon" aria-hidden="true">⌕</span>
          <input
            v-model="params.title"
            class="search-input"
            type="text"
            placeholder="Job title, e.g. Data Engineer"
            aria-label="Job title"
            @keydown.enter="search()"
          />
        </div>
        <div class="field-wrap field-wrap--sm">
          <span class="field-icon" aria-hidden="true">◎</span>
          <input
            v-model="params.location"
            class="search-input"
            type="text"
            placeholder="Location"
            aria-label="Location"
            @keydown.enter="search()"
          />
        </div>
        <div class="field-wrap field-wrap--xs">
          <select v-model.number="params.limit" class="search-select" aria-label="Results per page">
            <option :value="10">10</option>
            <option :value="25">25</option>
            <option :value="50">50</option>
          </select>
        </div>
        <button class="btn-search" :disabled="loading" @click="search()">
          {{ loading ? 'Searching…' : 'Search' }}
        </button>
      </div>

      <p v-if="error" class="error-msg" role="alert">{{ error }}</p>
    </div>

    <!-- Results -->
    <div class="results" :aria-busy="loading">
      <!-- Empty / idle state -->
      <div v-if="!searched" class="empty-state">
        <div class="empty-icon" aria-hidden="true">⌕</div>
        <p>Search for jobs to get started.<br/>Defaulting to <strong>Germany</strong> — adjust location as needed.</p>
      </div>

      <div v-else-if="loading && results.length === 0" class="empty-state" role="status">
        <p>Searching for jobs…</p>
      </div>

      <div v-else-if="results.length === 0 && !loading" class="empty-state">
        <div class="empty-icon" aria-hidden="true">∅</div>
        <p>No results found. Try broadening your search.</p>
      </div>

      <div v-else class="result-list">
        <div v-if="results.length" class="result-count">
          Results {{ params.offset + 1 }}–{{ params.offset + results.length }}
        </div>
        <JobCard
          v-for="job in results"
          :key="job.id"
          :job="job"
          :loading-save="savingId === job.id"
          @toggle-save="toggleSave"
          @apply="openApply"
        />
        <nav class="pagination" aria-label="Search result pages">
          <button
            class="page-btn"
            :disabled="loading || paginationStale || params.offset === 0"
            aria-label="Previous results page"
            @click="search(params.offset - params.limit)"
          >
            Previous
          </button>
          <span class="page-indicator">Page {{ Math.floor(params.offset / params.limit) + 1 }}</span>
          <button
            class="page-btn"
            :disabled="loading || paginationStale || !hasMore"
            aria-label="Next results page"
            @click="search(params.offset + params.limit)"
          >
            Next
          </button>
        </nav>
      </div>
    </div>
  </div>

  <ApplyModal
    v-model="modalOpen"
    :job="selectedJob"
    @applied="onApplied"
  />
</template>

<script setup>
import { ref, computed, inject, onDeactivated } from 'vue'
import { jobsApi } from '../api.js'
import JobCard    from '../components/JobCard.vue'
import ApplyModal from '../components/ApplyModal.vue'

const showToast = inject('toast')

const params = ref({ title: '', location: 'Germany', limit: 10, offset: 0 })
const results  = ref([])
const loading  = ref(false)
const savingId = ref(null)
const searched = ref(false)
const error    = ref('')
const hasMore  = ref(false)
const lastSearchKey = ref('')
const paginationStale = computed(() =>
  lastSearchKey.value !== JSON.stringify([params.value.title, params.value.location, params.value.limit])
)

const modalOpen  = ref(false)
const selectedJob = ref(null)
let searchController = null
let latestSearchId = 0

async function search(offset = 0) {
  const requestId = ++latestSearchId
  const queryKey = JSON.stringify([params.value.title, params.value.location, params.value.limit])
  const limit = params.value.limit
  searchController?.abort()
  searchController = new AbortController()
  loading.value  = true
  error.value    = ''
  searched.value = true
  if (offset === 0) {
    params.value.offset = 0
    results.value = []
    hasMore.value = false
  }

  try {
    const page = await jobsApi.search({ ...params.value, offset }, { signal: searchController.signal })
    if (requestId !== latestSearchId) return

    if (offset > 0 && page.length === 0) {
      hasMore.value = false
      error.value = 'No further results found.'
      return
    }

    params.value.offset = offset
    results.value = page
    hasMore.value = page.length === limit
    lastSearchKey.value = queryKey
  } catch (e) {
    if (e.name !== 'AbortError' && requestId === latestSearchId) {
      error.value = e.message
    }
  } finally {
    if (requestId === latestSearchId) loading.value = false
  }
}

async function toggleSave(job) {
  savingId.value = job.id
  try {
    const updated = job.saved
      ? await jobsApi.unsave(job.id)
      : await jobsApi.save(job.id)

    const idx = results.value.findIndex(j => j.id === job.id)
    if (idx !== -1) results.value[idx] = updated
    showToast(updated.saved ? 'Job saved' : 'Job unsaved')
  } catch (e) {
    showToast(e.message, 'error')
  } finally {
    savingId.value = null
  }
}

function openApply(job) {
  selectedJob.value = job
  modalOpen.value   = true
}

function onApplied() {
  showToast('Application recorded')
}

onDeactivated(() => {
  if (modalOpen.value) modalOpen.value = false
})
</script>

<style scoped>
.view {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* Search bar */
.search-bar {
  padding: 20px 32px;
  border-bottom: 1px solid var(--border);
  background: var(--surface);
  flex-shrink: 0;
}

.search-row {
  display: flex;
  gap: 8px;
  align-items: stretch;
  min-width: 0;
}

.field-wrap {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
}

.field-wrap--sm { flex: 0 0 180px; }
.field-wrap--xs { flex: 0 0 72px; }

.field-icon {
  position: absolute;
  left: 10px;
  color: var(--muted);
  font-size: 15px;
  pointer-events: none;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 9px 12px 9px 32px;
  height: 38px;
}

.search-select {
  width: 100%;
  padding: 9px 10px;
  height: 38px;
  cursor: pointer;
}

.btn-search {
  padding: 0 22px;
  height: 38px;
  background: var(--accent);
  color: #fff;
  font-weight: 500;
  font-size: 13px;
  border-radius: var(--radius-sm);
  white-space: nowrap;
  flex-shrink: 0;
}

.btn-search:hover:not(:disabled) { background: #6b8dff; }

.error-msg {
  margin-top: 10px;
  font-size: 13px;
  color: var(--red);
}

/* Results */
.results {
  flex: 1;
  overflow-y: auto;
  padding: 28px 32px;
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
  color: var(--muted);
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 14px;
  opacity: 0.4;
}

.empty-state p {
  font-size: 14px;
  line-height: 1.7;
}

.empty-state strong { color: var(--text); font-weight: 500; }

.result-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 820px;
}

.result-count {
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 6px;
  font-variant-numeric: tabular-nums;
  font-family: ui-monospace, monospace;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-top: 12px;
}

.page-btn {
  padding: 6px 12px;
  color: var(--text);
  background: var(--raised);
}

.page-btn:disabled {
  color: var(--muted);
}

.page-indicator {
  color: var(--muted);
  font-size: 12px;
}

@media (max-width: 700px) {
  .search-bar,
  .results {
    padding: 16px;
  }

  .search-row {
    flex-wrap: wrap;
  }

  .field-wrap {
    flex: 1 1 220px;
  }

  .field-wrap--sm {
    flex: 1 1 160px;
  }

  .field-wrap--xs {
    flex: 0 0 90px;
  }

  .btn-search {
    flex: 1 0 100%;
  }

  .empty-state {
    padding: 48px 16px;
  }
}

@media (max-width: 420px) {
  .field-wrap,
  .field-wrap--sm {
    flex-basis: 100%;
  }

  .field-wrap--xs {
    flex-basis: 100%;
  }
}
</style>
