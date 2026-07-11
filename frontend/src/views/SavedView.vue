<template>
  <div class="view">
    <div class="view-header">
      <div>
        <h2 class="view-title">Saved jobs</h2>
        <p class="view-sub">{{ jobs.length }} job{{ jobs.length !== 1 ? 's' : '' }} saved</p>
      </div>
      <button class="btn-refresh" :disabled="loading" @click="load">
        {{ loading ? '…' : '↺ Refresh' }}
      </button>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="skeleton-list">
      <div v-for="n in 3" :key="n" class="skeleton" />
    </div>

    <!-- Empty -->
    <div v-else-if="jobs.length === 0" class="empty-state">
      <div class="empty-icon">♡</div>
      <p>No saved jobs yet.<br/>Use the Search tab to find and save positions.</p>
    </div>

    <!-- List -->
    <div v-else class="job-list">
      <JobCard
        v-for="job in jobs"
        :key="job.id"
        :job="job"
        :loading-save="savingId === job.id"
        @toggle-save="unsave"
        @apply="openApply"
      />
    </div>
  </div>

  <ApplyModal
    v-model="modalOpen"
    :job="selectedJob"
    @applied="onApplied"
  />
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { jobsApi } from '../api.js'
import JobCard    from '../components/JobCard.vue'
import ApplyModal from '../components/ApplyModal.vue'

const showToast = inject('toast')

const jobs     = ref([])
const loading  = ref(false)
const savingId = ref(null)

const modalOpen   = ref(false)
const selectedJob = ref(null)

async function load() {
  loading.value = true
  try {
    jobs.value = await jobsApi.saved()
  } catch (e) {
    showToast(e.message, 'error')
  } finally {
    loading.value = false
  }
}

async function unsave(job) {
  savingId.value = job.id
  try {
    await jobsApi.unsave(job.id)
    jobs.value = jobs.value.filter(j => j.id !== job.id)
    showToast('Job removed from saved')
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

onMounted(load)
</script>

<style scoped>
.view {
  padding: 28px 32px;
  max-width: 820px;
}

.view-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
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

.job-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* Loading skeletons */
.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton {
  height: 120px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  animation: pulse 1.4s ease infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.4; }
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
  color: var(--muted);
}

.empty-icon {
  font-size: 32px;
  margin-bottom: 14px;
  opacity: 0.35;
}

.empty-state p { font-size: 14px; line-height: 1.7; }
</style>
