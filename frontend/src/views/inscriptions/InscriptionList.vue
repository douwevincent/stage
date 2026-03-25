<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, useMessage, NSelect, NInput
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { InscriptionService, type InscriptionDTO } from '@/api/InscriptionService'
import { AnneeAcademiqueService } from '@/api/AnneeAcademiqueService'
import { EtudiantService } from '@/api/EtudiantService'
import { ParcoursService } from '@/api/ParcoursService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<InscriptionDTO>({
  anneeAcademiqueId: null,
  etudiantId: null,
  parcoursId: null
})

const anneeOptions = ref<{ label: string, value: number }[]>([])
const etudiantOptions = ref<{ label: string, value: number }[]>([])
const parcoursOptions = ref<{ label: string, value: number }[]>([])
const selectedAnneeId = ref<number | null>(null)
const selectedEtudiantId = ref<number | null>(null)
const selectedParcoursId = ref<number | null>(null)
const searchQuery = ref('')
const sortField = ref('anneeAcademique.libelle')
const sortOrder = ref<'asc' | 'desc'>('asc')

const rules: FormRules = {
  anneeAcademiqueId: {
    required: true,
    type: 'number',
    message: 'L\'année académique est requise',
    trigger: 'change'
  },
  etudiantId: {
    required: true,
    type: 'number',
    message: 'L\'étudiant est requis',
    trigger: 'change'
  },
  parcoursId: {
    required: true,
    type: 'number',
    message: 'Le parcours est requis',
    trigger: 'change'
  }
}

const columns: DataTableColumns<InscriptionDTO> = [
  {
    title: 'Année Académique',
    key: 'anneeAcademiqueLibelle',
    minWidth: 180,
    render (row) {
      return row.anneeAcademiqueLibelle || row.anneeAcademiqueId || '-'
    }
  },
  {
    title: 'Étudiant',
    key: 'etudiantNom',
    minWidth: 220,
    render (row) {
      if (row.etudiantMatricule || row.etudiantNom) {
        return `${row.etudiantMatricule || ''} ${row.etudiantNom || ''}`.trim()
      }
      return row.etudiantId || '-'
    }
  },
  {
    title: 'Parcours',
    key: 'parcoursLibelle',
    minWidth: 220,
    render (row) {
      return row.parcoursLibelle || row.parcoursId || '-'
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
  { label: 'Année académique', value: 'anneeAcademique.libelle' },
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
      etudiantId: selectedEtudiantId.value,
      parcoursId: selectedParcoursId.value,
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
      ParcoursService.getAll(0, 200)
    ])

    const annees = anneesRes.data.content || []
    const etudiants = etudiantsRes.data.content || []
    const parcours = parcoursRes.data.content || []

    anneeOptions.value = annees.map((a: any) => ({
      label: a.libelle,
      value: a.id
    }))

    etudiantOptions.value = etudiants.map((e: any) => ({
      label: `${e.matricule || ''} ${e.nom || ''}`.trim(),
      value: e.id
    }))

    parcoursOptions.value = parcours.map((p: any) => ({
      label: p.libelle || `${p.specialiteCode || p.specialiteId} - ${p.niveauLibelle || p.niveauId}`,
      value: p.id
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
    anneeAcademiqueId: null,
    etudiantId: null,
    parcoursId: null
  })
  showModal.value = true
}

const handleEdit = (row: InscriptionDTO) => {
  modalTitle.value = 'Modifier une Inscription'
  Object.assign(formModel, {
    id: row.id,
    anneeAcademiqueId: row.anneeAcademiqueId,
    etudiantId: row.etudiantId,
    parcoursId: row.parcoursId
  })
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
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
            v-model:value="selectedEtudiantId"
            :options="etudiantOptions"
            placeholder="Filtrer par étudiant"
            clearable
            filterable
            @update:value="applyServerFilters"
          />
        </div>
        <div class="w-full md:w-64">
          <n-select
            v-model:value="selectedParcoursId"
            :options="parcoursOptions"
            placeholder="Filtrer par parcours"
            clearable
            @update:value="applyServerFilters"
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
        :scroll-x="980"
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
          <n-form-item label="Année académique" path="anneeAcademiqueId">
            <n-select
              v-model:value="formModel.anneeAcademiqueId"
              :options="anneeOptions"
              placeholder="Sélectionner une année académique"
            />
          </n-form-item>
          <n-form-item label="Étudiant" path="etudiantId">
            <n-select
              v-model:value="formModel.etudiantId"
              :options="etudiantOptions"
              placeholder="Sélectionner un étudiant"
              filterable
            />
          </n-form-item>
          <n-form-item label="Parcours" path="parcoursId">
            <n-select
              v-model:value="formModel.parcoursId"
              :options="parcoursOptions"
              placeholder="Sélectionner un parcours"
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