import api from './api.js';

/**
 * Servicio de pedidos: encapsula las llamadas HTTP al backend
 * relacionadas con el recurso /api/orders.
 */

/**
 * Crea un pedido a partir del carrito actual del usuario (y lo vacia).
 *
 * @param {number} userId identificador del usuario
 * @returns {Promise<Object>} el detalle del pedido creado
 */
export function createOrder(userId) {
  return api.post(`/orders/${userId}`).then((response) => response.data);
}

/**
 * Lista los pedidos de un usuario.
 *
 * @param {number} userId identificador del usuario
 * @returns {Promise<Array>} lista de pedidos
 */
export function getOrders(userId) {
  return api.get(`/orders/${userId}`).then((response) => response.data);
}

/**
 * Obtiene el detalle de un pedido (cabecera y lineas).
 *
 * @param {number} orderId identificador del pedido
 * @returns {Promise<Object>} el detalle del pedido
 */
export function getOrderDetail(orderId) {
  return api.get(`/orders/detail/${orderId}`).then((response) => response.data);
}

/**
 * Cambia el estado de un pedido.
 *
 * @param {number} orderId identificador del pedido
 * @param {string} estado  nuevo estado
 * @returns {Promise<Object>} el pedido actualizado
 */
export function updateStatus(orderId, estado) {
  return api.patch(`/orders/${orderId}/status`, { estado }).then((response) => response.data);
}
