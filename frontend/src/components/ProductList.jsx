import ProductCard from './ProductCard.jsx';

/**
 * Lista de productos: mapea cada producto a un {@link ProductCard}.
 * Es un componente de presentacion; recibe los productos y el manejador
 * para agregar al carrito desde la pagina que lo usa.
 *
 * @param {Object} props
 * @param {Array} props.products lista de productos a mostrar
 * @param {Function} props.onAdd callback al presionar "Agregar" en una tarjeta
 */
function ProductList({ products, onAdd }) {
  if (products.length === 0) {
    return <p className="product-list-empty">No se encontraron productos.</p>;
  }

  return (
    <div className="products-grid">
      {products.map((product) => (
        <ProductCard key={product.id} product={product} onAdd={onAdd} />
      ))}
    </div>
  );
}

export default ProductList;
