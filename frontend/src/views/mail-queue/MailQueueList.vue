<script setup lang="ts">
import {
  NAlert,
  NButton,
  NCard,
  NDataTable,
  NInputNumber,
  NPopconfirm,
  NSpace,
  NSelect,
  NTag,
  useMessage
} from 'naive-ui'
import type { DataTableColumns, SelectOption } from 'naive-ui'
import { computed, h, onMounted, ref } from 'vue'
import {
  MailQueueService,
  MailQueueStatut,
  type MailQueueDTO,
  type MailQueueStatut as MailQueueStatutType
} from '@/api/MailQueueService'

const message = useMessage()

const loading = ref(false)
const batchRetryLoading = ref(false)
const purgeLoading = ref(false)
const deletingId = ref<number | null>(null)
const retryingId = ref<number | null>(null)

const data = ref<MailQueueDTO[]>([])
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)

const filterStatut = ref<MailQueueStatutType | null>(null)
const purgeOlderThanDays = ref(30)

const statutOptions: SelectOption[] = [
  { label: 'En attente', value: MailQueueStatut.PENDING },
  { label: 'Envoyé', value: MailQueueStatut.SENT },
  { label: 'Échec', value: MailQueueStatut.FAILED }
]

const statutLabelMap = new Map<MailQueueStatutType, string>([
  [MailQueueStatut.PENDING, 'En attente'],
  [MailQueueStatut.SENT, 'Envoyé'],
  [MailQueueStatut.FAILED, 'Échec']
])

const formatDate = (value: string | null) => {
  if (!value) {
    return '-'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '-'
  }
  return date.toLocaleString('fr-FR')
}

const columns: DataTableColumns<MailQueueDTO> = [
  {
    title: 'Destinataire',
    key: 'destinataireEmail',
    minWidth: 200
  },
  {
    title: 'Sujet',
    key: 'sujet',
    minWidth: 260,
    ellipsis: { tooltip: true }
  },
  {
    title: 'Statut',
    key: 'statut',
    minWidth: 120,
    render: (row) => {
      const type = row.statut === MailQueueStatut.SENT
        ? 'success'
        : row.statut === MailQueueStatut.FAILED
          ? 'error'
          : 'warning'
      return h(NTag, { type }, { default: () => statutLabelMap.get(row.statut) ?? row.statut })
    }
  },
  {
    title: 'Date envoi',
    key: 'dateEnvoi',
    minWidth: 170,
    render: (row) => formatDate(row.dateEnvoi)
  },
  {
    title: 'Tentatives',
    key: 'nombreTentatives',
    minWidth: 110,
    align: 'center'
  },
  {
    title: 'Erreur',
    key: 'erreur',
    minWidth: 260,
    ellipsis: { tooltip: true },
    render: (row) => row.erreur || '-'
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 230,
    fixed: 'right',
    render: (row) => h(NSpace, { size: 'small' }, {
      default: () => [
        h(
          NButton,
          {
            size: 'small',
            type: 'warning',
            disabled: row.statut !== MailQueueStatut.FAILED,
            loading: retryingId.value === row.id,
            onClick: () => handleRetryOne(row.id)
          },
          { default: () => 'Relancer' }
        ),
        h(
          NPopconfirm,
          { onPositiveClick: () => handleDeleteOne(row.id) },
          {
            trigger: () => h(
              NButton,
              {
                size: 'small',
                type: 'error',
                loading: deletingId.value === row.id
              },
              { default: () => 'Supprimer' }
            ),
            default: () => 'Supprimer ce mail de l\'historique ?'
          }
        )
      ]
    })
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
    const res = await MailQueueService.getAll(page.value - 1, pageSize.value, filterStatut.value ?? undefined)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || 0
  } catch (error: any) {
    const errorMsg = error?.response?.data?.error || 'Erreur lors du chargement des mails'
    message.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  page.value = 1
  fetchData()
}

const handleRetryOne = async (id: number) => {
  retryingId.value = id
  try {
    await MailQueueService.retry(id)
    message.success('Mail relancé et remis en attente')
    fetchData()
  } catch (error: any) {
    const errorMsg = error?.response?.data?.error || 'Relance impossible'
    message.error(errorMsg)
  } finally {
    retryingId.value = null
  }
}

const handleRetryFailedBatch = async () => {
  batchRetryLoading.value = true
  try {
    const res = await MailQueueService.retryFailed(100)
    message.success(`${res.data.retried} mail(s) en échec relancé(s)`)
    fetchData()
  } catch (error: any) {
    const errorMsg = error?.response?.data?.error || 'Relance en lot impossible'
    message.error(errorMsg)
  } finally {
    batchRetryLoading.value = false
  }
}

const handleDeleteOne = async (id: number) => {
  deletingId.value = id
  try {
    await MailQueueService.delete(id)
    message.success('Mail supprimé')
    fetchData()
  } catch (error: any) {
    const errorMsg = error?.response?.data?.error || 'Suppression impossible'
    message.error(errorMsg)
  } finally {
    deletingId.value = null
  }
}

const handlePurgeOld = async () => {
  const days = Math.max(1, Math.min(3650, purgeOlderThanDays.value ?? 30))
  purgeLoading.value = true
  try {
    const [sentRes, failedRes] = await Promise.all([
      MailQueueService.cleanup(days, MailQueueStatut.SENT),
      MailQueueService.cleanup(days, MailQueueStatut.FAILED)
    ])

    const totalDeleted = (sentRes.data.deleted || 0) + (failedRes.data.deleted || 0)
    message.success(`${totalDeleted} mail(s) ancien(s) purgé(s)`)
    fetchData()
  } catch (error: any) {
    const errorMsg = error?.response?.data?.error || 'Purge impossible'
    message.error(errorMsg)
  } finally {
    purgeLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between gap-3 flex-wrap">
      <h1 class="text-2xl font-bold">Gestion des mails envoyés</h1>
      <n-space>
        <n-button type="warning" :loading="batchRetryLoading" @click="handleRetryFailedBatch">
          Relancer les échecs
        </n-button>
      </n-space>
    </div>

    <n-card>
      <div class="flex items-end gap-4 flex-wrap">
        <div class="min-w-[220px]">
          <p class="mb-1 text-sm text-gray-600">Filtrer par statut</p>
          <n-select
            v-model:value="filterStatut"
            :options="statutOptions"
            clearable
            placeholder="Choisir un statut"
            @update:value="handleFilterChange"
          />
        </div>

        <div class="min-w-[220px]">
          <p class="mb-1 text-sm text-gray-600">Purge: mails de plus de</p>
          <n-input-number v-model:value="purgeOlderThanDays" :min="1" :max="3650" class="w-full">
            <template #suffix>jours</template>
          </n-input-number>
        </div>

        <n-popconfirm @positive-click="handlePurgeOld">
          <template #trigger>
            <n-button type="error" :loading="purgeLoading">
              Purger anciens mails
            </n-button>
          </template>
          Cette action supprimera les mails SENT et FAILED plus anciens que le seuil choisi. Continuer ?
        </n-popconfirm>
      </div>

      <n-alert type="info" class="mt-4">
        La purge par défaut cible les statuts envoyés et en échec au-delà de 30 jours.
      </n-alert>

      <div class="mt-4">
        <n-data-table
          remote
          :columns="columns"
          :data="data"
          :loading="loading"
          :bordered="false"
          :pagination="pagination"
          :scroll-x="1300"
        />
      </div>
    </n-card>
  </div>
</template>
