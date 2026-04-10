<script setup lang="ts">
import {
  NButton,
  NCard,
  NDataTable,
  NDescriptions,
  NDescriptionsItem,
  NSpace,
  NTag,
  useMessage
} from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  EvaluationResultService,
  type EvaluationCriterionDetailDTO,
  type EvaluationResultDetailDTO,
  type SessionEvaluationStatut
} from '@/api/EvaluationResultService'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const loading = ref(false)
const detail = ref<EvaluationResultDetailDTO | null>(null)
const downloading = ref(false)

const sessionId = computed(() => Number(route.params.sessionId))

const criterionColumns: DataTableColumns<EvaluationCriterionDetailDTO> = [
  {
    title: 'Critere',
    key: 'critere',
    minWidth: 260,
    render (row) {
      return row.critere ?? '—'
    }
  },
  {
    title: 'Coefficient',
    key: 'coefficient',
    width: 120,
    render (row) {
      return formatScore(row.coefficient)
    }
  },
  {
    title: 'Note',
    key: 'note',
    width: 100,
    render (row) {
      return row.note ?? '—'
    }
  },
  {
    title: 'Commentaire',
    key: 'commentaire',
    minWidth: 280,
    render (row) {
      return row.commentaire ?? '—'
    }
  }
]

function formatScore (value: number | null | undefined): string {
  const safe = Number.isFinite(Number(value)) ? Number(value) : 0
  return Number.isInteger(safe) ? String(safe) : safe.toFixed(2)
}

function statusTagType (status: SessionEvaluationStatut | undefined): 'default' | 'warning' | 'success' {
  if (status === 'TERMINEE') return 'success'
  if (status === 'EN_COURS') return 'warning'
  return 'default'
}

async function fetchDetail () {
  if (!Number.isFinite(sessionId.value)) {
    message.error('Session invalide')
    router.push({ name: 'evaluation-results' })
    return
  }

  loading.value = true
  try {
    const response = await EvaluationResultService.getDetails(sessionId.value)
    detail.value = response.data
  } catch {
    message.error('Impossible de charger les details de l\'evaluation')
    router.push({ name: 'evaluation-results' })
  } finally {
    loading.value = false
  }
}

async function handleDownloadPdf () {
  if (!Number.isFinite(sessionId.value) || downloading.value) return
  downloading.value = true
  try {
    await EvaluationResultService.downloadSheet(sessionId.value)
  } catch {
    message.error('Impossible de generer la fiche PDF')
  } finally {
    downloading.value = false
  }
}

onMounted(fetchDetail)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Details de l'evaluation</h1>
      <NSpace>
        <NButton @click="router.push({ name: 'evaluation-results' })">Retour</NButton>
        <NButton type="primary" :loading="downloading" @click="handleDownloadPdf">
          Generer fiche PDF imprimable
        </NButton>
      </NSpace>
    </div>

    <NCard :loading="loading">
      <NDescriptions v-if="detail" label-placement="left" :column="2" bordered>
        <NDescriptionsItem label="Matricule">{{ detail.matricule || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Etudiant">{{ detail.etudiantNom || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Email">{{ detail.email || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Telephone">{{ detail.telephone || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Annee academique">{{ detail.anneeAcademique || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Departement">{{ detail.departement || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Niveau">{{ detail.niveau || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Specialite">{{ detail.specialite || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Entreprise">{{ detail.entrepriseNom || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Encadreur">{{ detail.encadreurNom || '—' }}</NDescriptionsItem>
        <NDescriptionsItem label="Periode">{{ `${detail.dateDebut || '—'} → ${detail.dateFin || '—'}` }}</NDescriptionsItem>
        <NDescriptionsItem label="Statut">
          <NTag size="small" :type="statusTagType(detail.statut)">{{ detail.statut }}</NTag>
        </NDescriptionsItem>
        <NDescriptionsItem label="Note totale">{{ `${formatScore(detail.totalScore)}/${formatScore(detail.maxScore)}` }}</NDescriptionsItem>
      </NDescriptions>
    </NCard>

    <div v-if="detail" class="space-y-4">
      <NCard v-for="category in detail.categories" :key="category.categorie" :title="category.categorie">
        <NDataTable
          :columns="criterionColumns"
          :data="category.criteres"
          :pagination="false"
          :bordered="false"
          :single-line="false"
          :scroll-x="820"
        />
      </NCard>
    </div>
  </div>
</template>
