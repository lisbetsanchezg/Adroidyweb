import { test, expect } from '@playwright/test';

/**
 * Pruebas e2e de autenticacion: validaciones de los formularios, registro de
 * un usuario nuevo (correo unico por corrida) e inicio de sesion.
 */
test.describe('Autenticacion', () => {
  test('el login valida el formato del correo', async ({ page }) => {
    await page.goto('/login');
    await page.getByLabel('Correo').fill('correo-invalido');
    await page.getByLabel('Contrasena').fill('12345');
    await page.getByRole('button', { name: 'Entrar' }).click();

    await expect(page.getByText('El correo no tiene un formato valido.')).toBeVisible();
  });

  test('el registro exige todos los campos', async ({ page }) => {
    await page.goto('/registro');
    await page.getByRole('button', { name: 'Registrarme' }).click();

    await expect(page.getByText('Todos los campos son obligatorios.')).toBeVisible();
  });

  test('registra un usuario nuevo y luego inicia sesion', async ({ page }) => {
    const email = `e2e_${Date.now()}@mail.com`;
    const password = 'test1234';

    // Registro
    await page.goto('/registro');
    await page.getByLabel('Nombre').fill('Usuario E2E');
    await page.getByLabel('Correo').fill(email);
    await page.getByLabel('Contrasena').fill(password);
    await page.getByRole('button', { name: 'Registrarme' }).click();

    // Tras registrarse, redirige al login
    await expect(page).toHaveURL(/\/login/);

    // Inicio de sesion con el usuario recien creado
    await page.getByLabel('Correo').fill(email);
    await page.getByLabel('Contrasena').fill(password);
    await page.getByRole('button', { name: 'Entrar' }).click();

    // La sesion queda iniciada: aparece el boton Salir en la navbar
    await expect(page.getByRole('button', { name: 'Salir' })).toBeVisible();
  });
});
