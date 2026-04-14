<script setup lang="ts">
import {
  NButton,
  NCard,
  NDataTable,
  NInput,
  NSelect,
  NSpace,
  NTag,
  useMessage
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { SearchOutlined } from '@vicons/antd'
import { Download, Eye, FileText } from 'lucide-vue-next'
import { computed, h, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { DepartementService } from '@/api/DepartementService'
import { NiveauService } from '@/api/NiveauService'
import { SpecialiteService } from '@/api/SpecialiteService'
import {
  EvaluationResultService,
  type EvaluationResultSummaryDTO,
  type SessionEvaluationStatut
} from '@/api/EvaluationResultService'

const message = useMessage()
const router = useRouter()
const loading = ref(false)
const data = ref<EvaluationResultSummaryDTO[]>([])
const downloadingSessionId = ref<number | null>(null)

const search = ref('')
const selectedDepartementId = ref<number | null>(null)
const selectedNiveauId = ref<number | null>(null)
const selectedSpecialiteId = ref<number | null>(null)

const departementOptions = ref<Array<{ label: string, value: number }>>([])
const niveauOptions = ref<Array<{ label: string, value: number }>>([])
const specialiteOptions = ref<Array<{ label: string, value: number }>>([])

const page = ref(1)
const pageSize = ref(20)
const itemCount = ref(0)

const columns: DataTableColumns<EvaluationResultSummaryDTO> = [
  {
    title: 'Matricule',
    key: 'matricule',
    width: 140,
    render (row) {
      return row.matricule ?? '—'
    }
  },
  {
    title: 'Nom etudiant',
    key: 'etudiantNom',
    minWidth: 220,
    render (row) {
      return row.etudiantNom ?? '—'
    }
  },
  {
    title: 'Departement',
    key: 'departement',
    minWidth: 180,
    render (row) {
      return row.departement ?? '—'
    }
  },
  {
    title: 'Niveau',
    key: 'niveau',
    width: 130,
    render (row) {
      return row.niveau ?? '—'
    }
  },
  {
    title: 'Specialite',
    key: 'specialite',
    minWidth: 200,
    render (row) {
      return row.specialite ?? '—'
    }
  },
  {
    title: 'Statut',
    key: 'statut',
    width: 130,
    render (row) {
      const typeByStatus: Record<SessionEvaluationStatut, 'default' | 'warning' | 'success'> = {
        EN_ATTENTE: 'default',
        EN_COURS: 'warning',
        TERMINEE: 'success'
      }
      return h(NTag, { size: 'small', type: typeByStatus[row.statut] }, { default: () => row.statut })
    }
  },
  {
    title: 'Note totale',
    key: 'noteTotale',
    width: 140,
    render (row) {
      return `${formatScore(row.totalScore)}/${formatScore(row.maxScore)}`
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 210,
    fixed: 'right',
    render (row) {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, {
            size: 'small',
            type: 'info',
            tertiary: true,
            onClick: () => openDetails(row.sessionId)
          }, {
            icon: () => h(Eye, { size: 16 }),
            default: () => 'Details'
          }),
          h(NButton, {
            size: 'small',
            type: 'primary',
            tertiary: true,
            loading: downloadingSessionId.value === row.sessionId,
            onClick: () => downloadPdf(row.sessionId)
          }, {
            icon: () => h(FileText, { size: 16 }),
            default: () => 'Fiche PDF'
          })
        ]
      })
    }
  }
]

const pagination = computed(() => ({
  page: page.value,
  pageSize: pageSize.value,
  itemCount: itemCount.value,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  onUpdatePage: (p: number) => {
    page.value = p
    fetchData()
  },
  onUpdatePageSize: (ps: number) => {
    pageSize.value = ps
    page.value = 1
    fetchData()
  }
}))

