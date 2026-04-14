<script setup lang="ts">
import { computed, h, ref, watch } from 'vue'
import type { Component } from 'vue'
import { NMenu } from 'naive-ui'
import type { MenuOption } from 'naive-ui'
import { RouterLink, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import {
  LayoutDashboard,
  Users,
  Briefcase,
  Settings,
  School,
  BookOpen,
} from 'lucide-vue-next'
import EnspmLogo from '@/components/common/EnspmLogo.vue'

const props = defineProps<{
  collapsed: boolean
}>()

function renderIcon (icon: Component) {
  return () => h(icon, { size: 20 })
}

const route = useRoute()
const authStore = useAuthStore()

const selectedKey = computed(() => {
  if (!route.name) return 'dashboard'
  return String(route.name)
})

function getParentGroupKey (routeKey: string): string | null {
  if (['annees-academiques', 'niveaux', 'departements', 'specialites', 'parcours'].includes(routeKey)) {
    return 'academique'
  }
  if (['etudiants-list', 'etudiants-search', 'etudiants-detail', 'etudiants-import', 'inscriptions'].includes(routeKey)) {
    return 'etudiants'
  }
  if (['stages', 'entreprises', 'encadreurs', 'type-stages', 'periode-stages'].includes(routeKey)) {
    return 'stages-group'
  }
  if (['baremes', 'criteres', 'bareme-criteres', 'evaluation-results', 'evaluation-export'].includes(routeKey)) {
    return 'evaluation'
  }
  if (['notifications', 'parametres', 'mail-queue'].includes(routeKey)) {
    return 'systeme'
  }
  if (['users-management'].includes(routeKey)) {
    return 'systeme'
  }
  return null
}

const expandedKeys = ref<string[]>([])

function handleExpandedKeysUpdate (keys: string[]) {
  if (keys.length === 0) {
    expandedKeys.value = []
    return
  }
  expandedKeys.value = [keys[keys.length - 1]]
}

watch(
  selectedKey,
  (routeKey) => {
    const parentKey = getParentGroupKey(routeKey)
    if (!parentKey) {
      expandedKeys.value = []
      return
    }
    if (expandedKeys.value[0] !== parentKey) {
      expandedKeys.value = [parentKey]
    }
  },
  { immediate: true }
)

const menuOptions = computed<MenuOption[]>(() => [
  {
    label: () => h(RouterLink, { to: { name: 'dashboard' } }, { default: () => 'Tableau de Bord' }),
    key: 'dashboard',
    icon: renderIcon(LayoutDashboard)
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: 'Structure académique',
    key: 'academique',
    icon: renderIcon(School),
    children: [
      {
        label: () => h(RouterLink, { to: { name: 'annees-academiques' } }, { default: () => 'Années Académiques' }),
        key: 'annees-academiques'
      },
      {
        label: () => h(RouterLink, { to: { name: 'niveaux' } }, { default: () => 'Niveaux' }),
        key: 'niveaux'
      },
      {
        label: () => h(RouterLink, { to: { name: 'departements' } }, { default: () => 'Départements' }),
        key: 'departements'
      },
      {
        label: () => h(RouterLink, { to: { name: 'specialites' } }, { default: () => 'Spécialités' }),
        key: 'specialites'
      },
      {
        label: () => h(RouterLink, { to: { name: 'parcours' } }, { default: () => 'Parcours' }),
        key: 'parcours'
      }
    ]
  },
  {
    label: 'Gestion étudiants',
    key: 'etudiants',
    icon: renderIcon(Users),
    children: [
      {
        label: () => h(RouterLink, { to: { name: 'etudiants-list' } }, { default: () => 'Liste des étudiants' }),
        key: 'etudiants-list'
      },
      {
        label: () => h(RouterLink, { to: { name: 'etudiants-search' } }, { default: () => 'Recherche étudiant' }),
        key: 'etudiants-search'
      },
      {
        label: () => h(RouterLink, { to: { name: 'etudiants-import' } }, { default: () => 'Importer étudiants' }),
        key: 'etudiants-import'
      },
      {
        label: () => h(RouterLink, { to: { name: 'inscriptions' } }, { default: () => 'Inscriptions' }),
        key: 'inscriptions'
      }
    ]
  },
  {
    label: 'Gestion stages',
    key: 'stages-group',
    icon: renderIcon(Briefcase),
    children: [
      {
        label: () => h(RouterLink, { to: { name: 'stages' } }, { default: () => 'Stages' }),
        key: 'stages'
      },
      {
        label: () => h(RouterLink, { to: { name: 'entreprises' } }, { default: () => 'Entreprises' }),
        key: 'entreprises'
      },
      {
        label: () => h(RouterLink, { to: { name: 'encadreurs' } }, { default: () => 'Encadreurs' }),
        key: 'encadreurs'
      },
      {
        label: () => h(RouterLink, { to: { name: 'type-stages' } }, { default: () => 'Types de Stage' }),
        key: 'type-stages'
      },
      {
        label: () => h(RouterLink, { to: { name: 'periode-stages' } }, { default: () => 'Périodes de Stage' }),
        key: 'periode-stages'
      }
    ]
  },
  {
    label: 'Évaluation',
    key: 'evaluation',
    icon: renderIcon(BookOpen),
    children: [
      {
        label: () => h(RouterLink, { to: { name: 'baremes' } }, { default: () => 'Barèmes' }),
        key: 'baremes'
      },
      {
        label: () => h(RouterLink, { to: { name: 'criteres' } }, { default: () => 'Critères' }),
        key: 'criteres'
      },
      {
        label: () => h(RouterLink, { to: { name: 'bareme-criteres' } }, { default: () => 'Assoc. Barème-Critères' }),
        key: 'bareme-criteres'
      },
      {
        label: () => h(RouterLink, { to: { name: 'evaluation-results' } }, { default: () => 'Résultats des évaluations' }),
        key: 'evaluation-results'
      },
      {
        label: () => h(RouterLink, { to: { name: 'evaluation-export' } }, { default: () => 'Exporter les evaluations' }),
        key: 'evaluation-export'
      }
    ]
  },
  {
    label: 'Système',
    key: 'systeme',
    icon: renderIcon(Settings),
    children: [
      {
        label: () => h(RouterLink, { to: { name: 'notifications' } }, { default: () => 'Notifications' }),
        key: 'notifications'
      },
      {
        label: () => h(RouterLink, { to: { name: 'parametres' } }, { default: () => 'Paramètres' }),
        key: 'parametres'
      },
      ...(authStore.isAdminOrSuperAdmin
        ? [{
            label: () => h(RouterLink, { to: { name: 'mail-queue' } }, { default: () => 'Mails envoyés' }),
            key: 'mail-queue'
          }]
        : []),
      ...(authStore.isSuperAdmin
        ? [{
            label: () => h(RouterLink, { to: { name: 'users-management' } }, { default: () => 'Utilisateurs' }),
            key: 'users-management'
          }]
        : [])
    ]
  }
])
</script>

<template>
  <aside 
    class="fixed left-0 top-0 h-screen bg-sidebar-light dark:bg-sidebar-dark transition-all duration-300 z-50 border-r border-gray-100 dark:border-gray-800 flex flex-col"
    :style="{ width: collapsed ? '80px' : '280px' }"
  >
    <div
      class="p-4 border-b border-gray-100 dark:border-gray-800 overflow-hidden"
      :class="collapsed ? 'flex justify-center' : 'flex items-center gap-3 whitespace-nowrap'"
    >
      <EnspmLogo :size="collapsed ? 36 : 44" class="flex-shrink-0" />
      <div v-if="!collapsed" class="min-w-0">
        <p class="font-manrope font-bold text-lg text-primary dark:text-cyan leading-tight">Stage Manager</p>
        <p class="text-xs text-gray-500 dark:text-gray-400 leading-tight">ENSPM</p>
      </div>
    </div>
    
    <div class="menu-scroll flex-1 overflow-y-auto py-6 px-3">
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="56"
        :collapsed-icon-size="20"
        :value="selectedKey"
        v-model:expanded-keys="expandedKeys"
        @update:expanded-keys="handleExpandedKeysUpdate"
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
  transition: background-color 0.2s ease, color 0.2s ease;
}

