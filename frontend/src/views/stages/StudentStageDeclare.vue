<script setup lang="ts">
import {
  NCard, NButton, NForm, NFormItem, NInput, NAutoComplete,
  NDatePicker, NUpload, NUploadDragger, NIcon, NSpace, NAlert,
  NSteps, NStep, useMessage
} from 'naive-ui'
import type { FormInst, FormRules, UploadFileInfo } from 'naive-ui'
import { InboxOutlined } from '@vicons/antd'
import { CheckCircle } from 'lucide-vue-next'
import { ref, reactive, computed } from 'vue'
import { EtudiantService, type EtudiantDTO, type StageDeclarationContextDTO } from '@/api/EtudiantService'
import { EntrepriseService, type EntrepriseDTO } from '@/api/EntrepriseService'
import { StageService } from '@/api/StageService'
import EnspmLogo from '@/components/common/EnspmLogo.vue'

const message = useMessage()

// ─── Steps ────────────────────────────────────────────────────────────────────
const currentStep = ref(0)   // 0 = validation matricule, 1 = détails stage
const success = ref(false)

// ─── Step 1: Validation matricule ─────────────────────────────────────────────
const matriculeRef = ref<FormInst | null>(null)
const matriculeForm = reactive({ matricule: '' })
const matriculeRules: FormRules = {
  matricule: { required: true, message: 'Le matricule est requis', trigger: 'blur' }
}
const validatingMatricule = ref(false)
const validatedEtudiant = ref<EtudiantDTO | null>(null)
const declarationContext = ref<StageDeclarationContextDTO | null>(null)
const matriculeError = ref('')

async function verifierMatricule () {
  await matriculeRef.value?.validate()
  validatingMatricule.value = true
  matriculeError.value = ''
  validatedEtudiant.value = null
  declarationContext.value = null
  try {
    const res = await EtudiantService.getStageDeclarationContext(matriculeForm.matricule.trim())
    declarationContext.value = res.data
    validatedEtudiant.value = res.data.etudiant
    stageForm.dateDebut = res.data.dateDebut ? new Date(res.data.dateDebut).getTime() : null
    stageForm.dateFin = res.data.dateFin ? new Date(res.data.dateFin).getTime() : null
    currentStep.value = 1
  } catch (err: any) {
    if (err?.response?.status === 404) {
      matriculeError.value = `Aucun étudiant trouvé avec le matricule « ${matriculeForm.matricule} »`
    } else if (err?.response?.status === 400) {
      matriculeError.value = err?.response?.data?.message ?? 'Impossible de déterminer le type de stage pour cet étudiant'
    } else {
      matriculeError.value = 'Erreur lors de la vérification du matricule'
    }
  } finally {
    validatingMatricule.value = false
  }
}

// ─── Step 2: Détails du stage ─────────────────────────────────────────────────
const stageRef = ref<FormInst | null>(null)
const stageForm = reactive({
  entrepriseInput: '',
  entrepriseSecteur: '',
  entrepriseId: null as number | null,
  ville: '',
  adresse: '',
  dateDebut: null as number | null,
  dateFin: null as number | null,
})
const uploadedFile = ref<File | null>(null)
const uploadFileList = ref<UploadFileInfo[]>([])
const uploadError = ref('')

const stageRules: FormRules = {
  entrepriseInput: { required: true, message: "L'entreprise est requise", trigger: ['blur', 'input'] },
  ville: { required: true, message: 'La ville est requise', trigger: 'blur' },
  adresse: { required: true, message: "L'adresse est requise", trigger: 'blur' },
  dateDebut: { required: true, type: 'number', message: 'La date de début est requise', trigger: 'change' },
  dateFin: { required: true, type: 'number', message: 'La date de fin est requise', trigger: 'change' },
}

// Entreprise autocomplete
const entrepriseSuggestions = ref<EntrepriseDTO[]>([])
const entrepriseOptions = computed(() =>
  entrepriseSuggestions.value.map(e => ({ label: e.nom, value: e.nom }))
)
const entrepriseSelectedExisting = computed(
  () => entrepriseSuggestions.value.find(e => e.nom === stageForm.entrepriseInput) ?? null
)
const showSecteur = computed(
  () => stageForm.entrepriseInput.trim() !== '' && entrepriseSelectedExisting.value === null
)

