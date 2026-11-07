import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import { createOrder } from '../services/orderService.js';
import { formatCurrency } from '../utils/format.js';
import './Checkout.css';

/** Expresion regular basica para validar el formato del correo. */
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Pagina de pago (SIMULADA). Muestra el formulario de envio y tarjeta junto
 * con el resumen del pedido tomado del carrito. Los datos son ficticios: no
 * se envian a ningun servidor ni se procesa ningun pago real.
 */
function Checkout() {
  const navigate = useNavigate();
  const { items, total, refreshCart } = useCart();
  const { currentUser } = useAuth();
  const { showToast } = useToast();
  const [form, setForm] = useState({
    name: '',
    email: '',
    address: '',
    card: '',
    exp: '',
    cvv: ''
  });

  /**
   * Actualiza el estado del formulario al escribir en un campo.
   *
   * @param {Object} event evento de cambio del input
   */
  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  /**
   * Valida los datos y confirma la compra (simulada). Vacia el carrito
   * y regresa al inicio si todo es correcto.
   */
  function handlePay() {
    if (!currentUser) {
      showToast('Inicia sesion para finalizar la compra.');
      navigate('/login');
      return;
    }

    const card = form.card.replace(/\s+/g, '');

    if (!form.name || !form.email || !form.address || !card || !form.exp || !form.cvv) {
      showToast('Completa todos los campos.');
      return;
    }
    if (!EMAIL_REGEX.test(form.email)) {
      showToast('Correo invalido.');
      return;
    }
    if (card.length < 15 || !/^\d+$/.test(card) || !/^\d{2}\/\d{2}$/.test(form.exp) || !/^\d{3}$/.test(form.cvv)) {
      showToast('Revisa los datos de la tarjeta.');
      return;
    }

    // Pago simulado: crea el pedido real en el backend (que vacia el carrito)
    createOrder(currentUser.id)
      .then(() => {
        showToast('✅ Pago autorizado. ¡Gracias por tu compra!');
        return refreshCart();
      })
      .then(() => setTimeout(() => navigate('/pedidos'), 1000))
      .catch((error) => showToast(error.response?.data?.error || 'No se pudo procesar el pedido.'));
  }

  return (
    <main className="container">
      <h1 className="page-title">Finaliza tu compra</h1>

      <div className="checkout-grid">
        <section className="checkout-card">
          <h2>Informacion de pago</h2>
          <p className="checkout-alert">Datos ficticios. Usa numeros de prueba.</p>

          <div className="input-group">
            <label htmlFor="name">Nombre completo</label>
            <input id="name" name="name" type="text" placeholder="Ej. Lisbet Sanchez" value={form.name} onChange={handleChange} />
          </div>
          <div className="input-group">
            <label htmlFor="email">Correo electronico</label>
            <input id="email" name="email" type="email" placeholder="ejemplo@mail.com" value={form.email} onChange={handleChange} />
          </div>
          <div className="input-group">
            <label htmlFor="address">Direccion de envio</label>
            <input id="address" name="address" type="text" placeholder="Calle 123, Ciudad" value={form.address} onChange={handleChange} />
          </div>

          <h2>Datos de tarjeta</h2>
          <p className="checkout-badge">💳 VISA • MASTERCARD • AMEX</p>

          <div className="input-group">
            <label htmlFor="card">Numero de tarjeta</label>
            <input id="card" name="card" type="text" maxLength="16" placeholder="1234 5678 9012 3456" value={form.card} onChange={handleChange} />
          </div>
          <div className="checkout-row">
            <div className="input-group">
              <label htmlFor="exp">Expiracion</label>
              <input id="exp" name="exp" type="text" maxLength="5" placeholder="MM/AA" value={form.exp} onChange={handleChange} />
            </div>
            <div className="input-group">
              <label htmlFor="cvv">CVV</label>
              <input id="cvv" name="cvv" type="text" maxLength="3" placeholder="321" value={form.cvv} onChange={handleChange} />
            </div>
          </div>

          <button type="button" className="btn-primary btn-block" onClick={handlePay}>
            Pagar ahora
          </button>
        </section>

        <aside className="checkout-card">
          <h2>Resumen del pedido</h2>
          {items.length === 0 ? (
            <div className="checkout-empty">Tu carrito esta vacio.</div>
          ) : (
            items.map((item) => (
              <div className="checkout-summary-item" key={item.idProducto}>
                <span>
                  {item.nombre} x{item.cantidad}
                </span>
                <span>{formatCurrency(item.subtotal)}</span>
              </div>
            ))
          )}
          <div className="checkout-total">
            <span>Total</span>
            <span>{formatCurrency(total)}</span>
          </div>
        </aside>
      </div>
    </main>
  );
}

export default Checkout;
