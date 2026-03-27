<script setup lang="ts">
import { 
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm, 
  NModal, NForm, NFormItem, useMessage 
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { EtudiantService, type EtudiantDTO } from '@/api/EtudiantService'

const message = useMessage()
const router = useRouter()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<EtudiantDTO>({
  matricule: '',
  nom: '',
  email: '',
  telephone: '',
  prenom: '',
  grade: '',
  dateNaissance: '',
  lieuNaissance: ''
})

const rules: FormRules = {
  matricule: { required: true, message: 'Le matricule est requis', trigger: 'blur' },
  nom: { required: true, message: 'Le nom est requis', trigger: 'blur' },
  email: { required: true, type: 'email', message: 'Email valide requis', trigger: 'blur' }
}

const columns: DataTableColumns<EtudiantDTO> = [
  { title: 'Matricule', key: 'matricule', minWidth: 150 },
  { title: 'Nom', key: 'nom', minWidth: 350 },
  { title: 'Email', key: 'email', minWidth: 200 },
  { title: 'Téléphone', key: 'telephone', minWidth: 150 },
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
              default: () => 'Voulez-vous vraiment supprimer cet étudiant ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<EtudiantDTO[]>([])
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
    const res = await EtudiantService.getAll(page.value - 1, pageSize.value)
    data.value = res.data.content || []
    // Support both flattened and wrapped response structures
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch (err) {
    message.error('Erreur lors du chargement des données')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter un Étudiant'
  Object.assign(formModel, {
    id: undefined,
    matricule: '',
    nom: '',
    email: '',
    telephone: '',
    prenom: '',
    grade: '',
    dateNaissance: '',
    lieuNaissance: ''
  })
  showModal.value = true
}

const handleEdit = (row: EtudiantDTO) => {
  modalTitle.value = 'Modifier un Étudiant'
  Object.assign(formModel, row)
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await EtudiantService.update(formModel.id, formModel)
          message.success('Étudiant modifié avec succès')
        } else {
          await EtudiantService.create(formModel)
          message.success('Étudiant ajouté avec succès')
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
    await EtudiantService.delete(id)
    message.success('Étudiant supprimé avec succès')
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
      <h1 class="text-2xl font-bold">Gestion des Étudiants</h1>
      <n-space>
        <n-button @click="router.push({ name: 'etudiants-import' })">
          Importer des étudiants
        </n-button>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon><PlusOutlined /></n-icon>
          </template>
          Ajouter un étudiant
        </n-button>
      </n-space>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <n-input placeholder="Rechercher un étudiant..." class="max-w-xs">
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
          <n-form-item label="Matricule" path="matricule">
            <n-input v-model:value="formModel.matricule" placeholder="Ex: 23ENSPM0556" />
          </n-form-item>
          <n-form-item label="Nom Complet" path="nom">
            <n-input v-model:value="formModel.nom" placeholder="Ex: Jean Paul" />
          </n-form-item>
          <n-form-item label="Email" path="email">
            <n-input v-model:value="formModel.email" placeholder="Ex: jean@example.com" />
          </n-form-item>
          <n-form-item label="Téléphone" path="telephone">
            <n-input v-model:value="formModel.telephone" placeholder="Ex: 692806492" />
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