async function onEntrepriseInput (val: string) {
  stageForm.entrepriseInput = val
  stageForm.entrepriseId = null
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
  stageForm.entrepriseInput = val
  const found = entrepriseSuggestions.value.find(e => e.nom === val)
  stageForm.entrepriseId = found?.id ?? null
}

// File upload
function handleFileChange (options: { fileList: UploadFileInfo[] }) {
  uploadFileList.value = options.fileList
  const last = options.fileList[options.fileList.length - 1]
  uploadedFile.value = last?.file ?? null
  uploadError.value = ''
}

function handleFileRemove () {
  uploadedFile.value = null
  uploadFileList.value = []
  return true
}

// Submit
const submitting = ref(false)

async function declarerStage () {
  await stageRef.value?.validate()
  if (!uploadedFile.value) {
    uploadError.value = "L'autorisation de stage est obligatoire"
    return
  }
  submitting.value = true
  try {
    const toDate = (ts: number | null) =>
      ts ? new Date(ts).toISOString().split('T')[0] : ''

    await StageService.declarer({
      etudiantMatricule: matriculeForm.matricule.trim(),
      entrepriseId: stageForm.entrepriseId,
      entrepriseNom: stageForm.entrepriseInput.trim(),
      entrepriseSecteur: stageForm.entrepriseSecteur || undefined,
      ville: stageForm.ville,
      adresse: stageForm.adresse,
      dateDebut: toDate(stageForm.dateDebut),
      dateFin: toDate(stageForm.dateFin),
      autorisation: uploadedFile.value,
    })
    success.value = true
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? err?.response?.data ?? 'Erreur lors de la déclaration'
    message.error(typeof msg === 'string' ? msg : 'Erreur lors de la déclaration')
  } finally {
    submitting.value = false
  }
}

