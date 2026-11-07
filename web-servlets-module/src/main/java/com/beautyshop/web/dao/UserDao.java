package com.beautyshop.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.beautyshop.web.config.DatabaseConnection;
import com.beautyshop.web.model.User;

/**
 * DAO (Data Access Object) para la entidad {@link User}.
 *
 * <p>Encapsula el acceso a la tabla {@code users} usando JDBC puro
 * ({@code Connection}, {@code PreparedStatement}, {@code ResultSet}).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class UserDao {

    /** Sentencia SQL para registrar un usuario nuevo. */
    private static final String SQL_INSERT =
            "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

    /** Sentencia SQL para verificar si un correo ya esta registrado. */
    private static final String SQL_EXISTS_EMAIL =
            "SELECT id FROM users WHERE email = ?";

    /** Sentencia SQL para validar credenciales (correo + hash de contrasena). */
    private static final String SQL_FIND_BY_CREDENTIALS =
            "SELECT id, name, email FROM users WHERE email = ? AND password = ?";

    /**
     * Registra un usuario nuevo en la base de datos.
     *
     * @param user usuario a registrar (con la contrasena ya cifrada)
     * @return {@code true} si se inserto correctamente
     * @throws SQLException si ocurre un error de acceso a datos
     */
    public boolean registerUser(User user) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {

            // Asigna los parametros del INSERT en orden
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());

            // executeUpdate devuelve el numero de filas insertadas
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Indica si ya existe un usuario con el correo indicado.
     *
     * @param email correo a verificar
     * @return {@code true} si el correo ya esta registrado
     * @throws SQLException si ocurre un error de acceso a datos
     */
    public boolean existsByEmail(String email) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_EXISTS_EMAIL)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                // Si hay al menos una fila, el correo ya existe
                return resultSet.next();
            }
        }
    }

    /**
     * Busca un usuario que coincida con el correo y el hash de la contrasena.
     *
     * <p>Se usa para validar el inicio de sesion. Devuelve el usuario (sin la
     * contrasena) si las credenciales son correctas, o {@code null} si no.</p>
     *
     * @param email        correo ingresado
     * @param hashedPassword hash de la contrasena ingresada
     * @return el usuario autenticado, o {@code null} si las credenciales no coinciden
     * @throws SQLException si ocurre un error de acceso a datos
     */
    public User findByCredentials(String email, String hashedPassword) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_CREDENTIALS)) {

            statement.setString(1, email);
            statement.setString(2, hashedPassword);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Construye el usuario a partir de la fila encontrada
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    return user;
                }
                return null;
            }
        }
    }
}
