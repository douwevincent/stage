<script setup lang="ts">
import { computed, h, onBeforeUnmount, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  NButton,
  NCard,
  NDataTable,
  NIcon,
  NInput,
  NSpace,
  NTag,
  NTooltip,
  useMessage
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { SearchOutlined } from '@vicons/antd'
import { Eye, Loader2 } from 'lucide-vue-next'
import { EtudiantService, type EtudiantDTO } from '@/api/EtudiantService'

const message = useMessage()
const router = useRouter()

const query = ref('')
const loading = ref(false)
const data = ref<EtudiantDTO[]>([])
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)
let searchDebounceTimer: ReturnType<typeof setTimeout> | null = null

const columns: DataTableColumns<EtudiantDTO> = [
  { title: 'Matricule', key: 'matricule', minWidth: 150 },
  { title: 'Nom', key: 'nom', minWidth: 220 },
  { title: 'Email', key: 'email', minWidth: 220 },
  {
    title: 'Téléphone',
    key: 'telephone',
    minWidth: 150,
    render (row) {
      return row.telephone || '-'
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 110,
    fixed: 'right',
    render (row) {
      return h(NSpace, null, {
        default: () => [
          h(NTooltip, null, {
            trigger: () => h(NButton, {
              size: 'small',
              quaternary: true,
              type: 'info',
              circle: true,
              onClick: () => openDetail(row)
            }, {
              default: () => h(NIcon, null, { default: () => h(Eye) })
            }),
            default: () => 'Voir détail'
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

const fetchData = async () => {
  loading.value = true
  try {
    const trimmed = query.value.trim()
    const response = trimmed.length > 0
      ? await EtudiantService.search(trimmed, page.value - 1, pageSize.value)
      : await EtudiantService.getAll(page.value - 1, pageSize.value)

    data.value = response.data.content || []
    itemCount.value = response.data.totalElements || response.data.page?.totalElements || 0
  } catch {
    message.error('Erreur lors de la recherche des étudiants')
  } finally {
    loading.value = false
  }
}

const search = () => {
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer)
    searchDebounceTimer = null
  }
  page.value = 1
  fetchData()
}

const openDetail = (row: EtudiantDTO) => {
  if (!row.id) {
    message.warning('Étudiant introuvable')
    return
  }
  router.push({ name: 'etudiants-detail', params: { id: row.id } })
}

watch(query, () => {
  page.value = 1
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer)
  }
  searchDebounceTimer = setTimeout(() => {
    fetchData()
  }, 400)
})

onBeforeUnmount(() => {
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer)
  }
})

fetchData()
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Recherche d'un étudiant</h1>
      <n-tag type="info" size="small" :bordered="false">
        Recherche par matricule ou nom
      </n-tag>
    </div>

    <n-card>
      <div class="mb-4 flex flex-col gap-3 md:flex-row md:items-center">
        <div class="space-y-1">
          <n-input
            v-model:value="query"
            clearable
            placeholder="Saisir un matricule ou un nom"
            class="md:max-w-lg"
            @keyup.enter="search"
          >
            <template #prefix>
              <n-icon><SearchOutlined /></n-icon>
            </template>
            <template #suffix>
              <n-icon v-if="loading" class="animate-spin">
                <Loader2 />
              </n-icon>
            </template>
          </n-input>
          <p v-if="loading" class="text-xs text-slate-500">Recherche en cours...</p>
        </div>
        <n-button type="primary" @click="search">
          Rechercher
        </n-button>
      </div>

      <n-data-table
        remote
        :columns="columns"
        :data="data"
        :loading="loading"
        :bordered="false"
        :pagination="pagination"
        :scroll-x="900"
      />
    </n-card>
  </div>
</template>
