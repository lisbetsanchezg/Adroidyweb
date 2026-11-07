package com.beautyshop.exception;

/**
 * Excepcion lanzada cuando un recurso solicitado no existe
 * (usuario, producto, linea de carrito o pedido).
 *
 * <p>El {@code GlobalExceptionHandler} la traduce a una respuesta HTTP 404.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /** Version para la serializacion de la excepcion. */
    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepcion con un mensaje descriptivo.
     *
     * @param message mensaje que explica el recurso no encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
