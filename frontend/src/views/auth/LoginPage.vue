<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NCard, NForm, NFormItem, NInput, useMessage } from 'naive-ui'
import { Mail, Lock } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/authStore'
import EnspmLogo from '@/components/common/EnspmLogo.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const message = useMessage()

const loading = ref(false)
const form = reactive({
  email: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.email || !form.password) {
    message.warning('Email et mot de passe requis')
    return
  }

  loading.value = true
  try {
    await authStore.login(form.email, form.password)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    await router.push(redirect)
  } catch (error: any) {
    const apiMessage = error?.response?.data?.message
    message.error(apiMessage || 'Connexion impossible')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 via-cyan-50 to-teal-50 p-4">
    <!-- Éléments de décoration -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute top-0 -left-4 w-72 h-72 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
      <div class="absolute top-0 -right-4 w-72 h-72 bg-cyan-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>
      <div class="absolute -bottom-8 left-20 w-72 h-72 bg-teal-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000"></div>
    </div>

    <!-- Conteneur principal -->
    <div class="relative z-10 w-full max-w-md">
      <!-- En-tête -->
      <div class="text-center mb-8">
        <EnspmLogo :size="110" class="mx-auto mb-4" />
        <h1 class="text-3xl font-bold text-gray-800 mb-2">Bienvenue</h1>
        <p class="text-gray-600">Stage Manager ENSPM</p>
      </div>

      <!-- Carte de connexion -->
      <n-card class="rounded-2xl shadow-2xl border border-white border-opacity-60 backdrop-blur-sm">
        <n-form @submit.prevent="handleLogin" class="space-y-4">
          <!-- Champ Email -->
          <n-form-item label="">
            <template #label>
              <div class="flex items-center space-x-2 text-gray-700 font-semibold mb-1">
                <Mail class="w-4 h-4 text-blue-500" />
                <span>Email</span>
              </div>
            </template>
            <n-input
              v-model:value="form.email"
              type="text"
              placeholder="superadmin@enspm.cm"
              size="large"
              class="input-focus"
            />
          </n-form-item>

          <!-- Champ Mot de passe -->
          <n-form-item label="">
            <template #label>
              <div class="flex items-center space-x-2 text-gray-700 font-semibold mb-1">
                <Lock class="w-4 h-4 text-blue-500" />
                <span>Mot de passe</span>
              </div>
            </template>
            <n-input
              v-model:value="form.password"
              type="password"
              show-password-on="click"
              placeholder="Entrez votre mot de passe"
              size="large"
              class="input-focus"
            />
          </n-form-item>

          <!-- Bouton Connexion -->
          <n-button
            type="primary"
            block
            :loading="loading"
            size="large"
            class="mt-6 bg-gradient-to-r from-blue-500 to-cyan-500 hover:from-blue-600 hover:to-cyan-600 font-semibold text-lg"
            @click="handleLogin"
          >
            Se connecter
          </n-button>
        </n-form>
      </n-card>

      <!-- Pied de page -->
      <div class="text-center mt-6 text-gray-600 text-sm">
        <p>© 2026 ENSPM - Tous droits réservés</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes blob {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}

.animation-delay-4000 {
  animation-delay: 4s;
}

:deep(.n-input__input) {
  transition: all 0.3s ease;
}

:deep(.n-input:focus-within) {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}
</style>
