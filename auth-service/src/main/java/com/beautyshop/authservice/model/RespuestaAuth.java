package com.beautyshop.authservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Objeto de respuesta de las operaciones de autenticacion.
 *
 * <p>Siempre incluye un {@code mensaje}. En las respuestas exitosas tambien
 * lleva el {@code nombre} y el {@code correo} del usuario; nunca la contrasena.
 * Los campos nulos se omiten del JSON.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespuestaAuth {

    /** Mensaje descriptivo del resultado de la operacion. */
    private String mensaje;

    /** Nombre del usuario (solo en respuestas exitosas). */
    private String nombre;

    /** Correo del usuario (solo en respuestas exitosas). */
    private String correo;

    /**
     * Constructor solo con mensaje (usado en errores).
     *
     * @param mensaje mensaje del resultado
     */
    public RespuestaAuth(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Constructor con mensaje y datos del usuario (respuestas exitosas).
     *
     * @param mensaje mensaje del resultado
     * @param nombre  nombre del usuario
     * @param correo  correo del usuario
     */
    public RespuestaAuth(String mensaje, String nombre, String correo) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.correo = correo;
    }

    /**
     * Devuelve el mensaje del resultado.
     *
     * @return el mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Devuelve el nombre del usuario.
     *
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el correo del usuario.
     *
     * @return el correo
     */
    public String getCorreo() {
        return correo;
    }
}
