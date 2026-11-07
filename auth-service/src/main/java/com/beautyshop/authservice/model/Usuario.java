package com.beautyshop.authservice.model;

/**
 * Entidad que representa un usuario del servicio de autenticacion.
 *
 * <p>Se usa tanto para recibir los datos de registro/login (JSON con los campos
 * {@code nombre}, {@code correo}, {@code contrasena}) como para almacenar el
 * usuario en memoria.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class Usuario {

    /** Nombre del usuario. */
    private String nombre;

    /** Correo electronico (identificador unico). */
    private String correo;

    /** Contrasena (se almacena cifrada). */
    private String contrasena;

    /**
     * Constructor vacio requerido para deserializar el JSON de la peticion.
     */
    public Usuario() {
    }

    /**
     * Constructor con los datos del usuario.
     *
     * @param nombre     nombre del usuario
     * @param correo     correo electronico
     * @param contrasena contrasena (cifrada al almacenar)
     */
    public Usuario(String nombre, String correo, String contrasena) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
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
     * Asigna el nombre del usuario.
     *
     * @param nombre el nombre a establecer
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el correo del usuario.
     *
     * @return el correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Asigna el correo del usuario.
     *
     * @param correo el correo a establecer
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Devuelve la contrasena del usuario.
     *
     * @return la contrasena
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Asigna la contrasena del usuario.
     *
     * @param contrasena la contrasena a establecer
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
