package com.beautyshop.order;

import java.math.BigDecimal;

/**
 * Entidad que representa una linea (detalle) de un pedido.
 *
 * <p>Refleja una fila de la tabla {@code order_items}: un producto dentro de un
 * pedido, con su cantidad y el precio unitario al momento de la compra.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class OrderItem {

    /** Identificador de la linea de pedido. */
    private Long id;

    /** Identificador del pedido al que pertenece. */
    private Long idPedido;

    /** Identificador del producto pedido. */
    private Long idProducto;

    /** Cantidad de unidades. */
    private Integer cantidad;

    /** Precio unitario al momento de la compra. */
    private BigDecimal precioUnitario;

    /**
     * Constructor vacio para poblar la entidad desde el ResultSet.
     */
    public OrderItem() {
    }

    /**
     * Constructor con los datos de la linea de pedido.
     *
     * @param idPedido       identificador del pedido
     * @param idProducto     identificador del producto
     * @param cantidad       cantidad de unidades
     * @param precioUnitario precio unitario
     */
    public OrderItem(Long idPedido, Long idProducto, Integer cantidad, BigDecimal precioUnitario) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /**
     * Devuelve el identificador de la linea.
     *
     * @return el id
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador de la linea.
     *
     * @param id el id a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el identificador del pedido.
     *
     * @return el id del pedido
     */
    public Long getIdPedido() {
        return idPedido;
    }

    /**
     * Asigna el identificador del pedido.
     *
     * @param idPedido el id de pedido a establecer
     */
    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    /**
     * Devuelve el identificador del producto.
     *
     * @return el id del producto
     */
    public Long getIdProducto() {
        return idProducto;
    }

    /**
     * Asigna el identificador del producto.
     *
     * @param idProducto el id de producto a establecer
     */
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Devuelve la cantidad de unidades.
     *
     * @return la cantidad
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Asigna la cantidad de unidades.
     *
     * @param cantidad la cantidad a establecer
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Devuelve el precio unitario.
     *
     * @return el precio unitario
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Asigna el precio unitario.
     *
     * @param precioUnitario el precio unitario a establecer
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
