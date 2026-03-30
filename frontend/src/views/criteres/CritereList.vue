<script setup lang="ts">
import { 
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm, 
  NModal, NForm, NFormItem, useMessage 
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { CritereService, type CritereDTO } from '@/api/CritereService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<CritereDTO>({
  libelle: '',
  categorie: ''
})

const rules: FormRules = {
  libelle: { required: true, message: 'Le libellé est requis', trigger: 'blur' },
  categorie: { required: true, message: 'La catégorie est requise', trigger: 'blur' }
}

const columns: DataTableColumns<CritereDTO> = [
  { title: 'Libellé', key: 'libelle', minWidth: 250 },
  { title: 'Catégorie', key: 'categorie', minWidth: 150 },
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
              default: () => 'Voulez-vous vraiment supprimer ce critère ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<CritereDTO[]>([])
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
    const res = await CritereService.getAll(page.value - 1, pageSize.value)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch (err) {
    message.error('Erreur lors du chargement des critères')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter un Critère'
  Object.assign(formModel, {
    id: undefined,
    libelle: '',
    categorie: ''
  })
  showModal.value = true
}

const handleEdit = (row: CritereDTO) => {
  modalTitle.value = 'Modifier un Critère'
  Object.assign(formModel, row)
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await CritereService.update(formModel.id, formModel)
          message.success('Critère modifié avec succès')
        } else {
          await CritereService.create(formModel)
          message.success('Critère ajouté avec succès')
        }
        showModal.value = false
        fetchData()
      } catch (err: any) {
        // Gérer les erreurs spécifiques
        if (err.response?.status === 409) {
          message.error('Impossible de supprimer ce critère car il est utilisé dans un ou plusieurs barèmes')
        } else {
          message.error('Erreur lors de l\'enregistrement')
        }
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    await CritereService.delete(id)
    message.success('Critère supprimé avec succès')
    fetchData()
  } catch (err: any) {
    // Gérer l'erreur 409 (CONFLICT) quand le critère est utilisé dans un barème
    if (err.response?.status === 409) {
      message.error('Impossible de supprimer ce critère car il est utilisé dans un ou plusieurs barèmes')
    } else {
      message.error('Erreur lors de la suppression')
    }
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Critères</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter un critère
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <n-input placeholder="Rechercher un critère..." class="max-w-xs">
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
          <n-form-item label="Libellé" path="libelle">
            <n-input v-model:value="formModel.libelle" placeholder="Ex: Assiduité et ponctualité" />
          </n-form-item>
          <n-form-item label="Catégorie" path="categorie">
            <n-input v-model:value="formModel.categorie" placeholder="Ex: Comportement, Compétence, Attitude" />
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
