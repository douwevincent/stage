<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton,
  NCard,
  NDataTable,
  NDescriptions,
  NDescriptionsItem,
  NForm,
  NFormItem,
  NModal,
  NSelect,
  NSpace,
  NTag,
  useMessage
} from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { AnneeAcademiqueService, type AnneeAcademiqueDTO } from '@/api/AnneeAcademiqueService'
import { EtudiantService, type EtudiantDTO } from '@/api/EtudiantService'
import { InscriptionService, type InscriptionDTO } from '@/api/InscriptionService'
import { ParcoursService } from '@/api/ParcoursService'

const message = useMessage()
const route = useRoute()
const router = useRouter()

const etudiant = ref<EtudiantDTO | null>(null)
const activeYear = ref<AnneeAcademiqueDTO | null>(null)
const activeYearMissing = ref(false)

const loadingStudent = ref(false)
const loadingInscriptions = ref(false)
const saving = ref(false)

const inscriptions = ref<InscriptionDTO[]>([])
const page = ref(1)
const pageSize = ref(10)
const itemCount = ref(0)

const showModal = ref(false)
const formRef = ref<FormInst | null>(null)
const parcoursOptions = ref<Array<{ label: string, value: number }>>([])

const formModel = reactive<{ parcoursId: number | null }>({
  parcoursId: null
})

const rules: FormRules = {
  parcoursId: {
    required: true,
    type: 'number',
    message: 'Le parcours est requis',
    trigger: 'change'
  }
}

const etudiantId = computed(() => {
  const rawId = route.params.id
  const parsed = Number(rawId)
  return Number.isFinite(parsed) ? parsed : null
})

const canShowInscrire = ref(false)

const updateInscriptionEligibility = async () => {
  if (!etudiantId.value || !activeYear.value?.id) {
    canShowInscrire.value = false
    return
  }

  try {
    const response = await InscriptionService.getAll(0, 1, {
      etudiantId: etudiantId.value,
      anneeAcademiqueId: activeYear.value.id
    })

    const total = response.data.totalElements || response.data.page?.totalElements || 0
    canShowInscrire.value = total === 0
  } catch {
    canShowInscrire.value = false
  }
}

const fetchStudent = async () => {
  if (!etudiantId.value) {
    message.error('Identifiant étudiant invalide')
    router.push({ name: 'etudiants-search' })
    return
  }

  loadingStudent.value = true
  try {
    const response = await EtudiantService.getOne(etudiantId.value)
    etudiant.value = response.data
  } catch {
    message.error('Impossible de charger les informations de l\'étudiant')
    router.push({ name: 'etudiants-search' })
  } finally {
    loadingStudent.value = false
  }
}

const fetchActiveYear = async () => {
  activeYearMissing.value = false
  activeYear.value = null

  try {
    const response = await AnneeAcademiqueService.getActive()
    activeYear.value = response.data
  } catch {
    activeYearMissing.value = true
  }
}

const fetchInscriptions = async () => {
  if (!etudiantId.value) return

  loadingInscriptions.value = true
  try {
    const response = await InscriptionService.getAll(page.value - 1, pageSize.value, {
      etudiantId: etudiantId.value,
      sort: 'anneeAcademique.libelle,desc'
    })

    inscriptions.value = response.data.content || []
    itemCount.value = response.data.totalElements || response.data.page?.totalElements || 0
  } catch {
    message.error('Erreur lors du chargement des inscriptions')
  } finally {
    loadingInscriptions.value = false
  }
}

const fetchParcoursOptions = async () => {
  try {
    const response = await ParcoursService.getAll(0, 200)
    const rows = response.data.content || []
    parcoursOptions.value = rows.map((p: any) => ({
      label: p.libelle || `${p.specialiteCode || p.specialiteId} - ${p.niveauLibelle || p.niveauId}`,
      value: p.id
    }))
  } catch {
    message.error('Erreur lors du chargement des parcours')
  }
}

const refreshAll = async () => {
  await Promise.all([fetchStudent(), fetchActiveYear(), fetchInscriptions(), fetchParcoursOptions()])
  await updateInscriptionEligibility()
}

const openInscrireModal = () => {
  if (!activeYear.value?.id || !etudiantId.value) {
    message.warning('Aucune année académique active disponible')
    return
  }

  formModel.parcoursId = null
  showModal.value = true
}

