package com.beautyshop.order;

import java.util.List;

/**
 * Objeto de respuesta con el detalle completo de un pedido:
 * la cabecera ({@link Order}) y sus lineas ({@link OrderItem}).
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class OrderDetail {

    /** Cabecera del pedido. */
    private Order order;

    /** Lineas (detalle) del pedido. */
    private List<OrderItem> items;

    /**
     * Constructor con la cabecera y las lineas del pedido.
     *
     * @param order cabecera del pedido
     * @param items lineas del pedido
     */
    public OrderDetail(Order order, List<OrderItem> items) {
        this.order = order;
        this.items = items;
    }

    /**
     * Devuelve la cabecera del pedido.
     *
     * @return el pedido
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Devuelve las lineas del pedido.
     *
     * @return la lista de lineas
     */
    public List<OrderItem> getItems() {
        return items;
    }
}
