import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '@/views/Dashboard.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
    },
    {
      path: '/etudiants',
      name: 'etudiants',
      component: () => import('@/views/etudiants/EtudiantList.vue'),
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
  ],
})

export default router
