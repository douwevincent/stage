<script setup lang="ts">
import { LucideMenu, LucideSearch, LucideSun, LucideMoon, LucideBell } from 'lucide-vue-next'

defineProps<{
  isDark: boolean
}>()

defineEmits<{
  (e: 'toggleTheme'): void
  (e: 'toggleSidebar'): void
}>()
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
      
      <div class="hidden md:flex items-center gap-2 bg-gray-100 dark:bg-gray-800 px-4 py-2 rounded-full w-[300px] border border-transparent focus-within:border-cyan transition-all">
        <LucideSearch :size="18" class="text-gray-400" />
        <input 
          type="text" 
          placeholder="Rechercher..." 
          class="bg-transparent border-none outline-none text-sm w-full text-gray-700 dark:text-gray-300"
        >
      </div>
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
          src="https://ui-avatars.com/api/?name=Sarah&background=06b6d4&color=fff" 
          alt="Sarah" 
          class="w-9 h-9 rounded-full object-cover ring-2 ring-gray-100 dark:ring-gray-800"
        >
        <div class="hidden sm:flex flex-col leading-tight">
          <span class="text-sm font-semibold text-gray-900 dark:text-white">Sarah</span>
          <span class="text-xs text-gray-400">Admin</span>
        </div>
      </div>
    </div>
  </header>
</template>
