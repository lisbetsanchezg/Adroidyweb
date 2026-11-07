import { createContext, useContext, useEffect, useState } from 'react';
import { login as loginRequest, register as registerRequest } from '../services/authService.js';

/** Clave usada para persistir el usuario autenticado en localStorage. */
const STORAGE_KEY = 'beautyshop_user';

const AuthContext = createContext(null);

/**
 * Proveedor de autenticacion.
 *
 * Mantiene el usuario autenticado en estado de React y lo persiste en
 * localStorage, de modo que la sesion sobrevive a la recarga de la pagina.
 * Expone las acciones de login, registro y cierre de sesion, que se apoyan en
 * el authService (llamadas reales al backend).
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children componentes hijos
 */
export function AuthProvider({ children }) {
  const [currentUser, setCurrentUser] = useState(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? JSON.parse(stored) : null;
  });

  // Persiste (o limpia) el usuario cada vez que cambia
  useEffect(() => {
    if (currentUser) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(currentUser));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [currentUser]);

  /**
   * Inicia sesion contra el backend y guarda el usuario autenticado.
   *
   * @param {string} email    correo del usuario
   * @param {string} password contrasena del usuario
   * @returns {Promise<Object>} el usuario autenticado
   */
  function login(email, password) {
    return loginRequest(email, password).then((user) => {
      setCurrentUser(user);
      return user;
    });
  }

  /**
   * Registra un usuario nuevo. No inicia sesion automaticamente: el usuario
   * debera autenticarse despues.
   *
   * @param {string} name     nombre del usuario
   * @param {string} email    correo del usuario
   * @param {string} password contrasena del usuario
   * @returns {Promise<Object>} el usuario registrado
   */
  function register(name, email, password) {
    return registerRequest(name, email, password);
  }

  /** Cierra la sesion del usuario actual. */
  function logout() {
    setCurrentUser(null);
  }

  const value = { currentUser, login, register, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * Hook para consumir el contexto de autenticacion.
 *
 * @returns {Object} API de autenticacion (currentUser, login, register, logout)
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider.');
  }
  return context;
}