.custom-menu :deep(.n-menu-item-content-header) {
  font-weight: 600;
}

.custom-menu :deep(.n-menu-item-content:not(.n-menu-item-content--selected):hover) {
  background: rgba(0, 104, 122, 0.1);
}

.custom-menu :deep(.n-menu-item-content--child-active) {
  background: rgba(0, 104, 122, 0.08);
}

.custom-menu :deep(.n-menu-item-content__arrow) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 9999px;
  background: rgba(148, 163, 184, 0.2);
  color: #0f172a;
  opacity: 0.95;
  transform: rotate(0deg) scale(1);
  will-change: transform;
  transition:
    transform 280ms cubic-bezier(0.22, 1, 0.36, 1),
    background-color 180ms ease,
    color 180ms ease,
    box-shadow 180ms ease;
}

.custom-menu :deep(.n-submenu .n-menu-item-content:hover .n-menu-item-content__arrow) {
  background: rgba(0, 104, 122, 0.2);
}

.custom-menu :deep(.n-submenu.n-submenu--open .n-menu-item-content__arrow),
.custom-menu :deep(.n-submenu .n-menu-item-content[aria-expanded='true'] .n-menu-item-content__arrow),
.custom-menu :deep(.n-menu-item-content--child-active .n-menu-item-content__arrow) {
  background: rgba(0, 104, 122, 0.25);
  color: #00687a;
  box-shadow: inset 0 0 0 1px rgba(0, 104, 122, 0.35);
  transform: rotate(90deg) scale(1.08);
}

.custom-menu :deep(.n-menu-item-content--selected .n-menu-item-content__arrow) {
  background: rgba(14, 165, 167, 0.14);
  color: #0f766e;
}

@media (prefers-reduced-motion: reduce) {
  .custom-menu :deep(.n-menu-item-content__arrow) {
    transition: none;
  }
}

.custom-menu :deep(.n-menu-item-content--selected) {
  background: rgba(125, 211, 252, 0.2) !important;
  color: #0f172a !important;
  box-shadow: inset 0 0 0 1px rgba(14, 116, 144, 0.16);
}

.custom-menu :deep(.n-menu-item-content--selected .n-menu-item-content__icon) {
  color: #0f766e !important;
}

@media (prefers-color-scheme: dark) {
  .custom-menu :deep(.n-menu-item-content--selected) {
    background: rgba(103, 232, 249, 0.16) !important;
    color: #ecfeff !important;
    box-shadow: inset 0 0 0 1px rgba(103, 232, 249, 0.24);
  }

  .custom-menu :deep(.n-menu-item-content--selected .n-menu-item-content__icon),
  .custom-menu :deep(.n-menu-item-content--selected .n-menu-item-content__arrow) {
    color: #cffafe !important;
  }
}

.menu-scroll::-webkit-scrollbar {
  width: 8px;
}

.menu-scroll::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.45);
  border-radius: 9999px;
}

.menu-scroll::-webkit-scrollbar-track {
  background: transparent;
}
</style>
