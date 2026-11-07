package com.beautyshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.beautyshop.cart.CartItem;
import com.beautyshop.cart.CartRepository;
import com.beautyshop.cart.CartResponse;
import com.beautyshop.cart.CartService;
import com.beautyshop.exception.ResourceNotFoundException;
import com.beautyshop.model.Product;
import com.beautyshop.repository.ProductRepository;
import com.beautyshop.repository.UserRepository;

/**
 * Pruebas unitarias de {@link CartService}.
 *
 * <p>Usan repositorios falsos en memoria (sin base de datos ni Mockito) para
 * validar las reglas de negocio del carrito: validaciones, producto o usuario
 * inexistente, suma de cantidades, actualizacion, retiro y calculo del total.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
class CartServiceTest {

    /** Identificador de un usuario que si existe en el repositorio falso. */
    private static final Long USER_ID = 1L;

    /** Identificador de un producto que si existe en el repositorio falso. */
    private static final Long PRODUCT_ID = 10L;

    /** Repositorio de carrito falso. */
    private FakeCartRepository cartRepository;

    /** Servicio bajo prueba. */
    private CartService cartService;

    /** Prepara un servicio nuevo antes de cada prueba. */
    @BeforeEach
    void setUp() {
        cartRepository = new FakeCartRepository();
        cartService = new CartService(cartRepository, new FakeProductRepository(), new FakeUserRepository());
    }

    /** Construye la solicitud de agregar un producto con la cantidad indicada. */
    private CartItem request(Long productId, Integer cantidad) {
        CartItem item = new CartItem();
        item.setIdProducto(productId);
        item.setCantidad(cantidad);
        return item;
    }

    /** Agregar un producto nuevo toma el precio del producto real y calcula el total. */
    @Test
    @DisplayName("addItem agrega una linea nueva con el precio del producto")
    void addItemCreatesNewLineWithProductPrice() {
        CartItem clientItem = request(PRODUCT_ID, 2);
        clientItem.setPrecioUnitario(new BigDecimal("1")); // el cliente intenta fijar su precio

        CartResponse cart = cartService.addItem(USER_ID, clientItem);

        assertEquals(1, cart.getItems().size());
        assertEquals(new BigDecimal("18000"), cart.getItems().get(0).getPrecioUnitario());
        assertEquals(new BigDecimal("36000"), cart.getTotal());
    }

    /** Agregar un producto que ya estaba suma las cantidades en la misma linea. */
    @Test
    @DisplayName("addItem suma la cantidad si el producto ya estaba en el carrito")
    void addItemSumsQuantityWhenProductExists() {
        cartService.addItem(USER_ID, request(PRODUCT_ID, 2));

        CartResponse cart = cartService.addItem(USER_ID, request(PRODUCT_ID, 3));

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getCantidad());
        assertEquals(new BigDecimal("90000"), cart.getTotal());
    }

    /** Una cantidad cero es rechazada. */
    @Test
    @DisplayName("addItem rechaza cantidad cero")
    void addItemRejectsZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(USER_ID, request(PRODUCT_ID, 0)));
    }

    /** Una cantidad negativa es rechazada. */
    @Test
    @DisplayName("addItem rechaza cantidad negativa")
    void addItemRejectsNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(USER_ID, request(PRODUCT_ID, -1)));
    }

    /** Una cantidad nula es rechazada. */
    @Test
    @DisplayName("addItem rechaza cantidad nula")
    void addItemRejectsNullQuantity() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(USER_ID, request(PRODUCT_ID, null)));
    }

    /** Un producto inexistente produce un error de recurso no encontrado. */
    @Test
    @DisplayName("addItem lanza ResourceNotFoundException si el producto no existe")
    void addItemFailsWhenProductMissing() {
        assertThrows(ResourceNotFoundException.class, () -> cartService.addItem(USER_ID, request(999L, 1)));
    }

    /** Un usuario inexistente produce un error de recurso no encontrado. */
    @Test
    @DisplayName("addItem lanza ResourceNotFoundException si el usuario no existe")
    void addItemFailsWhenUserMissing() {
        assertThrows(ResourceNotFoundException.class, () -> cartService.addItem(99L, request(PRODUCT_ID, 1)));
    }

    /** Actualizar la cantidad reemplaza el valor anterior y recalcula el total. */
    @Test
    @DisplayName("updateQuantity reemplaza la cantidad y recalcula el total")
    void updateQuantityReplacesQuantity() {
        cartService.addItem(USER_ID, request(PRODUCT_ID, 2));

        CartResponse cart = cartService.updateQuantity(USER_ID, PRODUCT_ID, 4);

        assertEquals(4, cart.getItems().get(0).getCantidad());
        assertEquals(new BigDecimal("72000"), cart.getTotal());
    }

    /** Actualizar con cantidad invalida es rechazado. */
    @Test
    @DisplayName("updateQuantity rechaza cantidad menor o igual a cero")
    void updateQuantityRejectsInvalidQuantity() {
        cartService.addItem(USER_ID, request(PRODUCT_ID, 2));
        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity(USER_ID, PRODUCT_ID, 0));
    }

    /** Actualizar un producto que no esta en el carrito produce 404. */
    @Test
    @DisplayName("updateQuantity lanza ResourceNotFoundException si el producto no esta en el carrito")
    void updateQuantityFailsWhenNotInCart() {
        assertThrows(ResourceNotFoundException.class, () -> cartService.updateQuantity(USER_ID, PRODUCT_ID, 2));
    }

    /** Quitar un producto deja el carrito vacio con total cero. */
    @Test
    @DisplayName("removeItem quita la linea y deja el total en cero")
    void removeItemEmptiesCart() {
        cartService.addItem(USER_ID, request(PRODUCT_ID, 2));

        CartResponse cart = cartService.removeItem(USER_ID, PRODUCT_ID);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotal()));
    }

    /** Quitar un producto que no esta en el carrito produce 404. */
    @Test
    @DisplayName("removeItem lanza ResourceNotFoundException si el producto no esta en el carrito")
    void removeItemFailsWhenNotInCart() {
        assertThrows(ResourceNotFoundException.class, () -> cartService.removeItem(USER_ID, PRODUCT_ID));
    }

    /** Un carrito sin lineas tiene total cero. */
    @Test
    @DisplayName("getCart devuelve carrito vacio con total cero")
    void getCartReturnsEmptyCart() {
        CartResponse cart = cartService.getCart(USER_ID);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(cart.getTotal()));
    }

    /** Consultar el carrito de un usuario inexistente produce 404. */
    @Test
    @DisplayName("getCart lanza ResourceNotFoundException si el usuario no existe")
    void getCartFailsWhenUserMissing() {
        assertThrows(ResourceNotFoundException.class, () -> cartService.getCart(99L));
    }

    /** El total es la suma de precio unitario por cantidad de cada linea. */
    @Test
    @DisplayName("calculateTotal suma precio unitario por cantidad")
    void calculateTotalSumsSubtotals() {
        List<CartItem> items = List.of(
                new CartItem(USER_ID, 1L, 2, new BigDecimal("10000")),
                new CartItem(USER_ID, 2L, 3, new BigDecimal("2500.50")));

        assertEquals(new BigDecimal("27501.50"), cartService.calculateTotal(items));
    }

    /** Vaciar el carrito elimina todas sus lineas. */
    @Test
    @DisplayName("clearCart elimina todas las lineas del usuario")
    void clearCartRemovesAllLines() {
        cartService.addItem(USER_ID, request(PRODUCT_ID, 2));

        cartService.clearCart(USER_ID);

        assertTrue(cartService.getCart(USER_ID).getItems().isEmpty());
    }

    /** Repositorio de carrito en memoria. */
    private static class FakeCartRepository extends CartRepository {

        /** Lineas almacenadas. */
        private final List<CartItem> items = new ArrayList<>();

        FakeCartRepository() {
            super(null);
        }

        @Override
        public CartItem insert(CartItem item) {
            items.add(item);
            return item;
        }

        @Override
        public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) {
            return items.stream()
                    .filter(i -> i.getIdUsuario().equals(userId) && i.getIdProducto().equals(productId))
                    .findFirst();
        }

        @Override
        public List<CartItem> findByUser(Long userId) {
            return items.stream().filter(i -> i.getIdUsuario().equals(userId)).toList();
        }

        @Override
        public boolean updateQuantity(Long userId, Long productId, Integer cantidad) {
            Optional<CartItem> found = findByUserAndProduct(userId, productId);
            found.ifPresent(i -> i.setCantidad(cantidad));
            return found.isPresent();
        }

        @Override
        public boolean delete(Long userId, Long productId) {
            return items.removeIf(i -> i.getIdUsuario().equals(userId) && i.getIdProducto().equals(productId));
        }

        @Override
        public void deleteByUser(Long userId) {
            items.removeIf(i -> i.getIdUsuario().equals(userId));
        }
    }

    /** Repositorio de productos falso: solo existe el producto {@link #PRODUCT_ID}. */
    private static class FakeProductRepository extends ProductRepository {

        FakeProductRepository() {
            super(null);
        }

        @Override
        public Optional<Product> findById(Long id) {
            if (!PRODUCT_ID.equals(id)) {
                return Optional.empty();
            }
            return Optional.of(new Product(id, "Labial Matte", "Larga duracion",
                    new BigDecimal("18000"), "labios", 40, "labial.jpg"));
        }
    }

    /** Repositorio de usuarios falso: solo existe el usuario {@link #USER_ID}. */
    private static class FakeUserRepository extends UserRepository {

        FakeUserRepository() {
            super(null);
        }

        @Override
        public boolean existsById(Long id) {
            return USER_ID.equals(id);
        }
    }
}
