import { Link, useNavigate } from 'react-router-dom';
import RegisterForm from '../components/RegisterForm.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import './Login.css';

/**
 * Pagina de registro. Usa {@link RegisterForm} y, al enviar, crea el usuario
 * contra POST /api/auth/register a traves de AuthContext. Tras registrarse,
 * redirige al login.
 */
function Register() {
  const navigate = useNavigate();
  const { register } = useAuth();
  const { showToast } = useToast();

  /**
   * Registra al usuario en el backend; si tiene exito, redirige al login.
   *
   * @param {string} name     nombre del usuario
   * @param {string} email    correo del usuario
   * @param {string} password contrasena del usuario
   * @returns {Promise} promesa del proceso de registro
   */
  function handleRegister(name, email, password) {
    return register(name, email, password).then(() => {
      showToast('Cuenta creada. Ahora inicia sesion.');
      navigate('/login');
    });
  }

  return (
    <div className="login-wrapper">
      <div className="login-header">Beauty Shop</div>
      <div className="login-card">
        <h2>Crear cuenta</h2>
        <p className="login-subtitle">Registrate para empezar a comprar.</p>

        <RegisterForm onSubmit={handleRegister} />

        <p className="login-toggle">
          ¿Ya tienes cuenta? <Link to="/login">Iniciar sesion</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;
