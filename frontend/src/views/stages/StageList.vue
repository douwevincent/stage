<script setup lang="ts">
import {
  NCard, NDataTable, NButton, NSpace, NIcon, NTooltip, NPopconfirm,
  NModal, NForm, NFormItem, NSelect, NInput, NAutoComplete, NTag,
  NDatePicker, NAlert, useMessage
} from 'naive-ui'
import type { FormInst, FormRules, DataTableColumns, SelectOption } from 'naive-ui'
import { PlusOutlined } from '@vicons/antd'
import { Edit, Trash2, CheckCircle, XCircle, FileText, UserPlus, UserCheck } from 'lucide-vue-next'
import { ref, h, onMounted, computed, reactive, watch } from 'vue'
import type { VNodeChild } from 'vue'
import { StageService, type StageDTO, type Statut } from '@/api/StageService'
import { EntrepriseService, type EntrepriseDTO } from '@/api/EntrepriseService'
import { EtudiantService } from '@/api/EtudiantService'
import { EncadreurService } from '@/api/EncadreurService'
import { TypeStageService, type TypeStageDTO } from '@/api/TypeStageService'
import { PeriodeStageService } from '@/api/PeriodeStageService'
import * as dateUtils from '@/utils/dateUtils'

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const showModal = ref(false)
const modalTitle = ref('')
const saving = ref(false)
const assignFormRef = ref<FormInst | null>(null)
const showAssignModal = ref(false)
const assigning = ref(false)
const stageToAssignId = ref<number | null>(null)
const assignModel = reactive<{ etudiantId: number | null }>({ etudiantId: null })
const assignRules: FormRules = {
  etudiantId: {
    required: true,
    type: 'number',
    message: 'Veuillez sélectionner un étudiant',
    trigger: 'change'
  }
}

const assignEncadreurFormRef = ref<FormInst | null>(null)
const quickCreateEncadreurFormRef = ref<FormInst | null>(null)
const showAssignEncadreurModal = ref(false)
const assigningEncadreur = ref(false)
const creatingEncadreur = ref(false)
const showQuickCreateEncadreur = ref(false)
const stageToAssignEncadreur = reactive<{
  stageId: number | null
  entrepriseId: number | null
  entrepriseNom: string
}>({
  stageId: null,
  entrepriseId: null,
  entrepriseNom: ''
})
const assignEncadreurModel = reactive<{ encadreurNom: string | null }>({ encadreurNom: null })
const assignEncadreurRules: FormRules = {
  encadreurNom: {
    required: true,
    message: 'Veuillez sélectionner un encadreur',
    trigger: 'change'
  }
}
const quickCreateEncadreurModel = reactive<{ nom: string, email: string }>({ nom: '', email: '' })
const quickCreateEncadreurRules: FormRules = {
  nom: {
    required: true,
    message: 'Le nom est requis',
    trigger: 'blur'
  },
  email: {
    required: true,
    message: "L'email est requis",
    trigger: ['blur', 'input']
  }
}

// ─── Filter ───────────────────────────────────────────────────────────────────
const statutFiltreOptions: SelectOption[] = [
  { label: 'Tous', value: '' },
  { label: 'En attente de validation', value: 'EN_ATTENTE_VALIDATION' },
  { label: 'Validé', value: 'VALIDE' },
  { label: 'Rejeté', value: 'REJETE' },
]
const selectedStatut = ref<string>('')

// ─── Form model ───────────────────────────────────────────────────────────────
interface StageFormModel {
  id?: number | null
  etudiantId: number | null
  typeStageId: number | null
  entrepriseId: number | null
  entrepriseInput: string
  entrepriseSecteur: string
  ville: string
  adresse: string
  dateDebut: number | null   // naive-ui NDatePicker uses timestamps
  dateFin: number | null
}

const formModel = reactive<StageFormModel>({
  id: null,
  etudiantId: null,
  typeStageId: null,
  entrepriseId: null,
  entrepriseInput: '',
  entrepriseSecteur: '',
  ville: '',
  adresse: '',
  dateDebut: null,
  dateFin: null,
})

