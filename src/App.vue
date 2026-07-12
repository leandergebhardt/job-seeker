<template>
  <div class="app">
    <header class="header">
      <span class="logo">
        <span class="logo-dot">◆</span> job.seeker
      </span>
      <nav class="nav">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          class="nav-btn"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.label }}
        </button>
      </nav>
    </header>

    <main class="main">
      <SearchView     v-if="activeTab === 'search'" />
      <SavedView      v-if="activeTab === 'saved'" />
      <ApplicationsView v-if="activeTab === 'applications'" />
    </main>

    <Transition name="toast">
      <div v-if="toast" class="toast" :class="toast.type">
        {{ toast.message }}
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import SearchView      from './views/SearchView.vue'
import SavedView       from './views/SavedView.vue'
import ApplicationsView from './views/ApplicationsView.vue'

const tabs = [
  { id: 'search',       label: 'Search' },
  { id: 'saved',        label: 'Saved' },
  { id: 'applications', label: 'Applications' },
]

const activeTab = ref('search')

const toast = ref(null)
let toastTimer = null

function showToast(message, type = 'success') {
  clearTimeout(toastTimer)
  toast.value = { message, type }
  toastTimer = setTimeout(() => { toast.value = null }, 2800)
}

provide('toast', showToast)
</script>

<style scoped>
.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 52px;
  border-bottom: 1px solid var(--border);
  background: var(--surface);
  flex-shrink: 0;
}

.logo {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: -0.01em;
  color: var(--text);
  user-select: none;
}

.logo-dot {
  color: var(--accent);
  margin-right: 1px;
}

.nav {
  display: flex;
  gap: 2px;
}

.nav-btn {
  padding: 5px 14px;
  border-radius: var(--radius-sm);
  color: var(--muted);
  background: transparent;
  font-size: 13px;
  font-weight: 400;
}

.nav-btn:hover {
  color: var(--text);
  background: var(--raised);
}

.nav-btn.active {
  color: var(--text);
  background: var(--raised);
}

.main {
  flex: 1;
  overflow-y: auto;
}

/* Toast */
.toast {
  position: fixed;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  padding: 9px 18px;
  border-radius: var(--radius);
  font-size: 13px;
  font-weight: 500;
  z-index: 9999;
  pointer-events: none;
  white-space: nowrap;
}

.toast.success {
  background: var(--green-bg);
  color: var(--green);
  border: 1px solid rgba(52, 211, 153, 0.2);
}

.toast.error {
  background: var(--red-bg);
  color: var(--red);
  border: 1px solid rgba(248, 113, 113, 0.2);
}
</style>
