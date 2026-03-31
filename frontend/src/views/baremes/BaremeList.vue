<script setup lang="ts">
import { 
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm, 
  NModal, NForm, NFormItem, useMessage, NCheckbox, NTag 
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { BaremeService, type BaremeDTO } from '@/api/BaremeService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<BaremeDTO>({
  code: '',
  libelle: '',
  actif: true,
  parDefaut: false
})

const rules: FormRules = {
  code: { required: true, message: 'Le code est requis', trigger: 'blur' },
  libelle: { required: true, message: 'Le libellé est requis', trigger: 'blur' }
}

const columns: DataTableColumns<BaremeDTO> = [
  { title: 'Code', key: 'code', minWidth: 120 },
  { title: 'Libellé', key: 'libelle', minWidth: 300 },
  {
    title: 'Actif',
    key: 'actif',
    width: 110,
    render (row) {
      return h(NTag, { type: row.actif ? 'success' : 'default', size: 'small' }, {
        default: () => row.actif ? 'Actif' : 'Inactif'
      })
    }
  },
  {
    title: 'Par défaut',
    key: 'parDefaut',
    width: 130,
    render (row) {
      return h(NTag, { type: row.parDefaut ? 'warning' : 'default', size: 'small' }, {
        default: () => row.parDefaut ? 'Par défaut' : 'Standard'
      })
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
              default: () => 'Voulez-vous vraiment supprimer ce barème ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<BaremeDTO[]>([])
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
    const res = await BaremeService.getAll(page.value - 1, pageSize.value)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch (err) {
    message.error('Erreur lors du chargement des barèmes')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter un Barème'
  Object.assign(formModel, {
    id: undefined,
    code: '',
    libelle: '',
    actif: true,
    parDefaut: false
  })
  showModal.value = true
}

const handleEdit = (row: BaremeDTO) => {
  modalTitle.value = 'Modifier un Barème'
  Object.assign(formModel, row)
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await BaremeService.update(formModel.id, formModel)
          message.success('Barème modifié avec succès')
        } else {
          await BaremeService.create(formModel)
          message.success('Barème ajouté avec succès')
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
    await BaremeService.delete(id)
    message.success('Barème supprimé avec succès')
    fetchData()
  } catch (err) {
    message.error('Erreur lors de la suppression')
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Barèmes</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter un barème
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <n-input placeholder="Rechercher un barème..." class="max-w-xs">
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
        label-width="100"
        label-align="left"
        require-mark-placement="right-hanging"
      >
        <div class="space-y-4">
          <n-form-item label="Code" path="code">
            <n-input v-model:value="formModel.code" placeholder="Ex: BAREME_OUVRIER" />
          </n-form-item>
          <n-form-item label="Libellé" path="libelle">
            <n-input v-model:value="formModel.libelle" placeholder="Ex: Barème Stage Ouvrier" />
          </n-form-item>
          <n-form-item label="Actif" path="actif">
            <n-checkbox v-model:checked="formModel.actif" />
          </n-form-item>
          <n-form-item label="Par défaut" path="parDefaut">
            <n-checkbox v-model:checked="formModel.parDefaut" />
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
