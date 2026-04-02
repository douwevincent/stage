<script setup lang="ts">
import {
  NButton,
  NCard,
  NDataTable,
  NForm,
  NFormItem,
  NInput,
  NModal,
  NPopconfirm,
  NSpace,
  NSelect,
  NTag,
  useMessage
} from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
import { UserAccountService, type UserCreateRequestDTO } from '@/api/UserAccountService'
import type { UserAccountDTO } from '@/api/AuthService'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const message = useMessage()

const loading = ref(false)
const data = ref<UserAccountDTO[]>([])
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)

const showCreateModal = ref(false)
const createLoading = ref(false)
const resetPasswordLoading = ref<number | null>(null)
const deleteLoading = ref<number | null>(null)
const formRef = ref<FormInst | null>(null)

const formModel = reactive<UserCreateRequestDTO>({
  email: '',
  password: '',
  role: 'OPERATEUR',
  active: true
})

const roleOptions = [
  { label: 'OPERATEUR', value: 'OPERATEUR' },
  { label: 'SCOLARITE', value: 'SCOLARITE' },
  { label: 'ADMIN', value: 'ADMIN' },
  { label: 'SUPER_ADMIN', value: 'SUPER_ADMIN' }
]

const rules: FormRules = {
  email: { required: true, message: 'Email requis', trigger: 'blur' },
  password: { required: true, message: 'Mot de passe requis', trigger: 'blur' },
  role: { required: true, message: 'Rôle requis', trigger: 'change' }
}

const columns: DataTableColumns<UserAccountDTO> = [
  { title: 'Email', key: 'email' },
  {
    title: 'Rôle',
    key: 'role',
    render(row) {
      return h(NTag, { type: row.role === 'SUPER_ADMIN' ? 'error' : 'info' }, { default: () => row.role })
    }
  },
  {
    title: 'Statut',
    key: 'active',
    render(row) {
      return h(NTag, { type: row.active ? 'success' : 'warning' }, { default: () => (row.active ? 'Actif' : 'Inactif') })
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 340,
    render(row) {
      return h(NSpace, null, {
        default: () => [
          h(
            NButton,
            {
              size: 'small',
              type: row.active ? 'warning' : 'success',
              onClick: () => (row.active ? handleDeactivate(row.id) : handleActivate(row.id))
            },
            { default: () => (row.active ? 'Désactiver' : 'Activer') }
          ),
          h(
            NButton,
            {
              size: 'small',
              type: 'default',
              loading: resetPasswordLoading.value === row.id,
              onClick: () => handleResetPassword(row.id)
            },
            { default: () => 'Reset MDP' }
          ),
          h(
            NPopconfirm,
            { onPositiveClick: () => handleDelete(row.id) },
            {
              trigger: () => h(
                NButton,
                {
                  size: 'small',
                  type: 'error',
                  loading: deleteLoading.value === row.id,
                  disabled: authStore.user?.id === row.id
                },
                { default: () => 'Supprimer' }
              ),
              default: () => 'Confirmer la suppression ?'
            }
          )
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

const fetchData = async () => {
  loading.value = true
  try {
    const res = await UserAccountService.getAll(page.value - 1, pageSize.value)
    data.value = res.data.content || []
    itemCount.value = res.data.totalElements || 0
  } catch {
    message.error('Erreur de chargement des utilisateurs')
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  formModel.email = ''
  formModel.password = ''
  formModel.role = 'OPERATEUR'
  formModel.active = true
  showCreateModal.value = true
}

const handleCreate = async () => {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      return
    }
    createLoading.value = true
    try {
      await UserAccountService.create(formModel)
      message.success('Utilisateur créé')
      showCreateModal.value = false
      fetchData()
    } catch (error: any) {
      const apiMessage = error?.response?.data?.error || error?.response?.data?.message
      message.error(apiMessage || 'Création impossible')
    } finally {
      createLoading.value = false
    }
  })
}

const handleActivate = async (id: number) => {
  try {
    await UserAccountService.activate(id)
    message.success('Utilisateur activé')
    fetchData()
  } catch (error: any) {
    message.error(error?.response?.data?.error || 'Activation impossible')
  }
}

const handleDeactivate = async (id: number) => {
  try {
    await UserAccountService.deactivate(id)
    message.success('Utilisateur désactivé')
    fetchData()
  } catch (error: any) {
    message.error(error?.response?.data?.error || 'Désactivation impossible')
  }
}

const handleResetPassword = async (id: number) => {
  const newPassword = window.prompt('Nouveau mot de passe (8 caractères minimum)')
  if (!newPassword) {
    return
  }
  resetPasswordLoading.value = id
  try {
    await UserAccountService.resetPassword(id, { newPassword })
    message.success('Mot de passe réinitialisé')
  } catch (error: any) {
    message.error(error?.response?.data?.error || 'Réinitialisation impossible')
  } finally {
    resetPasswordLoading.value = null
  }
}

const handleDelete = async (id: number) => {
  deleteLoading.value = id
  try {
    await UserAccountService.delete(id)
    message.success('Utilisateur supprimé')
    fetchData()
  } catch (error: any) {
    message.error(error?.response?.data?.error || 'Suppression impossible')
  } finally {
    deleteLoading.value = null
  }
}

onMounted(() => {
  if (!authStore.isSuperAdmin) {
    message.error('Accès réservé au SUPER_ADMIN')
    return
  }
  fetchData()
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Utilisateurs</h1>
      <n-button type="primary" @click="openCreateModal">Créer un utilisateur</n-button>
    </div>

    <n-card>
      <n-data-table
        remote
        :columns="columns"
        :data="data"
        :loading="loading"
        :bordered="false"
        :pagination="pagination"
        :scroll-x="920"
      />
    </n-card>

    <n-modal
      v-model:show="showCreateModal"
      preset="card"
      title="Nouvel utilisateur"
      class="max-w-lg"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-form ref="formRef" :model="formModel" :rules="rules" label-placement="top">
        <div class="space-y-2">
          <n-form-item label="Email" path="email">
            <n-input v-model:value="formModel.email" placeholder="user@enspm.cm" />
          </n-form-item>
          <n-form-item label="Mot de passe" path="password">
            <n-input v-model:value="formModel.password" type="password" show-password-on="click" />
          </n-form-item>
          <n-form-item label="Rôle" path="role">
            <n-select v-model:value="formModel.role" :options="roleOptions" />
          </n-form-item>
        </div>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showCreateModal = false">Annuler</n-button>
          <n-button type="primary" :loading="createLoading" @click="handleCreate">Créer</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>
