<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, useMessage, NSelect, NInput
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive, watch } from 'vue'
import { InscriptionService, type InscriptionDTO } from '@/api/InscriptionService'
import { AnneeAcademiqueService } from '@/api/AnneeAcademiqueService'
import { EtudiantService } from '@/api/EtudiantService'
import { ParcoursService } from '@/api/ParcoursService'
import { mapParcoursCatalog, useParcoursCascade, type ParcoursCatalogEntry } from '@/composables/useParcoursCascade'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

type InscriptionFormModel = InscriptionDTO & {
  departementId: number | null
  niveauId: number | null
  specialiteId: number | null
}

const formModel = reactive<InscriptionFormModel>({
  anneeAcademiqueId: null,
  etudiantId: null,
  parcoursId: null,
  departementId: null,
  niveauId: null,
  specialiteId: null
})

const annees = ref<any[]>([])
const anneeOptions = ref<{ label: string, value: number }[]>([])
const etudiantOptions = ref<{ label: string, value: number }[]>([])
const parcoursCatalog = ref<ParcoursCatalogEntry[]>([])
const selectedAnneeId = ref<number | null>(null)
const searchQuery = ref('')
const sortField = ref('etudiant.nom')
const sortOrder = ref<'asc' | 'desc'>('asc')
const filterCascade = useParcoursCascade(parcoursCatalog)
const formCascade = useParcoursCascade(parcoursCatalog)

const defaultAnneeId = computed(() => {
  const activeAnnee = annees.value.find((a: any) => a.actif === true)
  return activeAnnee?.id || null
})

const isFormDeptAndNiveauDefined = computed(() => {
  return formCascade.departementId.value !== null && formCascade.niveauId.value !== null
})

const isFilterDeptAndNiveauDefined = computed(() => {
  return filterCascade.departementId.value !== null && filterCascade.niveauId.value !== null
})

const rules: FormRules = {
  etudiantId: {
    required: true,
    type: 'number',
    message: 'L\'étudiant est requis',
    trigger: 'change'
  },
  departementId: {
    required: true,
    type: 'number',
    message: 'Le département est requis',
    trigger: 'change'
  },
  niveauId: {
    required: true,
    type: 'number',
    message: 'Le niveau est requis',
    trigger: 'change'
  },
  specialiteId: {
    required: true,
    type: 'number',
    message: 'La spécialité est requise',
    trigger: 'change'
  }
}

const parcoursMetaMap = computed(() => {
  const map = new Map<number, { departementCode: string, niveauLibelle: string, specialiteCode: string }>()
  parcoursCatalog.value.forEach((entry) => {
    map.set(entry.id, {
      departementCode: entry.departementLabel || '-',
      niveauLibelle: entry.niveauLabel || '-',
      specialiteCode: entry.specialiteLabel || '-'
    })
  })
  return map
})

