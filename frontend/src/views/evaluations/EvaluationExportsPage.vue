<script setup lang="ts">
import {
  NAlert,
  NButton,
  NCard,
  NRadio,
  NRadioGroup,
  NSelect,
  NStep,
  NSteps,
  NSpace,
  useMessage
} from 'naive-ui'
import { computed, onMounted, ref, watch } from 'vue'
import { NiveauService } from '@/api/NiveauService'
import { ParcoursService } from '@/api/ParcoursService'
import { TypeStageService } from '@/api/TypeStageService'
import { EvaluationResultService } from '@/api/EvaluationResultService'
import { mapParcoursCatalog, useParcoursCascade, type ParcoursCatalogEntry } from '@/composables/useParcoursCascade'

type ExportType = 'niveau' | 'parcours' | 'type-stage'
type ExportFormat = 'pdf' | 'excel'

const message = useMessage()
const currentStep = ref(0)
const downloadingExportKey = ref<string | null>(null)
const selectedExportType = ref<ExportType | null>(null)
const selectedFormat = ref<ExportFormat | null>(null)

const niveauOptions = ref<Array<{ label: string, value: number }>>([])
const parcoursCatalog = ref<ParcoursCatalogEntry[]>([])
const typeStageOptions = ref<Array<{ label: string, value: number }>>([])
const parcoursCascade = useParcoursCascade(parcoursCatalog)

const selectedIdByType = ref<Record<ExportType, number | null>>({
  niveau: null,
  parcours: null,
  'type-stage': null
})

const exportTypeLabels: Record<ExportType, string> = {
  niveau: 'Par niveau',
  parcours: 'Par parcours',
  'type-stage': 'Par type de stage'
}

const formatLabels: Record<ExportFormat, string> = {
  pdf: 'PDF',
  excel: 'Excel'
}

const currentEntityOptions = computed(() => {
  if (selectedExportType.value === 'niveau') return niveauOptions.value
  if (selectedExportType.value === 'type-stage') return typeStageOptions.value
  return []
})

const currentEntityPlaceholder = computed(() => {
  if (selectedExportType.value === 'niveau') return 'Selectionner un niveau'
  if (selectedExportType.value === 'parcours') return 'Selectionnez departement, niveau et specialite'
  if (selectedExportType.value === 'type-stage') return 'Selectionner un type de stage'
  return 'Selectionner une option'
})

const currentEntityId = computed<number | null>({
  get () {
    if (!selectedExportType.value) return null
    if (selectedExportType.value === 'parcours') return parcoursCascade.resolvedParcoursId.value
    return selectedIdByType.value[selectedExportType.value]
  },
  set (value) {
    if (!selectedExportType.value) return
    if (selectedExportType.value === 'parcours') return
    selectedIdByType.value[selectedExportType.value] = value
  }
})

const currentEntityLabel = computed(() => {
  if (!currentEntityId.value) return null
  if (selectedExportType.value === 'parcours') return parcoursCascade.resolvedParcoursLabel.value
  return currentEntityOptions.value.find(option => option.value === currentEntityId.value)?.label ?? null
})

const canGoNextFromStep1 = computed(() => selectedExportType.value !== null)
const canGoNextFromStep2 = computed(() => currentEntityId.value !== null && selectedFormat.value !== null)
const currentDownloadKey = computed(() => {
  if (!selectedExportType.value || !selectedFormat.value) return null
  return `${selectedExportType.value}-${selectedFormat.value}`
})

async function loadOptions () {
  try {
    const [niveaux, parcours, typeStages] = await Promise.all([
      NiveauService.getAll(0, 200),
      ParcoursService.getCatalog(),
      TypeStageService.getAll(0, 200)
    ])

    niveauOptions.value = (niveaux.data?.content ?? []).map((item: any) => ({
      label: item.libelle,
      value: item.id
    }))

    parcoursCatalog.value = mapParcoursCatalog(parcours)

    typeStageOptions.value = (typeStages.data?.content ?? []).map((item: any) => ({
      label: item.libelle,
      value: item.id
    }))
  } catch {
    message.error('Impossible de charger les options d export')
  }
}

function goToStep2 () {
  if (!selectedExportType.value) {
    message.warning('Selectionnez un type d export')
    return
  }
  currentStep.value = 1
}

function goToStep3 () {
  if (!selectedExportType.value) {
    message.warning('Selectionnez un type d export')
    return
  }
  if (!currentEntityId.value) {
    message.warning(currentEntityPlaceholder.value)
    return
  }
  if (!selectedFormat.value) {
    message.warning('Selectionnez un format')
    return
  }
  currentStep.value = 2
}