const rules: FormRules = {
  typeStageId: { required: true, type: 'number', message: 'Le type de stage est requis', trigger: ['blur', 'change'] },
  ville: { required: true, message: 'La ville est requise', trigger: 'blur' },
  adresse: { required: true, message: "L'adresse est requise", trigger: 'blur' },
  entrepriseInput: { required: true, message: "L'entreprise est requise", trigger: ['blur', 'input'] },
  dateDebut: { required: true, type: 'number', message: 'La date de début est requise', trigger: 'change' },
  dateFin: { required: true, type: 'number', message: 'La date de fin est requise', trigger: 'change' },
}

const typeStageOptions = ref<SelectOption[]>([])

async function loadTypeStageOptions () {
  try {
    const res = await TypeStageService.getAll(0, 100)
    const all = res.data?.content ?? res.data ?? []
    typeStageOptions.value = all.map((item: TypeStageDTO) => ({
      label: item.libelle,
      value: item.id ?? 0
    }))
  } catch {
    typeStageOptions.value = []
    message.error('Erreur lors du chargement des types de stage')
  }
}

async function prefillDatesFromPeriodeStage (typeStageId: number | null) {
  if (!typeStageId) {
    formModel.dateDebut = null
    formModel.dateFin = null
    return
  }

  try {
    const res = await PeriodeStageService.getActiveByTypeStageId(typeStageId)
    formModel.dateDebut = dateUtils.parseApiDate(res.data?.dateDebut) ?? null
    formModel.dateFin = dateUtils.parseApiDate(res.data?.dateFin) ?? null
  } catch (err: any) {
    formModel.dateDebut = null
    formModel.dateFin = null
    if (err?.response?.status === 404) {
      message.warning('Aucune période de stage active ne correspond au type sélectionné')
    } else {
      message.error('Erreur lors du chargement de la période de stage')
    }
  }
}

// ─── Autocomplete entreprise ─────────────────────────────────────────────────
const entrepriseSuggestions = ref<EntrepriseDTO[]>([])
const entrepriseOptions = computed(() =>
  entrepriseSuggestions.value.map(e => ({ label: e.nom, value: e.nom }))
)
const entrepriseSelectedExisting = computed(
  () => entrepriseSuggestions.value.find(e => e.nom === formModel.entrepriseInput) ?? null
)
const showSecteurField = computed(
  () => formModel.entrepriseInput.trim() !== '' && entrepriseSelectedExisting.value === null
)

async function onEntrepriseInput (val: string) {
  formModel.entrepriseInput = val
  formModel.entrepriseId = null
  if (val.length >= 2) {
    try {
      const res = await EntrepriseService.rechercheParNom(val)
      entrepriseSuggestions.value = res.data
    } catch {
      entrepriseSuggestions.value = []
    }
  } else {
    entrepriseSuggestions.value = []
  }
}

function onEntrepriseSelect (val: string) {
  formModel.entrepriseInput = val
  const found = entrepriseSuggestions.value.find(e => e.nom === val)
  formModel.entrepriseId = found?.id ?? null
}

// ─── Etudiant select ─────────────────────────────────────────────────────────
const etudiantOptions = ref<SelectOption[]>([])
const etudiantLoading = ref(false)
const encadreurOptions = ref<SelectOption[]>([])
const encadreurLoading = ref(false)
const encadreurSearchResults = ref<Array<{ id?: number, nom?: string | null, email?: string | null }>>([])

async function loadEtudiantOptions (query = '') {
  etudiantLoading.value = true
  try {
    const res = await EtudiantService.search(query, 0, 20)
    const all = res.data?.content ?? res.data ?? []
    etudiantOptions.value = all
      .map((e: any) => ({ label: `${e.matricule} – ${e.nom ?? ''} ${e.prenom ?? ''}`.trim(), value: e.id }))
  } catch {
    etudiantOptions.value = []
  } finally {
    etudiantLoading.value = false
  }
}

async function loadEncadreurOptions (query = '') {
  if (!stageToAssignEncadreur.entrepriseId) {
    encadreurOptions.value = []
    return
  }
  encadreurLoading.value = true
  try {
    const res = await EncadreurService.search({
      entrepriseId: stageToAssignEncadreur.entrepriseId,
      q: query,
      page: 0,
      size: 20
    })
    const all = res.data?.content ?? res.data ?? []
    encadreurSearchResults.value = all
    encadreurOptions.value = all.map((e: any) => ({
      label: `${e.nom ?? ''}${e.email ? ` (${e.email})` : ''}`.trim(),
      value: e.nom ?? ''
    }))
  } catch {
    encadreurSearchResults.value = []
    encadreurOptions.value = []
  } finally {
    encadreurLoading.value = false
  }
}