const columns: DataTableColumns<InscriptionDTO> = [
  {
    title: 'Matricule',
    key: 'etudiantMatricule',
    minWidth: 120,
    render (row) {
      return row.etudiantMatricule || '-'
    }
  },
  {
    title: 'Nom',
    key: 'etudiantNom',
    minWidth: 150,
    render (row) {
      return row.etudiantNom || '-'
    }
  },
  {
    title: 'Département',
    key: 'parcoursId',
    minWidth: 120,
    render (row) {
      const meta = parcoursMetaMap.value.get(row.parcoursId || 0)
      return meta?.departementCode || '-'
    }
  },
  {
    title: 'Niveau',
    key: 'parcoursNiveauLibelle',
    minWidth: 120,
    render (row) {
      const meta = parcoursMetaMap.value.get(row.parcoursId || 0)
      return meta?.niveauLibelle || '-'
    }
  },
  {
    title: 'Spécialité',
    key: 'parcoursSpecialiteCode',
    minWidth: 150,
    render (row) {
      const meta = parcoursMetaMap.value.get(row.parcoursId || 0)
      return meta?.specialiteCode || '-'
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 100,
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
              onClick: () => handleEdit(row)
            }, { default: () => h(NIcon, null, { default: () => h(Edit) }) }),
            default: () => 'Éditer'
          }),
          h(NTooltip, null, {
            trigger: () => h(NPopconfirm, {
              onPositiveClick: () => handleDelete(row.id!)
            }, {
              trigger: () => h(NButton, {
                size: 'small',
                quaternary: true,
                type: 'error',
                circle: true
              }, { default: () => h(NIcon, null, { default: () => h(Trash2) }) }),
              default: () => 'Voulez-vous vraiment supprimer cette inscription ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<InscriptionDTO[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)
const sortFieldOptions = [
  { label: 'Étudiant', value: 'etudiant.nom' },
  { label: 'Parcours', value: 'parcours.specialite.code' }
]
const sortOrderOptions = [
  { label: 'Croissant', value: 'asc' },
  { label: 'Décroissant', value: 'desc' }
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
    const res = await InscriptionService.getAll(page.value - 1, pageSize.value, {
      anneeAcademiqueId: selectedAnneeId.value,
      parcoursId: filterCascade.resolvedParcoursId.value,
      departementId: filterCascade.departementId.value,
      niveauId: filterCascade.niveauId.value,
      specialiteId: filterCascade.specialiteId.value,
      q: searchQuery.value,
      sort: `${sortField.value},${sortOrder.value}`
    })
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch {
    message.error('Erreur lors du chargement des inscriptions')
  } finally {
    loading.value = false
  }
}

const applyServerFilters = () => {
  page.value = 1
  fetchData()
}

const fetchOptions = async () => {
  try {
    const [anneesRes, etudiantsRes, parcoursRes] = await Promise.all([
      AnneeAcademiqueService.getAll(0, 200),
      EtudiantService.getAll(0, 200),
      ParcoursService.getCatalog()
    ])

    annees.value = anneesRes.data.content || []
    const etudiants = etudiantsRes.data.content || []
    parcoursCatalog.value = mapParcoursCatalog(parcoursRes)

    anneeOptions.value = annees.value.map((a: any) => ({
      label: a.libelle,
      value: a.id
    }))

    etudiantOptions.value = etudiants.map((e: any) => ({
      label: `${e.matricule || ''} ${e.nom || ''}`.trim(),
      value: e.id
    }))
  } catch {
    message.error('Erreur lors du chargement des options')
  }
}

const fetchAllInscriptionsForValidation = async () => {
  const all: InscriptionDTO[] = []
  let currentPage = 0
  const size = 200

  while (true) {
    const res = await InscriptionService.getAll(currentPage, size)
    const chunk = res.data.content || []
    all.push(...chunk)

    const totalPages = res.data.totalPages || res.data.page?.totalPages
    if (typeof totalPages === 'number') {
      if (currentPage + 1 >= totalPages) {
        break
      }
    } else if (chunk.length < size) {
      break
    }

    currentPage += 1
  }

  return all
}

const hasDuplicateInscription = async () => {
  if (formModel.anneeAcademiqueId == null || formModel.etudiantId == null || formModel.parcoursId == null) {
    return false
  }

  const allInscriptions = await fetchAllInscriptionsForValidation()
  return allInscriptions.some((inscription) => {
    return inscription.anneeAcademiqueId === formModel.anneeAcademiqueId &&
      inscription.etudiantId === formModel.etudiantId &&
      inscription.parcoursId === formModel.parcoursId &&
      inscription.id !== formModel.id
  })
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter une Inscription'
  Object.assign(formModel, {
    id: undefined,
    anneeAcademiqueId: defaultAnneeId.value,
    etudiantId: null,
    parcoursId: null,
    departementId: null,
    niveauId: null,
    specialiteId: null
  })
  formCascade.resetSelection()
  formRef.value?.restoreValidation()
  showModal.value = true
}

const handleEdit = (row: InscriptionDTO) => {
  modalTitle.value = 'Modifier une Inscription'
  Object.assign(formModel, {
    id: row.id,
    anneeAcademiqueId: row.anneeAcademiqueId,
    etudiantId: row.etudiantId,
    parcoursId: row.parcoursId,
    departementId: null,
    niveauId: null,
    specialiteId: null
  })
  formCascade.setSelectionFromParcoursId(row.parcoursId)
  formRef.value?.restoreValidation()
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      if (formModel.parcoursId == null) {
        message.error('La spécialité est requise')
        return
      }

      saving.value = true
      try {
        const isDuplicate = await hasDuplicateInscription()
        if (isDuplicate) {
          message.warning('Cette inscription existe déjà pour cet étudiant, cette année et ce parcours')
          return
        }

        if (formModel.id) {
          await InscriptionService.update(formModel.id, formModel)
          message.success('Inscription modifiée avec succès')
        } else {
          await InscriptionService.create(formModel)
          message.success('Inscription ajoutée avec succès')
        }
        showModal.value = false
        fetchData()
      } catch {
        message.error('Impossible d\'enregistrer cette inscription (doublon ou données invalides)')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    await InscriptionService.delete(id)
    message.success('Inscription supprimée avec succès')
    fetchData()
  } catch {
    message.error('Erreur lors de la suppression')
  }
}

onMounted(() => {
  fetchData()
  fetchOptions()
})

watch(
  [filterCascade.departementId, filterCascade.niveauId, filterCascade.specialiteId],
  () => {
    applyServerFilters()
  }
)

watch(
  () => formModel.departementId,
  (value) => {
    if (formCascade.departementId.value !== value) {
      formCascade.departementId.value = value
    }
  }
)

watch(
  () => formModel.niveauId,
  (value) => {
    if (formCascade.niveauId.value !== value) {
      formCascade.niveauId.value = value
    }
  }
)

watch(
  () => formModel.specialiteId,
  (value) => {
    if (formCascade.specialiteId.value !== value) {
      formCascade.specialiteId.value = value
    }
  }
)

watch(
  [formCascade.departementId, formCascade.niveauId, formCascade.specialiteId, formCascade.resolvedParcoursId],
  () => {
    if (formModel.departementId !== formCascade.departementId.value) {
      formModel.departementId = formCascade.departementId.value
    }
    if (formModel.niveauId !== formCascade.niveauId.value) {
      formModel.niveauId = formCascade.niveauId.value
    }
    if (formModel.specialiteId !== formCascade.specialiteId.value) {
      formModel.specialiteId = formCascade.specialiteId.value
    }
    formModel.parcoursId = formCascade.resolvedParcoursId.value
  },
  { immediate: true }
)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Inscriptions</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter une inscription
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex flex-wrap items-center gap-3">
        <div class="w-full md:w-64">
          <n-input
            v-model:value="searchQuery"
            placeholder="Rechercher une inscription..."
            @update:value="applyServerFilters"
          >
            <template #prefix>
              <n-icon><SearchOutlined /></n-icon>
            </template>
          </n-input>
        </div>
        <div class="w-full md:w-56">
          <n-select
            v-model:value="selectedAnneeId"
            :options="anneeOptions"
            placeholder="Filtrer par année"
            clearable
            @update:value="applyServerFilters"
          />
        </div>
        <div class="w-full md:w-64">
          <n-select
            v-model:value="filterCascade.departementId.value"
            :options="filterCascade.departementOptions.value"
            placeholder="Filtrer par département"
            clearable
          />
        </div>
        <div class="w-full md:w-56">
          <n-select
            v-model:value="filterCascade.niveauId.value"
            :options="filterCascade.niveauOptions.value"
            placeholder="Filtrer par niveau"
            clearable
          />
        </div>
        <div class="w-full md:w-64">
          <n-select
            v-model:value="filterCascade.specialiteId.value"
            :options="filterCascade.specialiteOptions.value"
            placeholder="Filtrer par spécialité"
            :disabled="!isFilterDeptAndNiveauDefined"
            clearable
          />
        </div>
        <div class="w-full md:w-56">
          <n-select
            v-model:value="sortField"
            :options="sortFieldOptions"
            placeholder="Trier par"
            @update:value="applyServerFilters"
          />
        </div>
        <div class="w-full md:w-48">
          <n-select
            v-model:value="sortOrder"
            :options="sortOrderOptions"
            placeholder="Ordre"
            @update:value="applyServerFilters"
          />
        </div>
      </div>
      <n-data-table
        remote
        :columns="columns"
        :data="data"
        :loading="loading"
        :bordered="false"
        :pagination="pagination"
        :scroll-x="1200"
      />
    </n-card>

    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="modalTitle"
      class="max-w-lg"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-form
        ref="formRef"
        :model="formModel"
        :rules="rules"
        label-placement="left"
        label-width="140"
        label-align="left"
        require-mark-placement="right-hanging"
      >
        <div class="space-y-4">
          <div class="text-xs text-gray-500 mb-2">
            Année académique: {{ anneeOptions.find(opt => opt.value === formModel.anneeAcademiqueId)?.label || 'Non disponible' }}
          </div>
          <n-form-item label="Étudiant" path="etudiantId">
            <n-select
              v-model:value="formModel.etudiantId"
              :options="etudiantOptions"
              placeholder="Sélectionner un étudiant"
              filterable
            />
          </n-form-item>
          <n-form-item label="Département" path="departementId">
            <n-select
              v-model:value="formModel.departementId"
              :options="formCascade.departementOptions.value"
              placeholder="Sélectionner un département"
              clearable
            />
          </n-form-item>
          <n-form-item label="Niveau" path="niveauId">
            <n-select
              v-model:value="formModel.niveauId"
              :options="formCascade.niveauOptions.value"
              placeholder="Sélectionner un niveau"
              clearable
            />
          </n-form-item>
          <n-form-item label="Spécialité" path="specialiteId">
            <n-select
              v-model:value="formModel.specialiteId"
              :options="formCascade.specialiteOptions.value"
              placeholder="Sélectionner une spécialité"
              :disabled="!isFormDeptAndNiveauDefined"
              clearable
            />
          </n-form-item>
        </div>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">Annuler</n-button>
          <n-button type="primary" :loading="saving" @click="handleSave">
            Enregistrer
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>