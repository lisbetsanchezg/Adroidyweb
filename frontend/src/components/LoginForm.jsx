import { useState } from 'react';
import './AuthForm.css';

/** Expresion regular basica para validar el formato del correo. */
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * Formulario controlado de inicio de sesion.
 *
 * Maneja sus propios campos con useState, valida antes de enviar (campos
 * vacios y formato de correo) y delega el envio en el callback onSubmit.
 *
 * @param {Object} props
 * @param {Function} props.onSubmit callback (email, password) que devuelve una promesa
 */
function LoginForm({ onSubmit }) {
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

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
   * Valida los campos y, si son correctos, envia las credenciales.
   *
   * @param {Object} event evento submit del formulario
   */
  function handleSubmit(event) {
    event.preventDefault();
    setError('');

    // Validacion basica antes de llamar al backend
    if (!form.email.trim() || !form.password.trim()) {
      setError('Completa correo y contrasena.');
      return;
    }
    if (!EMAIL_REGEX.test(form.email)) {
      setError('El correo no tiene un formato valido.');
      return;
    }

    setSubmitting(true);
    onSubmit(form.email, form.password)
      .catch((err) => setError(err.message))
      .finally(() => setSubmitting(false));
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit} noValidate>
      {error && <p className="auth-form-error">{error}</p>}

      <div className="input-group">
        <label htmlFor="login-email">Correo</label>
        <input
          id="login-email"
          name="email"
          type="email"
          placeholder="ejemplo@mail.com"
          value={form.email}
          onChange={handleChange}
        />
      </div>

      <div className="input-group">
        <label htmlFor="login-password">Contrasena</label>
        <input
          id="login-password"
          name="password"
          type="password"
          placeholder="••••••••"
          value={form.password}
          onChange={handleChange}
        />
      </div>

      <button type="submit" className="btn-primary btn-block" disabled={submitting}>
        {submitting ? 'Entrando...' : 'Entrar'}
      </button>
    </form>
  );
}

export default LoginForm;