// ─── Table data ───────────────────────────────────────────────────────────────
const tableData = ref<StageDTO[]>([])
const loading = ref(false)
const pagination = reactive({ page: 1, pageSize: 20, itemCount: 0, showSizePicker: true, pageSizes: [10, 20, 50] })

async function loadData () {
  loading.value = true
  try {
    const statut = selectedStatut.value as Statut | undefined
    const res = await StageService.getAll(
      pagination.page - 1,
      pagination.pageSize,
      statut || undefined
    )
    const data = res.data
    tableData.value = data?.content ?? data ?? []
    pagination.itemCount = data?.totalElements ?? tableData.value.length
  } catch {
    message.error('Erreur lors du chargement des stages')
  } finally {
    loading.value = false
  }
}

watch(selectedStatut, () => { pagination.page = 1; loadData() })

function handlePageChange (page: number) { pagination.page = page; loadData() }
function handlePageSizeChange (size: number) { pagination.pageSize = size; pagination.page = 1; loadData() }

onMounted(loadData)

// ─── Statut badge ─────────────────────────────────────────────────────────────
function statutTag (statut: Statut | null | undefined) {
  if (!statut) return h(NTag, { size: 'small' }, { default: () => '—' })
  const typeMap: Record<Statut, 'warning' | 'success' | 'error'> = {
    EN_ATTENTE_VALIDATION: 'warning',
    VALIDE: 'success',
    REJETE: 'error',
  }
  const labelMap: Record<Statut, string> = {
    EN_ATTENTE_VALIDATION: 'En attente',
    VALIDE: 'Validé',
    REJETE: 'Rejeté',
  }
  return h(NTag, { type: typeMap[statut], size: 'small' }, { default: () => labelMap[statut] })
}

