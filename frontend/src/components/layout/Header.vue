<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { LucideMenu, LucideSun, LucideMoon, LucideBell, LucideLogOut } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/authStore'

defineProps<{
  isDark: boolean
}>()

defineEmits<{
  (e: 'toggleTheme'): void
  (e: 'toggleSidebar'): void
}>()

const router = useRouter()
const authStore = useAuthStore()

const userLabel = computed(() => authStore.user?.email || 'Utilisateur')
const roleLabel = computed(() => authStore.user?.role || '-')

const handleLogout = async () => {
  authStore.logout()
  await router.push({ name: 'login' })
}
</script>

<template>
  <header class="h-[72px] px-6 flex items-center justify-between bg-header-light dark:bg-header-dark backdrop-blur-md sticky top-0 z-[90] border-b border-gray-100 dark:border-gray-800 transition-colors duration-300">
    <div class="flex items-center gap-4">
      <button 
        @click="$emit('toggleSidebar')"
        class="w-10 h-10 flex items-center justify-center rounded-xl text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800 hover:text-gray-900 dark:hover:text-white transition-all cursor-pointer"
      >
        <LucideMenu :size="20" />
      </button>
    </div>
    <div>
      <p class="text-xs lg:text-3xl text-lime-950 text-shadow-orange-950 font-semibold  dark:text-shadow-none dark:text-cyan mb-4 pt-4">ECOLE NATIONALE SUPÉRIEURE POLYTECHNIQUE DE MAROUA</p>
    </div>
    
    <div class="flex items-center gap-4">
      <button 
        @click="$emit('toggleTheme')"
        class="w-10 h-10 flex items-center justify-center rounded-xl text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800 transition-all cursor-pointer"
      >
        <LucideSun v-if="!isDark" :size="20" />
        <LucideMoon v-else :size="20" />
      </button>
      
      <button class="relative w-10 h-10 flex items-center justify-center rounded-xl text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800 transition-all cursor-pointer">
        <LucideBell :size="20" />
        <span class="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-zinc-800"></span>
      </button>
      
      <div class="flex items-center gap-3 pl-4 border-l border-gray-100 dark:border-gray-800">
        <img 
          :src="`https://ui-avatars.com/api/?name=${encodeURIComponent(userLabel)}&background=06b6d4&color=fff`"
          :alt="userLabel"
          class="w-9 h-9 rounded-full object-cover ring-2 ring-gray-100 dark:ring-gray-800"
        >
        <div class="hidden sm:flex flex-col leading-tight">
          <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ userLabel }}</span>
          <span class="text-xs text-gray-400">{{ roleLabel }}</span>
        </div>
        <button
          @click="handleLogout"
          class="w-10 h-10 flex items-center justify-center rounded-xl text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800 hover:text-red-500 transition-all cursor-pointer"
          title="Déconnexion"
        >
          <LucideLogOut :size="18" />
        </button>
      </div>
    </div>
  </header>
</template>
