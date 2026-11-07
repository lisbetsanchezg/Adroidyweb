import { Link, useNavigate } from 'react-router-dom';
import CartSummary from '../components/CartSummary.jsx';
import { useCart } from '../context/CartContext.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import { createOrder } from '../services/orderService.js';
import './Cart.css';

/**
 * Pagina del carrito. Muestra el contenido del carrito del usuario (desde el
 * backend) mediante {@link CartSummary} y permite finalizar la compra creando
 * un pedido. Si no hay sesion, invita a iniciarla.
 */
function Cart() {
  const navigate = useNavigate();
  const { items, total, changeQuantity, removeItem, refreshCart } = useCart();
  const { currentUser } = useAuth();
  const { showToast } = useToast();

  // Sin sesion no hay carrito asociado
  if (!currentUser) {
    return (
      <main className="container">
        <h1 className="page-title">Tu carrito</h1>
        <div className="cart-login-prompt">
          <p>Inicia sesion para ver y gestionar tu carrito.</p>
          <Link to="/login" className="btn-primary">
            Iniciar sesion
          </Link>
        </div>
      </main>
    );
  }

  /**
   * Crea el pedido a partir del carrito y redirige al historial de pedidos.
   */
  function handleCheckout() {
    createOrder(currentUser.id)
      .then(() => {
        showToast('¡Pedido creado con exito!');
        return refreshCart();
      })
      .then(() => navigate('/pedidos'))
      .catch((error) => showToast(error.response?.data?.error || 'No se pudo crear el pedido.'));
  }

  return (
    <main className="container">
      <h1 className="page-title">Tu carrito</h1>
      <CartSummary
        items={items}
        total={total}
        onChangeQuantity={changeQuantity}
        onRemove={removeItem}
        onCheckout={handleCheckout}
      />
    </main>
  );
}

export default Cart;