// ─── Columns ──────────────────────────────────────────────────────────────────
const columns: DataTableColumns<StageDTO> = [
  {
    title: 'Étudiant',
    key: 'etudiant',
    minWidth: 180,
    render (row) {
      if (row.etudiantMatricule || row.etudiantNom) {
        return `${row.etudiantMatricule ?? ''} ${row.etudiantNom ?? ''}`.trim()
      }
      return '—'
    }
  },
  {
    title: 'Entreprise',
    key: 'entrepriseNom',
    minWidth: 160,
    render (row) { return row.entrepriseNom ?? '—' }
  },
  {
    title: 'Encadreur',
    key: 'encadreurNom',
    minWidth: 170,
    render (row) { return row.encadreurNom ?? '—' }
  },
  {
    title: 'Ville',
    key: 'ville',
    minWidth: 120,
    render (row) { return row.ville ?? '—' }
  },
  {
    title: 'Dates',
    key: 'dates',
    minWidth: 180,
    render (row) { return `${row.dateDebut ?? '—'} → ${row.dateFin ?? '—'}` }
  },
  {
    title: 'Statut',
    key: 'statut',
    minWidth: 130,
    render (row) { return statutTag(row.statut) }
  },
  {
    title: 'Source',
    key: 'source',
    minWidth: 110,
    render (row) {
      if (!row.source) return '—'
      return h(NTag, {
        size: 'small',
        type: row.source === 'ETUDIANT' ? 'info' : 'default'
      }, { default: () => row.source === 'ETUDIANT' ? 'Étudiant' : 'Opérateur' })
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 260,
    fixed: 'right',
    render (row) {
      const buttons: VNodeChild[] = []

      // Edit
      buttons.push(h(NTooltip, null, {
        trigger: () => h(NButton, {
          size: 'small', quaternary: true, type: 'info', circle: true,
          onClick: () => handleEdit(row)
        }, { default: () => h(NIcon, null, { default: () => h(Edit) }) }),
        default: () => 'Éditer'
      }))

      // Delete
      buttons.push(h(NTooltip, null, {
        trigger: () => h(NPopconfirm, {
          onPositiveClick: () => handleDelete(row.id!)
        }, {
          trigger: () => h(NButton, {
            size: 'small', quaternary: true, type: 'error', circle: true
          }, { default: () => h(NIcon, null, { default: () => h(Trash2) }) }),
          default: () => 'Supprimer ce stage ?'
        }),
        default: () => 'Supprimer'
      }))

      // Validate
      if (row.statut === 'EN_ATTENTE_VALIDATION') {
        buttons.push(h(NTooltip, null, {
          trigger: () => h(NButton, {
            size: 'small', quaternary: true, type: 'success', circle: true,
            onClick: () => handleValider(row.id!)
          }, { default: () => h(NIcon, null, { default: () => h(CheckCircle) }) }),
          default: () => 'Valider'
        }))

        buttons.push(h(NTooltip, null, {
          trigger: () => h(NButton, {
            size: 'small', quaternary: true, type: 'error', circle: true,
            onClick: () => handleRejeter(row.id!)
          }, { default: () => h(NIcon, null, { default: () => h(XCircle) }) }),
          default: () => 'Rejeter'
        }))
      }

      // Assign student when missing
      if (!row.etudiantId) {
        buttons.push(h(NTooltip, null, {
          trigger: () => h(NButton, {
            size: 'small', quaternary: true, type: 'primary', circle: true,
            onClick: () => openAssignModal(row)
          }, { default: () => h(NIcon, null, { default: () => h(UserPlus) }) }),
          default: () => 'Assigner un étudiant'
        }))
      }

      // Assign/reassign supervisor only when stage already has a student and an entreprise
      if (row.etudiantId && row.entrepriseId) {
        buttons.push(h(NTooltip, null, {
          trigger: () => h(NButton, {
            size: 'small', quaternary: true,
            type: row.encadreurId ? 'default' : 'primary',
            circle: true,
            onClick: () => openAssignEncadreurModal(row)
          }, { default: () => h(NIcon, null, { default: () => h(UserCheck) }) }),
          default: () => row.encadreurId ? 'Réassigner un encadreur' : 'Assigner un encadreur'
        }))
      }

      // Autorisation
      if (row.cheminAutorisation) {
        buttons.push(h(NTooltip, null, {
          trigger: () => h(NButton, {
            size: 'small', quaternary: true, type: 'warning', circle: true,
            onClick: () => handleViewAutorisation(row.id!)
          }, { default: () => h(NIcon, null, { default: () => h(FileText) }) }),
          default: () => "Voir l'autorisation"
        }))
      }

      return h(NSpace, null, { default: () => buttons })
    }
  }
]

// ─── Actions ──────────────────────────────────────────────────────────────────
function resetForm () {
  Object.assign(formModel, {
    id: null, etudiantId: null, typeStageId: null,
    entrepriseId: null, entrepriseInput: '', entrepriseSecteur: '',
    ville: '', adresse: '', dateDebut: null, dateFin: null
  })
  entrepriseSuggestions.value = []
  etudiantOptions.value = []
}

function openCreateModal () {
  resetForm()
  loadEtudiantOptions('')
  loadTypeStageOptions()
  modalTitle.value = 'Nouveau stage'
  showModal.value = true
}

function handleEdit (row: StageDTO) {
  resetForm()
  Object.assign(formModel, {
    id: row.id,
    etudiantId: row.etudiantId ?? null,
    typeStageId: row.typeStageId ?? null,
    entrepriseId: row.entrepriseId ?? null,
    entrepriseInput: row.entrepriseNom ?? '',
    ville: row.ville ?? '',
    adresse: row.adresse ?? '',
    dateDebut: dateUtils.parseApiDate(row.dateDebut) ?? null,
    dateFin: dateUtils.parseApiDate(row.dateFin) ?? null,
  })
  if (row.etudiantId && (row.etudiantMatricule || row.etudiantNom)) {
    etudiantOptions.value = [{
      label: `${row.etudiantMatricule ?? ''} – ${row.etudiantNom ?? ''}`.trim(),
      value: row.etudiantId
    }]
  }
  if (row.typeStageId && row.typeStageLibelle) {
    typeStageOptions.value = [{
      label: row.typeStageLibelle,
      value: row.typeStageId
    }]
  } else {
    loadTypeStageOptions()
  }
  modalTitle.value = 'Modifier le stage'
  showModal.value = true
}

async function handleDelete (id: number) {
  try {
    await StageService.delete(id)
    message.success('Stage supprimé')
    loadData()
  } catch {
    message.error('Erreur lors de la suppression')
  }
}

async function handleValider (id: number) {
  try {
    await StageService.valider(id)
    message.success('Stage validé')
    loadData()
  } catch {
    message.error('Erreur lors de la validation')
  }
}

async function handleRejeter (id: number) {
  try {
    await StageService.rejeter(id)
    message.warning('Stage rejeté')
    loadData()
  } catch {
    message.error('Erreur lors du rejet')
  }
}

function openAssignModal (row: StageDTO) {
  stageToAssignId.value = row.id ?? null
  assignModel.etudiantId = null
  loadEtudiantOptions('')
  showAssignModal.value = true
}

function resetQuickCreateEncadreurForm () {
  quickCreateEncadreurModel.nom = ''
  quickCreateEncadreurModel.email = ''
}

function openAssignEncadreurModal (row: StageDTO) {
  if (!row.id) {
    message.error('Stage invalide')
    return
  }
  if (!row.entrepriseId) {
    message.warning('Veuillez d\'abord renseigner une entreprise pour ce stage')
    return
  }
  stageToAssignEncadreur.stageId = row.id
  stageToAssignEncadreur.entrepriseId = row.entrepriseId
  stageToAssignEncadreur.entrepriseNom = row.entrepriseNom ?? ''
  assignEncadreurModel.encadreurNom = row.encadreurNom ?? null
  showQuickCreateEncadreur.value = false
  resetQuickCreateEncadreurForm()
  loadEncadreurOptions('')
  showAssignEncadreurModal.value = true
}

async function handleAssignEtudiant () {
  await assignFormRef.value?.validate()
  if (!stageToAssignId.value || !assignModel.etudiantId) return
  assigning.value = true
  try {
    await StageService.assignerEtudiant(stageToAssignId.value, assignModel.etudiantId)
    message.success('Étudiant assigné avec succès')
    showAssignModal.value = false
    await loadData()
  } catch (err: any) {
    const apiMessage = err?.response?.data?.message
    message.error(apiMessage || 'Erreur lors de l\'assignation de l\'étudiant')
  } finally {
    assigning.value = false
  }
}

async function handleAssignEncadreur () {
  await assignEncadreurFormRef.value?.validate()
  if (!stageToAssignEncadreur.stageId || !assignEncadreurModel.encadreurNom) return

  const selected = encadreurSearchResults.value.find(
    e => (e.nom ?? '').trim().toLowerCase() === assignEncadreurModel.encadreurNom!.trim().toLowerCase()
  )
  if (!selected?.id) {
    message.error('Encadreur introuvable pour ce nom, veuillez sélectionner une valeur de la liste')
    return
  }

  assigningEncadreur.value = true
  try {
    await StageService.assignerEncadreur(stageToAssignEncadreur.stageId, selected.id)
    message.success('Encadreur assigné avec succès')
    showAssignEncadreurModal.value = false
    await loadData()
  } catch (err: any) {
    const apiMessage = err?.response?.data?.message
    message.error(apiMessage || 'Erreur lors de l\'assignation de l\'encadreur')
  } finally {
    assigningEncadreur.value = false
  }
}

async function handleQuickCreateEncadreur () {
  await quickCreateEncadreurFormRef.value?.validate()
  if (!stageToAssignEncadreur.entrepriseId) return
  creatingEncadreur.value = true
  try {
    const created = await EncadreurService.create({
      nom: quickCreateEncadreurModel.nom.trim(),
      prenom: null,
      email: quickCreateEncadreurModel.email.trim(),
      entrepriseId: stageToAssignEncadreur.entrepriseId
    })
    const createdNom = created.data?.nom
    if (created.data?.id) {
      await loadEncadreurOptions('')
      assignEncadreurModel.encadreurNom = createdNom ?? quickCreateEncadreurModel.nom.trim()
      showQuickCreateEncadreur.value = false
      resetQuickCreateEncadreurForm()
      message.success('Encadreur créé, vous pouvez maintenant l\'assigner')
    }
  } catch {
    message.error('Erreur lors de la création de l\'encadreur')
  } finally {
    creatingEncadreur.value = false
  }
}

function handleViewAutorisation (id: number) {
  window.open(StageService.getAutorisationUrl(id), '_blank')
}

async function handleSave () {
  await formRef.value?.validate()
  saving.value = true
  try {
    // Resolve entreprise
    let entrepriseId = formModel.entrepriseId
    if (!entrepriseId && formModel.entrepriseInput.trim()) {
      const res = await EntrepriseService.rechercheOuCree(
        formModel.entrepriseInput.trim(),
        formModel.entrepriseSecteur
      )
      entrepriseId = res.data.id ?? null
    }

    const payload: StageDTO = {
      id: formModel.id ?? undefined,
      etudiantId: formModel.etudiantId,
      typeStageId: formModel.typeStageId,
      entrepriseId,
      ville: formModel.ville,
      adresse: formModel.adresse,
      dateDebut: dateUtils.formatDateForApi(formModel.dateDebut),
      dateFin: dateUtils.formatDateForApi(formModel.dateFin),
    }

    if (payload.id) {
      // The backend performs a full entity mapping on update, so we merge with
      // the current persisted stage to avoid dropping non-edited associations.
      const existing = (await StageService.getOne(payload.id)).data
      const mergedPayload: StageDTO = {
        ...existing,
        ...payload,
        id: payload.id,
      }
      await StageService.update(payload.id, mergedPayload)
      message.success('Stage mis à jour')
    } else {
      await StageService.create(payload)
      message.success('Stage créé')
    }
    showModal.value = false
    loadData()
  } catch (err: any) {
    if (!err?.response?.status) {
      // validation error already shown
    } else {
      const apiMessage = err?.response?.data?.message
      message.error(apiMessage || "Erreur lors de l'enregistrement")
    }
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold">Gestion des Stages</h1>
      <NButton type="primary" @click="openCreateModal">
        <template #icon><NIcon><PlusOutlined /></NIcon></template>
        Nouveau Stage
      </NButton>
    </div>

    <NCard>
      <!-- Filter -->
      <div class="mb-4 flex gap-4 items-center">
        <span class="text-sm font-medium text-gray-600 dark:text-gray-400">Filtrer par statut :</span>
        <NSelect
          v-model:value="selectedStatut"
          :options="statutFiltreOptions"
          style="width: 220px"
          placeholder="Tous"
        />
      </div>

      <NDataTable
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        :scroll-x="1320"
        remote
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
      />
    </NCard>

    <!-- Create/Edit Modal -->
    <NModal v-model:show="showModal" :title="modalTitle" preset="card" style="width: 560px">
      <NForm ref="formRef" :model="formModel" :rules="rules" label-placement="top">
        <!-- Étudiant (optionnel) -->
        <NFormItem label="Étudiant (optionnel)" path="etudiantId">
          <NSelect
            v-model:value="formModel.etudiantId"
            :options="etudiantOptions"
            :loading="etudiantLoading"
            filterable
            remote
            clearable
            placeholder="Rechercher par matricule ou nom…"
            @focus="loadEtudiantOptions('')"
            @search="loadEtudiantOptions"
          />
        </NFormItem>

        <NFormItem label="Type de stage" path="typeStageId">
          <NSelect
            v-model:value="formModel.typeStageId"
            :options="typeStageOptions"
            clearable
            filterable
            placeholder="Sélectionner un type de stage"
            @focus="loadTypeStageOptions"
            @update:value="prefillDatesFromPeriodeStage"
          />
        </NFormItem>

        <!-- Entreprise autocomplete -->
        <NFormItem label="Entreprise" path="entrepriseInput">
          <NAutoComplete
            v-model:value="formModel.entrepriseInput"
            :options="entrepriseOptions"
            placeholder="Saisir le nom de l'entreprise…"
            clearable
            @input="onEntrepriseInput"
            @select="onEntrepriseSelect"
          />
        </NFormItem>

        <!-- Secteur (si nouvelle entreprise) -->
        <NFormItem v-if="showSecteurField" label="Secteur d'activité">
          <NInput v-model:value="formModel.entrepriseSecteur" placeholder="Ex: Informatique, BTP…" />
        </NFormItem>

        <!-- Ville -->
        <NFormItem label="Ville" path="ville">
          <NInput v-model:value="formModel.ville" placeholder="Ville du stage" />
        </NFormItem>

        <!-- Adresse -->
        <NFormItem label="Adresse" path="adresse">
          <NInput v-model:value="formModel.adresse" type="textarea" :rows="2" placeholder="Adresse complète" />
        </NFormItem>

        <!-- Dates -->
        <div class="grid grid-cols-2 gap-4">
          <NFormItem label="Date de début" path="dateDebut">
            <NDatePicker v-model:value="formModel.dateDebut" type="date" style="width: 100%" clearable />
          </NFormItem>
          <NFormItem label="Date de fin" path="dateFin">
            <NDatePicker v-model:value="formModel.dateFin" type="date" style="width: 100%" clearable />
          </NFormItem>
        </div>
      </NForm>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="showModal = false">Annuler</NButton>
          <NButton type="primary" :loading="saving" @click="handleSave">Enregistrer</NButton>
        </NSpace>
      </template>
    </NModal>

    <NModal v-model:show="showAssignModal" title="Assigner un étudiant" preset="card" style="width: 500px">
      <NForm ref="assignFormRef" :model="assignModel" :rules="assignRules" label-placement="top">
        <NAlert type="info" class="mb-4">
          Sélectionnez l'étudiant à associer à ce stage.
        </NAlert>
        <NFormItem label="Étudiant" path="etudiantId">
          <NSelect
            v-model:value="assignModel.etudiantId"
            :options="etudiantOptions"
            :loading="etudiantLoading"
            filterable
            remote
            clearable
            placeholder="Rechercher par matricule ou nom…"
            @focus="loadEtudiantOptions('')"
            @search="loadEtudiantOptions"
          />
        </NFormItem>
      </NForm>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="showAssignModal = false">Annuler</NButton>
          <NButton type="primary" :loading="assigning" @click="handleAssignEtudiant">Assigner</NButton>
        </NSpace>
      </template>
    </NModal>

    <NModal v-model:show="showAssignEncadreurModal" title="Assigner un encadreur" preset="card" style="width: 560px">
      <NForm ref="assignEncadreurFormRef" :model="assignEncadreurModel" :rules="assignEncadreurRules" label-placement="top">
        <NAlert type="info" class="mb-4">
          Sélectionnez un encadreur de l'entreprise
          <strong>{{ stageToAssignEncadreur.entrepriseNom || 'du stage' }}</strong>.
        </NAlert>
        <NFormItem label="Encadreur" path="encadreurNom">
          <NSelect
            v-model:value="assignEncadreurModel.encadreurNom"
            :options="encadreurOptions"
            :loading="encadreurLoading"
            filterable
            remote
            clearable
            placeholder="Rechercher par nom ou email…"
            @focus="loadEncadreurOptions('')"
            @search="loadEncadreurOptions"
          />
        </NFormItem>
      </NForm>

      <div class="mt-2 border-t pt-4">
        <div class="mb-2 flex items-center justify-between">
          <span class="text-sm font-medium">Encadreur introuvable ?</span>
          <NButton text type="primary" @click="showQuickCreateEncadreur = !showQuickCreateEncadreur">
            {{ showQuickCreateEncadreur ? 'Masquer' : 'Créer rapidement' }}
          </NButton>
        </div>
        <NForm
          v-if="showQuickCreateEncadreur"
          ref="quickCreateEncadreurFormRef"
          :model="quickCreateEncadreurModel"
          :rules="quickCreateEncadreurRules"
          label-placement="top"
        >
          <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
            <NFormItem label="Nom" path="nom">
              <NInput v-model:value="quickCreateEncadreurModel.nom" placeholder="Nom de l'encadreur" />
            </NFormItem>
            <NFormItem label="Email" path="email">
              <NInput v-model:value="quickCreateEncadreurModel.email" placeholder="email@entreprise.com" />
            </NFormItem>
          </div>
          <NSpace justify="end">
            <NButton :loading="creatingEncadreur" @click="handleQuickCreateEncadreur">
              Créer l'encadreur
            </NButton>
          </NSpace>
        </NForm>
      </div>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="showAssignEncadreurModal = false">Annuler</NButton>
          <NButton type="primary" :loading="assigningEncadreur" @click="handleAssignEncadreur">Assigner</NButton>
        </NSpace>
      </template>
    </NModal>
  </div>
</template>
