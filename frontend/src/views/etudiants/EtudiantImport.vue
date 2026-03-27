<script setup lang="ts">
import {
  NAlert,
  NButton,
  NCard,
  NDataTable,
  NDivider,
  NIcon,
  NSpace,
  NTag,
  useMessage,
  type DataTableColumns,
} from 'naive-ui'
import { Upload, ArrowLeft } from 'lucide-vue-next'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import ExcelJS from 'exceljs'
import {
  EtudiantService,
  type EtudiantImportResultDTO,
  type EtudiantImportRowDTO,
  type ImportRowMessageDTO,
} from '@/api/EtudiantService'

const message = useMessage()
const router = useRouter()

const selectedFileName = ref('')
const importRows = ref<EtudiantImportRowDTO[]>([])
const importing = ref(false)
const result = ref<EtudiantImportResultDTO | null>(null)

const expectedHeaders = [
  'No',
  'Matricule',
  'Nom',
  'Email',
  'Telephone',
  'Libellé niveau',
  'Code département',
  'Code spécialité',
]

const detailsColumns: DataTableColumns<ImportRowMessageDTO> = [
  { title: 'No', key: 'no', width: 90 },
  { title: 'Matricule', key: 'matricule', minWidth: 180 },
  { title: 'Message', key: 'message', minWidth: 360 },
]

const canImport = computed(() => importRows.value.length > 0 && !importing.value)

const normalizeHeader = (value: string): string => {
  return value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .trim()
    .toLowerCase()
}

const getCell = (row: Record<string, unknown>, keys: string[]): string => {
  for (const key of keys) {
    const value = row[key]
    if (value !== undefined && value !== null) {
      return String(value).trim()
    }
  }
  return ''
}

const formatCellValue = (value: unknown): string => {
  if (value === undefined || value === null) {
    return ''
  }
  if (typeof value === 'object') {
    if ('text' in (value as Record<string, unknown>) && typeof (value as Record<string, unknown>).text === 'string') {
      return ((value as Record<string, unknown>).text as string).trim()
    }
    if ('result' in (value as Record<string, unknown>) && (value as Record<string, unknown>).result !== undefined) {
      return String((value as Record<string, unknown>).result).trim()
    }
  }
  return String(value).trim()
}

const onFileSelected = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  result.value = null
  importRows.value = []

  if (!file) {
    selectedFileName.value = ''
    return
  }

  const lowerName = file.name.toLowerCase()
  if (!lowerName.endsWith('.xlsx')) {
    message.error('Le fichier doit être au format .xlsx')
    selectedFileName.value = ''
    input.value = ''
    return
  }

  selectedFileName.value = file.name

  try {
    const buffer = await file.arrayBuffer()
    const workbook = new ExcelJS.Workbook()
    await workbook.xlsx.load(buffer)
    const worksheet = workbook.worksheets[0]

    if (!worksheet) {
      message.error('Le fichier ne contient aucune feuille')
      return
    }

    const firstRowValues = worksheet.getRow(1).values
    const headerValues: unknown[] = Array.isArray(firstRowValues)
      ? firstRowValues.slice(1)
      : []
    const headers = headerValues.map((header: unknown) => normalizeHeader(formatCellValue(header)))
    const normalizedHeaders = new Set(headers.filter(Boolean))

    const missingHeaders = expectedHeaders.filter(
      (header) => !normalizedHeaders.has(normalizeHeader(header)),
    )

    if (missingHeaders.length > 0) {
      message.error(`Colonnes manquantes: ${missingHeaders.join(', ')}`)
      return
    }

    const parsedRows: EtudiantImportRowDTO[] = []
    worksheet.eachRow((row: ExcelJS.Row, rowNumber: number) => {
      if (rowNumber === 1) {
        return
      }

      const normalizedRow: Record<string, unknown> = {}
      headers.forEach((header: string, index: number) => {
        if (!header) {
          return
        }
        normalizedRow[header] = formatCellValue(row.getCell(index + 1).value)
      })

      if (Object.values(normalizedRow).every((value) => String(value).trim() === '')) {
        return
      }

      parsedRows.push({
        no: Number(getCell(normalizedRow, ['no'])) || undefined,
        matricule: getCell(normalizedRow, ['matricule']),
        nom: getCell(normalizedRow, ['nom']),
        email: getCell(normalizedRow, ['email']),
        telephone: getCell(normalizedRow, ['telephone']),
        libelleNiveau: getCell(normalizedRow, ['libelle niveau']),
        codeDepartement: getCell(normalizedRow, ['code departement']),
        codeSpecialite: getCell(normalizedRow, ['code specialite']),
      })
    })

    if (parsedRows.length === 0) {
      message.error('Le fichier ne contient aucune ligne de données')
      return
    }

    importRows.value = parsedRows

    message.success(`${importRows.value.length} ligne(s) prête(s) pour l'import`) 
  } catch (error) {
    message.error('Erreur lors de la lecture du fichier Excel')
  }
}

