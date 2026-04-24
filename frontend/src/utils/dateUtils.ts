import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

dayjs.extend(utc)
dayjs.extend(timezone)

/**
 * Convertit un timestamp local (ms) en string ISO format "yyyy-MM-dd" pour l'API
 * 
 * @param ts Timestamp en millisecondes (local timezone)
 * @returns String au format "yyyy-MM-dd" ou null
 */
export function formatDateForApi(ts: number | null | undefined): string | null {
  if (!ts) return null
  // Le timestamp est déjà en timezone locale, on le formate directement
  return dayjs(ts).format('YYYY-MM-DD')
}

/**
 * Convertit une string ISO date "yyyy-MM-dd" de l'API en timestamp local (ms)
 * 
 * @param dateStr String au format "yyyy-MM-dd" (UTC depuis API)
 * @returns Timestamp en millisecondes (interpreted as local midnight)
 */
export function parseApiDate(dateStr: string | null | undefined): number | null {
  if (!dateStr) return null
  // Parse comme date UTC à minuit, puis retourne le timestamp
  return dayjs.utc(dateStr, 'YYYY-MM-DD').valueOf()
}

/**
 * Formate une date pour affichage
 * 
 * @param ts Timestamp ou date
 * @param format Format dayjs (défaut: 'DD/MM/YYYY')
 * @returns String formatée
 */
export function format(
  ts: number | string | Date | null | undefined,
  format: string = 'DD/MM/YYYY'
): string {
  if (!ts) return ''
  if (typeof ts === 'string') {
    // Si c'est une string ISO de l'API, la parser en UTC puis afficher
    return dayjs.utc(ts).format(format)
  }
  return dayjs(ts).format(format)
}

/**
 * Convertit un timestamp local en ISO string pour debug/logging
 */
export function toISO(ts: number | null | undefined): string | null {
  if (!ts) return null
  return dayjs(ts).toISOString()
}
