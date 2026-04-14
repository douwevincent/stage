<script setup lang="ts">
import {
  NButton,
  NCard,
  NSelect,
  NSpace,
  useMessage
} from 'naive-ui'
import { onMounted, ref } from 'vue'
import { NiveauService } from '@/api/NiveauService'
import { ParcoursService } from '@/api/ParcoursService'
import { TypeStageService } from '@/api/TypeStageService'
import { EvaluationResultService } from '@/api/EvaluationResultService'

const message = useMessage()
const downloadingExportKey = ref<string | null>(null)

const niveauOptions = ref<Array<{ label: string, value: number }>>([])
const parcoursOptions = ref<Array<{ label: string, value: number }>>([])
const typeStageOptions = ref<Array<{ label: string, value: number }>>([])

const exportNiveauId = ref<number | null>(null)
const exportParcoursId = ref<number | null>(null)
const exportTypeStageId = ref<number | null>(null)

async function loadOptions () {
  try {
    const [niveaux, parcours, typeStages] = await Promise.all([
      NiveauService.getAll(0, 200),
      ParcoursService.getAll(0, 500),
      TypeStageService.getAll(0, 200)
    ])

    niveauOptions.value = (niveaux.data?.content ?? []).map((item: any) => ({
      label: item.libelle,
      value: item.id
    }))

    parcoursOptions.value = (parcours.data?.content ?? [])
      .map((item: any) => ({
        label: item.libelle ?? `${item.specialiteIntitule ?? 'Specialite'} - ${item.niveauLibelle ?? 'Niveau'}`,
        value: item.id
      }))
      .sort((a: any, b: any) => a.label.localeCompare(b.label))

    typeStageOptions.value = (typeStages.data?.content ?? []).map((item: any) => ({
      label: item.libelle,
      value: item.id
    }))
  } catch {
    message.error('Impossible de charger les options d export')
  }
}

async function exportByNiveau (format: 'pdf' | 'excel') {
  if (!exportNiveauId.value) {
    message.warning('Selectionnez un niveau')
    return
  }
  const key = `niveau-${format}`
  if (downloadingExportKey.value === key) return
  downloadingExportKey.value = key
  try {
    await EvaluationResultService.downloadExportByNiveau(exportNiveauId.value, format)
    message.success(`Export ${format.toUpperCase()} genere`)
  } catch {
    message.error(`Impossible d exporter en ${format.toUpperCase()}`)
  } finally {
    downloadingExportKey.value = null
  }
}

async function exportByParcours (format: 'pdf' | 'excel') {
  if (!exportParcoursId.value) {
    message.warning('Selectionnez un parcours')
    return
  }
  const key = `parcours-${format}`
  if (downloadingExportKey.value === key) return
  downloadingExportKey.value = key
  try {
    await EvaluationResultService.downloadExportByParcours(exportParcoursId.value, format)
    message.success(`Export ${format.toUpperCase()} genere`)
  } catch {
    message.error(`Impossible d exporter en ${format.toUpperCase()}`)
  } finally {
    downloadingExportKey.value = null
  }
}

async function exportByTypeStage (format: 'pdf' | 'excel') {
  if (!exportTypeStageId.value) {
    message.warning('Selectionnez un type de stage')
    return
  }
  const key = `type-stage-${format}`
  if (downloadingExportKey.value === key) return
  downloadingExportKey.value = key
  try {
    await EvaluationResultService.downloadExportByTypeStage(exportTypeStageId.value, format)
    message.success(`Export ${format.toUpperCase()} genere`)
  } catch {
    message.error(`Impossible d exporter en ${format.toUpperCase()}`)
  } finally {
    downloadingExportKey.value = null
  }
}

onMounted(loadOptions)
</script>

<template>
  <div class="space-y-4">
    <div>
      <h1 class="text-2xl font-bold">Export des evaluations</h1>
      <p class="text-sm text-slate-500">
        Telechargez les resultats en PDF ou Excel par niveau, parcours ou type de stage.
      </p>
    </div>

    <div class="grid grid-cols-1 gap-4 lg:grid-cols-3">
      <NCard>
        <p class="mb-2 text-sm font-semibold">Par Niveau</p>
        <NSpace vertical>
          <NSelect
            v-model:value="exportNiveauId"
            :options="niveauOptions"
            clearable
            placeholder="Selectionner un niveau"
          />
          <NSpace>
            <NButton
              type="primary"
              secondary
              :loading="downloadingExportKey === 'niveau-pdf'"
              @click="exportByNiveau('pdf')"
            >
              Export PDF
            </NButton>
            <NButton
              type="success"
              secondary
              :loading="downloadingExportKey === 'niveau-excel'"
              @click="exportByNiveau('excel')"
            >
              Export Excel
            </NButton>
          </NSpace>
        </NSpace>
      </NCard>

      <NCard>
        <p class="mb-2 text-sm font-semibold">Par Parcours</p>
        <NSpace vertical>
          <NSelect
            v-model:value="exportParcoursId"
            :options="parcoursOptions"
            filterable
            clearable
            placeholder="Selectionner un parcours"
          />
          <NSpace>
            <NButton
              type="primary"
              secondary
              :loading="downloadingExportKey === 'parcours-pdf'"
              @click="exportByParcours('pdf')"
            >
              Export PDF
            </NButton>
            <NButton
              type="success"
              secondary
              :loading="downloadingExportKey === 'parcours-excel'"
              @click="exportByParcours('excel')"
            >
              Export Excel
            </NButton>
          </NSpace>
        </NSpace>
      </NCard>

      <NCard>
        <p class="mb-2 text-sm font-semibold">Par Type de Stage</p>
        <NSpace vertical>
          <NSelect
            v-model:value="exportTypeStageId"
            :options="typeStageOptions"
            clearable
            placeholder="Selectionner un type de stage"
          />
          <NSpace>
            <NButton
              type="primary"
              secondary
              :loading="downloadingExportKey === 'type-stage-pdf'"
              @click="exportByTypeStage('pdf')"
            >
              Export PDF
            </NButton>
            <NButton
              type="success"
              secondary
              :loading="downloadingExportKey === 'type-stage-excel'"
              @click="exportByTypeStage('excel')"
            >
              Export Excel
            </NButton>
          </NSpace>
        </NSpace>
      </NCard>
    </div>
  </div>
</template>