function recommencer () {
  currentStep.value = 0
  success.value = false
  matriculeForm.matricule = ''
  validatedEtudiant.value = null
  declarationContext.value = null
  matriculeError.value = ''
  Object.assign(stageForm, {
    entrepriseInput: '', entrepriseSecteur: '', entrepriseId: null,
    ville: '', adresse: '', dateDebut: null, dateFin: null
  })
  uploadedFile.value = null
  uploadFileList.value = []
  uploadError.value = ''
  entrepriseSuggestions.value = []
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-6">
    <div style="width: 100%; max-width: 560px">
      <!-- Header -->
      <div class="text-center mb-8">
        <EnspmLogo :size="104" class="mx-auto mb-4" />
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white">Déclaration de Stage</h1>
        <p class="text-gray-500 dark:text-gray-400 mt-2">Déclarez votre stage auprès de l'établissement</p>
      </div>

      <!-- Success screen -->
      <NCard v-if="success" class="text-center">
        <div class="py-8 space-y-4">
          <NIcon size="64" color="#18a058">
            <CheckCircle />
          </NIcon>
          <h2 class="text-xl font-semibold text-gray-800 dark:text-white">Stage déclaré avec succès</h2>
          <p class="text-gray-500">Votre stage est en attente de validation par un opérateur.</p>
          <NButton type="primary" @click="recommencer">Faire une nouvelle déclaration</NButton>
        </div>
      </NCard>

      <!-- Steps form -->
      <template v-else>
        <NCard>
          <NSteps :current="currentStep" class="mb-8">
            <NStep title="Identification" description="Vérification du matricule" />
            <NStep title="Détails du stage" description="Informations sur le stage" />
          </NSteps>

          <!-- Step 0: Matricule -->
          <div v-if="currentStep === 0">
            <NForm ref="matriculeRef" :model="matriculeForm" :rules="matriculeRules" label-placement="top">
              <NFormItem label="Numéro Matricule" path="matricule">
                <NInput
                  v-model:value="matriculeForm.matricule"
                  placeholder="Entrez votre matricule"
                  @keydown.enter="verifierMatricule"
                />
              </NFormItem>
            </NForm>

            <NAlert v-if="matriculeError" type="error" class="mb-4">
              {{ matriculeError }}
            </NAlert>

            <NButton
              type="primary"
              block
              :loading="validatingMatricule"
              @click="verifierMatricule"
            >
              Vérifier et continuer
            </NButton>
          </div>

          <!-- Step 1: Stage details -->
          <div v-else>
            <!-- Etudiant confirmé -->
            <NAlert type="success" class="mb-6">
              Étudiant identifié :
              <strong>{{ validatedEtudiant?.matricule }} — {{ validatedEtudiant?.nom }} {{ validatedEtudiant?.prenom }}</strong>
            </NAlert>

            <NAlert v-if="declarationContext" type="info" class="mb-6">
              Type de stage déduit depuis votre niveau d'inscription :
              <strong>{{ declarationContext.typeStageLibelle }}</strong>.
              Les dates ont été préremplies à partir de la période de stage active correspondante.
            </NAlert>

            <NForm ref="stageRef" :model="stageForm" :rules="stageRules" label-placement="top">
              <!-- Entreprise -->
              <NFormItem label="Entreprise" path="entrepriseInput">
                <NAutoComplete
                  v-model:value="stageForm.entrepriseInput"
                  :options="entrepriseOptions"
                  placeholder="Saisir le nom de l'entreprise…"
                  clearable
                  @input="onEntrepriseInput"
                  @select="onEntrepriseSelect"
                />
              </NFormItem>

              <!-- Secteur (si nouvelle entreprise) -->
              <NFormItem v-if="showSecteur" label="Secteur d'activité">
                <NInput
                  v-model:value="stageForm.entrepriseSecteur"
                  placeholder="Ex: Informatique, BTP, Finance…"
                />
              </NFormItem>

              <!-- Ville -->
              <NFormItem label="Ville" path="ville">
                <NInput v-model:value="stageForm.ville" placeholder="Ville du stage" />
              </NFormItem>

              <!-- Adresse -->
              <NFormItem label="Adresse" path="adresse">
                <NInput
                  v-model:value="stageForm.adresse"
                  type="textarea"
                  :rows="2"
                  placeholder="Adresse complète de l'entreprise"
                />
              </NFormItem>

              <!-- Dates -->
              <div class="grid grid-cols-2 gap-4">
                <NFormItem label="Date de début" path="dateDebut">
                  <NDatePicker v-model:value="stageForm.dateDebut" type="date" style="width: 100%" clearable />
                </NFormItem>
                <NFormItem label="Date de fin" path="dateFin">
                  <NDatePicker v-model:value="stageForm.dateFin" type="date" style="width: 100%" clearable />
                </NFormItem>
              </div>

              <!-- Autorisation upload -->
              <NFormItem label="Autorisation de stage *">
                <div class="w-full">
                  <NUpload
                    :file-list="uploadFileList"
                    accept=".pdf,image/*"
                    :max="1"
                    @change="handleFileChange"
                    @remove="handleFileRemove"
                  >
                    <NUploadDragger>
                      <div class="py-4 text-center">
                        <NIcon size="40" :depth="3">
                          <InboxOutlined />
                        </NIcon>
                        <p class="mt-2 text-sm">
                          Glissez votre fichier ici ou <span class="text-primary">cliquez pour parcourir</span>
                        </p>
                        <p class="text-xs text-gray-400 mt-1">PDF ou image, max 10 Mo</p>
                      </div>
                    </NUploadDragger>
                  </NUpload>
                  <NAlert v-if="uploadError" type="error" class="mt-2" size="small">
                    {{ uploadError }}
                  </NAlert>
                </div>
              </NFormItem>
            </NForm>

            <NSpace class="mt-4">
              <NButton @click="currentStep = 0">Retour</NButton>
              <NButton
                type="primary"
                :loading="submitting"
                @click="declarerStage"
              >
                Déclarer le stage
              </NButton>
            </NSpace>
          </div>
        </NCard>
      </template>
    </div>
  </div>
</template>
