import './SearchBar.css';

/**
 * Barra de busqueda: input controlado que notifica los cambios al componente
 * padre para filtrar productos.
 *
 * @param {Object} props
 * @param {string} props.value    valor actual del input (estado controlado)
 * @param {Function} props.onChange callback que recibe el nuevo texto
 * @param {string} [props.placeholder] texto de ayuda del input
 */
function SearchBar({ value, onChange, placeholder = 'Buscar productos...' }) {
  return (
    <div className="search-bar">
      <span className="search-bar-icon">🔍</span>
      <input
        type="text"
        className="search-bar-input"
        value={value}
        placeholder={placeholder}
        onChange={(event) => onChange(event.target.value)}
      />
    </div>
  );
}

export default SearchBar;
