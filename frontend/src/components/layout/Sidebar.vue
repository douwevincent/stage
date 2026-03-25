<script setup lang="ts">
import { h } from 'vue'
import type { Component } from 'vue'
import { NMenu } from 'naive-ui'
import type { MenuOption } from 'naive-ui'
import { RouterLink } from 'vue-router'
import {
  LayoutDashboard,
  Users,
  Building2,
  GraduationCap,
  Briefcase,
  Factory,
  Calendar,
  ScrollText,
  Settings,
  Layers,
  School,
  Tags
} from 'lucide-vue-next'

const props = defineProps<{
  collapsed: boolean
}>()

function renderIcon (icon: Component) {
  return () => h(icon, { size: 20 })
}

const menuOptions: MenuOption[] = [
  {
    label: () => h(RouterLink, { to: { name: 'dashboard' } }, { default: () => 'Tableau de Bord' }),
    key: 'dashboard',
    icon: renderIcon(LayoutDashboard)
  },
  {
    label: () => h(RouterLink, { to: { name: 'departements' } }, { default: () => 'Départements' }),
    key: 'departements',
    icon: renderIcon(Building2)
  },
  {
    label: () => h(RouterLink, { to: { name: 'specialites' } }, { default: () => 'Spécialités' }),
    key: 'specialites',
    icon: renderIcon(GraduationCap)
  },
  {
    label: () => h(RouterLink, { to: { name: 'etudiants' } }, { default: () => 'Étudiants' }),
    key: 'etudiants',
    icon: renderIcon(Users)
  },
  {
    label: () => h(RouterLink, { to: { name: 'stages' } }, { default: () => 'Stages' }),
    key: 'stages',
    icon: renderIcon(Briefcase)
  },
  {
    label: () => h(RouterLink, { to: { name: 'entreprises' } }, { default: () => 'Entreprises' }),
    key: 'entreprises',
    icon: renderIcon(Factory)
  },
  {
    label: () => h(RouterLink, { to: { name: 'annees-academiques' } }, { default: () => 'Années Académiques' }),
    key: 'annees-academiques',
    icon: renderIcon(Calendar)
  },
  {
    label: () => h(RouterLink, { to: { name: 'niveaux' } }, { default: () => 'Niveaux' }),
    key: 'niveaux',
    icon: renderIcon(School)
  },
  {
    label: () => h(RouterLink, { to: { name: 'type-stages' } }, { default: () => 'Types de Stage' }),
    key: 'type-stages',
    icon: renderIcon(Tags)
  },
  {
    label: 'Notes',
    key: 'notes',
    icon: renderIcon(ScrollText)
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: 'Paramètres',
    key: 'settings',
    icon: renderIcon(Settings)
  }
]
</script>

<template>
  <aside 
    class="fixed left-0 top-0 h-screen bg-sidebar-light dark:bg-sidebar-dark transition-all duration-300 z-50 border-r border-gray-100 dark:border-gray-800 flex flex-col"
    :style="{ width: collapsed ? '80px' : '280px' }"
  >
    <div class="p-6 flex items-center gap-3 border-b border-gray-100 dark:border-gray-800 overflow-hidden whitespace-nowrap">
      <div class="w-8 h-8 rounded-lg bg-cyan flex items-center justify-center flex-shrink-0 text-white shadow-sm">
        <Layers :size="20" />
      </div>
      <span v-if="!collapsed" class="font-manrope font-bold text-lg text-primary dark:text-cyan">Stage Manager</span>
    </div>
    
    <div class="flex-1 overflow-y-auto py-6 px-3">
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="56"
        :collapsed-icon-size="20"
        :options="menuOptions"
        :indent="18"
        class="custom-menu"
      />
    </div>
  </aside>
</template>

<style scoped>
.custom-menu :deep(.n-menu-item-content) {
  border-radius: 12px;
  margin-bottom: 4px;
}

.custom-menu :deep(.n-menu-item-content--selected) {
  background: linear-gradient(135deg, #1e3a8a 0%, #00687a 100%) !important;
  color: #ffffff !important;
}

.custom-menu :deep(.n-menu-item-content--selected .n-menu-item-content__icon) {
  color: #ffffff !important;
}
</style>
