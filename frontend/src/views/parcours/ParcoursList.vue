<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, useMessage, NSelect, NInput
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive, watch } from 'vue'
import { ParcoursService, type ParcoursDTO } from '@/api/ParcoursService'
import { BaremeService, type BaremeDTO } from '@/api/BaremeService'
import { SpecialiteService, type SpecialiteDTO } from '@/api/SpecialiteService'
import { NiveauService, type NiveauDTO } from '@/api/NiveauService'
import { DepartementService, type DepartementDTO } from '@/api/DepartementService'
import { mapParcoursCatalog, useParcoursCascade, type ParcoursCatalogEntry } from '@/composables/useParcoursCascade'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

type OptionItem = { label: string, value: number }
type SpecialiteOption = OptionItem & { departementId: number | null }

const formModel = reactive<ParcoursDTO>({
  specialiteId: null,
  niveauId: null,
  baremeId: null
})

const departementOptions = ref<OptionItem[]>([])
const niveauOptions = ref<OptionItem[]>([])
const specialiteOptions = ref<SpecialiteOption[]>([])
const baremeOptions = ref<OptionItem[]>([])
const defaultBaremeId = ref<number | null>(null)
const parcoursCatalog = ref<ParcoursCatalogEntry[]>([])
const selectedBaremeId = ref<number | null>(null)
const formDepartementId = ref<number | null>(null)
const formNiveauId = ref<number | null>(null)
const formSpecialiteId = ref<number | null>(null)
const searchQuery = ref('')
const sortField = ref('specialite.code')
const sortOrder = ref<'asc' | 'desc'>('asc')
const filterCascade = useParcoursCascade(parcoursCatalog)

const formatCodeLabel = (code?: string | null, label?: string | null) => code || label || '-'

const modalSpecialiteOptions = computed(() => {
  if (formDepartementId.value == null) {
    return specialiteOptions.value.map(({ departementId: _departementId, ...option }) => option)
  }

  return specialiteOptions.value
    .filter((option) => option.departementId === formDepartementId.value)
    .map(({ departementId: _departementId, ...option }) => option)
})

const rules: FormRules = {
  specialiteId: { required: true, type: 'number', message: 'La spécialité est requise', trigger: 'change' },
  niveauId: { required: true, type: 'number', message: 'Le niveau est requis', trigger: 'change' },
  baremeId: { type: 'number', trigger: 'change' }
}

