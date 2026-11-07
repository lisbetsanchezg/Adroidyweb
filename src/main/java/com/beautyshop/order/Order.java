package com.beautyshop.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la cabecera de un pedido.
 *
 * <p>Refleja una fila de la tabla {@code orders}: el pedido de un usuario con su
 * fecha, estado y total.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class Order {

    /** Identificador del pedido. */
    private Long idPedido;

    /** Identificador del usuario que realizo el pedido. */
    private Long idUsuario;

    /** Fecha y hora en que se creo el pedido. */
    private LocalDateTime fecha;

    /** Estado del pedido (pendiente, pagado, enviado, entregado, cancelado). */
    private String estado;

    /** Total del pedido. */
    private BigDecimal total;

    /**
     * Constructor vacio para poblar la entidad desde el ResultSet.
     */
    public Order() {
    }

    /**
     * Constructor con los datos de la cabecera del pedido.
     *
     * @param idUsuario identificador del usuario
     * @param fecha     fecha del pedido
     * @param estado    estado del pedido
     * @param total     total del pedido
     */
    public Order(Long idUsuario, LocalDateTime fecha, String estado, BigDecimal total) {
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
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
     * @param idPedido el id a establecer
     */
    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
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
     * Devuelve la fecha del pedido.
     *
     * @return la fecha
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Asigna la fecha del pedido.
     *
     * @param fecha la fecha a establecer
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Devuelve el estado del pedido.
     *
     * @return el estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Asigna el estado del pedido.
     *
     * @param estado el estado a establecer
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Devuelve el total del pedido.
     *
     * @return el total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Asigna el total del pedido.
     *
     * @param total el total a establecer
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
