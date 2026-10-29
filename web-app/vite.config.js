import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api/academic':  { target: 'http://localhost:8081', rewrite: p => p.replace(/^\/api\/academic/, '') },
      '/api/analytics': { target: 'http://localhost:8082', rewrite: p => p.replace(/^\/api\/analytics/, '/analytics') },
    },
  },
});
