import { useState } from 'react';
import './AuthForm.css';

/** Expresion regular basica para validar el formato del correo. */
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/** Longitud minima exigida para la contrasena. */
const MIN_PASSWORD_LENGTH = 4;

/**
 * Formulario controlado de registro.
 *
 * Maneja sus propios campos con useState, valida antes de enviar (campos
 * vacios, formato de correo y longitud de contrasena) y delega el envio en
 * el callback onSubmit.
 *
 * @param {Object} props
 * @param {Function} props.onSubmit callback (name, email, password) que devuelve una promesa
 */
function RegisterForm({ onSubmit }) {
  const [form, setForm] = useState({ name: '', email: '', password: '' });
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
   * Valida los campos y, si son correctos, envia los datos de registro.
   *
   * @param {Object} event evento submit del formulario
   */
  function handleSubmit(event) {
    event.preventDefault();
    setError('');

    // Validacion basica antes de llamar al backend
    if (!form.name.trim() || !form.email.trim() || !form.password.trim()) {
      setError('Todos los campos son obligatorios.');
      return;
    }
    if (!EMAIL_REGEX.test(form.email)) {
      setError('El correo no tiene un formato valido.');
      return;
    }
    if (form.password.length < MIN_PASSWORD_LENGTH) {
      setError(`La contrasena debe tener al menos ${MIN_PASSWORD_LENGTH} caracteres.`);
      return;
    }

    setSubmitting(true);
    onSubmit(form.name, form.email, form.password)
      .catch((err) => setError(err.message))
      .finally(() => setSubmitting(false));
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit} noValidate>
      {error && <p className="auth-form-error">{error}</p>}

      <div className="input-group">
        <label htmlFor="register-name">Nombre</label>
        <input
          id="register-name"
          name="name"
          type="text"
          placeholder="Tu nombre"
          value={form.name}
          onChange={handleChange}
        />
      </div>

      <div className="input-group">
        <label htmlFor="register-email">Correo</label>
        <input
          id="register-email"
          name="email"
          type="email"
          placeholder="ejemplo@mail.com"
          value={form.email}
          onChange={handleChange}
        />
      </div>

      <div className="input-group">
        <label htmlFor="register-password">Contrasena</label>
        <input
          id="register-password"
          name="password"
          type="password"
          placeholder="••••••••"
          value={form.password}
          onChange={handleChange}
        />
      </div>

      <button type="submit" className="btn-primary btn-block" disabled={submitting}>
        {submitting ? 'Creando...' : 'Registrarme'}
      </button>
    </form>
  );
}

export default RegisterForm;
