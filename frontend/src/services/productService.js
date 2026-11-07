import api from './api.js';

/**
 * Servicio de productos: encapsula las llamadas HTTP al backend
 * relacionadas con el recurso /api/products.
 */

/**
 * Obtiene la lista completa de productos.
 *
 * @returns {Promise<Array>} arreglo de productos
 */
export function getProducts() {
  return api.get('/products').then((response) => response.data);
}

/**
 * Obtiene un producto por su identificador.
 *
 * @param {number} id identificador del producto
 * @returns {Promise<Object>} el producto encontrado
 */
export function getProductById(id) {
  return api.get(`/products/${id}`).then((response) => response.data);
}
