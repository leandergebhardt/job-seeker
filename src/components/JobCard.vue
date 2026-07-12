<template>
  <article class="card" :class="{ saved: job.saved }">
    <div class="card-accent" />

    <div class="card-body">
      <div class="card-header">
        <div class="card-meta">
          <h3 class="card-title">{{ job.title }}</h3>
          <div class="card-sub">
            <span>{{ job.company }}</span>
            <span v-if="job.location" class="dot">·</span>
            <span v-if="job.location">{{ job.location }}</span>
          </div>
        </div>
        <time class="card-date">{{ formatDate(job.datePosted) }}</time>
      </div>

      <p v-if="job.description" class="card-desc">{{ job.description }}</p>

      <div class="card-footer">
        <a v-if="job.url" :href="job.url" target="_blank" rel="noopener" class="btn-link">
          View posting ↗
        </a>
        <div class="card-actions">
          <button
            class="btn btn-ghost"
            :class="{ 'btn-saved': job.saved }"
            :disabled="loadingSave"
            @click.stop="$emit('toggle-save', job)"
          >
            {{ job.saved ? 'Saved' : 'Save' }}
          </button>
          <button
            class="btn btn-primary"
            @click.stop="$emit('apply', job)"
          >
            Apply
          </button>
        </div>
      </div>
    </div>
  </article>
</template>

<script setup>
defineProps({ job: Object, loadingSave: Boolean })
defineEmits(['toggle-save', 'apply'])

function formatDate(d) {
  if (!d) return ''
  try {
    return new Date(d).toLocaleDateString('en-GB', {
      day: 'numeric', month: 'short', year: 'numeric',
    })
  } catch { return d }
}
</script>

<style scoped>
.card {
  position: relative;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  transition: background var(--ease), border-color var(--ease), transform var(--ease);
  overflow: hidden;
}

.card:hover {
  background: var(--raised);
  border-color: var(--border-focus);
  transform: translateY(-1px);
}

.card:hover .card-accent,
.card.saved .card-accent {
  opacity: 1;
}

.card-accent {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--accent);
  opacity: 0;
  transition: opacity var(--ease);
}

.card.saved .card-accent {
  background: var(--green);
}

.card-body {
  padding: 18px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 10px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--text);
  line-height: 1.4;
}

.card-sub {
  display: flex;
  gap: 5px;
  font-size: 13px;
  color: var(--muted);
  margin-top: 3px;
}

.dot { color: var(--faint); }

.card-date {
  font-size: 12px;
  color: var(--muted);
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
  flex-shrink: 0;
  padding-top: 2px;
  font-family: ui-monospace, 'SF Mono', monospace;
}

.card-desc {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.65;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 14px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.btn-link {
  font-size: 12px;
  color: var(--muted);
  text-decoration: none;
  transition: color var(--ease);
}

.btn-link:hover { color: var(--accent); }

.card-actions {
  display: flex;
  gap: 6px;
  margin-left: auto;
}

.btn {
  padding: 5px 13px;
  font-size: 13px;
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.btn-ghost {
  background: var(--faint);
  color: var(--muted);
}

.btn-ghost:hover {
  background: var(--border-focus);
  color: var(--text);
}

.btn-ghost.btn-saved {
  background: var(--green-bg);
  color: var(--green);
}

.btn-primary {
  background: var(--accent-bg);
  color: var(--accent);
  border: 1px solid var(--accent-bd);
}

.btn-primary:hover {
  background: rgba(91, 127, 255, 0.14);
}
</style>
