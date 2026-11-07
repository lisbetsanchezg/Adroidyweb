package com.beautyshop.cart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos del carrito de compras.
 *
 * <p>Usa JDBC puro ({@code Connection}, {@code PreparedStatement},
 * {@code ResultSet}) sobre la tabla {@code cart_items}. Las conexiones se
 * obtienen del {@link DataSource} configurado en {@code config/DatabaseConfig}.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Repository
public class CartRepository {

    /** Sentencia SQL para insertar una linea de carrito. */
    private static final String SQL_INSERT =
            "INSERT INTO cart_items (user_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

    /** Sentencia SQL para buscar una linea por usuario y producto. */
    private static final String SQL_FIND_BY_USER_PRODUCT =
            "SELECT id, user_id, product_id, quantity, unit_price " +
            "FROM cart_items WHERE user_id = ? AND product_id = ?";

    /** Sentencia SQL para listar las lineas del carrito de un usuario. */
    private static final String SQL_FIND_BY_USER =
            "SELECT id, user_id, product_id, quantity, unit_price " +
            "FROM cart_items WHERE user_id = ? ORDER BY id";

    /** Sentencia SQL para actualizar la cantidad de una linea. */
    private static final String SQL_UPDATE_QUANTITY =
            "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?";

    /** Sentencia SQL para eliminar una linea del carrito. */
    private static final String SQL_DELETE =
            "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";

    /** Sentencia SQL para vaciar el carrito de un usuario. */
    private static final String SQL_DELETE_BY_USER =
            "DELETE FROM cart_items WHERE user_id = ?";

    /** Origen de datos que provee las conexiones JDBC hacia MySQL. */
    private final DataSource dataSource;

    /**
     * Constructor con inyeccion del {@link DataSource}.
     *
     * @param dataSource origen de datos configurado en {@code DatabaseConfig}
     */
    public CartRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserta una linea nueva en el carrito.
     *
     * @param item linea a insertar
     * @return la linea insertada
     */
    public CartItem insert(CartItem item) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {

            statement.setLong(1, item.getIdUsuario());
            statement.setLong(2, item.getIdProducto());
            statement.setInt(3, item.getCantidad());
            statement.setBigDecimal(4, item.getPrecioUnitario());
            statement.executeUpdate();
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar al carrito: " + e.getMessage(), e);
        }
    }

    /**
     * Busca una linea del carrito por usuario y producto.
     *
     * @param userId    identificador del usuario
     * @param productId identificador del producto
     * @return un {@link Optional} con la linea si existe
     */
    public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_USER_PRODUCT)) {

            statement.setLong(1, userId);
            statement.setLong(2, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToCartItem(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el carrito: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todas las lineas del carrito de un usuario.
     *
     * @param userId identificador del usuario
     * @return lista de lineas (vacia si el carrito esta vacio)
     */
    public List<CartItem> findByUser(Long userId) {
        List<CartItem> items = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_USER)) {

            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapRowToCartItem(resultSet));
                }
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar el carrito: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza la cantidad de una linea del carrito.
     *
     * @param userId    identificador del usuario
     * @param productId identificador del producto
     * @param cantidad  nueva cantidad
     * @return {@code true} si se actualizo alguna fila
     */
    public boolean updateQuantity(Long userId, Long productId, Integer cantidad) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_QUANTITY)) {

            statement.setInt(1, cantidad);
            statement.setLong(2, userId);
            statement.setLong(3, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el carrito: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una linea del carrito.
     *
     * @param userId    identificador del usuario
     * @param productId identificador del producto
     * @return {@code true} si se elimino alguna fila
     */
    public boolean delete(Long userId, Long productId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setLong(1, userId);
            statement.setLong(2, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al quitar del carrito: " + e.getMessage(), e);
        }
    }

    /**
     * Vacia por completo el carrito de un usuario.
     *
     * @param userId identificador del usuario
     */
    public void deleteByUser(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_USER)) {

            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al vaciar el carrito: " + e.getMessage(), e);
        }
    }

    /**
     * Convierte la fila actual de un {@link ResultSet} en un {@link CartItem}.
     *
     * @param resultSet resultado posicionado en la fila a mapear
     * @return la linea de carrito construida
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private CartItem mapRowToCartItem(ResultSet resultSet) throws SQLException {
        CartItem item = new CartItem();
        item.setId(resultSet.getLong("id"));
        item.setIdUsuario(resultSet.getLong("user_id"));
        item.setIdProducto(resultSet.getLong("product_id"));
        item.setCantidad(resultSet.getInt("quantity"));
        item.setPrecioUnitario(resultSet.getBigDecimal("unit_price"));
        return item;
    }
}
