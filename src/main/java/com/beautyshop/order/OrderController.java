package com.beautyshop.order;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST de la gestion de pedidos.
 *
 * <p>Ruta base: {@code /api/orders}. Delega la logica en {@link OrderService}.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Creacion y gestion de pedidos")
public class OrderController {

    /** Servicio con la logica de pedidos. */
    private final OrderService orderService;

    /**
     * Constructor con inyeccion del servicio.
     *
     * @param orderService servicio de pedidos
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Crea un pedido a partir del carrito actual del usuario y vacia el carrito.
     *
     * <p>Endpoint: {@code POST /api/orders/{userId}}</p>
     *
     * @param userId identificador del usuario
     * @return el detalle del pedido creado con HTTP 201 (CREATED)
     */
    @Operation(summary = "Crear pedido",
            description = "Crea un pedido a partir del carrito actual del usuario y vacia el carrito.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado con su detalle"),
        @ApiResponse(responseCode = "400", description = "El carrito esta vacio"),
        @ApiResponse(responseCode = "404", description = "Usuario inexistente")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<OrderDetail> createOrder(
            @Parameter(description = "Identificador del usuario") @PathVariable Long userId) {
        OrderDetail detail = orderService.createOrder(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(detail);
    }

    /**
     * Lista los pedidos de un usuario.
     *
     * <p>Endpoint: {@code GET /api/orders/{userId}}</p>
     *
     * @param userId identificador del usuario
     * @return lista de pedidos con HTTP 200 (OK)
     */
    @Operation(summary = "Listar pedidos del usuario",
            description = "Devuelve todos los pedidos de un usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos"),
        @ApiResponse(responseCode = "404", description = "Usuario inexistente")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getOrders(
            @Parameter(description = "Identificador del usuario") @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    /**
     * Consulta el detalle de un pedido.
     *
     * <p>Endpoint: {@code GET /api/orders/detail/{orderId}}</p>
     *
     * @param orderId identificador del pedido
     * @return el detalle del pedido con HTTP 200 (OK)
     */
    @Operation(summary = "Detalle de un pedido",
            description = "Devuelve la cabecera y las lineas de un pedido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle del pedido"),
        @ApiResponse(responseCode = "404", description = "Pedido inexistente")
    })
    @GetMapping("/detail/{orderId}")
    public ResponseEntity<OrderDetail> getOrderDetail(
            @Parameter(description = "Identificador del pedido") @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    /**
     * Cambia el estado de un pedido.
     *
     * <p>Endpoint: {@code PATCH /api/orders/{orderId}/status}</p>
     *
     * @param orderId identificador del pedido
     * @param body    cuerpo JSON con la clave {@code estado}
     * @return el pedido actualizado con HTTP 200 (OK)
     */
    @Operation(summary = "Cambiar estado del pedido",
            description = "Actualiza el estado del pedido (pendiente, pagado, enviado, entregado, cancelado). "
                    + "El nuevo estado se envia en el cuerpo JSON con la clave 'estado'.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "400", description = "Estado invalido"),
        @ApiResponse(responseCode = "404", description = "Pedido inexistente")
    })
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(
            @Parameter(description = "Identificador del pedido") @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, body.get("estado")));
    }
}
