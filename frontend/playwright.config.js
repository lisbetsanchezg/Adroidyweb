import { defineConfig, devices } from '@playwright/test';

/**
 * Configuracion de las pruebas e2e de BeautyShop.
 *
 * Requisitos para ejecutarlas:
 *  - El backend Spring Boot debe estar corriendo en el puerto 8090
 *    (con MySQL y las tablas cargadas, incluido el usuario demo).
 *  - El frontend se levanta automaticamente (webServer) si no esta activo.
 *
 * Ejecutar con:  npx playwright test
 */
export default defineConfig({
  testDir: './e2e',
  timeout: 30000,
  expect: { timeout: 10000 },
  fullyParallel: false,
  workers: 1,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://127.0.0.1:4300',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure'
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] }
    }
  ],
  // Levanta el frontend si no esta corriendo; reutiliza el que ya este activo.
  webServer: {
    command: 'npm run dev',
    url: 'http://127.0.0.1:4300',
    reuseExistingServer: true,
    timeout: 60000
  }
});
