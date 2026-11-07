import { useState } from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import './LoginDrawer.css';

/**
 * Panel lateral (drawer) para iniciar sesion o crear cuenta.
 * Reproduce el comportamiento del drawer del mockup de inicio.
 *
 * @param {Object} props
 * @param {boolean} props.open indica si el drawer esta abierto
 * @param {Function} props.onClose callback para cerrar el drawer
 */
function LoginDrawer({ open, onClose }) {
  const { login, register } = useAuth();
  const { showToast } = useToast();
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', email: '', password: '' });

  /**
   * Actualiza el estado del formulario al escribir en un campo.
   *
   * @param {Object} event evento de cambio del input
   */
  function handleChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  /** Cierra el drawer y reinicia el formulario al modo de inicio de sesion. */
  function handleClose() {
    setMode('login');
    setForm({ name: '', email: '', password: '' });
    onClose();
  }

  /** Envia el formulario de inicio de sesion contra el backend. */
  function handleLogin() {
    login(form.email, form.password)
      .then((user) => {
        showToast(`Bienvenido(a), ${user.name}`);
        handleClose();
      })
      .catch((error) => showToast(error.message));
  }

  /** Envia el formulario de registro contra el backend. */
  function handleRegister() {
    register(form.name, form.email, form.password)
      .then(() => {
        showToast('Cuenta creada. Ahora inicia sesion.');
        setMode('login');
        setForm({ name: '', email: form.email, password: '' });
      })
      .catch((error) => showToast(error.message));
  }

  return (
    <>
      <div className={`drawer-backdrop ${open ? 'show' : ''}`} onClick={handleClose} />
      <aside className={`drawer ${open ? 'show' : ''}`}>
        <div className="drawer-header">
          <h3>{mode === 'login' ? 'Iniciar sesion' : 'Crear cuenta'}</h3>
          <button type="button" className="drawer-close" aria-label="Cerrar" onClick={handleClose}>
            ×
          </button>
        </div>

        <div className="drawer-content">
          {mode === 'register' && (
            <div className="input-group">
              <label htmlFor="drawer-name">Nombre</label>
              <input
                id="drawer-name"
                name="name"
                type="text"
                placeholder="Tu nombre"
                value={form.name}
                onChange={handleChange}
              />
            </div>
          )}

          <div className="input-group">
            <label htmlFor="drawer-email">Correo</label>
            <input
              id="drawer-email"
              name="email"
              type="email"
              placeholder="ejemplo@mail.com"
              value={form.email}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="drawer-password">Contrasena</label>
            <input
              id="drawer-password"
              name="password"
              type="password"
              placeholder="••••••••"
              value={form.password}
              onChange={handleChange}
            />
          </div>

          {mode === 'login' ? (
            <>
              <button type="button" className="btn-primary btn-block" onClick={handleLogin}>
                Entrar
              </button>
              <p className="drawer-link">
                ¿No tienes cuenta? <span onClick={() => setMode('register')}>Crear cuenta</span>
              </p>
            </>
          ) : (
            <>
              <button type="button" className="btn-primary btn-block" onClick={handleRegister}>
                Registrarse
              </button>
              <p className="drawer-link">
                ¿Ya tienes cuenta? <span onClick={() => setMode('login')}>Iniciar sesion</span>
              </p>
            </>
          )}
        </div>
      </aside>
    </>
  );
}

export default LoginDrawer;
