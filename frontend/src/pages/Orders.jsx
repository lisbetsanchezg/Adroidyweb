import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import OrderHistory from '../components/OrderHistory.jsx';
import { getOrders } from '../services/orderService.js';
import { useAuth } from '../context/AuthContext.jsx';
import './Orders.css';

/**
 * Pagina de historial de pedidos. Consume GET /api/orders/{userId} y muestra
 * la lista mediante {@link OrderHistory}. Si no hay sesion, invita a iniciarla.
 */
function Orders() {
  const { currentUser } = useAuth();
  const [orders, setOrders] = useState([]);
  const [status, setStatus] = useState('loading');

  // Carga los pedidos del usuario cuando hay sesion
  useEffect(() => {
    if (!currentUser) {
      return;
    }
    getOrders(currentUser.id)
      .then((data) => {
        setOrders(data);
        setStatus('ok');
      })
      .catch(() => setStatus('error'));
  }, [currentUser]);

  if (!currentUser) {
    return (
      <main className="container">
        <h1 className="page-title">Mis pedidos</h1>
        <div className="orders-prompt">
          <p>Inicia sesion para ver tus pedidos.</p>
          <Link to="/login" className="btn-primary">
            Iniciar sesion
          </Link>
        </div>
      </main>
    );
  }

  return (
    <main className="container">
      <h1 className="page-title">Mis pedidos</h1>
      {status === 'loading' && <p className="orders-msg">Cargando pedidos...</p>}
      {status === 'error' && (
        <p className="orders-msg orders-error">No se pudieron cargar los pedidos.</p>
      )}
      {status === 'ok' && <OrderHistory orders={orders} />}
    </main>
  );
}

export default Orders;
