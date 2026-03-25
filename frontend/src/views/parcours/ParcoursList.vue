<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, useMessage, NSelect, NInput
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { ParcoursService, type ParcoursDTO } from '@/api/ParcoursService'
import { SpecialiteService } from '@/api/SpecialiteService'
import { NiveauService } from '@/api/NiveauService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<ParcoursDTO>({
  specialiteId: null,
  niveauId: null
})

const specialiteOptions = ref<{ label: string, value: number }[]>([])
const niveauOptions = ref<{ label: string, value: number }[]>([])
const selectedSpecialiteId = ref<number | null>(null)
const selectedNiveauId = ref<number | null>(null)
const searchQuery = ref('')
const sortField = ref('specialite.code')
const sortOrder = ref<'asc' | 'desc'>('asc')

const rules: FormRules = {
  specialiteId: { required: true, type: 'number', message: 'La spécialité est requise', trigger: 'change' },
  niveauId: { required: true, type: 'number', message: 'Le niveau est requis', trigger: 'change' }
}

const columns: DataTableColumns<ParcoursDTO> = [
  {
    title: 'Spécialité',
    key: 'specialiteCode',
    minWidth: 180,
    render (row) {
      return row.specialiteCode || row.specialiteIntitule || row.specialiteId || '-'
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
    title: 'Libellé Parcours',
    key: 'libelle',
    minWidth: 220,
    render (row) {
      return row.libelle || '-'
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
      specialiteId: selectedSpecialiteId.value,
      niveauId: selectedNiveauId.value,
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

const fetchOptions = async () => {
  try {
    const [specialitesRes, niveauxRes] = await Promise.all([
      SpecialiteService.getAll(0, 200),
      NiveauService.getAll(0, 200)
    ])

    const specialites = specialitesRes.data.content || []
    const niveaux = niveauxRes.data.content || []

    specialiteOptions.value = specialites.map((s: any) => ({
      label: s.code ? `${s.code} - ${s.intitule || ''}`.trim() : s.intitule,
      value: s.id
    }))

    niveauOptions.value = niveaux.map((n: any) => ({
      label: n.libelle,
      value: n.id
    }))
  } catch {
    message.error('Erreur lors du chargement des options')
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter un Parcours'
  Object.assign(formModel, {
    id: undefined,
    specialiteId: null,
    niveauId: null
  })
  showModal.value = true
}

const handleEdit = (row: ParcoursDTO) => {
  modalTitle.value = 'Modifier un Parcours'
  Object.assign(formModel, {
    id: row.id,
    specialiteId: row.specialiteId,
    niveauId: row.niveauId
  })
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await ParcoursService.update(formModel.id, formModel)
          message.success('Parcours modifié avec succès')
        } else {
          await ParcoursService.create(formModel)
          message.success('Parcours ajouté avec succès')
        }
        showModal.value = false
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
            v-model:value="selectedSpecialiteId"
            :options="specialiteOptions"
            placeholder="Filtrer par spécialité"
            clearable
            @update:value="applyServerFilters"
          />
        </div>
        <div class="w-full md:w-56">
          <n-select
            v-model:value="selectedNiveauId"
            :options="niveauOptions"
            placeholder="Filtrer par niveau"
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
          <n-form-item label="Spécialité" path="specialiteId">
            <n-select
              v-model:value="formModel.specialiteId"
              :options="specialiteOptions"
              placeholder="Sélectionner une spécialité"
            />
          </n-form-item>
          <n-form-item label="Niveau" path="niveauId">
            <n-select
              v-model:value="formModel.niveauId"
              :options="niveauOptions"
              placeholder="Sélectionner un niveau"
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