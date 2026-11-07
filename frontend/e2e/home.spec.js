import { test, expect } from '@playwright/test';

/**
 * Pruebas e2e de la pagina de inicio: carga de productos desde la API
 * y filtrado con el buscador controlado.
 */
test.describe('Home', () => {
  test('muestra el hero y carga productos desde la API', async ({ page }) => {
    await page.goto('/');
    await expect(
      page.getByRole('heading', { name: /Productos que simplifican tu rutina/i })
    ).toBeVisible();
    // Al menos un producto del backend debe aparecer
    await expect(page.getByText('Rubor en Polvo Coral')).toBeVisible();
  });

  test('el buscador filtra los productos por nombre', async ({ page }) => {
    await page.goto('/');
    await page.getByPlaceholder('Buscar productos...').fill('rubor');

    // Queda el que coincide y desaparecen los demas
    await expect(page.getByText('Rubor en Polvo Coral')).toBeVisible();
    await expect(page.getByText('Labial Mate Rojo Pasion')).toHaveCount(0);
  });
});
