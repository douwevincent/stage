<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { NConfigProvider, NMessageProvider, NDialogProvider, NNotificationProvider, NGlobalStyle, darkTheme } from 'naive-ui'
import type { GlobalThemeOverrides } from 'naive-ui'
import { RouterView } from 'vue-router'
import Sidebar from '@/components/layout/Sidebar.vue'
import Header from '@/components/layout/Header.vue'

const isDark = ref(localStorage.getItem('theme') === 'dark')

const theme = computed(() => (isDark.value ? darkTheme : null))

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#00236f',
    primaryColorHover: '#1e3a8a',
    primaryColorPressed: '#001a54',
    primaryColorSuppl: '#1e3a8a',
    borderRadius: '12px'
  },
  Card: {
    borderRadius: '20px',
    boxShadow: '0 8px 32px rgba(0, 35, 111, 0.06)'
  },
  Button: {
    borderRadiusMedium: '12px'
  }
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  const newTheme = isDark.value ? 'dark' : 'light'
  localStorage.setItem('theme', newTheme)
  document.documentElement.setAttribute('data-theme', newTheme)
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

onMounted(() => {
  const currentTheme = localStorage.getItem('theme') || 'light'
  isDark.value = currentTheme === 'dark'
  document.documentElement.setAttribute('data-theme', currentTheme)
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
})

const collapsed = ref(window.innerWidth < 1024)
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}
</script>

<template>
  <n-config-provider :theme="theme" :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <n-global-style />
          <div class="app-container flex min-h-screen bg-app-light dark:bg-app-dark font-inter transition-colors duration-300">
            <Sidebar :collapsed="collapsed" />
            <div 
              class="flex-1 flex flex-col min-w-0 transition-all duration-300"
              :style="{ marginLeft: collapsed ? '80px' : '280px' }"
            >
              <Header :is-dark="isDark" @toggle-theme="toggleTheme" @toggle-sidebar="toggleSidebar" />
              <main class="flex-1 p-8 overflow-y-auto">
                <RouterView />
              </main>
              <footer class="p-6 text-center border-t border-gray-100 dark:border-gray-800 text-gray-500 text-sm">
                <p>&copy; 2026 ENSPM Stage Manager. Tous droits réservés.</p>
              </footer>
            </div>
          </div>
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Manrope:wght@600;700;800&display=swap');

.app-container {
  overflow-x: hidden;
}

h1, h2, h3 {
  font-family: 'Manrope', sans-serif;
}
</style>
