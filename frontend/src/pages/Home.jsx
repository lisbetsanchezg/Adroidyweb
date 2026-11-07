import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SearchBar from '../components/SearchBar.jsx';
import ProductList from '../components/ProductList.jsx';
import { getProducts } from '../services/productService.js';
import { useCart } from '../context/CartContext.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { useToast } from '../context/ToastContext.jsx';
import './Home.css';

/** Imagen del hero de la pagina de inicio. */
const HERO_IMAGE =
  'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=900&q=80';

/**
 * Pagina de inicio: hero de bienvenida, buscador y listado de productos
 * obtenidos desde GET /api/products. Permite filtrar por nombre y agregar
 * productos al carrito (que vive en el backend).
 */
function Home() {
  const navigate = useNavigate();
  const { addItem } = useCart();
  const { currentUser } = useAuth();
  const { showToast } = useToast();
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState('');

  // Carga todos los productos al montar la pagina
  useEffect(() => {
    getProducts()
      .then((data) => setProducts(data))
      .catch(() => setProducts([]));
  }, []);

  // Filtra los productos por nombre segun el texto del buscador
  const filtered = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) {
      return products;
    }
    return products.filter((product) => product.name.toLowerCase().includes(term));
  }, [products, search]);

  /**
   * Agrega un producto al carrito. Si no hay sesion, invita a iniciarla.
   *
   * @param {Object} product producto a agregar
   */
  function handleAdd(product) {
    addItem(product)
      .then(() => showToast(`${product.name} agregado al carrito`))
      .catch(() => {
        showToast('Inicia sesion para agregar al carrito.');
        navigate('/login');
      });
  }

  return (
    <main className="container">
      <section className="hero">
        <div className="hero-text">
          <h1>Productos que simplifican tu rutina de belleza</h1>
          <p>Encuentra maquillaje y skincare premium con envio rapido y garantias.</p>
          <div className="hero-cta">
            <button type="button" className="btn-primary" onClick={() => navigate('/catalogo')}>
              Ver catalogo
            </button>
            {currentUser ? (
              <span className="hero-greeting">Hola de nuevo, {currentUser.name.split(' ')[0]} 💄</span>
            ) : (
              <button type="button" className="btn-secondary" onClick={() => navigate('/login')}>
                Iniciar sesion
              </button>
            )}
          </div>
        </div>
        <div className="hero-image">
          <img src={HERO_IMAGE} alt="Cosmeticos" />
        </div>
      </section>

      <section>
        <h2 className="section-title">Nuestros productos</h2>
        <SearchBar value={search} onChange={setSearch} />
        {products.length === 0 ? (
          <p className="hero-empty">
            No se pudieron cargar los productos. Verifica que el backend este en ejecucion.
          </p>
        ) : (
          <ProductList products={filtered} onAdd={handleAdd} />
        )}
      </section>
    </main>
  );
}

export default Home;
