package com.beautyshop.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.beautyshop.cart.CartItem;
import com.beautyshop.cart.CartResponse;
import com.beautyshop.cart.CartService;
import com.beautyshop.exception.ResourceNotFoundException;
import com.beautyshop.repository.UserRepository;

/**
 * Servicio con la logica de negocio de la gestion de pedidos.
 *
 * <p>Crea pedidos a partir del contenido del carrito, lista pedidos, consulta
 * su detalle y cambia su estado.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Service
public class OrderService {

    /** Estado inicial de un pedido recien creado. */
    private static final String ESTADO_INICIAL = "pendiente";

    /** Estados validos que puede tener un pedido. */
    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("pendiente", "pagado", "enviado", "entregado", "cancelado");

    /** Repositorio de acceso a datos de pedidos. */
    private final OrderRepository orderRepository;

    /** Servicio del carrito, del que se toman las lineas para crear el pedido. */
    private final CartService cartService;

    /** Repositorio de usuarios, para validar la existencia del usuario. */
    private final UserRepository userRepository;

    /**
     * Constructor con inyeccion de dependencias.
     *
     * @param orderRepository repositorio de pedidos
     * @param cartService     servicio del carrito
     * @param userRepository  repositorio de usuarios
     */
    public OrderService(OrderRepository orderRepository, CartService cartService,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    /**
     * Crea un pedido a partir del contenido actual del carrito del usuario
     * y vacia el carrito.
     *
     * @param userId identificador del usuario
     * @return el detalle del pedido creado (cabecera y lineas)
     */
    public OrderDetail createOrder(Long userId) {
        // getCart valida que el usuario exista y calcula el total
        CartResponse cart = cartService.getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio; no se puede crear el pedido.");
        }

        // Cabecera del pedido con estado inicial y total del carrito
        Order order = new Order(userId, LocalDateTime.now(), ESTADO_INICIAL, cart.getTotal());
        order = orderRepository.saveOrder(order);

        // Copia cada linea del carrito al detalle del pedido
        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(order.getIdPedido(), cartItem.getIdProducto(),
                    cartItem.getCantidad(), cartItem.getPrecioUnitario());
            orderRepository.saveOrderItem(orderItem);
            items.add(orderItem);
        }

        // Vacia el carrito una vez creado el pedido
        cartService.clearCart(userId);

        return new OrderDetail(order, items);
    }

    /**
     * Lista los pedidos de un usuario.
     *
     * @param userId identificador del usuario
     * @return lista de pedidos (vacia si no tiene)
     */
    public List<Order> getOrdersByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("El usuario " + userId + " no existe.");
        }
        return orderRepository.findByUser(userId);
    }

    /**
     * Obtiene el detalle completo de un pedido (cabecera y lineas).
     *
     * @param orderId identificador del pedido
     * @return el detalle del pedido
     */
    public OrderDetail getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El pedido " + orderId + " no existe."));
        List<OrderItem> items = orderRepository.findItemsByOrder(orderId);
        return new OrderDetail(order, items);
    }

    /**
     * Cambia el estado de un pedido.
     *
     * @param orderId identificador del pedido
     * @param estado  nuevo estado (pendiente, pagado, enviado, entregado, cancelado)
     * @return el pedido actualizado
     */
    public Order updateStatus(Long orderId, String estado) {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado.trim().toLowerCase())) {
            throw new IllegalArgumentException(
                    "Estado invalido. Validos: " + ESTADOS_VALIDOS);
        }

        String nuevoEstado = estado.trim().toLowerCase();
        boolean updated = orderRepository.updateStatus(orderId, nuevoEstado);
        if (!updated) {
            throw new ResourceNotFoundException("El pedido " + orderId + " no existe.");
        }

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El pedido " + orderId + " no existe."));
    }
}
