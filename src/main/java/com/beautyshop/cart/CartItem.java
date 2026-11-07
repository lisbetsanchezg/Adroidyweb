package com.beautyshop.cart;

import java.math.BigDecimal;

/**
 * Entidad que representa una linea del carrito de compras.
 *
 * <p>Refleja una fila de la tabla {@code cart_items}: un producto agregado por
 * un usuario, con su cantidad y el precio unitario al momento de agregarlo.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class CartItem {

    /** Identificador de la linea de carrito. */
    private Long id;

    /** Identificador del usuario dueno del carrito. */
    private Long idUsuario;

    /** Identificador del producto agregado. */
    private Long idProducto;

    /** Cantidad de unidades del producto. */
    private Integer cantidad;

    /** Precio unitario del producto al agregarlo. */
    private BigDecimal precioUnitario;

    /**
     * Constructor vacio para deserializar el JSON y poblar desde el ResultSet.
     */
    public CartItem() {
    }

    /**
     * Constructor con los datos de la linea de carrito.
     *
     * @param idUsuario      identificador del usuario
     * @param idProducto     identificador del producto
     * @param cantidad       cantidad de unidades
     * @param precioUnitario precio unitario
     */
    public CartItem(Long idUsuario, Long idProducto, Integer cantidad, BigDecimal precioUnitario) {
        this.idUsuario = idUsuario;
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
     * Devuelve el identificador del usuario.
     *
     * @return el id del usuario
     */
    public Long getIdUsuario() {
        return idUsuario;
    }

    /**
     * Asigna el identificador del usuario.
     *
     * @param idUsuario el id de usuario a establecer
     */
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
