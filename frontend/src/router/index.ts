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
      meta: { layout: 'public' },
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
      path: '/parametres',
      name: 'parametres',
      component: () => import('@/views/parametres/ParametresList.vue'),
    },
  ],
})

export default router
