package com.beautyshop.cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * Objeto de respuesta que representa el contenido completo del carrito de un
 * usuario junto con el total calculado.
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class CartResponse {

    /** Identificador del usuario dueno del carrito. */
    private Long idUsuario;

    /** Lineas del carrito. */
    private List<CartItem> items;

    /** Total del carrito (suma de precio unitario * cantidad de cada linea). */
    private BigDecimal total;

    /**
     * Constructor con todos los datos del carrito.
     *
     * @param idUsuario identificador del usuario
     * @param items     lineas del carrito
     * @param total     total calculado
     */
    public CartResponse(Long idUsuario, List<CartItem> items, BigDecimal total) {
        this.idUsuario = idUsuario;
        this.items = items;
        this.total = total;
    }

    /**
     * Devuelve el identificador del usuario.
     *
     * @return el id del usuario
     */
    public Long getIdUsuario() {
        return idUsuario;
    }

    /**
     * Devuelve las lineas del carrito.
     *
     * @return la lista de lineas
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * Devuelve el total del carrito.
     *
     * @return el total
     */
    public BigDecimal getTotal() {
        return total;
    }
}
