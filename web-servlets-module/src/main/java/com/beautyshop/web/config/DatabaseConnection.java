package com.beautyshop.web.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad de conexion a la base de datos MySQL mediante JDBC puro.
 *
 * <p>Al no usar Spring en este modulo, la conexion se abre directamente con
 * {@link DriverManager}. Usuario y contrasena se leen de las variables de
 * entorno {@code BEAUTYSHOP_DB_USER} y {@code BEAUTYSHOP_DB_PASSWORD}; si no
 * estan definidas se usan los valores por defecto de las constantes. Ajusta
 * usuario, contrasena y nombre de la BD segun tu entorno local.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public final class DatabaseConnection {

    /** URL de conexion JDBC hacia el servidor MySQL. */
    private static final String URL =
            "jdbc:mysql://localhost:3306/beautyshop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    /** Usuario de la base de datos (por defecto, o el de BEAUTYSHOP_DB_USER). */
    private static final String USER = readEnv("BEAUTYSHOP_DB_USER", "root");

    /** Contrasena de la base de datos (por defecto, o la de BEAUTYSHOP_DB_PASSWORD). */
    private static final String PASSWORD = readEnv("BEAUTYSHOP_DB_PASSWORD", "root");

    /** Nombre completo de la clase del driver JDBC de MySQL. */
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    /**
     * Constructor privado para impedir la creacion de instancias
     * (clase de utilidad con miembros estaticos).
     */
    private DatabaseConnection() {
    }

    /**
     * Lee una variable de entorno y devuelve un valor por defecto si no existe.
     *
     * @param name         nombre de la variable de entorno
     * @param defaultValue valor a usar si la variable no esta definida
     * @return el valor de la variable o el valor por defecto
     */
    private static String readEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null ? value : defaultValue;
    }

    /**
     * Abre y devuelve una nueva conexion JDBC hacia la base de datos.
     *
     * <p>Carga el driver de MySQL y solicita la conexion a {@code DriverManager}.
     * Quien invoca este metodo es responsable de cerrar la conexion.</p>
     *
     * @return una conexion activa a MySQL
     * @throws SQLException si el driver no se encuentra o falla la conexion
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Carga explicita del driver de MySQL (buena practica didactica)
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el driver de MySQL: " + e.getMessage(), e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
