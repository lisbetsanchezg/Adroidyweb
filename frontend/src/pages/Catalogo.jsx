import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ProductList from '../components/ProductList.jsx';
import { getProducts } from '../services/productService.js';
import { useCart } from '../context/CartContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import './Catalogo.css';

/**
 * Pagina de catalogo: muestra todos los productos obtenidos desde la API
 * y permite agregarlos al carrito. Maneja estados de carga y error.
 */
function Catalogo() {
  const navigate = useNavigate();
  const { addItem } = useCart();
  const { showToast } = useToast();
  const [products, setProducts] = useState([]);
  const [status, setStatus] = useState('loading');

  // Carga todos los productos al montar la pagina
  useEffect(() => {
    getProducts()
      .then((data) => {
        setProducts(data);
        setStatus('ok');
      })
      .catch(() => setStatus('error'));
  }, []);

  /**
   * Agrega un producto al carrito y muestra una notificacion.
   *
   * @param {Object} product producto a agregar
   */
  function handleAdd(product) {
    addItem(product)
      .then(() => showToast(`${product.name} agregado`))
      .catch(() => {
        showToast('Inicia sesion para agregar al carrito.');
        navigate('/login');
      });
  }

  return (
    <main className="container">
      <section className="catalogo-hero">
        <h1>Catalogo Premium</h1>
        <p>Maquillaje y cuidado facial seleccionados para ti.</p>
      </section>

      {status === 'loading' && <p className="catalogo-msg">Cargando productos...</p>}

      {status === 'error' && (
        <p className="catalogo-msg catalogo-error">
          No se pudieron cargar los productos. Verifica que el backend (puerto 8080) este en
          ejecucion y que la base de datos tenga productos.
        </p>
      )}

      {status === 'ok' && products.length === 0 && (
        <p className="catalogo-msg">Aun no hay productos registrados.</p>
      )}

      {status === 'ok' && products.length > 0 && (
        <ProductList products={products} onAdd={handleAdd} />
      )}
    </main>
  );
}

export default Catalogo;
