<script setup lang="ts">
withDefaults(defineProps<{
  size?: number
  showText?: boolean
}>(), {
  size: 96,
  showText: false
})
</script>

<template>
  <div class="flex flex-col items-center">
    <img
      src="/logo-enspm.png"
      alt="Logo ENSPM"
      :style="{ width: `${size}px`, height: `${size}px` }"
      class="object-contain"
    />
    <p v-if="showText" class="mt-2 text-sm text-gray-600 dark:text-gray-300">
      ENSPM
    </p>
  </div>
</template>
