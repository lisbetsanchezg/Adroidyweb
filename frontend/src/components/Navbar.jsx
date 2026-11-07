import { NavLink, Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import './Navbar.css';

/**
 * Barra de navegacion superior comun a todas las paginas.
 * Muestra el logo, los enlaces, un contador del carrito y, segun el estado de
 * la sesion, el boton de Login o el nombre del usuario con la opcion de salir.
 */
function Navbar() {
  const navigate = useNavigate();
  const { totalCount } = useCart();
  const { currentUser, logout } = useAuth();
  const { showToast } = useToast();

  /** Cierra la sesion y regresa al inicio. */
  function handleLogout() {
    logout();
    showToast('Sesion cerrada.');
    navigate('/');
  }

  return (
    <header className="navbar">
      <Link to="/" className="navbar-logo">
        Beauty Shop
      </Link>
      <nav className="navbar-links">
        <NavLink to="/" end>
          Inicio
        </NavLink>
        <NavLink to="/catalogo">Productos</NavLink>
        <NavLink to="/carrito">
          Carrito
          {totalCount > 0 && <span className="navbar-badge">{totalCount}</span>}
        </NavLink>
        {currentUser && <NavLink to="/pedidos">Pedidos</NavLink>}

        {currentUser ? (
          <span className="navbar-user">
            <span className="navbar-username">Hola, {currentUser.name}</span>
            <button type="button" className="navbar-logout" onClick={handleLogout}>
              Salir
            </button>
          </span>
        ) : (
          <>
            <NavLink to="/login" className="navbar-login">
              Login
            </NavLink>
            <NavLink to="/registro">Registro</NavLink>
          </>
        )}
      </nav>
    </header>
  );
}

export default Navbar;
