<template>
  <div class="view">
    <!-- Search bar -->
    <div class="search-bar">
      <div class="search-row">
        <div class="field-wrap">
          <span class="field-icon">⌕</span>
          <input
            v-model="params.title"
            class="search-input"
            type="text"
            placeholder="Job title, e.g. Data Engineer"
            @keydown.enter="search"
          />
        </div>
        <div class="field-wrap field-wrap--sm">
          <span class="field-icon">◎</span>
          <input
            v-model="params.location"
            class="search-input"
            type="text"
            placeholder="Location"
            @keydown.enter="search"
          />
        </div>
        <div class="field-wrap field-wrap--xs">
          <select v-model.number="params.limit" class="search-select">
            <option :value="10">10</option>
            <option :value="25">25</option>
            <option :value="50">50</option>
          </select>
        </div>
        <button class="btn-search" :disabled="loading" @click="search">
          {{ loading ? 'Searching…' : 'Search' }}
        </button>
      </div>

      <p v-if="error" class="error-msg">{{ error }}</p>
    </div>

    <!-- Results -->
    <div class="results">
      <!-- Empty / idle state -->
      <div v-if="!searched" class="empty-state">
        <div class="empty-icon">⌕</div>
        <p>Search for jobs to get started.<br/>Defaulting to <strong>Germany</strong> — adjust location as needed.</p>
      </div>

      <div v-else-if="results.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">∅</div>
        <p>No results found. Try broadening your search.</p>
      </div>

      <div v-else class="result-list">
        <div v-if="results.length" class="result-count">
          {{ results.length }} result{{ results.length !== 1 ? 's' : '' }}
        </div>
        <JobCard
          v-for="job in results"
          :key="job.id"
          :job="job"
          :loading-save="savingId === job.id"
          @toggle-save="toggleSave"
          @apply="openApply"
        />
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
import { ref, inject } from 'vue'
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

const modalOpen  = ref(false)
const selectedJob = ref(null)

async function search() {
  loading.value  = true
  error.value    = ''
  searched.value = true
  try {
    results.value = await jobsApi.search(params.value)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
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
</style>
