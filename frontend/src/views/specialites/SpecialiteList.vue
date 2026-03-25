<script setup lang="ts">
import { 
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm, 
  NModal, NForm, NFormItem, useMessage, NSelect
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive, watch } from 'vue'
import { SpecialiteService, type SpecialiteDTO } from '@/api/SpecialiteService'
import { DepartementService } from '@/api/DepartementService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<SpecialiteDTO>({
  code: '',
  intitule: '',
  departementId: null
})

const departementOptions = ref<{ label: string, value: number }[]>([])
const selectedDepartementId = ref<number | null>(null)

const rules: FormRules = {
  code: { required: true, message: 'Le code est requis', trigger: 'blur' },
  intitule: { required: true, message: 'L\'intitulé est requis', trigger: 'blur' },
  departementId: { required: true, type: 'number', message: 'Le département est requis', trigger: 'change' }
}

const columns: DataTableColumns<SpecialiteDTO> = [
  { title: 'Code', key: 'code', minWidth: 100 },
  { title: 'Intitulé', key: 'intitule', minWidth: 250 },
  { 
    title: 'Département', 
    key: 'departementId', 
    minWidth: 150,
    render (row) {
      const dept = departementOptions.value.find(d => d.value === row.departementId)
      return dept ? dept.label : row.departementId
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
              default: () => 'Voulez-vous vraiment supprimer cette spécialité ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<SpecialiteDTO[]>([])
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

const fetchData = async () => {
  loading.value = true
  try {
    const res = await SpecialiteService.getAll(page.value - 1, pageSize.value, selectedDepartementId.value)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch (err) {
    message.error('Erreur lors du chargement des données')
  } finally {
    loading.value = false
  }
}

watch(selectedDepartementId, () => {
  page.value = 1
  fetchData()
})

const fetchDepartements = async () => {
  try {
    const res = await DepartementService.getAll(0, 100)
    const depts = res.data.content || []
    departementOptions.value = depts.map((d: any) => ({
      label: d.code,
      value: d.id
    }))
  } catch (err) {
    console.error('Erreur lors du chargement des départements')
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter une Spécialité'
  Object.assign(formModel, {
    id: undefined,
    code: '',
    intitule: '',
    departementId: null
  })
  showModal.value = true
}

const handleEdit = (row: SpecialiteDTO) => {
  modalTitle.value = 'Modifier une Spécialité'
  Object.assign(formModel, row)
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await SpecialiteService.update(formModel.id, formModel)
          message.success('Spécialité modifiée avec succès')
        } else {
          await SpecialiteService.create(formModel)
          message.success('Spécialité ajoutée avec succès')
        }
        showModal.value = false
        fetchData()
      } catch (err) {
        message.error('Erreur lors de l\'enregistrement')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    await SpecialiteService.delete(id)
    message.success('Spécialité supprimée avec succès')
    fetchData()
  } catch (err) {
    message.error('Erreur lors de la suppression')
  }
}

onMounted(() => {
  fetchData()
  fetchDepartements()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Spécialités</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter une spécialité
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <div class="w-full max-w-xs">
          <n-select
            v-model:value="selectedDepartementId"
            :options="departementOptions"
            placeholder="Filtrer par département"
            clearable
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
          <n-form-item label="Code" path="code">
            <n-input v-model:value="formModel.code" placeholder="Ex: INFO" />
          </n-form-item>
          <n-form-item label="Intitulé" path="intitule">
            <n-input v-model:value="formModel.intitule" placeholder="Ex: Informatique" />
          </n-form-item>
          <n-form-item label="Département" path="departementId">
            <n-select
              v-model:value="formModel.departementId"
              :options="departementOptions"
              placeholder="Sélectionner un département"
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
