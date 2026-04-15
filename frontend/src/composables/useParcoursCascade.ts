import { computed, ref, watch, type Ref } from 'vue'
import type { ParcoursDTO } from '@/api/ParcoursService'

export type CascadeOption = { label: string, value: number }

export interface ParcoursCatalogEntry {
  id: number
  departementId: number
  departementLabel: string
  niveauId: number
  niveauLabel: string
  specialiteId: number
  specialiteLabel: string
  parcoursLabel: string
}

export interface ParcoursCascadeState {
  departementId: Ref<number | null>
  niveauId: Ref<number | null>
  specialiteId: Ref<number | null>
  departementOptions: Readonly<Ref<CascadeOption[]>>
  niveauOptions: Readonly<Ref<CascadeOption[]>>
  specialiteOptions: Readonly<Ref<CascadeOption[]>>
  resolvedParcoursId: Readonly<Ref<number | null>>
  resolvedParcoursLabel: Readonly<Ref<string | null>>
  resetSelection: () => void
  setSelectionFromParcoursId: (parcoursId: number | null | undefined) => void
}

const compareLabels = (left: string, right: string) => left.localeCompare(right, 'fr', { sensitivity: 'base' })

const formatCodeLabel = (code?: string | null, label?: string | null) => code || label || '-'

const buildUniqueOptions = (
  items: ParcoursCatalogEntry[],
  valueSelector: (item: ParcoursCatalogEntry) => number,
  labelSelector: (item: ParcoursCatalogEntry) => string
) => {
  const uniqueOptions = new Map<number, string>()

  items.forEach((item) => {
    const value = valueSelector(item)
    if (!uniqueOptions.has(value)) {
      uniqueOptions.set(value, labelSelector(item))
    }
  })

  return Array.from(uniqueOptions.entries())
    .map(([value, label]) => ({ value, label }))
    .sort((left, right) => compareLabels(left.label, right.label))
}

export const mapParcoursCatalog = (items: ParcoursDTO[]) => {
  return items
    .filter((item): item is ParcoursDTO & { id: number, departementId: number, niveauId: number, specialiteId: number } => {
      return typeof item.id === 'number' &&
        typeof item.departementId === 'number' &&
        typeof item.niveauId === 'number' &&
        typeof item.specialiteId === 'number'
    })
    .map((item) => ({
      id: item.id,
      departementId: item.departementId,
      departementLabel: formatCodeLabel(item.departementCode, item.departementIntitule),
      niveauId: item.niveauId,
      niveauLabel: item.niveauLibelle || '-',
      specialiteId: item.specialiteId,
      specialiteLabel: formatCodeLabel(item.specialiteCode, item.specialiteIntitule),
      parcoursLabel: item.libelle || `${formatCodeLabel(item.specialiteCode, item.specialiteIntitule)} - ${item.niveauLibelle || '-'}`
    }))
}

export const useParcoursCascade = (catalog: Ref<ParcoursCatalogEntry[]>) => {
  const departementId = ref<number | null>(null)
  const niveauId = ref<number | null>(null)
  const specialiteId = ref<number | null>(null)
  const pendingParcoursId = ref<number | null>(null)

  const departementOptions = computed(() => buildUniqueOptions(
    catalog.value.filter((item) =>
      (niveauId.value == null || item.niveauId === niveauId.value) &&
      (specialiteId.value == null || item.specialiteId === specialiteId.value)
    ),
    (item) => item.departementId,
    (item) => item.departementLabel
  ))

  const niveauOptions = computed(() => buildUniqueOptions(
    catalog.value.filter((item) =>
      (departementId.value == null || item.departementId === departementId.value) &&
      (specialiteId.value == null || item.specialiteId === specialiteId.value)
    ),
    (item) => item.niveauId,
    (item) => item.niveauLabel
  ))

  const specialiteOptions = computed(() => buildUniqueOptions(
    catalog.value.filter((item) =>
      (departementId.value == null || item.departementId === departementId.value) &&
      (niveauId.value == null || item.niveauId === niveauId.value)
    ),
    (item) => item.specialiteId,
    (item) => item.specialiteLabel
  ))

  const resolvedParcours = computed(() => {
    if (departementId.value == null || niveauId.value == null || specialiteId.value == null) {
      return null
    }

    return catalog.value.find((item) => item.departementId === departementId.value &&
      item.niveauId === niveauId.value &&
      item.specialiteId === specialiteId.value) || null
  })

  const resetSelection = () => {
    pendingParcoursId.value = null
    departementId.value = null
    niveauId.value = null
    specialiteId.value = null
  }

  const setSelectionFromParcoursId = (parcoursId: number | null | undefined) => {
    if (parcoursId == null) {
      resetSelection()
      return
    }

    pendingParcoursId.value = parcoursId

    const match = catalog.value.find((item) => item.id === parcoursId)
    if (!match) {
      return
    }

    pendingParcoursId.value = null
    departementId.value = match.departementId
    niveauId.value = match.niveauId
    specialiteId.value = match.specialiteId
  }

  watch(catalog, () => {
    if (pendingParcoursId.value != null) {
      setSelectionFromParcoursId(pendingParcoursId.value)
    }
  })

  watch([departementOptions, niveauOptions, specialiteOptions], () => {
    if (
      departementId.value != null &&
      !departementOptions.value.some((option) => option.value === departementId.value)
    ) {
      departementId.value = null
    }

    if (
      niveauId.value != null &&
      !niveauOptions.value.some((option) => option.value === niveauId.value)
    ) {
      niveauId.value = null
    }

    if (
      specialiteId.value != null &&
      !specialiteOptions.value.some((option) => option.value === specialiteId.value)
    ) {
      specialiteId.value = null
    }
  }, { immediate: true })

  return {
    departementId,
    niveauId,
    specialiteId,
    departementOptions,
    niveauOptions,
    specialiteOptions,
    resolvedParcoursId: computed(() => resolvedParcours.value?.id ?? null),
    resolvedParcoursLabel: computed(() => resolvedParcours.value?.parcoursLabel ?? null),
    resetSelection,
    setSelectionFromParcoursId
  }
}