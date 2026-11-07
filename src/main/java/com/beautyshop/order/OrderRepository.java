package com.beautyshop.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos de pedidos y su detalle.
 *
 * <p>Usa JDBC puro ({@code Connection}, {@code PreparedStatement},
 * {@code ResultSet}) sobre las tablas {@code orders} y {@code order_items}.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Repository
public class OrderRepository {

    /** Sentencia SQL para insertar la cabecera de un pedido. */
    private static final String SQL_INSERT_ORDER =
            "INSERT INTO orders (user_id, order_date, status, total) VALUES (?, ?, ?, ?)";

    /** Sentencia SQL para insertar una linea de pedido. */
    private static final String SQL_INSERT_ITEM =
            "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

    /** Sentencia SQL para listar los pedidos de un usuario. */
    private static final String SQL_FIND_BY_USER =
            "SELECT id, user_id, order_date, status, total " +
            "FROM orders WHERE user_id = ? ORDER BY order_date DESC";

    /** Sentencia SQL para buscar un pedido por su id. */
    private static final String SQL_FIND_BY_ID =
            "SELECT id, user_id, order_date, status, total FROM orders WHERE id = ?";

    /** Sentencia SQL para listar las lineas de un pedido. */
    private static final String SQL_FIND_ITEMS =
            "SELECT id, order_id, product_id, quantity, unit_price " +
            "FROM order_items WHERE order_id = ? ORDER BY id";

    /** Sentencia SQL para actualizar el estado de un pedido. */
    private static final String SQL_UPDATE_STATUS =
            "UPDATE orders SET status = ? WHERE id = ?";

    /** Origen de datos que provee las conexiones JDBC hacia MySQL. */
    private final DataSource dataSource;

    /**
     * Constructor con inyeccion del {@link DataSource}.
     *
     * @param dataSource origen de datos configurado en {@code DatabaseConfig}
     */
    public OrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserta la cabecera de un pedido y le asigna el id generado.
     *
     * @param order pedido a insertar
     * @return el pedido con su id asignado
     */
    public Order saveOrder(Order order) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SQL_INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, order.getIdUsuario());
            statement.setTimestamp(2, Timestamp.valueOf(order.getFecha()));
            statement.setString(3, order.getEstado());
            statement.setBigDecimal(4, order.getTotal());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setIdPedido(generatedKeys.getLong(1));
                }
            }
            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Inserta una linea de detalle de un pedido.
     *
     * @param item linea a insertar
     */
    public void saveOrderItem(OrderItem item) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT_ITEM)) {

            statement.setLong(1, item.getIdPedido());
            statement.setLong(2, item.getIdProducto());
            statement.setInt(3, item.getCantidad());
            statement.setBigDecimal(4, item.getPrecioUnitario());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el detalle del pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Lista los pedidos de un usuario, del mas reciente al mas antiguo.
     *
     * @param userId identificador del usuario
     * @return lista de pedidos (vacia si no tiene)
     */
    public List<Order> findByUser(Long userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_USER)) {

            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapRowToOrder(resultSet));
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los pedidos: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un pedido por su identificador.
     *
     * @param orderId identificador del pedido
     * @return un {@link Optional} con el pedido si existe
     */
    public Optional<Order> findById(Long orderId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {

            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToOrder(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Lista las lineas de detalle de un pedido.
     *
     * @param orderId identificador del pedido
     * @return lista de lineas del pedido
     */
    public List<OrderItem> findItemsByOrder(Long orderId) {
        List<OrderItem> items = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ITEMS)) {

            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapRowToOrderItem(resultSet));
                }
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar el detalle del pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza el estado de un pedido.
     *
     * @param orderId identificador del pedido
     * @param estado  nuevo estado
     * @return {@code true} si se actualizo alguna fila
     */
    public boolean updateStatus(Long orderId, String estado) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATUS)) {

            statement.setString(1, estado);
            statement.setLong(2, orderId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estado del pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Convierte la fila actual de un {@link ResultSet} en un {@link Order}.
     *
     * @param resultSet resultado posicionado en la fila a mapear
     * @return el pedido construido
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private Order mapRowToOrder(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setIdPedido(resultSet.getLong("id"));
        order.setIdUsuario(resultSet.getLong("user_id"));
        order.setFecha(resultSet.getTimestamp("order_date").toLocalDateTime());
        order.setEstado(resultSet.getString("status"));
        order.setTotal(resultSet.getBigDecimal("total"));
        return order;
    }

    /**
     * Convierte la fila actual de un {@link ResultSet} en un {@link OrderItem}.
     *
     * @param resultSet resultado posicionado en la fila a mapear
     * @return la linea de pedido construida
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private OrderItem mapRowToOrderItem(ResultSet resultSet) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(resultSet.getLong("id"));
        item.setIdPedido(resultSet.getLong("order_id"));
        item.setIdProducto(resultSet.getLong("product_id"));
        item.setCantidad(resultSet.getInt("quantity"));
        item.setPrecioUnitario(resultSet.getBigDecimal("unit_price"));
        return item;
    }
}
