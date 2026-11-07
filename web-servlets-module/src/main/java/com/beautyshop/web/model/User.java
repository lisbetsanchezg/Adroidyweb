package com.beautyshop.web.model;

/**
 * Entidad que representa un usuario registrado de BeautyShop.
 *
 * <p>Refleja una fila de la tabla {@code users}. Se usa para transportar los
 * datos del usuario entre los servlets y la capa de acceso a datos (DAO).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class User {

    /** Identificador unico del usuario. */
    private Long id;

    /** Nombre del usuario. */
    private String name;

    /** Correo electronico (unico). */
    private String email;

    /** Hash de la contrasena. */
    private String password;

    /**
     * Constructor vacio para poblar la entidad por setters.
     */
    public User() {
    }

    /**
     * Constructor con los datos basicos de registro.
     *
     * @param name     nombre del usuario
     * @param email    correo electronico
     * @param password hash de la contrasena
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Devuelve el identificador del usuario.
     *
     * @return el id
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador del usuario.
     *
     * @param id el id a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del usuario.
     *
     * @return el nombre
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el nombre del usuario.
     *
     * @param name el nombre a establecer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Devuelve el correo del usuario.
     *
     * @return el correo
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asigna el correo del usuario.
     *
     * @param email el correo a establecer
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el hash de la contrasena.
     *
     * @return la contrasena
     */
    public String getPassword() {
        return password;
    }

    /**
     * Asigna el hash de la contrasena.
     *
     * @param password la contrasena a establecer
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
