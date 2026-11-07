import { createContext, useContext, useState, useCallback } from 'react';
import Toast from '../components/Toast.jsx';

const ToastContext = createContext(null);

/**
 * Proveedor de notificaciones tipo "toast".
 *
 * Expone la funcion showToast para mostrar mensajes breves en la esquina
 * inferior derecha, reutilizando la logica de los mockups.
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children componentes hijos
 */
export function ToastProvider({ children }) {
  const [message, setMessage] = useState('');
  const [visible, setVisible] = useState(false);

  /**
   * Muestra un mensaje temporal.
   *
   * @param {string} text texto a mostrar
   */
  const showToast = useCallback((text) => {
    setMessage(text);
    setVisible(true);
    setTimeout(() => setVisible(false), 1800);
  }, []);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <Toast message={message} visible={visible} />
    </ToastContext.Provider>
  );
}

/**
 * Hook para mostrar notificaciones desde cualquier componente.
 *
 * @returns {{ showToast: (text: string) => void }}
 */
export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast debe usarse dentro de un ToastProvider.');
  }
  return context;
}
