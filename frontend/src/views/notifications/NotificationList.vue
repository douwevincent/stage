<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NInput, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, NSelect, NInputNumber, useMessage, NSwitch, NDivider
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns } from 'naive-ui'
import { PlusOutlined } from '@vicons/antd'
import { Edit, Trash2 } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive } from 'vue'
import { NotificationService, type NotificationDTO, NotificationReferenceDateType } from '@/api/NotificationService'
import { TypeStageService, type TypeStageDTO } from '@/api/TypeStageService'

interface SelectOption {
  label: string
  value: string | number
}

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)
const filterTypeStageId = ref<number | null>(null)

const formModel = reactive<NotificationDTO>({
  id: undefined,
  typeStageId: null,
  referenceDateType: null,
  offsetDays: null,
  objet: '',
  contenuTemplate: '',
  actif: true
})

const typeStageOptions = ref<SelectOption[]>([])
const referenceDateTypeOptions = ref<SelectOption[]>([
  { label: 'Début de période', value: NotificationReferenceDateType.DEBUT_PERIODE },
  { label: 'Fin de période', value: NotificationReferenceDateType.FIN_PERIODE },
  { label: 'Jours avant fin de stage', value: NotificationReferenceDateType.JOURS_AVANT_FIN_STAGE },
  { label: 'Jours après fin de stage', value: NotificationReferenceDateType.JOURS_APRES_FIN_STAGE }
])

const typeStageMap = computed(() => {
  const entries = typeStageOptions.value.map((option) => [option.value, option.label] as const)
  return new Map<number | string, string>(entries)
})

const referenceDateTypeMap = new Map<string, string>([
  [NotificationReferenceDateType.DEBUT_PERIODE, 'Début de période'],
  [NotificationReferenceDateType.FIN_PERIODE, 'Fin de période'],
  [NotificationReferenceDateType.JOURS_AVANT_FIN_STAGE, 'Jours avant fin de stage'],
  [NotificationReferenceDateType.JOURS_APRES_FIN_STAGE, 'Jours après fin de stage']
])

const rules: FormRules = {
  typeStageId: { required: true, type: 'number', message: 'Le type de stage est requis', trigger: ['blur', 'change'] },
  referenceDateType: { required: true, type: 'string', message: 'Le type de référence est requis', trigger: ['blur', 'change'] },
  offsetDays: {
    required: true,
    type: 'number',
    message: 'Le décalage en jours est requis',
    trigger: ['blur', 'change']
  },
  objet: {
    required: true,
    validator: () => {
      if (!formModel.objet || formModel.objet.trim().length === 0) {
        return new Error('L\'objet est requis')
      }
      return true
    },
    trigger: ['blur', 'change']
  },
  contenuTemplate: {
    required: true,
    validator: () => {
      if (!formModel.contenuTemplate || formModel.contenuTemplate.trim().length === 0) {
        return new Error('Le contenu du template est requis')
      }
      return true
    },
    trigger: ['blur', 'change']
  }
}

const columns: DataTableColumns<NotificationDTO> = [
  {
    title: 'Type de stage',
    key: 'typeStageId',
    minWidth: 150,
    render: (row) => typeStageMap.value.get(row.typeStageId ?? -1) ?? `#${row.typeStageId ?? '-'}`
  },
  {
    title: 'Type de référence',
    key: 'referenceDateType',
    minWidth: 180,
    render: (row) => referenceDateTypeMap.get(row.referenceDateType ?? '') ?? 'N/A'
  },
  {
    title: 'Décalage (jours)',
    key: 'offsetDays',
    minWidth: 120,
    align: 'center',
    render: (row) => row.offsetDays
  },
  {
    title: 'Objet',
    key: 'objet',
    minWidth: 200,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: 'Actif',
    key: 'actif',
    minWidth: 80,
    align: 'center',
    render: (row) => row.actif ? '✓' : '✗'
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
              default: () => 'Voulez-vous vraiment supprimer cette notification ?'
            }),
            default: () => 'Supprimer'
          })
        ]
      })
    }
  }
]

