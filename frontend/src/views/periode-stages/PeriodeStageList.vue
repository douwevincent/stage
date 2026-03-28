<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, NSelect, NDatePicker, NAlert, useMessage
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { PeriodeStageService, type PeriodeStageDTO } from '@/api/PeriodeStageService'
import { TypeStageService, type TypeStageDTO } from '@/api/TypeStageService'
import { AnneeAcademiqueService, type AnneeAcademiqueDTO } from '@/api/AnneeAcademiqueService'

interface SelectOption {
  label: string
  value: number
}

interface PeriodeStageFormModel {
  id?: number
  typeStageId: number | null
  anneeAcademiqueId: number | null
  dateDebut: string | null
  dateFin: string | null
}

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<PeriodeStageFormModel>({
  typeStageId: null,
  anneeAcademiqueId: null,
  dateDebut: null,
  dateFin: null
})

const typeStageOptions = ref<SelectOption[]>([])
const activeAnneeAcademique = ref<AnneeAcademiqueDTO | null>(null)

const typeStageMap = computed(() => {
  const entries = typeStageOptions.value.map((option) => [option.value, option.label] as const)
  return new Map<number, string>(entries)
})

const rules: FormRules = {
  typeStageId: { required: true, type: 'number', message: 'Le type de stage est requis', trigger: ['blur', 'change'] },
  dateDebut: {
    required: true,
    validator: () => {
      if (!formModel.dateDebut) {
        return new Error('La date de début est requise')
      }
      return true
    },
    trigger: ['blur', 'change']
  },
  dateFin: {
    required: true,
    validator: () => {
      if (!formModel.dateFin) {
        return new Error('La date de fin est requise')
      }
      if (formModel.dateDebut && formModel.dateFin < formModel.dateDebut) {
        return new Error('La date de fin doit être postérieure à la date de début')
      }
      return true
    },
    trigger: ['blur', 'change']
  }
}

const columns: DataTableColumns<PeriodeStageDTO> = [
  {
    title: 'Type de stage',
    key: 'typeStageId',
    minWidth: 180,
    render: (row) => typeStageMap.value.get(row.typeStageId ?? -1) ?? `#${row.typeStageId ?? '-'}`
  },
  { title: 'Date début', key: 'dateDebut', minWidth: 120 },
  { title: 'Date fin', key: 'dateFin', minWidth: 120 },
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
              default: () => 'Voulez-vous vraiment supprimer cette période de stage ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<PeriodeStageDTO[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)

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

const fetchSelectData = async () => {
  try {
    const [typeRes, activeAnneeRes] = await Promise.all([
      TypeStageService.getAll(0, 200),
      AnneeAcademiqueService.getActive()
    ])

    typeStageOptions.value = (typeRes.data.content || []).map((item: TypeStageDTO) => ({
      label: item.libelle,
      value: item.id!
    }))
    activeAnneeAcademique.value = activeAnneeRes.data
    formModel.anneeAcademiqueId = activeAnneeRes.data.id ?? null
  } catch {
    activeAnneeAcademique.value = null
    message.error('Erreur lors du chargement des données de référence (année active manquante ?)')
  }
}

const fetchData = async () => {
  if (!activeAnneeAcademique.value?.id) {
    data.value = []
    itemCount.value = 0
    return
  }

  loading.value = true
  try {
    const res = await PeriodeStageService.getAll(page.value - 1, pageSize.value, activeAnneeAcademique.value.id)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch {
    message.error('Erreur lors du chargement des données')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(formModel, {
    id: undefined,
    typeStageId: null,
    anneeAcademiqueId: activeAnneeAcademique.value?.id ?? null,
    dateDebut: null,
    dateFin: null
  })
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter une Période de Stage'
  resetForm()
  showModal.value = true
}

const handleEdit = (row: PeriodeStageDTO) => {
  modalTitle.value = 'Modifier une Période de Stage'
  Object.assign(formModel, {
    id: row.id,
    typeStageId: row.typeStageId,
    anneeAcademiqueId: activeAnneeAcademique.value?.id ?? null,
    dateDebut: row.dateDebut,
    dateFin: row.dateFin
  })
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (!activeAnneeAcademique.value?.id) {
          message.error('Aucune année académique active. Veuillez en activer une avant de continuer.')
          return
        }

        const payload: PeriodeStageDTO = {
          id: formModel.id,
          typeStageId: formModel.typeStageId,
          anneeAcademiqueId: activeAnneeAcademique.value.id,
          dateDebut: formModel.dateDebut!,
          dateFin: formModel.dateFin!
        }

        if (formModel.id) {
          await PeriodeStageService.update(formModel.id, payload)
          message.success('Période de stage modifiée avec succès')
        } else {
          await PeriodeStageService.create(payload)
          message.success('Période de stage ajoutée avec succès')
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
    await PeriodeStageService.delete(id)
    message.success('Période de stage supprimée avec succès')
    fetchData()
  } catch {
    message.error('Erreur lors de la suppression')
  }
}

onMounted(async () => {
  await fetchSelectData()
  await fetchData()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Périodes de Stage</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter une période de stage
      </n-button>
    </div>

    <n-card>
      <n-alert
        v-if="activeAnneeAcademique"
        type="info"
        class="mb-4"
        :show-icon="false"
      >
        Année académique active : <strong>{{ activeAnneeAcademique.libelle }}</strong>
      </n-alert>
      <n-alert
        v-else
        type="warning"
        class="mb-4"
        :show-icon="false"
      >
        Aucune année académique active. Activez-en une pour afficher et créer des périodes de stage.
      </n-alert>

      <div class="mb-4 flex items-center space-x-4">
        <n-input placeholder="Rechercher une période de stage..." class="max-w-xs">
          <template #prefix>
            <n-icon><SearchOutlined /></n-icon>
          </template>
        </n-input>
      </div>

      <n-data-table
        remote
        :columns="columns"
        :data="data"
        :loading="loading"
        :bordered="false"
        :pagination="pagination"
        :scroll-x="950"
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
        label-width="160"
        label-align="left"
        require-mark-placement="right-hanging"
      >
        <div class="space-y-4">
          <n-form-item label="Type de stage" path="typeStageId">
            <n-select
              v-model:value="formModel.typeStageId"
              :options="typeStageOptions"
              placeholder="Sélectionner un type de stage"
              filterable
            />
          </n-form-item>

          <n-form-item label="Date de début" path="dateDebut">
            <n-date-picker
              v-model:formatted-value="formModel.dateDebut"
              type="date"
              value-format="yyyy-MM-dd"
              format="dd/MM/yyyy"
              class="w-full"
              clearable
            />
          </n-form-item>

          <n-form-item label="Date de fin" path="dateFin">
            <n-date-picker
              v-model:formatted-value="formModel.dateFin"
              type="date"
              value-format="yyyy-MM-dd"
              format="dd/MM/yyyy"
              class="w-full"
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
