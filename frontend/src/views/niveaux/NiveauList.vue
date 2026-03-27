<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, NSelect, useMessage
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { NiveauService, type NiveauDTO } from '@/api/NiveauService'
import { TypeStageService } from '@/api/TypeStageService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<NiveauDTO>({
  libelle: '',
  typeStageId: null,
  typeStageLibelle: null
})

const rules: FormRules = {
  libelle: { required: true, message: 'Le libellé est requis', trigger: 'blur' }
}

const columns: DataTableColumns<NiveauDTO> = [
  { title: 'Libellé', key: 'libelle', minWidth: 300 },
  {
    title: 'Type de stage',
    key: 'typeStageLibelle',
    minWidth: 220,
    render (row) {
      return row.typeStageLibelle || row.typeStageId || '-'
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
              default: () => 'Voulez-vous vraiment supprimer ce niveau ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<NiveauDTO[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)
const typeStageOptions = ref<{ label: string, value: number }[]>([])

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
    const res = await NiveauService.getAll(page.value - 1, pageSize.value)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch {
    message.error('Erreur lors du chargement des données')
  } finally {
    loading.value = false
  }
}

const fetchTypeStages = async () => {
  try {
    const res = await TypeStageService.getAll(0, 200)
    typeStageOptions.value = (res.data.content || []).map((typeStage: { id: number, libelle: string }) => ({
      label: typeStage.libelle,
      value: typeStage.id
    }))
  } catch {
    message.error('Erreur lors du chargement des types de stage')
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter un Niveau'
  Object.assign(formModel, {
    id: undefined,
    libelle: '',
    typeStageId: null,
    typeStageLibelle: null
  })
  showModal.value = true
}

const handleEdit = (row: NiveauDTO) => {
  modalTitle.value = 'Modifier un Niveau'
  Object.assign(formModel, row)
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await NiveauService.update(formModel.id, formModel)
          message.success('Niveau modifié avec succès')
        } else {
          await NiveauService.create(formModel)
          message.success('Niveau ajouté avec succès')
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
    await NiveauService.delete(id)
    message.success('Niveau supprimé avec succès')
    fetchData()
  } catch {
    message.error('Erreur lors de la suppression')
  }
}

onMounted(fetchData)
onMounted(fetchTypeStages)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Niveaux</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter un niveau
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <n-input placeholder="Rechercher un niveau..." class="max-w-xs">
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
        :scroll-x="800"
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
          <n-form-item label="Libellé" path="libelle">
            <n-input v-model:value="formModel.libelle" placeholder="Ex: Licence 3" />
          </n-form-item>
          <n-form-item label="Type de stage" path="typeStageId">
            <n-select
              v-model:value="formModel.typeStageId"
              :options="typeStageOptions"
              placeholder="Sélectionner un type de stage"
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