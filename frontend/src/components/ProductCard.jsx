import { formatCurrency, productImageUrl } from '../utils/format.js';
import './ProductCard.css';

/** Imagen de reemplazo cuando el producto no tiene una URL de imagen valida. */
const PLACEHOLDER_IMAGE =
  'https://images.unsplash.com/photo-1512496015851-a90fb38ba796?auto=format&fit=crop&w=720&q=80';

/**
 * Tarjeta reutilizable que muestra un producto y permite agregarlo al carrito.
 *
 * @param {Object} props
 * @param {Object} props.product producto a mostrar (id, name, description, price, image)
 * @param {Function} props.onAdd callback que se ejecuta al presionar "Agregar"
 */
function ProductCard({ product, onAdd }) {
  /**
   * Usa una imagen de reemplazo si la imagen del producto falla al cargar.
   *
   * @param {Object} event evento de error de la etiqueta img
   */
  function handleImageError(event) {
    if (event.target.src !== PLACEHOLDER_IMAGE) {
      event.target.src = PLACEHOLDER_IMAGE;
    }
  }

  return (
    <article className="product-card">
      <img
        src={productImageUrl(product.image) || PLACEHOLDER_IMAGE}
        alt={product.name}
        onError={handleImageError}
      />
      <div className="product-card-body">
        <h3 className="product-card-title">{product.name}</h3>
        <p className="product-card-desc">{product.description}</p>
        <p className="product-card-price">{formatCurrency(product.price)}</p>
        <button type="button" className="btn-primary btn-block" onClick={() => onAdd(product)}>
          Agregar
        </button>
      </div>
    </article>
  );
}

export default ProductCard;
