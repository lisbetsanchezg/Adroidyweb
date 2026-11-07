package com.beautyshop.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.beautyshop.model.User;

/**
 * Repositorio de acceso a datos para la entidad {@link User}.
 *
 * <p>Usa JDBC puro ({@code Connection}, {@code PreparedStatement},
 * {@code ResultSet}) sobre la tabla {@code users}. Las conexiones se obtienen
 * del {@link DataSource} configurado en {@code config/DatabaseConfig}.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Repository
public class UserRepository {

    /** Sentencia SQL para registrar un usuario nuevo. */
    private static final String SQL_INSERT =
            "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

    /** Sentencia SQL para verificar si un correo ya esta registrado. */
    private static final String SQL_EXISTS_EMAIL =
            "SELECT id FROM users WHERE email = ?";

    /** Sentencia SQL para validar credenciales (correo + hash de contrasena). */
    private static final String SQL_FIND_BY_CREDENTIALS =
            "SELECT id, name, email FROM users WHERE email = ? AND password = ?";

    /** Sentencia SQL para verificar si un usuario existe por su id. */
    private static final String SQL_EXISTS_BY_ID =
            "SELECT id FROM users WHERE id = ?";

    /** Origen de datos que provee las conexiones JDBC hacia MySQL. */
    private final DataSource dataSource;

    /**
     * Constructor con inyeccion del {@link DataSource}.
     *
     * @param dataSource origen de datos configurado en {@code DatabaseConfig}
     */
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Registra un usuario nuevo y le asigna el id generado por la base de datos.
     *
     * @param user usuario a registrar (con la contrasena ya cifrada)
     * @return el usuario con su id asignado
     */
    public User save(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            // Asigna los parametros del INSERT en orden
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.executeUpdate();

            // Recupera el id autoincremental generado por MySQL
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Indica si ya existe un usuario con el correo indicado.
     *
     * @param email correo a verificar
     * @return {@code true} si el correo ya esta registrado
     */
    public boolean existsByEmail(String email) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_EXISTS_EMAIL)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                // Si hay al menos una fila, el correo ya existe
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el correo: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un usuario que coincida con el correo y el hash de la contrasena.
     *
     * <p>Se usa para validar el inicio de sesion. El usuario devuelto no
     * incluye la contrasena.</p>
     *
     * @param email          correo ingresado
     * @param hashedPassword hash de la contrasena ingresada
     * @return un {@link Optional} con el usuario si las credenciales coinciden
     */
    public Optional<User> findByCredentials(String email, String hashedPassword) {
        try (Connection connection = dataSource.getConnection();
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
                    return Optional.of(user);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar las credenciales: " + e.getMessage(), e);
        }
    }

    /**
     * Indica si existe un usuario con el id indicado.
     *
     * @param id identificador del usuario
     * @return {@code true} si el usuario existe
     */
    public boolean existsById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_EXISTS_BY_ID)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el usuario: " + e.getMessage(), e);
        }
    }
}