const data = ref<NotificationDTO[]>([])
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
    const typeRes = await TypeStageService.getAll(0, 200)
    typeStageOptions.value = (typeRes.data.content || []).map((item: TypeStageDTO) => ({
      label: item.libelle,
      value: item.id!
    }))
  } catch {
    message.error('Erreur lors du chargement des données de référence')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await NotificationService.getAll(page.value - 1, pageSize.value, filterTypeStageId.value ?? undefined)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || res.data.page?.totalElements || 0
  } catch (error: any) {
    const errorMsg = error.response?.data?.error || 'Erreur lors du chargement des données'
    message.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(formModel, {
    id: undefined,
    typeStageId: null,
    referenceDateType: null,
    offsetDays: null,
    objet: '',
    contenuTemplate: '',
    actif: true
  })
}

const handleAdd = () => {
  modalTitle.value = 'Ajouter une Notification'
  resetForm()
  showModal.value = true
}

const handleEdit = (row: NotificationDTO) => {
  modalTitle.value = 'Modifier une Notification'
  Object.assign(formModel, {
    id: row.id,
    typeStageId: row.typeStageId,
    referenceDateType: row.referenceDateType,
    offsetDays: row.offsetDays,
    objet: row.objet,
    contenuTemplate: row.contenuTemplate,
    actif: row.actif
  })
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      saving.value = true
      try {
        const payload: NotificationDTO = {
          id: formModel.id,
          typeStageId: formModel.typeStageId,
          referenceDateType: formModel.referenceDateType,
          offsetDays: formModel.offsetDays,
          objet: formModel.objet,
          contenuTemplate: formModel.contenuTemplate,
          actif: formModel.actif
        }

        if (formModel.id) {
          await NotificationService.update(formModel.id, payload)
          message.success('Notification modifiée avec succès')
        } else {
          await NotificationService.create(payload)
          message.success('Notification ajoutée avec succès')
        }
        showModal.value = false
        fetchData()
      } catch (error: any) {
        const errorMsg = error.response?.data?.error || 'Erreur lors de l\'enregistrement'
        message.error(errorMsg)
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = async (id: number) => {
  try {
    await NotificationService.delete(id)
    message.success('Notification supprimée avec succès')
    fetchData()
  } catch (error: any) {
    const errorMsg = error.response?.data?.error || 'Erreur lors de la suppression'
    message.error(errorMsg)
  }
}

const handleFilterChange = () => {
  page.value = 1
  fetchData()
}

onMounted(async () => {
  await fetchSelectData()
  await fetchData()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Notifications</h1>
      <n-button type="primary" @click="handleAdd">
        <template #icon>
          <n-icon><PlusOutlined /></n-icon>
        </template>
        Ajouter une notification
      </n-button>
    </div>

    <n-card>
      <div class="mb-4 flex items-center space-x-4">
        <n-select
          v-model:value="filterTypeStageId"
          :options="[
            { label: 'Tous les types de stage', value: null as any },
            ...typeStageOptions
          ]"
          placeholder="Filtrer par type de stage"
          class="max-w-xs"
          clearable
          @update:value="handleFilterChange"
        />
      </div>

      <n-data-table
        remote
        :columns="columns"
        :data="data"
        :loading="loading"
        :bordered="false"
        :pagination="pagination"
        :scroll-x="1100"
      />
    </n-card>

    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="modalTitle"
      class="max-w-2xl"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-form
        ref="formRef"
        :model="formModel"
        :rules="rules"
        label-placement="left"
        label-width="200"
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

          <n-form-item label="Type de référence" path="referenceDateType">
            <n-select
              v-model:value="formModel.referenceDateType"
              :options="referenceDateTypeOptions"
              placeholder="Sélectionner un type de référence"
              filterable
            />
          </n-form-item>

          <n-form-item label="Décalage en jours" path="offsetDays">
            <n-input-number
              v-model:value="formModel.offsetDays"
              placeholder="Ex: -15, 0, +5"
              :min="-365"
              :max="365"
              :step="1"
              class="w-full"
            />
          </n-form-item>

          <n-divider style="margin: 12px 0" />

          <n-form-item label="Objet" path="objet">
            <n-input
              v-model:value="formModel.objet"
              placeholder="Sujet de l'email de notification"
              type="text"
            />
          </n-form-item>

          <n-form-item label="Contenu du template" path="contenuTemplate">
            <n-input
              v-model:value="formModel.contenuTemplate"
              placeholder="Contenu du template d'email"
              type="textarea"
              :rows="6"
            />
          </n-form-item>

          <n-form-item label="Actif">
            <n-switch v-model:value="formModel.actif" />
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
