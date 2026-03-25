<script setup lang="ts">
import { 
  LucideUsers, 
  LucideBriefcase, 
  LucideFactory, 
  LucideAlertCircle, 
  LucideTrendingUp,
  LucideFileCheck,
  LucidePlusCircle
} from 'lucide-vue-next'

const stats = [
  { 
    title: 'Étudiants', 
    value: '1,200', 
    trend: '+12%', 
    icon: LucideUsers, 
    colorClass: 'bg-indigo-500',
    trendClass: 'text-emerald-500'
  },
  { 
    title: 'Stages Actifs', 
    value: '450', 
    badge: 'En cours', 
    badgeClass: 'bg-cyan-500/10 text-cyan-500',
    icon: LucideBriefcase, 
    colorClass: 'bg-cyan-500'
  },
  { 
    title: 'Entreprises', 
    value: '150', 
    badge: 'Global', 
    badgeClass: 'bg-emerald-500/10 text-emerald-500',
    icon: LucideFactory, 
    colorClass: 'bg-emerald-500'
  },
  { 
    title: 'En Attente', 
    value: '85', 
    badge: 'Action requise', 
    badgeClass: 'bg-amber-500/10 text-amber-500',
    icon: LucideAlertCircle, 
    colorClass: 'bg-amber-500'
  }
]

const activities = [
  {
    icon: LucideFileCheck,
    iconClass: 'bg-indigo-500/10 text-indigo-500',
    title: 'Convention signée',
    user: 'Lucas Martin',
    info: 'Département Informatique • Il y a 2 heures'
  },
  {
    icon: LucidePlusCircle,
    iconClass: 'bg-emerald-500/10 text-emerald-500',
    title: 'Nouveau partenaire',
    user: 'TechFlux Solutions',
    info: 'Vérification complétée • Il y a 5 heures'
  }
]
</script>

<template>
  <div class="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-700">
    <div class="welcome-section">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">Bienvenue, Sarah !</h1>
      <p class="text-gray-500 dark:text-gray-400">Voici un aperçu de l'activité du système aujourd'hui.</p>
    </div>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div 
        v-for="stat in stats" 
        :key="stat.title"
        class="bg-white/70 dark:bg-zinc-800/70 backdrop-blur-lg border border-gray-100 dark:border-gray-700 p-6 rounded-[20px] flex items-center gap-5 shadow-ambient hover:-translate-y-1 transition-all duration-300"
      >
        <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center text-white shrink-0', stat.colorClass]">
          <component :is="stat.icon" :size="24" />
        </div>
        <div class="flex flex-col">
          <h3 class="text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-1">{{ stat.title }}</h3>
          <div class="text-2xl font-bold text-gray-900 dark:text-white mb-1">{{ stat.value }}</div>
          <div v-if="stat.trend" :class="['flex items-center gap-1 text-xs font-bold font-manrope', stat.trendClass]">
            <LucideTrendingUp :size="12" />
            <span>{{ stat.trend }}</span>
          </div>
          <div v-else-if="stat.badge" :class="['inline-block px-2 py-0.5 rounded-md text-[10px] font-bold uppercase self-start', stat.badgeClass]">
            {{ stat.badge }}
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 bg-white dark:bg-zinc-800 border border-gray-100 dark:border-gray-700 p-6 rounded-[20px] shadow-ambient flex flex-col gap-5">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-bold text-gray-900 dark:text-white">Évolution des Stages</h2>
          <button class="text-cyan text-sm font-semibold hover:underline cursor-pointer">Voir détails</button>
        </div>
        <div class="flex-1 min-h-[240px] bg-gray-50 dark:bg-zinc-900/50 rounded-xl flex items-center justify-center relative overflow-hidden">
          <div class="w-[90%] h-[100px] border-b-2 border-cyan bg-gradient-to-r from-transparent to-cyan/20"></div>
          <span class="absolute text-gray-400 text-xs italic">Graphique d'évolution...</span>
        </div>
      </div>
      
      <div class="bg-white dark:bg-zinc-800 border border-gray-100 dark:border-gray-700 p-6 rounded-[20px] shadow-ambient flex flex-col gap-6">
        <div class="flex items-center justify-between">
          <h2 class="text-lg font-bold text-gray-900 dark:text-white">Par Département</h2>
        </div>
        <div class="flex-1 min-h-[240px] bg-gray-50 dark:bg-zinc-900/50 rounded-xl flex items-end justify-center gap-4 p-5">
          <div class="flex-1 bg-gradient-to-t from-primary/80 to-cyan rounded-t-lg transition-all hover:brightness-110" style="height: 80%" title="Informatique"></div>
          <div class="flex-1 bg-gradient-to-t from-primary/80 to-cyan rounded-t-lg transition-all hover:brightness-110" style="height: 40%" title="Génie Civil"></div>
          <div class="flex-1 bg-gradient-to-t from-primary/80 to-cyan rounded-t-lg transition-all hover:brightness-110" style="height: 30%" title="Électronique"></div>
          <div class="flex-1 bg-gradient-to-t from-primary/80 to-cyan rounded-t-lg transition-all hover:brightness-110" style="height: 55%" title="Marketing"></div>
        </div>
        <button class="w-full bg-gradient-to-br from-primary-container to-secondary text-white font-bold py-3 rounded-xl hover:scale-[1.02] transition-transform shadow-lg cursor-pointer">
          Rapport détaillé
        </button>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="bg-white dark:bg-zinc-800 border border-gray-100 dark:border-gray-700 p-6 rounded-[20px] shadow-ambient">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-lg font-bold text-gray-900 dark:text-white">Dernières Activités</h2>
      </div>
      <div class="space-y-4">
        <div v-for="act in activities" :key="act.title" class="flex gap-4 p-3 rounded-xl hover:bg-gray-50 dark:hover:bg-zinc-900/50 transition-colors group cursor-pointer">
          <div :class="['w-10 h-10 rounded-xl flex items-center justify-center shrink-0', act.iconClass]">
            <component :is="act.icon" :size="20" />
          </div>
          <div class="flex flex-col">
            <p class="text-sm dark:text-gray-300">
              <span class="font-bold text-gray-900 dark:text-white">{{ act.title }}</span> - {{ act.user }}
            </p>
            <span class="text-xs text-gray-500 group-hover:text-gray-600 dark:group-hover:text-gray-400">{{ act.info }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
