import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Configuracion de Vite para el frontend de BeautyShop.
// El proxy redirige las llamadas '/api' y las fotos de productos '/images'
// al backend Spring Boot (puerto 8090), evitando problemas de CORS durante el desarrollo.
export default defineConfig({
  plugins: [react()],
  server: {
    host: '127.0.0.1',
    port: 4300,
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8090',
        changeOrigin: true
      },
      '/images': {
        target: 'http://127.0.0.1:8090',
        changeOrigin: true
      }
    }
  }
});
