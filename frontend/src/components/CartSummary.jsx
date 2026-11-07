import { formatCurrency } from '../utils/format.js';
import './CartSummary.css';

/**
 * Resumen del carrito: muestra las lineas con controles de cantidad, el boton
 * para quitar cada producto, el total y el boton para finalizar la compra.
 * Es un componente de presentacion: la logica (llamadas a la API) vive en la
 * pagina que lo usa a traves de los callbacks recibidos por props.
 *
 * @param {Object} props
 * @param {Array} props.items          lineas del carrito (enriquecidas)
 * @param {number} props.total         total del carrito
 * @param {Function} props.onChangeQuantity callback (idProducto, delta)
 * @param {Function} props.onRemove    callback (idProducto)
 * @param {Function} props.onCheckout  callback para crear el pedido
 */
function CartSummary({ items, total, onChangeQuantity, onRemove, onCheckout }) {
  if (items.length === 0) {
    return <p className="cart-summary-empty">Tu carrito esta vacio.</p>;
  }

  return (
    <div className="cart-summary">
      <div className="cart-summary-list">
        {items.map((item) => (
          <div className="cart-summary-item" key={item.idProducto}>
            <div className="cart-summary-info">
              <span className="cart-summary-name">{item.nombre}</span>
              <span className="cart-summary-unit">{formatCurrency(item.precioUnitario)} c/u</span>
            </div>
            <div className="cart-summary-qty">
              <button type="button" onClick={() => onChangeQuantity(item.idProducto, -1)}>
                −
              </button>
              <span>{item.cantidad}</span>
              <button type="button" onClick={() => onChangeQuantity(item.idProducto, 1)}>
                +
              </button>
            </div>
            <span className="cart-summary-subtotal">{formatCurrency(item.subtotal)}</span>
            <button
              type="button"
              className="cart-summary-remove"
              aria-label="Quitar"
              onClick={() => onRemove(item.idProducto)}
            >
              ✕
            </button>
          </div>
        ))}
      </div>

      <div className="cart-summary-footer">
        <span className="cart-summary-total">Total: {formatCurrency(total)}</span>
        <button type="button" className="btn-primary" onClick={onCheckout}>
          Finalizar compra
        </button>
      </div>
    </div>
  );
}

export default CartSummary;
