import { Link, useNavigate } from 'react-router-dom';
import LoginForm from '../components/LoginForm.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import './Login.css';

/**
 * Pagina de inicio de sesion. Usa {@link LoginForm} y, al enviar, valida
 * las credenciales contra POST /api/auth/login a traves de AuthContext.
 */
function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { showToast } = useToast();

  /**
   * Envia las credenciales al backend; si son validas, redirige al inicio.
   *
   * @param {string} email    correo del usuario
   * @param {string} password contrasena del usuario
   * @returns {Promise} promesa del proceso de login
   */
  function handleLogin(email, password) {
    return login(email, password).then((user) => {
      showToast(`Bienvenido(a), ${user.name}`);
      navigate('/');
    });
  }

  return (
    <div className="login-wrapper">
      <div className="login-header">Beauty Shop</div>
      <div className="login-card">
        <h2>Iniciar sesion</h2>
        <p className="login-subtitle">Accede para ver tu carrito y tus pedidos.</p>

        <LoginForm onSubmit={handleLogin} />

        <p className="login-toggle">
          ¿No tienes cuenta? <Link to="/registro">Crear cuenta</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;
