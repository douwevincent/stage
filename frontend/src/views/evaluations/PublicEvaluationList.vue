<script setup lang="ts">
import { NCard, NButton, NDataTable, NAlert, NSpin, NTag, useMessage } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { computed, h, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { PublicEvaluationService, type PublicEvaluationStageItemDTO } from '@/api/PublicEvaluationService'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const code = computed(() => String(route.params.code ?? ''))
const loading = ref(false)
const stages = ref<PublicEvaluationStageItemDTO[]>([])
const errorMessage = ref('')
const completionMessage = ref('')

const columns: DataTableColumns<PublicEvaluationStageItemDTO> = [
  {
    title: 'Etudiant',
    key: 'etudiant',
    render (row) {
      return `${row.matricule ?? ''} ${row.etudiantNom ?? ''}`.trim() || '—'
    }
  },
  {
    title: 'Entreprise',
    key: 'entrepriseNom',
    render (row) {
      return row.entrepriseNom ?? '—'
    }
  },
  {
    title: 'Periode',
    key: 'periode',
    render (row) {
      return `${row.dateDebut ?? '—'} → ${row.dateFin ?? '—'}`
    }
  },
  {
    title: 'Date limite',
    key: 'dateLimite',
    render (row) {
      return row.dateLimite
        ? h(NTag, { type: 'warning', size: 'small' }, { default: () => row.dateLimite as string })
        : '—'
    }
  },
  {
    title: 'Note totale',
    key: 'noteTotale',
    render (row) {
      if (row.statut !== 'TERMINEE') {
        return '—'
      }
      return `${formatScore(row.totalScore)}/${formatScore(row.maxScore)}`
    }
  },
  {
    title: 'Action',
    key: 'action',
    render (row) {
      const isDone = row.statut === 'TERMINEE'
      return h(NButton, {
        type: 'primary',
        size: 'small',
        disabled: isDone,
        onClick: () => openEvaluation(row.stageId)
      }, { default: () => isDone ? 'Deja evalue' : 'Evaluer' })
    }
  }
]

function formatScore (value: number | null | undefined): string {
  const safe = Number.isFinite(Number(value)) ? Number(value) : 0
  return Number.isInteger(safe) ? String(safe) : safe.toFixed(2)
}

async function loadStages () {
  if (!code.value) {
    errorMessage.value = 'Lien d\'evaluation invalide'
    return
  }

  loading.value = true
  errorMessage.value = ''
  completionMessage.value = ''

  try {
    const response = await PublicEvaluationService.getStages(code.value)
    stages.value = response.data ?? []

    if (stages.value.length === 1 && stages.value[0].statut !== 'TERMINEE') {
      openEvaluation(stages.value[0].stageId)
      return
    }

    if (stages.value.length === 0) {
      completionMessage.value = 'Merci, vous avez termine l\'evaluation de vos etudiants.'
    }
  } catch (error: any) {
    const status = error?.response?.status
    if (status === 404) {
      errorMessage.value = 'Lien d\'evaluation introuvable'
    } else if (status === 410) {
      errorMessage.value = 'Lien d\'evaluation expire'
    } else {
      errorMessage.value = 'Impossible de charger les stages a evaluer'
    }
  } finally {
    loading.value = false
  }
}

function openEvaluation (stageId: number) {
  router.push({
    name: 'public-evaluation-form',
    params: {
      code: code.value,
      stageId
    }
  }).catch(() => {
    message.error('Navigation impossible vers le formulaire')
  })
}

onMounted(loadStages)
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-6">
    <div style="width: 100%; max-width: 980px">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white">Evaluation des stages</h1>
        <p class="text-gray-500 dark:text-gray-400 mt-2">
          Selectionnez le stage que vous souhaitez evaluer
        </p>
      </div>

      <NCard>
        <NSpin :show="loading">
          <NAlert v-if="errorMessage" type="error" class="mb-4">
            {{ errorMessage }}
          </NAlert>

          <NAlert v-else-if="completionMessage" type="success" class="mb-4">
            {{ completionMessage }}
          </NAlert>

          <NDataTable
            v-else
            :columns="columns"
            :data="stages"
            :pagination="false"
            :single-line="false"
            size="small"
          />
        </NSpin>
      </NCard>
    </div>
  </div>
</template>