async function downloadExport () {
  if (!selectedExportType.value || !currentEntityId.value || !selectedFormat.value) {
    message.warning('Completez les informations de l export')
    return
  }

  const key = `${selectedExportType.value}-${selectedFormat.value}`
  if (downloadingExportKey.value === key) return
  downloadingExportKey.value = key

  try {
    if (selectedExportType.value === 'niveau') {
      await EvaluationResultService.downloadExportByNiveau(currentEntityId.value, selectedFormat.value)
    } else if (selectedExportType.value === 'parcours') {
      await EvaluationResultService.downloadExportByParcours(currentEntityId.value, selectedFormat.value)
    } else {
      await EvaluationResultService.downloadExportByTypeStage(currentEntityId.value, selectedFormat.value)
    }
    message.success(`Export ${selectedFormat.value.toUpperCase()} genere`)
  } catch {
    message.error(`Impossible d exporter en ${selectedFormat.value.toUpperCase()}`)
  } finally {
    downloadingExportKey.value = null
  }
}

function resetWizard () {
  currentStep.value = 0
  selectedExportType.value = null
  selectedFormat.value = null
  parcoursCascade.resetSelection()
  selectedIdByType.value = {
    niveau: null,
    parcours: null,
    'type-stage': null
  }
  downloadingExportKey.value = null
}

watch(selectedExportType, (value) => {
  if (value !== 'parcours') {
    parcoursCascade.resetSelection()
  }
})

onMounted(loadOptions)
</script>

<template>
  <div class="space-y-4 max-w-4xl">
    <div>
      <h1 class="text-2xl font-bold">Assistant d export des evaluations</h1>
      <p class="text-sm text-slate-500">
        Assistant d export en 3 etapes pour telecharger les resultats en PDF ou Excel.
      </p>
    </div>

    <NCard>
      <NSteps :current="currentStep" class="mb-6">
        <NStep title="Type d export" description="Choisissez le contexte" />
        <NStep title="Parametres" description="Selection et format" />
        <NStep title="Confirmation" description="Resume et telechargement" />
      </NSteps>

      <div v-if="currentStep === 0" class="space-y-4">
        <p class="text-sm text-slate-500">Selectionnez le type d export a generer.</p>
        <NRadioGroup v-model:value="selectedExportType" name="export-type">
          <NSpace vertical>
            <NRadio value="niveau">Par niveau</NRadio>
            <NRadio value="parcours">Par parcours</NRadio>
            <NRadio value="type-stage">Par type de stage</NRadio>
          </NSpace>
        </NRadioGroup>

        <NSpace justify="end">
          <NButton type="primary" :disabled="!canGoNextFromStep1" @click="goToStep2">
            Suivant
          </NButton>
        </NSpace>
      </div>

      <div v-else-if="currentStep === 1" class="space-y-4">
        <NAlert type="info" :show-icon="false">
          {{ selectedExportType ? exportTypeLabels[selectedExportType] : '-' }}
        </NAlert>

        <div v-if="selectedExportType === 'parcours'" class="space-y-3">
          <NSelect
            v-model:value="parcoursCascade.departementId.value"
            :options="parcoursCascade.departementOptions.value"
            placeholder="Selectionner un departement"
            clearable
          />
          <NSelect
            v-model:value="parcoursCascade.niveauId.value"
            :options="parcoursCascade.niveauOptions.value"
            placeholder="Selectionner un niveau"
            clearable
          />
          <NSelect
            v-model:value="parcoursCascade.specialiteId.value"
            :options="parcoursCascade.specialiteOptions.value"
            placeholder="Selectionner une specialite"
            clearable
          />
        </div>
        <NSelect
          v-else
          v-model:value="currentEntityId"
          :options="currentEntityOptions"
          :placeholder="currentEntityPlaceholder"
          clearable
        />

        <div>
          <p class="mb-2 text-sm font-medium">Format</p>
          <NRadioGroup v-model:value="selectedFormat" name="export-format">
            <NSpace>
              <NRadio value="pdf">PDF</NRadio>
              <NRadio value="excel">Excel</NRadio>
            </NSpace>
          </NRadioGroup>
        </div>

        <NSpace justify="space-between">
          <NButton @click="currentStep = 0">Precedent</NButton>
          <NButton type="primary" :disabled="!canGoNextFromStep2" @click="goToStep3">
            Suivant
          </NButton>
        </NSpace>
      </div>

      <div v-else class="space-y-4">
        <p class="text-sm text-slate-500">Verifiez votre selection avant de telecharger.</p>

        <div class="rounded-md border border-slate-200 bg-slate-50 p-4 text-sm space-y-2">
          <p><span class="font-semibold">Type:</span> {{ selectedExportType ? exportTypeLabels[selectedExportType] : '-' }}</p>
          <p><span class="font-semibold">Selection:</span> {{ currentEntityLabel ?? '-' }}</p>
          <p><span class="font-semibold">Format:</span> {{ selectedFormat ? formatLabels[selectedFormat] : '-' }}</p>
        </div>

        <NSpace justify="space-between">
          <NSpace>
            <NButton @click="currentStep = 1">Precedent</NButton>
            <NButton secondary @click="resetWizard">Nouvel export</NButton>
          </NSpace>
          <NButton
            type="primary"
            :loading="downloadingExportKey === currentDownloadKey"
            :disabled="!canGoNextFromStep2"
            @click="downloadExport"
          >
            Telecharger
          </NButton>
        </NSpace>
      </div>
    </NCard>
  </div>
</template>
