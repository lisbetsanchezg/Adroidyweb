import { formatCurrency } from '../utils/format.js';
import './OrderHistory.css';

/**
 * Da formato legible a una fecha ISO (por ejemplo "25/07/2026 09:44").
 *
 * @param {string} isoDate fecha en formato ISO
 * @returns {string} fecha formateada
 */
function formatDate(isoDate) {
  const date = new Date(isoDate);
  return date.toLocaleString('es-CO', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

/**
 * Historial de pedidos: muestra la lista de pedidos de un usuario.
 * Es un componente de presentacion; recibe los pedidos ya consultados.
 *
 * @param {Object} props
 * @param {Array} props.orders lista de pedidos (idPedido, fecha, estado, total)
 */
function OrderHistory({ orders }) {
  if (orders.length === 0) {
    return <p className="order-history-empty">Aun no tienes pedidos.</p>;
  }

  return (
    <div className="order-history">
      {orders.map((order) => (
        <article className="order-card" key={order.idPedido}>
          <div className="order-card-head">
            <span className="order-card-id">Pedido #{order.idPedido}</span>
            <span className={`order-status order-status-${order.estado}`}>{order.estado}</span>
          </div>
          <p className="order-card-date">{formatDate(order.fecha)}</p>
          <p className="order-card-total">{formatCurrency(order.total)}</p>
        </article>
      ))}
    </div>
  );
}

export default OrderHistory;
