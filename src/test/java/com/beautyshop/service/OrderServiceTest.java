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
import com.beautyshop.cart.CartService;
import com.beautyshop.exception.ResourceNotFoundException;
import com.beautyshop.model.Product;
import com.beautyshop.order.Order;
import com.beautyshop.order.OrderDetail;
import com.beautyshop.order.OrderItem;
import com.beautyshop.order.OrderRepository;
import com.beautyshop.order.OrderService;
import com.beautyshop.repository.ProductRepository;
import com.beautyshop.repository.UserRepository;

/**
 * Pruebas unitarias de {@link OrderService}.
 *
 * <p>Usan repositorios falsos en memoria (sin base de datos ni Mockito) y un
 * {@link CartService} real sobre ellos, para validar la creacion del pedido a
 * partir del carrito, el calculo del total, el vaciado del carrito, la
 * consulta de pedidos y la validacion de estados.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
class OrderServiceTest {

    /** Identificador de un usuario que si existe. */
    private static final Long USER_ID = 1L;

    /** Repositorio de carrito falso. */
    private FakeCartRepository cartRepository;

    /** Repositorio de pedidos falso. */
    private FakeOrderRepository orderRepository;

    /** Servicio de carrito real (con repositorios falsos). */
    private CartService cartService;

    /** Servicio bajo prueba. */
    private OrderService orderService;

    /** Prepara servicios nuevos antes de cada prueba. */
    @BeforeEach
    void setUp() {
        cartRepository = new FakeCartRepository();
        orderRepository = new FakeOrderRepository();
        FakeUserRepository userRepository = new FakeUserRepository();
        cartService = new CartService(cartRepository, new FakeProductRepository(), userRepository);
        orderService = new OrderService(orderRepository, cartService, userRepository);
    }

    /** Deja en el carrito dos unidades de producto 1 (10000) y una del producto 2 (5000). */
    private void fillCart() {
        cartRepository.insert(new CartItem(USER_ID, 1L, 2, new BigDecimal("10000")));
        cartRepository.insert(new CartItem(USER_ID, 2L, 1, new BigDecimal("5000")));
    }

    /** El pedido nace en estado pendiente con el total del carrito. */
    @Test
    @DisplayName("createOrder crea el pedido pendiente con el total del carrito")
    void createOrderUsesCartTotalAndPendingStatus() {
        fillCart();

        OrderDetail detail = orderService.createOrder(USER_ID);

        assertEquals("pendiente", detail.getOrder().getEstado());
        assertEquals(new BigDecimal("25000"), detail.getOrder().getTotal());
        assertEquals(USER_ID, detail.getOrder().getIdUsuario());
    }

    /** Cada linea del carrito se copia al detalle del pedido con su precio. */
    @Test
    @DisplayName("createOrder copia las lineas del carrito al pedido")
    void createOrderCopiesCartLines() {
        fillCart();

        OrderDetail detail = orderService.createOrder(USER_ID);

        assertEquals(2, detail.getItems().size());
        assertEquals(2, orderRepository.items.size());
        OrderItem first = detail.getItems().get(0);
        assertEquals(detail.getOrder().getIdPedido(), first.getIdPedido());
        assertEquals(2, first.getCantidad());
        assertEquals(new BigDecimal("10000"), first.getPrecioUnitario());
    }

    /** Tras crear el pedido el carrito queda vacio. */
    @Test
    @DisplayName("createOrder vacia el carrito")
    void createOrderEmptiesCart() {
        fillCart();

        orderService.createOrder(USER_ID);

        assertTrue(cartService.getCart(USER_ID).getItems().isEmpty());
    }

    /** No se puede crear un pedido con el carrito vacio. */
    @Test
    @DisplayName("createOrder rechaza carrito vacio")
    void createOrderRejectsEmptyCart() {
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(USER_ID));
        assertTrue(orderRepository.orders.isEmpty());
    }

    /** Un usuario inexistente produce 404 al crear el pedido. */
    @Test
    @DisplayName("createOrder lanza ResourceNotFoundException si el usuario no existe")
    void createOrderFailsWhenUserMissing() {
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(99L));
    }

    /** El historial devuelve los pedidos del usuario. */
    @Test
    @DisplayName("getOrdersByUser devuelve los pedidos del usuario")
    void getOrdersByUserReturnsOrders() {
        fillCart();
        orderService.createOrder(USER_ID);

        List<Order> orders = orderService.getOrdersByUser(USER_ID);

        assertEquals(1, orders.size());
    }

    /** Un usuario sin pedidos recibe una lista vacia. */
    @Test
    @DisplayName("getOrdersByUser devuelve lista vacia si no hay pedidos")
    void getOrdersByUserReturnsEmptyList() {
        assertTrue(orderService.getOrdersByUser(USER_ID).isEmpty());
    }

    /** Un usuario inexistente produce 404 al consultar el historial. */
    @Test
    @DisplayName("getOrdersByUser lanza ResourceNotFoundException si el usuario no existe")
    void getOrdersByUserFailsWhenUserMissing() {
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrdersByUser(99L));
    }

    /** El detalle incluye cabecera y lineas. */
    @Test
    @DisplayName("getOrderDetail devuelve cabecera y lineas")
    void getOrderDetailReturnsHeaderAndLines() {
        fillCart();
        Long orderId = orderService.createOrder(USER_ID).getOrder().getIdPedido();

        OrderDetail detail = orderService.getOrderDetail(orderId);

        assertEquals(orderId, detail.getOrder().getIdPedido());
        assertEquals(2, detail.getItems().size());
    }

    /** Un pedido inexistente produce 404 al consultar el detalle. */
    @Test
    @DisplayName("getOrderDetail lanza ResourceNotFoundException si el pedido no existe")
    void getOrderDetailFailsWhenMissing() {
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderDetail(999L));
    }

    /** Un estado valido se guarda normalizado (sin espacios y en minuscula). */
    @Test
    @DisplayName("updateStatus normaliza y guarda un estado valido")
    void updateStatusNormalizesValidStatus() {
        fillCart();
        Long orderId = orderService.createOrder(USER_ID).getOrder().getIdPedido();

        Order updated = orderService.updateStatus(orderId, "  PAGADO ");

        assertEquals("pagado", updated.getEstado());
    }

    /** Un estado desconocido es rechazado. */
    @Test
    @DisplayName("updateStatus rechaza un estado invalido")
    void updateStatusRejectsInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(1L, "perdido"));
    }

    /** Un estado nulo es rechazado. */
    @Test
    @DisplayName("updateStatus rechaza estado nulo")
    void updateStatusRejectsNullStatus() {
        assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(1L, null));
    }

    /** Cambiar el estado de un pedido inexistente produce 404. */
    @Test
    @DisplayName("updateStatus lanza ResourceNotFoundException si el pedido no existe")
    void updateStatusFailsWhenOrderMissing() {
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateStatus(999L, "enviado"));
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
        public List<CartItem> findByUser(Long userId) {
            return items.stream().filter(i -> i.getIdUsuario().equals(userId)).toList();
        }

        @Override
        public void deleteByUser(Long userId) {
            items.removeIf(i -> i.getIdUsuario().equals(userId));
        }
    }

    /** Repositorio de pedidos en memoria. */
    private static class FakeOrderRepository extends OrderRepository {

        /** Cabeceras almacenadas. */
        private final List<Order> orders = new ArrayList<>();

        /** Lineas almacenadas. */
        private final List<OrderItem> items = new ArrayList<>();

        FakeOrderRepository() {
            super(null);
        }

        @Override
        public Order saveOrder(Order order) {
            order.setIdPedido((long) orders.size() + 1);
            orders.add(order);
            return order;
        }

        @Override
        public void saveOrderItem(OrderItem item) {
            items.add(item);
        }

        @Override
        public List<Order> findByUser(Long userId) {
            return orders.stream().filter(o -> o.getIdUsuario().equals(userId)).toList();
        }

        @Override
        public Optional<Order> findById(Long orderId) {
            return orders.stream().filter(o -> o.getIdPedido().equals(orderId)).findFirst();
        }

        @Override
        public List<OrderItem> findItemsByOrder(Long orderId) {
            return items.stream().filter(i -> i.getIdPedido().equals(orderId)).toList();
        }

        @Override
        public boolean updateStatus(Long orderId, String estado) {
            Optional<Order> found = findById(orderId);
            found.ifPresent(o -> o.setEstado(estado));
            return found.isPresent();
        }
    }

    /** Repositorio de productos falso; el carrito de estas pruebas no lo consulta. */
    private static class FakeProductRepository extends ProductRepository {

        FakeProductRepository() {
            super(null);
        }

        @Override
        public Optional<Product> findById(Long id) {
            return Optional.empty();
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