const runImport = async () => {
  if (!canImport.value) {
    return
  }

  importing.value = true
  try {
    const res = await EtudiantService.importRows(importRows.value)
    result.value = res.data
    message.success('Import terminé')
  } catch (error: any) {
    const backendMessage = error?.response?.data?.message
    message.error(backendMessage || 'Erreur lors de l\'import')
  } finally {
    importing.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Importer des Étudiants</h1>
      <n-button @click="router.push({ name: 'etudiants-list' })">
        <template #icon>
          <n-icon><ArrowLeft /></n-icon>
        </template>
        Retour à la liste
      </n-button>
    </div>

    <n-card>
      <n-space vertical :size="14">
        <n-alert type="info" :show-icon="false">
          Colonnes attendues: No, Matricule, Nom, Email, Telephone, Libellé niveau, code département, code spécialité
        </n-alert>

        <div class="flex flex-wrap items-center gap-3">
          <label class="inline-flex cursor-pointer items-center gap-2 rounded-md border px-4 py-2">
            <n-icon><Upload /></n-icon>
            <span>Sélectionner un fichier</span>
            <input
              class="hidden"
              type="file"
              accept=".xlsx"
              @change="onFileSelected"
            >
          </label>

          <n-tag v-if="selectedFileName" type="success" size="small">
            {{ selectedFileName }}
          </n-tag>

          <n-button type="primary" :disabled="!canImport" :loading="importing" @click="runImport">
            Lancer l'import
          </n-button>
        </div>

        <n-alert v-if="importRows.length > 0" type="success" :show-icon="false">
          {{ importRows.length }} ligne(s) détectée(s).
        </n-alert>
      </n-space>
    </n-card>

    <n-card v-if="result">
      <n-space vertical :size="10">
        <h2 class="text-lg font-semibold">Résumé</h2>
        <div class="grid grid-cols-1 gap-3 md:grid-cols-3">
          <n-tag type="info">Total lignes: {{ result.totalLignes }}</n-tag>
          <n-tag type="success">Étudiants créés: {{ result.etudiantsCrees }}</n-tag>
          <n-tag type="warning">Étudiants existants: {{ result.etudiantsExistants }}</n-tag>
          <n-tag type="success">Inscriptions créées: {{ result.inscriptionsCreees }}</n-tag>
          <n-tag type="warning">Avertissements: {{ result.avertissements }}</n-tag>
          <n-tag type="error">Erreurs: {{ result.erreurs }}</n-tag>
        </div>

        <n-divider />

        <template v-if="result.erreurs > 0">
          <h3 class="text-base font-semibold">Détails des erreurs</h3>
          <n-data-table
            :columns="detailsColumns"
            :data="result.detailsErreurs || []"
            :bordered="false"
            :max-height="280"
            :scroll-x="720"
          />
        </template>

        <template v-if="result.avertissements > 0">
          <h3 class="text-base font-semibold">Détails des avertissements</h3>
          <n-data-table
            :columns="detailsColumns"
            :data="result.detailsAvertissements || []"
            :bordered="false"
            :max-height="280"
            :scroll-x="720"
          />
        </template>
      </n-space>
    </n-card>
  </div>
</template>