const saveInscription = async () => {
  formRef.value?.validate(async (errors) => {
    if (errors) return
    if (!activeYear.value?.id || !etudiantId.value || !formModel.parcoursId) {
      message.error('Données d\'inscription invalides')
      return
    }

    saving.value = true
    try {
      await InscriptionService.create({
        anneeAcademiqueId: activeYear.value.id,
        etudiantId: etudiantId.value,
        parcoursId: formModel.parcoursId
      })

      message.success('Inscription effectuée avec succès')
      showModal.value = false
      await fetchInscriptions()
      await updateInscriptionEligibility()
    } catch {
      message.error('Impossible d\'inscrire cet étudiant (doublon ou données invalides)')
    } finally {
      saving.value = false
    }
  })
}

const columns: DataTableColumns<InscriptionDTO> = [
  {
    title: 'Année académique',
    key: 'anneeAcademiqueLibelle',
    minWidth: 180,
    render (row) {
      return row.anneeAcademiqueLibelle || row.anneeAcademiqueId || '-'
    }
  },
  {
    title: 'Parcours',
    key: 'parcoursLibelle',
    minWidth: 220,
    render (row) {
      return row.parcoursLibelle || row.parcoursId || '-'
    }
  },
  {
    title: 'Spécialité',
    key: 'parcoursSpecialiteCode',
    minWidth: 180,
    render (row) {
      return row.parcoursSpecialiteCode || row.parcoursSpecialiteIntitule || '-'
    }
  },
  {
    title: 'Niveau',
    key: 'parcoursNiveauLibelle',
    minWidth: 140,
    render (row) {
      return row.parcoursNiveauLibelle || '-'
    }
  },
  {
    title: 'Année active',
    key: 'active-tag',
    width: 140,
    render (row) {
      const isActiveYear = Boolean(activeYear.value?.id) && row.anneeAcademiqueId === activeYear.value?.id
      return isActiveYear
        ? 'Oui'
        : 'Non'
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
    fetchInscriptions()
  },
  onUpdatePageSize: (ps: number) => {
    pageSize.value = ps
    page.value = 1
    fetchInscriptions()
  }
}))

watch(
  () => route.params.id,
  async () => {
    page.value = 1
    await refreshAll()
  }
)

onMounted(refreshAll)
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 class="text-2xl font-bold">Fiche étudiant</h1>
        <p class="text-sm text-slate-500">
          Détails personnels et historique des inscriptions
        </p>
      </div>
      <n-space>
        <n-button @click="router.push({ name: 'etudiants-search' })">
          Retour à la recherche
        </n-button>
        <n-button
          v-if="canShowInscrire"
          type="primary"
          :disabled="activeYearMissing"
          @click="openInscrireModal"
        >
          Inscrire
        </n-button>
      </n-space>
    </div>

    <n-card :loading="loadingStudent">
      <template #header>
        Informations personnelles
      </template>

      <n-descriptions
        bordered
        label-placement="left"
        :column="2"
      >
        <n-descriptions-item label="Matricule">
          {{ etudiant?.matricule || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="Nom complet">
          {{ etudiant?.nom || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="Email">
          {{ etudiant?.email || '-' }}
        </n-descriptions-item>
        <n-descriptions-item label="Téléphone">
          {{ etudiant?.telephone || '-' }}
        </n-descriptions-item>
      </n-descriptions>
    </n-card>

    <n-card>
      <template #header>
        <div class="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
          <span>Inscriptions de l'étudiant</span>
          <n-space>
            <n-tag v-if="activeYear?.libelle" type="success" :bordered="false">
              Année active: {{ activeYear.libelle }}
            </n-tag>
            <n-tag v-else type="warning" :bordered="false">
              Aucune année académique active
            </n-tag>
          </n-space>
        </div>
      </template>

      <n-data-table
        remote
        :columns="columns"
        :data="inscriptions"
        :loading="loadingInscriptions"
        :bordered="false"
        :pagination="pagination"
        :scroll-x="860"
      />
    </n-card>

    <n-modal
      v-model:show="showModal"
      preset="card"
      title="Inscrire cet étudiant"
      class="max-w-lg"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-form
        ref="formRef"
        :model="formModel"
        :rules="rules"
        label-placement="left"
        label-width="150"
      >
        <div class="space-y-4">
          <n-form-item label="Étudiant">
            <span>{{ etudiant?.matricule }} - {{ etudiant?.nom }}</span>
          </n-form-item>
          <n-form-item label="Année académique">
            <span>{{ activeYear?.libelle || '-' }}</span>
          </n-form-item>
          <n-form-item label="Parcours" path="parcoursId">
            <n-select
              v-model:value="formModel.parcoursId"
              :options="parcoursOptions"
              filterable
              placeholder="Sélectionner un parcours"
            />
          </n-form-item>
        </div>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">Annuler</n-button>
          <n-button type="primary" :loading="saving" @click="saveInscription">
            Inscrire
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>
