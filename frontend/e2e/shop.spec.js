import { test, expect } from '@playwright/test';

/** Credenciales del usuario demo sembrado en la base de datos. */
const DEMO_EMAIL = 'demo@beautyshop.com';
const DEMO_PASSWORD = 'demo1234';

/**
 * Inicia sesion con el usuario demo a traves de la interfaz.
 *
 * @param {import('@playwright/test').Page} page pagina de Playwright
 */
async function loginAsDemo(page) {
  await page.goto('/login');
  await page.getByLabel('Correo').fill(DEMO_EMAIL);
  await page.getByLabel('Contrasena').fill(DEMO_PASSWORD);
  await page.getByRole('button', { name: 'Entrar' }).click();
  await expect(page.getByRole('button', { name: 'Salir' })).toBeVisible();
}

/**
 * Prueba e2e del flujo de compra completo: login, agregar al carrito,
 * verlo en el carrito y finalizar la compra generando un pedido.
 */
test.describe('Flujo de compra', () => {
  test('login, agregar al carrito, finalizar y ver el pedido', async ({ page }) => {
    await loginAsDemo(page);

    // Agrega el primer producto del listado al carrito
    await page.goto('/');
    await page.getByRole('button', { name: 'Agregar' }).first().click();
    await expect(page.getByText(/agregado al carrito/i)).toBeVisible();

    // El carrito muestra contenido y permite finalizar la compra
    await page.goto('/carrito');
    const finalizar = page.getByRole('button', { name: 'Finalizar compra' });
    await expect(finalizar).toBeVisible();

    // Finaliza la compra -> se crea el pedido y redirige al historial
    await finalizar.click();
    await expect(page).toHaveURL(/\/pedidos/);
    await expect(page.getByText(/Pedido #\d+/).first()).toBeVisible();
  });

  test('el carrito pide iniciar sesion si no hay usuario', async ({ page }) => {
    await page.goto('/carrito');
    await expect(page.getByText('Inicia sesion para ver y gestionar tu carrito.')).toBeVisible();
  });
});
