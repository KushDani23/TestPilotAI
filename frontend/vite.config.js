import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Vite config for TestPilot AI frontend
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Proxy API calls to the Spring Boot backend during development.
    // This means /api/... in the frontend will be forwarded to http://localhost:8080/api/...
    // This is cleaner than specifying the full backend URL in every fetch call.
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
