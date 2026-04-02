import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginPage.vue'),
      meta: { layout: 'public', public: true },
    },
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
    },
    {
      path: '/etudiants',
      redirect: '/etudiants/liste',
    },
    {
      path: '/etudiants/liste',
      name: 'etudiants-list',
      component: () => import('@/views/etudiants/EtudiantList.vue'),
    },
    {
      path: '/etudiants/importer',
      name: 'etudiants-import',
      component: () => import('@/views/etudiants/EtudiantImport.vue'),
    },
    {
      path: '/entreprises',
      name: 'entreprises',
      component: () => import('@/views/entreprises/EntrepriseList.vue'),
    },
    {
      path: '/departements',
      name: 'departements',
      component: () => import('@/views/departements/DepartementList.vue'),
    },
    {
      path: '/specialites',
      name: 'specialites',
      component: () => import('@/views/specialites/SpecialiteList.vue'),
    },
    {
      path: '/stages',
      name: 'stages',
      component: () => import('@/views/stages/StageList.vue'),
    },
    {
      path: '/declaration-stage',
      name: 'declaration-stage',
      component: () => import('@/views/stages/StudentStageDeclare.vue'),
      meta: { layout: 'public', public: true },
    },
    {
      path: '/annees-academiques',
      name: 'annees-academiques',
      component: () => import('@/views/annees-academiques/AnneeAcademiqueList.vue'),
    },
    {
      path: '/niveaux',
      name: 'niveaux',
      component: () => import('@/views/niveaux/NiveauList.vue'),
    },
    {
      path: '/type-stages',
      name: 'type-stages',
      component: () => import('@/views/type-stages/TypeStageList.vue'),
    },
    {
      path: '/periode-stages',
      name: 'periode-stages',
      component: () => import('@/views/periode-stages/PeriodeStageList.vue'),
    },
    {
      path: '/parcours',
      name: 'parcours',
      component: () => import('@/views/parcours/ParcoursList.vue'),
    },
    {
      path: '/inscriptions',
      name: 'inscriptions',
      component: () => import('@/views/inscriptions/InscriptionList.vue'),
    },
    {
      path: '/baremes',
      name: 'baremes',
      component: () => import('@/views/baremes/BaremeList.vue'),
    },
    {
      path: '/criteres',
      name: 'criteres',
      component: () => import('@/views/criteres/CritereList.vue'),
    },
    {
      path: '/bareme-criteres',
      name: 'bareme-criteres',
      component: () => import('@/views/bareme-criteres/BaremeCritereList.vue'),
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: () => import('@/views/notifications/NotificationList.vue'),
    },
    {
      path: '/parametres',
      name: 'parametres',
      component: () => import('@/views/parametres/ParametresList.vue'),
    },
    {
      path: '/utilisateurs',
      name: 'users-management',
      component: () => import('@/views/users/UserManagement.vue'),
      meta: { requiresSuperAdmin: true },
    },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('auth_token')
  const user = localStorage.getItem('auth_user')
  let currentRole: string | null = null
  if (user) {
    try {
      currentRole = JSON.parse(user).role
    } catch {
      currentRole = null
    }
  }
  const isPublic = Boolean(to.meta.public)

  if (!isPublic && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.name === 'login' && token) {
    return { name: 'dashboard' }
  }

  if (to.meta.requiresSuperAdmin && currentRole !== 'SUPER_ADMIN') {
    return { name: 'dashboard' }
  }

  return true
})

export default router
