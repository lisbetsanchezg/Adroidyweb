import api from './api.js';

/**
 * Servicio del carrito: encapsula las llamadas HTTP al backend
 * relacionadas con el recurso /api/cart/{userId}.
 */

/**
 * Obtiene el contenido del carrito de un usuario (con el total calculado).
 *
 * @param {number} userId identificador del usuario
 * @returns {Promise<Object>} carrito con { idUsuario, items, total }
 */
export function getCart(userId) {
  return api.get(`/cart/${userId}`).then((response) => response.data);
}

/**
 * Agrega un producto al carrito del usuario.
 *
 * @param {number} userId     identificador del usuario
 * @param {number} idProducto identificador del producto
 * @param {number} cantidad   cantidad a agregar
 * @returns {Promise<Object>} el carrito actualizado
 */
export function addItem(userId, idProducto, cantidad) {
  return api
    .post(`/cart/${userId}/items`, { idProducto, cantidad })
    .then((response) => response.data);
}

/**
 * Actualiza la cantidad de un producto del carrito.
 *
 * @param {number} userId    identificador del usuario
 * @param {number} productId identificador del producto
 * @param {number} cantidad  nueva cantidad
 * @returns {Promise<Object>} el carrito actualizado
 */
export function updateItem(userId, productId, cantidad) {
  return api
    .put(`/cart/${userId}/items/${productId}`, { cantidad })
    .then((response) => response.data);
}

/**
 * Quita un producto del carrito.
 *
 * @param {number} userId    identificador del usuario
 * @param {number} productId identificador del producto
 * @returns {Promise<Object>} el carrito actualizado
 */
export function removeItem(userId, productId) {
  return api.delete(`/cart/${userId}/items/${productId}`).then((response) => response.data);
}
