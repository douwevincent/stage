<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import {
  LucideUsers,
  LucideBriefcase,
  LucideAlertCircle,
  LucideClipboardCheck,
  LucideUserX,
  LucideFactory
} from 'lucide-vue-next'
import { DashboardService, type DashboardStatsDTO } from '../api/DashboardService'

const loading = ref(false)
const errorMessage = ref('')
const statsData = ref<DashboardStatsDTO | null>(null)

const formatNumber = (value: number | undefined) => new Intl.NumberFormat('fr-FR').format(value ?? 0)

const cards = computed(() => {
  const data = statsData.value

  return [
    {
      title: 'Étudiants inscrits',
      value: formatNumber(data?.nombreEtudiantsInscrits),
      icon: LucideUsers,
      colorClass: 'bg-indigo-500'
    },
    {
      title: 'Stages enregistrés',
      value: formatNumber(data?.nombreStagesEnregistres),
      icon: LucideBriefcase,
      colorClass: 'bg-cyan-500'
    },
    {
      title: 'En attente de validation',
      value: formatNumber(data?.nombreStagesEnAttenteValidation),
      icon: LucideAlertCircle,
      colorClass: 'bg-amber-500'
    },
    {
      title: 'En attente de notation',
      value: formatNumber(data?.nombreStagesEnAttenteNotation),
      icon: LucideClipboardCheck,
      colorClass: 'bg-purple-500'
    },
    {
      title: 'Stages sans étudiant',
      value: formatNumber(data?.nombreStagesSansEtudiant),
      icon: LucideUserX,
      colorClass: 'bg-rose-500'
    },
    {
      title: 'Entreprises avec stages',
      value: formatNumber(data?.nombreEntreprisesAvecStages),
      icon: LucideFactory,
      colorClass: 'bg-emerald-500'
    }
  ]
})

const loadStats = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const { data } = await DashboardService.getStatsAnneeActive()
    statsData.value = data
  } catch {
    errorMessage.value = "Impossible de charger les statistiques de l'année active."
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void loadStats()
})
</script>

<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="welcome-section text-center">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">Tableau de bord</h1>
      <p class="text-gray-500 dark:text-gray-400">
        Vue synthétique pour l'année académique active
        <span v-if="statsData">({{ statsData.anneeAcademiqueLibelle }})</span>.
      </p>
    </div>

    <div v-if="errorMessage" class="bg-red-50 text-red-700 border border-red-200 rounded-xl px-4 py-3">
      {{ errorMessage }}
    </div>

    <div v-if="loading" class="text-sm text-gray-500 dark:text-gray-400">
      Chargement des statistiques...
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div 
        v-for="stat in cards" 
        :key="stat.title"
        class="bg-white/70 dark:bg-zinc-800/70 backdrop-blur-lg border border-gray-100 dark:border-gray-700 p-6 rounded-[20px] flex items-center gap-5 shadow-ambient hover:-translate-y-1 transition-all duration-300"
      >
        <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center text-white shrink-0', stat.colorClass]">
          <component :is="stat.icon" :size="24" />
        </div>
        <div class="flex flex-col">
          <h3 class="text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-1">{{ stat.title }}</h3>
          <div class="text-2xl font-bold text-gray-900 dark:text-white mb-1">{{ stat.value }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