const columns: DataTableColumns<ParcoursDTO> = [
  {
    title: 'Département',
    key: 'departementCode',
    minWidth: 220,
    render (row) {
      return row.departementCode || row.departementIntitule || '-'
    }
  },
  {
    title: 'Spécialité',
    key: 'specialiteCode',
    minWidth: 180,
    render (row) {
      return row.specialiteCode || row.specialiteIntitule || '-'
    }
  },
  {
    title: 'Niveau',
    key: 'niveauLibelle',
    minWidth: 180,
    render (row) {
      return row.niveauLibelle || row.niveauId || '-'
    }
  },
  {
    title: 'Barème',
    key: 'baremeCode',
    minWidth: 150,
    render (row) {
      return row.baremeCode || row.baremeId || '-'
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
              default: () => 'Voulez-vous vraiment supprimer ce parcours ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<ParcoursDTO[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)
const sortFieldOptions = [
  { label: 'Spécialité', value: 'specialite.code' },
  { label: 'Niveau', value: 'niveau.libelle' }
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
    const res = await ParcoursService.getAll(page.value - 1, pageSize.value, {
      departementId: filterCascade.departementId.value,
      specialiteId: filterCascade.specialiteId.value,
      niveauId: filterCascade.niveauId.value,
      baremeId: selectedBaremeId.value,
      q: searchQuery.value,
      sort: `${sortField.value},${sortOrder.value}`
    })
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch {
    message.error('Erreur lors du chargement des parcours')
  } finally {
    loading.value = false
  }
}

const applyServerFilters = () => {
  page.value = 1
  fetchData()
}

const handleBaremeFilterUpdate = (value: number | null) => {
  selectedBaremeId.value = value
  applyServerFilters()
}

const fetchOptions = async () => {
  try {
    const [departementsRes, specialitesRes, niveauxRes, baremesRes, parcoursRes] = await Promise.all([
      DepartementService.getAll(0, 200),
      SpecialiteService.getAll(0, 200),
      NiveauService.getAll(0, 200),
      BaremeService.getAll(0, 200),
      ParcoursService.getCatalog()
    ])

    const departements = departementsRes.data.content || []
    const specialites = specialitesRes.data.content || []
    const niveaux = niveauxRes.data.content || []
    const baremes = (baremesRes.data.content || []).filter((bareme: BaremeDTO) => bareme.actif)
    parcoursCatalog.value = mapParcoursCatalog(parcoursRes)
    defaultBaremeId.value = baremes.find((bareme: BaremeDTO) => bareme.parDefaut && typeof bareme.id === 'number')?.id ?? null

    departementOptions.value = departements
      .filter((departement: DepartementDTO) => typeof departement.id === 'number')
      .map((departement: DepartementDTO) => ({
        label: formatCodeLabel(departement.code, departement.intitule),
        value: departement.id as number
      }))
      .sort((left: OptionItem, right: OptionItem) => left.label.localeCompare(right.label, 'fr', { sensitivity: 'base' }))

    specialiteOptions.value = specialites
      .filter((specialite: SpecialiteDTO) => typeof specialite.id === 'number')
      .map((specialite: SpecialiteDTO) => ({
        label: formatCodeLabel(specialite.code, specialite.intitule),
        value: specialite.id as number,
        departementId: specialite.departementId
      }))
      .sort((left: SpecialiteOption, right: SpecialiteOption) => left.label.localeCompare(right.label, 'fr', { sensitivity: 'base' }))

    niveauOptions.value = niveaux
      .filter((niveau: NiveauDTO) => typeof niveau.id === 'number')
      .map((niveau: NiveauDTO) => ({
        label: niveau.libelle,
        value: niveau.id as number
      }))
      .sort((left: OptionItem, right: OptionItem) => left.label.localeCompare(right.label, 'fr', { sensitivity: 'base' }))

    baremeOptions.value = baremes
      .filter((bareme: BaremeDTO) => typeof bareme.id === 'number')
      .map((bareme: BaremeDTO) => ({
        label: bareme.code,
        value: bareme.id as number
      }))
      .sort((left: OptionItem, right: OptionItem) => left.label.localeCompare(right.label, 'fr', { sensitivity: 'base' }))
  } catch {
    message.error('Erreur lors du chargement des options')
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter un Parcours'
  Object.assign(formModel, {
    id: undefined,
    specialiteId: null,
    niveauId: null,
    baremeId: defaultBaremeId.value
  })
  formDepartementId.value = null
  formNiveauId.value = null
  formSpecialiteId.value = null
  showModal.value = true
}

const handleEdit = (row: ParcoursDTO) => {
  modalTitle.value = 'Modifier un Parcours'
  Object.assign(formModel, {
    id: row.id,
    specialiteId: row.specialiteId,
    niveauId: row.niveauId,
    baremeId: row.baremeId
  })
  formDepartementId.value = row.departementId || specialiteOptions.value.find((option) => option.value === row.specialiteId)?.departementId || null
  formNiveauId.value = row.niveauId || null
  formSpecialiteId.value = row.specialiteId || null
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        const payload: ParcoursDTO = {
          id: formModel.id,
          specialiteId: formModel.specialiteId,
          niveauId: formModel.niveauId,
          baremeId: formModel.baremeId
        }

        if (formModel.id) {
          await ParcoursService.update(formModel.id, payload)
          message.success('Parcours modifié avec succès')
        } else {
          await ParcoursService.create(payload)
          message.success('Parcours ajouté avec succès')
        }
        showModal.value = false
        await fetchOptions()
        fetchData()
      } catch {
        message.error('Erreur lors de l\'enregistrement')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    await ParcoursService.delete(id)
    message.success('Parcours supprimé avec succès')
    await fetchOptions()
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
  [formDepartementId, formNiveauId, formSpecialiteId],
  () => {
    const selectedSpecialite = specialiteOptions.value.find((option) => option.value === formSpecialiteId.value)
    if (selectedSpecialite && formDepartementId.value != null && selectedSpecialite.departementId !== formDepartementId.value) {
      formSpecialiteId.value = null
      formModel.specialiteId = null
    } else {
      formModel.specialiteId = formSpecialiteId.value
    }

    formModel.niveauId = formNiveauId.value
  },
  { immediate: true }
)

watch(formSpecialiteId, (value) => {
  if (value == null) {
    return
  }

  const selectedSpecialite = specialiteOptions.value.find((option) => option.value === value)
  if (selectedSpecialite && selectedSpecialite.departementId != null) {
    formDepartementId.value = selectedSpecialite.departementId
  }
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Parcours</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter un parcours
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex flex-wrap items-center gap-3">
        <div class="w-full md:w-64">
          <n-input
            v-model:value="searchQuery"
            placeholder="Rechercher un parcours..."
            @update:value="applyServerFilters"
          >
            <template #prefix>
              <n-icon><SearchOutlined /></n-icon>
            </template>
          </n-input>
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
            clearable
          />
        </div>
        <div class="w-full md:w-56">
          <n-select
            :value="selectedBaremeId"
            :options="baremeOptions"
            placeholder="Filtrer par barème"
            clearable
            @update:value="handleBaremeFilterUpdate"
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
        :scroll-x="900"
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
        label-width="120"
        label-align="left"
        require-mark-placement="right-hanging"
      >
        <div class="space-y-4">
          <n-form-item label="Département">
            <n-select
              v-model:value="formDepartementId"
              :options="departementOptions"
              placeholder="Sélectionner un département"
              clearable
            />
          </n-form-item>
          <n-form-item label="Niveau" path="niveauId">
            <n-select
              v-model:value="formNiveauId"
              :options="niveauOptions"
              placeholder="Sélectionner un niveau"
              clearable
            />
          </n-form-item>
          <n-form-item label="Spécialité" path="specialiteId">
            <n-select
              v-model:value="formSpecialiteId"
              :options="modalSpecialiteOptions"
              placeholder="Sélectionner une spécialité"
              clearable
            />
          </n-form-item>
          <n-form-item label="Barème" path="baremeId">
            <n-select
              v-model:value="formModel.baremeId"
              :options="baremeOptions"
              placeholder="Sélectionner un barème"
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