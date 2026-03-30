<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, useMessage, NSelect, NInputNumber
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { BaremeCritereService, type BaremeCritereDTO } from '@/api/BaremeService'
import { BaremeService, type BaremeDTO } from '@/api/BaremeService'
import { CritereService, type CritereDTO } from '@/api/CritereService'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)

const formModel = reactive<BaremeCritereDTO>({
  baremeId: null,
  critereId: null,
  coefficient: null
})

const baremeOptions = ref<{ label: string, value: number }[]>([])
const critereOptions = ref<{ label: string, value: number }[]>([])
const selectedBaremeId = ref<number | null>(null)

const rules: FormRules = {
  baremeId: {
    required: true,
    type: 'number',
    message: 'Le barème est requis',
    trigger: 'change'
  },
  critereId: {
    required: true,
    type: 'number',
    message: 'Le critère est requis',
    trigger: 'change'
  },
  coefficient: [
    {
      required: true,
      type: 'number',
      message: 'Le coefficient est requis',
      trigger: 'change'
    },
    {
      validator: (_, value) => {
        if (value !== null && (value < 0 || value > 20)) {
          return new Error('Le coefficient doit être entre 0 et 20')
        }
        return true
      },
      trigger: 'blur'
    }
  ]
}

const columns: DataTableColumns<BaremeCritereDTO> = [
  {
    title: 'Barème',
    key: 'baremeCode',
    minWidth: 200,
    render (row) {
      return `${row.baremeCode || ''} (${row.baremeId || '-'})`
    }
  },
  {
    title: 'Critère',
    key: 'critereLibelle',
    minWidth: 250,
    render (row) {
      return row.critereLibelle || row.critereId || '-'
    }
  },
  {
    title: 'Coefficient',
    key: 'coefficient',
    width: 120,
    render (row) {
      return row.coefficient ? `${row.coefficient}/20` : '-'
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
              default: () => 'Voulez-vous vraiment supprimer cette association ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<BaremeCritereDTO[]>([])
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

const loadOptions = async () => {
  try {
    const [baremeRes, critereRes] = await Promise.all([
      BaremeService.getAll(0, 1000),
      CritereService.getAll(0, 1000)
    ])
    
    baremeOptions.value = (baremeRes.data.content || []).map((b: BaremeDTO) => ({
      label: b.libelle || b.code || `Barème ${b.id}`,
      value: b.id!
    }))

    critereOptions.value = (critereRes.data.content || []).map((c: CritereDTO) => ({
      label: c.libelle || `Critère ${c.id}`,
      value: c.id!
    }))
  } catch (err) {
    message.error('Erreur lors du chargement des options')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await BaremeCritereService.getAll(page.value - 1, pageSize.value, selectedBaremeId.value)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch (err) {
    message.error('Erreur lors du chargement des associations')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter une association Barème-Critère'
  Object.assign(formModel, {
    id: undefined,
    baremeId: null,
    critereId: null,
    coefficient: null
  })
  showModal.value = true
}

const handleEdit = (row: BaremeCritereDTO) => {
  modalTitle.value = 'Modifier l\'association Barème-Critère'
  Object.assign(formModel, row)
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        if (formModel.id) {
          await BaremeCritereService.update(formModel.id, formModel)
          message.success('Association modifiée avec succès')
        } else {
          await BaremeCritereService.create(formModel)
          message.success('Association ajoutée avec succès')
        }
        showModal.value = false
        fetchData()
      } catch (err: any) {
        if (err.response?.status === 400) {
          message.error('Le coefficient doit être entre 0 et 20')
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
    await BaremeCritereService.delete(id)
    message.success('Association supprimée avec succès')
    fetchData()
  } catch (err) {
    message.error('Erreur lors de la suppression')
  }
}

const handleBaremeFilterChange = () => {
  page.value = 1
  fetchData()
}

onMounted(async () => {
  await loadOptions()
  fetchData()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Associations Barème-Critères</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter une association
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <div class="flex items-center gap-2">
          <span>Filtrer par Barème:</span>
          <n-select
            v-model:value="selectedBaremeId"
            :options="[
              { label: 'Tous les barèmes', value: null, type: 'group' } as any,
              ...baremeOptions
            ]"
            clearable
            class="w-48"
            @update:value="handleBaremeFilterChange"
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
        label-width="100"
        label-align="left"
        require-mark-placement="right-hanging"
      >
        <div class="space-y-4">
          <n-form-item label="Barème" path="baremeId">
            <n-select
              v-model:value="formModel.baremeId"
              :options="baremeOptions"
              placeholder="Sélectionnez un barème"
              filterable
            />
          </n-form-item>
          <n-form-item label="Critère" path="critereId">
            <n-select
              v-model:value="formModel.critereId"
              :options="critereOptions"
              placeholder="Sélectionnez un critère"
              filterable
            />
          </n-form-item>
          <n-form-item label="Coefficient" path="coefficient">
            <n-input-number
              v-model:value="formModel.coefficient"
              placeholder="Entre 0 et 20"
              :min="0"
              :max="20"
              :step="0.5"
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
