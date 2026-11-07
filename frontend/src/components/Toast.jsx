import './Toast.css';

/**
 * Notificacion breve que aparece en la esquina inferior derecha.
 *
 * @param {Object} props
 * @param {string} props.message texto a mostrar
 * @param {boolean} props.visible indica si el toast esta visible
 */
function Toast({ message, visible }) {
  return <div className={`toast ${visible ? 'show' : ''}`}>{message}</div>;
}

export default Toast;
