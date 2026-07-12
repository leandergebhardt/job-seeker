<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="overlay" @click.self="$emit('update:modelValue', false)">
        <div class="modal" role="dialog" aria-modal="true">

          <div class="modal-header">
            <div>
              <h2 class="modal-title">Record application</h2>
              <p class="modal-sub">{{ job?.title }} · {{ job?.company }}</p>
            </div>
            <button class="close-btn" @click="$emit('update:modelValue', false)">✕</button>
          </div>

          <div class="modal-body">
            <label class="field">
              <span class="label">Status</span>
              <select v-model="form.status">
                <option v-for="s in STATUSES" :key="s" :value="s">{{ s }}</option>
              </select>
            </label>

            <label class="field">
              <span class="label">Resume URL <span class="opt">optional</span></span>
              <input v-model="form.resumeUrl" type="url" placeholder="https://..." />
            </label>

            <label class="field">
              <span class="label">Notes <span class="opt">optional</span></span>
              <textarea v-model="form.notes" rows="3" placeholder="Contact name, referral, deadline..." />
            </label>

            <label class="field">
              <span class="label">Cover letter <span class="opt">optional</span></span>
              <textarea v-model="form.coverLetter" rows="5" placeholder="Dear Hiring Manager..." />
            </label>
          </div>

          <div class="modal-footer">
            <button class="btn-cancel" @click="$emit('update:modelValue', false)">Cancel</button>
            <button class="btn-submit" :disabled="loading" @click="submit">
              {{ loading ? 'Saving…' : 'Save application' }}
            </button>
          </div>

          <p v-if="error" class="modal-error">{{ error }}</p>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, watch } from 'vue'
import { appsApi } from '../api.js'

const props = defineProps({ modelValue: Boolean, job: Object })
const emit  = defineEmits(['update:modelValue', 'applied'])

const STATUSES = ['APPLIED', 'INTERVIEWING', 'OFFER', 'REJECTED', 'WITHDRAWN']

const blank = () => ({ status: 'APPLIED', resumeUrl: '', notes: '', coverLetter: '' })
const form  = ref(blank())
const loading = ref(false)
const error   = ref('')

// Reset form each time modal opens
watch(() => props.modelValue, (v) => { if (v) { form.value = blank(); error.value = '' } })

async function submit() {
  if (!props.job) return
  loading.value = true
  error.value   = ''
  try {
    const app = await appsApi.create({ jobId: props.job.id, ...form.value })
    emit('applied', app)
    emit('update:modelValue', false)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.65);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 24px;
}

.modal {
  background: var(--raised);
  border: 1px solid var(--border-focus);
  border-radius: 10px;
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 22px 24px 0;
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
}

.modal-sub {
  font-size: 13px;
  color: var(--muted);
  margin-top: 3px;
}

.close-btn {
  background: transparent;
  color: var(--muted);
  font-size: 14px;
  padding: 4px 6px;
  border-radius: var(--radius-sm);
}

.close-btn:hover { color: var(--text); background: var(--faint); }

.modal-body {
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.label {
  font-size: 12px;
  font-weight: 500;
  color: var(--muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.opt {
  font-weight: 400;
  text-transform: none;
  letter-spacing: 0;
  color: var(--faint);
  margin-left: 4px;
}

select, input, textarea {
  width: 100%;
  padding: 8px 10px;
}

textarea { resize: vertical; }

.modal-footer {
  padding: 0 24px 22px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.btn-cancel {
  padding: 7px 16px;
  background: var(--faint);
  color: var(--muted);
  font-weight: 500;
}

.btn-cancel:hover { color: var(--text); }

.btn-submit {
  padding: 7px 18px;
  background: var(--accent);
  color: #fff;
  font-weight: 500;
}

.btn-submit:hover { background: #6b8dff; }

.modal-error {
  padding: 0 24px 16px;
  font-size: 13px;
  color: var(--red);
}

/* transitions */
.modal-enter-active, .modal-leave-active { transition: all 200ms ease; }
.modal-enter-from, .modal-leave-to { opacity: 0; transform: scale(0.97) translateY(6px); }
</style>
