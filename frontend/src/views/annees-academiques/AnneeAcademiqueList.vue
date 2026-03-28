<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, NTag, useMessage
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2, Power } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { AnneeAcademiqueService, type AnneeAcademiqueDTO } from '@/api/AnneeAcademiqueService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<AnneeAcademiqueDTO>({
  libelle: ''
})

const rules: FormRules = {
  libelle: { required: true, message: 'Le libellé est requis', trigger: 'blur' }
}

const columns: DataTableColumns<AnneeAcademiqueDTO> = [
  { title: 'Libellé', key: 'libelle', minWidth: 120 },
  {
    title: 'Statut',
    key: 'actif',
    width: 150,
    render (row) {
      return h(NTag, { type: row.actif ? 'success' : 'default', size: 'small' }, { default: () => row.actif ? 'Actif' : 'Inactif' })
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 150,
    fixed: 'right',
    render (row) {
      return h(NSpace, null, {
        default: () => [
          ...(!row.actif ? [
            h(NTooltip, null, {
              trigger: () => h(NButton, {
                size: 'small',
                quaternary: true,
                type: 'success',
                circle: true,
                onClick: () => handleActivate(row.id!)
              }, { default: () => h(NIcon, null, { default: () => h(Power) }) }),
              default: () => 'Activer'
            })
          ] : []),
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
              default: () => 'Voulez-vous vraiment supprimer cette année académique ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<AnneeAcademiqueDTO[]>([])
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
    const res = await AnneeAcademiqueService.getAll(page.value - 1, pageSize.value)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch {
    message.error('Erreur lors du chargement des données')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter une Année Académique'
  Object.assign(formModel, {
    id: undefined,
    libelle: ''
  })
  showModal.value = true
}

const handleEdit = (row: AnneeAcademiqueDTO) => {
  modalTitle.value = 'Modifier une Année Académique'
  Object.assign(formModel, row)
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await AnneeAcademiqueService.update(formModel.id, formModel)
          message.success('Année académique modifiée avec succès')
        } else {
          await AnneeAcademiqueService.create(formModel)
          message.success('Année académique ajoutée avec succès')
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
    await AnneeAcademiqueService.delete(id)
    message.success('Année académique supprimée avec succès')
    fetchData()
  } catch {
    message.error('Erreur lors de la suppression')
  }
}

const handleActivate = async (id: number) => {
  try {
    await AnneeAcademiqueService.activate(id)
    message.success('Année académique activée avec succès')
    fetchData()
  } catch {
    message.error('Erreur lors de l\'activation')
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Années Académiques</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter une année académique
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <n-input placeholder="Rechercher une année académique..." class="max-w-xs">
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
        label-width="140"
        label-align="left"
        require-mark-placement="right-hanging"
      >
        <div class="space-y-4">
          <n-form-item label="Libellé" path="libelle">
            <n-input v-model:value="formModel.libelle" placeholder="Ex: 2025-2026" />
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