<script setup lang="ts">
import {
  NCard,
  NDataTable,
  NButton,
  NSpace,
  NInput,
  NIcon,
  NTooltip,
  NPopconfirm,
  NModal,
  NForm,
  NFormItem,
  NSelect,
  useMessage
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns, SelectOption } from 'naive-ui'
import { PlusOutlined, SearchOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { EncadreurService, type EncadreurDTO } from '@/api/EncadreurService'
import { EntrepriseService } from '@/api/EntrepriseService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)
const loading = ref(false)
const entrepriseLoading = ref(false)

const data = ref<EncadreurDTO[]>([])
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)
const searchQuery = ref('')
const selectedEntrepriseFilter = ref<number | null>(null)

const entrepriseOptions = ref<SelectOption[]>([])
const entrepriseNameMap = ref<Record<number, string>>({})

const formModel = reactive<EncadreurDTO>({
  nom: '',
  email: '',
  entrepriseId: 0,
})

const rules: FormRules = {
  nom: { required: true, message: 'Le nom est requis', trigger: 'blur' },
  email: { required: true, type: 'email', message: 'Email valide requis', trigger: ['blur', 'input'] },
  entrepriseId: { required: true, type: 'number', message: "L'entreprise est requise", trigger: 'change' }
}

const columns: DataTableColumns<EncadreurDTO> = [
  { title: 'Nom', key: 'nom', minWidth: 220 },
  { title: 'Email', key: 'email', minWidth: 220 },
  {
    title: 'Entreprise',
    key: 'entrepriseId',
    minWidth: 220,
    render (row) {
      if (!row.entrepriseId) return '—'
      return entrepriseNameMap.value[row.entrepriseId] ?? `#${row.entrepriseId}`
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 110,
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
              default: () => 'Voulez-vous vraiment supprimer cet encadreur ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
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

async function loadEntrepriseOptions (q = '') {
  entrepriseLoading.value = true
  try {
    const res = await EntrepriseService.rechercheParNom(q)
    const entreprises = res.data ?? []
    entrepriseOptions.value = entreprises.map((e) => ({
      label: e.nom,
      value: e.id as number
    }))
    for (const e of entreprises) {
      if (e.id) {
        entrepriseNameMap.value[e.id] = e.nom
      }
    }
  } catch {
    entrepriseOptions.value = []
  } finally {
    entrepriseLoading.value = false
  }
}

async function preloadEntrepriseNames () {
  try {
    const res = await EntrepriseService.getAll(0, 200)
    const entreprises = res.data?.content ?? res.data ?? []
    const nextMap: Record<number, string> = { ...entrepriseNameMap.value }
    for (const e of entreprises) {
      if (e.id) {
        nextMap[e.id] = e.nom
      }
    }
    entrepriseNameMap.value = nextMap
  } catch {
    // Non bloquant: la table affiche l'ID si le nom n'est pas disponible.
  }
}

async function fetchData () {
  loading.value = true
  try {
    const q = searchQuery.value.trim()
    const useSearch = q.length > 0 || selectedEntrepriseFilter.value !== null
    const res = useSearch
      ? await EncadreurService.search({
        entrepriseId: selectedEntrepriseFilter.value,
        q,
        page: page.value - 1,
        size: pageSize.value
      })
      : await EncadreurService.getAll(page.value - 1, pageSize.value)

    const payload = res.data
    data.value = payload?.content ?? payload ?? []
    itemCount.value = payload?.totalElements ?? payload?.page?.totalElements ?? data.value.length
  } catch {
    message.error('Erreur lors du chargement des encadreurs')
  } finally {
    loading.value = false
  }
}

function resetForm () {
  Object.assign(formModel, {
    id: undefined,
    nom: '',
    email: '',
    entrepriseId: 0,
  })
}

function handleAdd () {
  modalTitle.value = 'Ajouter un Encadreur'
  resetForm()
  showModal.value = true
  loadEntrepriseOptions('')
}

function handleEdit (row: EncadreurDTO) {
  modalTitle.value = 'Modifier un Encadreur'
  Object.assign(formModel, {
    id: row.id,
    nom: row.nom,
    email: row.email,
    entrepriseId: row.entrepriseId,
  })
  const entrepriseId = row.entrepriseId
  if (entrepriseId && entrepriseNameMap.value[entrepriseId]) {
    entrepriseOptions.value = [{
      label: entrepriseNameMap.value[entrepriseId],
      value: entrepriseId
    }]
  }
  showModal.value = true
}

async function handleSave () {
  await formRef.value?.validate()
  saving.value = true
  try {
    const payload: EncadreurDTO = {
      id: formModel.id,
      nom: formModel.nom.trim(),
      email: formModel.email.trim(),
      entrepriseId: formModel.entrepriseId
    }

    if (payload.id) {
      await EncadreurService.update(payload.id, payload)
      message.success('Encadreur modifié avec succès')
    } else {
      await EncadreurService.create(payload)
      message.success('Encadreur ajouté avec succès')
    }
    showModal.value = false
    await fetchData()
  } catch (err: any) {
    const apiMessage = err?.response?.data?.message
    message.error(apiMessage || "Erreur lors de l'enregistrement")
  } finally {
    saving.value = false
  }
}

async function handleDelete (id: number) {
  try {
    await EncadreurService.delete(id)
    message.success('Encadreur supprimé avec succès')
    await fetchData()
  } catch (err: any) {
    const apiMessage = err?.response?.data?.message
    message.error(apiMessage || 'Erreur lors de la suppression')
  }
}

function applyFilters () {
  page.value = 1
  fetchData()
}

function clearFilters () {
  searchQuery.value = ''
  selectedEntrepriseFilter.value = null
  page.value = 1
  fetchData()
}

onMounted(async () => {
  await preloadEntrepriseNames()
  await fetchData()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Encadreurs</h1>
      <NButton type="primary" @click="handleAdd">
        <template #icon>
          <NIcon><PlusOutlined /></NIcon>
        </template>
        Ajouter un encadreur
      </NButton>
    </div>

    <NCard>
      <div class="mb-4 flex flex-wrap items-center gap-3">
        <NInput
          v-model:value="searchQuery"
          placeholder="Rechercher par nom ou email..."
          class="max-w-xs"
          @keyup.enter="applyFilters"
        >
          <template #prefix>
            <NIcon><SearchOutlined /></NIcon>
          </template>
        </NInput>

        <NSelect
          v-model:value="selectedEntrepriseFilter"
          :options="entrepriseOptions"
          :loading="entrepriseLoading"
          filterable
          remote
          clearable
          class="min-w-[240px]"
          placeholder="Filtrer par entreprise"
          @focus="loadEntrepriseOptions('')"
          @search="loadEntrepriseOptions"
        />

        <NSpace>
          <NButton type="primary" @click="applyFilters">Filtrer</NButton>
          <NButton @click="clearFilters">Réinitialiser</NButton>
        </NSpace>
      </div>

      <NDataTable
        remote
        :columns="columns"
        :data="data"
        :loading="loading"
        :bordered="false"
        :pagination="pagination"
        :scroll-x="900"
      />
    </NCard>

    <NModal
      v-model:show="showModal"
      preset="card"
      :title="modalTitle"
      class="max-w-xl"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <NForm
        ref="formRef"
        :model="formModel"
        :rules="rules"
        label-placement="left"
        label-width="110"
        label-align="left"
        require-mark-placement="right-hanging"
      >
        <div class="space-y-4">
          <NFormItem label="Nom" path="nom">
            <NInput v-model:value="formModel.nom" placeholder="Nom de l'encadreur" />
          </NFormItem>

          <NFormItem label="Email" path="email">
            <NInput v-model:value="formModel.email" placeholder="email@entreprise.com" />
          </NFormItem>

          <NFormItem label="Entreprise" path="entrepriseId">
            <NSelect
              v-model:value="formModel.entrepriseId"
              :options="entrepriseOptions"
              :loading="entrepriseLoading"
              filterable
              remote
              clearable
              placeholder="Rechercher une entreprise..."
              @focus="loadEntrepriseOptions('')"
              @search="loadEntrepriseOptions"
            />
          </NFormItem>
        </div>
      </NForm>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="showModal = false">Annuler</NButton>
          <NButton type="primary" :loading="saving" @click="handleSave">
            Enregistrer
          </NButton>
        </NSpace>
      </template>
    </NModal>
  </div>
</template>
