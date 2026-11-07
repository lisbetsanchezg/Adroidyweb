import api from './api.js';

/**
 * Servicio de autenticacion: llamadas HTTP reales al backend Spring Boot
 * (endpoints /api/auth). Devuelve datos en JSON; no interviene ninguna JSP.
 */

/**
 * Extrae un mensaje de error legible de una respuesta de axios.
 *
 * @param {Object} error error lanzado por axios
 * @param {string} fallback mensaje por defecto
 * @returns {string} mensaje de error para mostrar al usuario
 */
function extractError(error, fallback) {
  if (error.response && error.response.data && error.response.data.error) {
    return error.response.data.error;
  }
  return fallback;
}

/**
 * Inicia sesion contra el backend.
 *
 * @param {string} email    correo del usuario
 * @param {string} password contrasena del usuario
 * @returns {Promise<Object>} datos del usuario autenticado (sin contrasena)
 */
export function login(email, password) {
  return api
    .post('/auth/login', { email, password })
    .then((response) => response.data)
    .catch((error) => {
      throw new Error(extractError(error, 'No se pudo iniciar sesion.'));
    });
}

/**
 * Registra un usuario nuevo en el backend.
 *
 * @param {string} name     nombre del usuario
 * @param {string} email    correo del usuario
 * @param {string} password contrasena del usuario
 * @returns {Promise<Object>} datos del usuario registrado (sin contrasena)
 */
export function register(name, email, password) {
  return api
    .post('/auth/register', { name, email, password })
    .then((response) => response.data)
    .catch((error) => {
      throw new Error(extractError(error, 'No se pudo completar el registro.'));
    });
}
