<script setup lang="ts">
import {
  NAlert,
  NButton,
  NCard,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSpin,
  NSpace,
  useMessage
} from 'naive-ui'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { PublicEvaluationService, type PublicEvaluationFormDTO } from '@/api/PublicEvaluationService'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const code = computed(() => String(route.params.code ?? ''))
const stageId = computed(() => Number(route.params.stageId))

const loading = ref(false)
const submitting = ref(false)
const successMessage = ref('')
const errorMessage = ref('')
const formData = ref<PublicEvaluationFormDTO | null>(null)

const notes = reactive<Record<number, number | null>>({})
const comments = reactive<Record<number, string>>({})

async function loadForm () {
  if (!code.value || Number.isNaN(stageId.value)) {
    errorMessage.value = 'Lien d\'evaluation invalide'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await PublicEvaluationService.getForm(code.value, stageId.value)
    formData.value = response.data

    for (const category of formData.value.categories) {
      for (const criterion of category.criteres) {
        notes[criterion.critereId] = null
        comments[criterion.critereId] = ''
      }
    }
  } catch (error: any) {
    const status = error?.response?.status
    if (status === 404) {
      errorMessage.value = 'Stage introuvable pour ce lien'
    } else if (status === 409) {
      errorMessage.value = 'Cette evaluation a deja ete soumise'
    } else if (status === 410) {
      errorMessage.value = 'Le lien d\'evaluation est expire'
    } else {
      errorMessage.value = 'Impossible de charger le formulaire d\'evaluation'
    }
  } finally {
    loading.value = false
  }
}

function validateBeforeSubmit (): boolean {
  if (!formData.value) {
    return false
  }

  for (const category of formData.value.categories) {
    for (const criterion of category.criteres) {
      const currentValue = notes[criterion.critereId]
      if (currentValue == null) {
        message.error(`La note du critere "${criterion.libelle}" est obligatoire`)
        return false
      }
      if (currentValue < 0) {
        message.error(`La note du critere "${criterion.libelle}" ne peut pas etre negative`)
        return false
      }
      if (currentValue > criterion.coefficient) {
        message.error(`La note du critere "${criterion.libelle}" depasse le maximum ${criterion.coefficient}`)
        return false
      }
    }
  }

  return true
}

async function submitEvaluation () {
  if (!formData.value || !validateBeforeSubmit()) {
    return
  }

  submitting.value = true
  errorMessage.value = ''

  try {
    const payload = formData.value.categories.flatMap(category =>
      category.criteres.map(criterion => ({
        critereId: criterion.critereId,
        valeur: Number(notes[criterion.critereId]),
        commentaire: comments[criterion.critereId] || null
      }))
    )

    const response = await PublicEvaluationService.submit(code.value, formData.value.stageId, payload)
    successMessage.value = response.data.message || 'Evaluation enregistree avec succes'
  } catch (error: any) {
    const apiMessage = error?.response?.data?.message
    if (typeof apiMessage === 'string' && apiMessage.length > 0) {
      errorMessage.value = apiMessage
    } else {
      errorMessage.value = 'Erreur lors de la soumission de l\'evaluation'
    }
  } finally {
    submitting.value = false
  }
}

function backToList () {
  router.push({ name: 'public-evaluation-list', params: { code: code.value } })
}

onMounted(loadForm)
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-6">
    <div style="width: 100%; max-width: 920px">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white">Formulaire d'evaluation</h1>
        <p class="text-gray-500 dark:text-gray-400 mt-2">
          Renseignez chaque critere. La note maximale est limitee par le coefficient.
        </p>
      </div>

      <NCard>
        <NSpin :show="loading">
          <NAlert v-if="errorMessage" type="error" class="mb-4">
            {{ errorMessage }}
          </NAlert>

          <NAlert v-if="successMessage" type="success" class="mb-4">
            {{ successMessage }}
          </NAlert>

          <template v-if="formData && !successMessage">
            <div class="mb-6 text-sm text-gray-600 dark:text-gray-300">
              <p><strong>Etudiant:</strong> {{ formData.matricule }} {{ formData.etudiantNom }}</p>
              <p><strong>Entreprise:</strong> {{ formData.entrepriseNom }}</p>
              <p><strong>Periode:</strong> {{ formData.dateDebut }} -> {{ formData.dateFin }}</p>
            </div>

            <NForm label-placement="top">
              <NCard
                v-for="category in formData.categories"
                :key="category.categorie"
                class="mb-4"
                size="small"
              >
                <template #header>
                  {{ category.categorie }}
                </template>

                <div
                  v-for="criterion in category.criteres"
                  :key="criterion.critereId"
                  class="mb-4 border-b border-gray-100 pb-4"
                >
                  <NFormItem
                    :label="`${criterion.libelle} (max: ${criterion.coefficient})`"
                    required
                  >
                    <NInputNumber
                      v-model:value="notes[criterion.critereId]"
                      :min="0"
                      :max="criterion.coefficient"
                      :precision="0"
                      style="width: 180px"
                    />
                  </NFormItem>

                  <NFormItem label="Commentaire (optionnel)">
                    <NInput
                      v-model:value="comments[criterion.critereId]"
                      type="textarea"
                      :rows="2"
                      placeholder="Ajouter un commentaire si necessaire"
                    />
                  </NFormItem>
                </div>
              </NCard>
            </NForm>

            <NSpace>
              <NButton @click="backToList">Retour a la liste</NButton>
              <NButton type="primary" :loading="submitting" @click="submitEvaluation">
                Soumettre l'evaluation
              </NButton>
            </NSpace>
          </template>

          <template v-if="successMessage">
            <NSpace>
              <NButton type="primary" @click="backToList">Voir les autres stages</NButton>
            </NSpace>
          </template>
        </NSpin>
      </NCard>
    </div>
  </div>
</template>
