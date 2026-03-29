<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NIcon, NTooltip,
  NModal, NForm, NFormItem, NInput, useMessage
} from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { Edit } from 'lucide-vue-next'
import { computed, h, onMounted, reactive, ref } from 'vue'
import { AppSettingService, type AppSettingDTO } from '@/api/AppSettingService'

const message = useMessage()
const loading = ref(false)
const saving = ref(false)
const showModal = ref(false)
const formRef = ref<FormInst | null>(null)
const selectedSetting = ref<AppSettingDTO | null>(null)

const data = ref<AppSettingDTO[]>([])

const formModel = reactive({
  valeur: ''
})

const rules: FormRules = {
  valeur: {
    required: true,
    message: 'La valeur est obligatoire',
    trigger: ['blur', 'change']
  }
}

const isLongText = computed(() => selectedSetting.value?.type === 'TEXT')
const isSecret = computed(() => selectedSetting.value?.secret === true)

const columns: DataTableColumns<AppSettingDTO> = [
  { title: 'Clé', key: 'cle', width: 260 },
  { title: 'Description', key: 'description', minWidth: 280 },
  {
    title: 'Valeur',
    key: 'valeur',
    minWidth: 320,
    render (row) {
      if (row.secret) {
        return row.valeur
      }
      if (row.type === 'TEXT' && row.valeur.length > 80) {
        return `${row.valeur.slice(0, 80)}...`
      }
      return row.valeur
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
              disabled: !row.modifiable,
              onClick: () => handleEdit(row)
            }, {
              default: () => h(NIcon, null, { default: () => h(Edit) })
            }),
            default: () => row.modifiable ? 'Modifier' : 'Non modifiable'
          })
        ]
      })
    }
  }
]

const fetchData = async () => {
  loading.value = true
  try {
    const res = await AppSettingService.getAll()
    data.value = res.data || []
  } catch {
    message.error('Erreur lors du chargement des paramètres')
  } finally {
    loading.value = false
  }
}

const handleEdit = (row: AppSettingDTO) => {
  selectedSetting.value = row
  formModel.valeur = row.secret ? '' : row.valeur
  showModal.value = true
}

const handleSave = async () => {
  formRef.value?.validate(async (errors) => {
    if (!errors && selectedSetting.value) {
      saving.value = true
      try {
        await AppSettingService.update(selectedSetting.value.cle, formModel.valeur)
        showModal.value = false
        message.success('Paramètre mis à jour avec succès')
        await fetchData()
      } catch {
        message.error('Erreur lors de la mise à jour du paramètre')
      } finally {
        saving.value = false
      }
    }
  })
}

onMounted(fetchData)
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Paramètres de l'application</h1>
    </div>

    <n-card>
      <n-data-table
        :columns="columns"
        :data="data"
        :loading="loading"
        :bordered="false"
        :scroll-x="1100"
      />
    </n-card>

    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="selectedSetting ? `Modifier ${selectedSetting.cle}` : 'Modifier paramètre'"
      class="max-w-2xl"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-form
        ref="formRef"
        :model="formModel"
        :rules="rules"
        label-placement="left"
        label-width="140"
        label-align="left"
      >
        <div class="space-y-4">
          <n-form-item label="Description">
            <n-input :value="selectedSetting?.description || ''" readonly />
          </n-form-item>

          <n-form-item label="Nouvelle valeur" path="valeur">
            <n-input
              v-if="!isLongText"
              v-model:value="formModel.valeur"
              :type="isSecret ? 'password' : 'text'"
              :placeholder="isSecret ? 'Saisir un nouveau mot de passe SMTP' : 'Saisir la valeur'"
              show-password-on="click"
            />
            <n-input
              v-else
              v-model:value="formModel.valeur"
              type="textarea"
              :autosize="{ minRows: 6, maxRows: 12 }"
              placeholder="Saisir le template"
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