function formatScore (value: number | null | undefined): string {
  const safe = Number.isFinite(Number(value)) ? Number(value) : 0
  return Number.isInteger(safe) ? String(safe) : safe.toFixed(2)
}

function openDetails (sessionId: number) {
  router.push({ name: 'evaluation-result-detail', params: { sessionId } })
}

function goToExportPage () {
  router.push({ name: 'evaluation-export' })
}

async function downloadPdf (sessionId: number) {
  if (downloadingSessionId.value === sessionId) return
  downloadingSessionId.value = sessionId
  try {
    await EvaluationResultService.downloadSheet(sessionId)
  } catch {
    message.error('Impossible de generer la fiche PDF')
  } finally {
    downloadingSessionId.value = null
  }
}

async function fetchData () {
  loading.value = true
  try {
    const response = await EvaluationResultService.getAll({
      page: page.value - 1,
      size: pageSize.value,
      niveauId: selectedNiveauId.value,
      departementId: selectedDepartementId.value,
      specialiteId: selectedSpecialiteId.value,
      q: search.value.trim() || undefined
    })
    data.value = response.data?.content ?? []
    itemCount.value = response.data?.totalElements ?? 0
  } catch {
    message.error('Impossible de charger les resultats des evaluations')
  } finally {
    loading.value = false
  }
}

async function loadFilterOptions () {
  try {
    const [departements, niveaux] = await Promise.all([
      DepartementService.getAll(0, 200),
      NiveauService.getAll(0, 200)
    ])
    departementOptions.value = (departements.data?.content ?? []).map((item: any) => ({
      label: item.intitule,
      value: item.id
    }))
    niveauOptions.value = (niveaux.data?.content ?? []).map((item: any) => ({
      label: item.libelle,
      value: item.id
    }))
  } catch {
    message.error('Impossible de charger les options de filtre')
  }
}

async function loadSpecialites () {
  try {
    const response = await SpecialiteService.getAll(0, 200, selectedDepartementId.value)
    specialiteOptions.value = (response.data?.content ?? []).map((item: any) => ({
      label: item.intitule,
      value: item.id
    }))
  } catch {
    specialiteOptions.value = []
  }
}

function applyFilters () {
  page.value = 1
  fetchData()
}

watch(selectedDepartementId, async () => {
  selectedSpecialiteId.value = null
  await loadSpecialites()
  applyFilters()
})

watch([selectedNiveauId, selectedSpecialiteId], () => {
  applyFilters()
})

onMounted(async () => {
  await loadFilterOptions()
  await loadSpecialites()
  await fetchData()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Resultats des evaluations</h1>
      <NButton type="primary" secondary @click="goToExportPage">
        <template #icon>
          <Download :size="16" />
        </template>
        Aller a l export
      </NButton>
    </div>

    <NCard>
      <div class="mb-4">
        <NSpace wrap>
          <NSelect
            v-model:value="selectedDepartementId"
            :options="departementOptions"
            clearable
            placeholder="Filtrer par departement"
            style="width: 240px"
          />
          <NSelect
            v-model:value="selectedNiveauId"
            :options="niveauOptions"
            clearable
            placeholder="Filtrer par niveau"
            style="width: 220px"
          />
          <NSelect
            v-model:value="selectedSpecialiteId"
            :options="specialiteOptions"
            clearable
            placeholder="Filtrer par specialite"
            style="width: 260px"
          />
          <NInput
            v-model:value="search"
            placeholder="Rechercher par nom ou matricule"
            clearable
            style="width: 320px"
            @keyup.enter="applyFilters"
          >
            <template #prefix>
              <SearchOutlined />
            </template>
          </NInput>
          <NButton type="primary" @click="applyFilters">Rechercher</NButton>
        </NSpace>
      </div>

      <NDataTable
        remote
        :columns="columns"
        :data="data"
        :loading="loading"
        :pagination="pagination"
        :bordered="false"
        :scroll-x="1450"
      />
    </NCard>
  </div>
</template>
