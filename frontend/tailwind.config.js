/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#00236f',
          container: '#1e3a8a',
        },
        secondary: '#00687a',
        cyan: '#06b6d4',
        accent: '#06b6d4',
        app: {
          light: '#faf8ff',
          dark: '#0b1326',
        },
        sidebar: {
          light: '#f4f3fa',
          dark: '#131b2e',
        },
        header: {
          light: 'rgba(250, 248, 255, 0.8)',
          dark: 'rgba(11, 19, 38, 0.8)',
        },
        card: {
          light: '#ffffff',
          dark: '#171f33',
        }
      },
      fontFamily: {
        inter: ['Inter', 'sans-serif'],
        manrope: ['Manrope', 'sans-serif'],
      },
      boxShadow: {
        ambient: '0 8px 32px rgba(0, 35, 111, 0.06)',
      }
    },
  },
  plugins: [],
}
