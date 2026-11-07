import { createContext, useContext, useEffect, useState, useCallback, useRef } from 'react';
import { useAuth } from './AuthContext.jsx';
import {
  getCart,
  addItem as addItemApi,
  updateItem as updateItemApi,
  removeItem as removeItemApi
} from '../services/cartService.js';
import { getProducts } from '../services/productService.js';

const CartContext = createContext(null);

/**
 * Proveedor del carrito de compras conectado al backend.
 *
 * El carrito vive en el backend (tabla cart_items) y se identifica por el
 * usuario autenticado (AuthContext). Como la API del carrito solo devuelve
 * ids de producto, aqui se enriquecen las lineas con el nombre y la imagen
 * del producto para poder mostrarlas.
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children componentes hijos
 */
export function CartProvider({ children }) {
  const { currentUser } = useAuth();
  const [items, setItems] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);

  // Cache del mapa de productos (id -> producto) para no pedirlo en cada cambio
  const productMapRef = useRef(null);

  /**
   * Devuelve un mapa id -> producto, pidiendolo al backend una sola vez.
   *
   * @returns {Promise<Map>} mapa de productos por id
   */
  const getProductMap = useCallback(async () => {
    if (!productMapRef.current) {
      const products = await getProducts();
      productMapRef.current = new Map(products.map((product) => [product.id, product]));
    }
    return productMapRef.current;
  }, []);

  /**
   * Enriquece las lineas crudas del backend (solo ids) con datos del producto.
   *
   * @param {Array} rawItems lineas devueltas por la API del carrito
   * @returns {Promise<Array>} lineas con nombre, imagen y subtotal
   */
  const enrich = useCallback(
    async (rawItems) => {
      const map = await getProductMap();
      return rawItems.map((item) => {
        const product = map.get(item.idProducto) || {};
        return {
          idProducto: item.idProducto,
          nombre: product.name || `Producto ${item.idProducto}`,
          imagen: product.image,
          cantidad: item.cantidad,
          precioUnitario: Number(item.precioUnitario),
          subtotal: Number(item.precioUnitario) * item.cantidad
        };
      });
    },
    [getProductMap]
  );

  /**
   * Carga el carrito del usuario actual desde el backend.
   * Si no hay sesion, deja el carrito vacio.
   */
  const refreshCart = useCallback(async () => {
    if (!currentUser) {
      setItems([]);
      setTotal(0);
      return;
    }
    setLoading(true);
    try {
      const cart = await getCart(currentUser.id);
      setItems(await enrich(cart.items));
      setTotal(Number(cart.total));
    } finally {
      setLoading(false);
    }
  }, [currentUser, enrich]);

  // Recarga el carrito cada vez que cambia el usuario (login/logout)
  useEffect(() => {
    refreshCart();
  }, [refreshCart]);

  /**
   * Aplica al estado local el carrito devuelto por una operacion del backend.
   *
   * @param {Object} cart respuesta del backend con { items, total }
   */
  async function applyCart(cart) {
    setItems(await enrich(cart.items));
    setTotal(Number(cart.total));
  }

  /**
   * Agrega un producto al carrito (una unidad). Requiere sesion iniciada.
   *
   * @param {Object} product producto a agregar
   */
  async function addItem(product) {
    if (!currentUser) {
      throw new Error('Debes iniciar sesion para agregar al carrito.');
    }
    await applyCart(await addItemApi(currentUser.id, product.id, 1));
  }

  /**
   * Cambia la cantidad de una linea sumando/restando delta.
   * Si la cantidad llega a 0, elimina la linea.
   *
   * @param {number} idProducto identificador del producto
   * @param {number} delta       variacion (+1 o -1)
   */
  async function changeQuantity(idProducto, delta) {
    if (!currentUser) {
      return;
    }
    const item = items.find((line) => line.idProducto === idProducto);
    if (!item) {
      return;
    }
    const nuevaCantidad = item.cantidad + delta;
    if (nuevaCantidad <= 0) {
      await removeItem(idProducto);
      return;
    }
    await applyCart(await updateItemApi(currentUser.id, idProducto, nuevaCantidad));
  }

  /**
   * Quita un producto del carrito.
   *
   * @param {number} idProducto identificador del producto
   */
  async function removeItem(idProducto) {
    if (!currentUser) {
      return;
    }
    await applyCart(await removeItemApi(currentUser.id, idProducto));
  }

  // Cantidad total de unidades en el carrito (para el contador de la Navbar)
  const totalCount = items.reduce((sum, item) => sum + item.cantidad, 0);

  const value = {
    items,
    total,
    totalCount,
    loading,
    addItem,
    changeQuantity,
    removeItem,
    refreshCart
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

/**
 * Hook para consumir el contexto del carrito.
 *
 * @returns {Object} API del carrito
 */
export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart debe usarse dentro de un CartProvider.');
  }
  return context;
}
