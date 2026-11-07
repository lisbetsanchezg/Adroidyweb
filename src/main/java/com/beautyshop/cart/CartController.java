package com.beautyshop.cart;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST del carrito de compras.
 *
 * <p>Ruta base: {@code /api/cart}. Delega la logica en {@link CartService} y
 * devuelve el contenido del carrito con el total en cada operacion.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@Tag(name = "Carrito", description = "Gestion del carrito de compras por usuario")
public class CartController {

    /** Servicio con la logica del carrito. */
    private final CartService cartService;

    /**
     * Constructor con inyeccion del servicio.
     *
     * @param cartService servicio del carrito
     */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Agrega un producto al carrito del usuario.
     *
     * <p>Endpoint: {@code POST /api/cart/{userId}/items}</p>
     *
     * @param userId identificador del usuario
     * @param item   datos de la linea (idProducto y cantidad) en el cuerpo JSON
     * @return el carrito actualizado con HTTP 201 (CREATED)
     */
    @Operation(summary = "Agregar producto al carrito",
            description = "Agrega un producto (idProducto y cantidad) al carrito del usuario. "
                    + "Si el producto ya estaba, suma la cantidad.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carrito actualizado"),
        @ApiResponse(responseCode = "400", description = "Cantidad invalida (debe ser mayor que 0)"),
        @ApiResponse(responseCode = "404", description = "Usuario o producto inexistente")
    })
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItem(
            @Parameter(description = "Identificador del usuario") @PathVariable Long userId,
            @RequestBody CartItem item) {
        CartResponse cart = cartService.addItem(userId, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    /**
     * Quita un producto del carrito del usuario.
     *
     * <p>Endpoint: {@code DELETE /api/cart/{userId}/items/{productId}}</p>
     *
     * @param userId    identificador del usuario
     * @param productId identificador del producto a quitar
     * @return el carrito actualizado con HTTP 200 (OK)
     */
    @Operation(summary = "Quitar producto del carrito",
            description = "Elimina un producto del carrito del usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito actualizado"),
        @ApiResponse(responseCode = "404", description = "El producto no esta en el carrito")
    })
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @Parameter(description = "Identificador del usuario") @PathVariable Long userId,
            @Parameter(description = "Identificador del producto") @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    /**
     * Lista el contenido del carrito del usuario con el total calculado.
     *
     * <p>Endpoint: {@code GET /api/cart/{userId}}</p>
     *
     * @param userId identificador del usuario
     * @return el carrito con sus lineas y el total, con HTTP 200 (OK)
     */
    @Operation(summary = "Ver carrito",
            description = "Lista el contenido del carrito del usuario con el total calculado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contenido del carrito con el total"),
        @ApiResponse(responseCode = "404", description = "Usuario inexistente")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @Parameter(description = "Identificador del usuario") @PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    /**
     * Actualiza la cantidad de un producto del carrito.
     *
     * <p>Endpoint: {@code PUT /api/cart/{userId}/items/{productId}}</p>
     *
     * @param userId    identificador del usuario
     * @param productId identificador del producto
     * @param item      cuerpo JSON con la nueva cantidad
     * @return el carrito actualizado con HTTP 200 (OK)
     */
    @Operation(summary = "Actualizar cantidad",
            description = "Fija la cantidad de un producto que ya esta en el carrito.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito actualizado"),
        @ApiResponse(responseCode = "400", description = "Cantidad invalida (debe ser mayor que 0)"),
        @ApiResponse(responseCode = "404", description = "El producto no esta en el carrito")
    })
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @Parameter(description = "Identificador del usuario") @PathVariable Long userId,
            @Parameter(description = "Identificador del producto") @PathVariable Long productId,
            @RequestBody CartItem item) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, productId, item.getCantidad()));
    }
}
