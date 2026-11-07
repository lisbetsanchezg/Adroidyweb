package com.beautyshop.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.beautyshop.model.Product;

/**
 * Repositorio de acceso a datos para la entidad {@link Product}.
 *
 * <p>Implementa las 4 operaciones CRUD (crear, leer, actualizar, eliminar)
 * usando JDBC puro: {@code java.sql.Connection}, {@code PreparedStatement} y
 * {@code ResultSet}. No se utiliza ningun ORM.</p>
 *
 * <p>Las conexiones se obtienen del {@link DataSource} configurado en
 * {@code config/DatabaseConfig} y se cierran automaticamente mediante
 * <em>try-with-resources</em> para evitar fugas de recursos.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Repository
public class ProductRepository {

    /** Sentencia SQL para insertar un producto nuevo. */
    private static final String SQL_INSERT =
            "INSERT INTO products (name, description, price, category, stock, image) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    /** Sentencia SQL para consultar todos los productos. */
    private static final String SQL_SELECT_ALL =
            "SELECT id, name, description, price, category, stock, image FROM products ORDER BY id";

    /** Sentencia SQL para consultar un producto por su id. */
    private static final String SQL_SELECT_BY_ID =
            "SELECT id, name, description, price, category, stock, image FROM products WHERE id = ?";

    /** Sentencia SQL para actualizar un producto existente. */
    private static final String SQL_UPDATE =
            "UPDATE products SET name = ?, description = ?, price = ?, category = ?, " +
            "stock = ?, image = ? WHERE id = ?";

    /** Sentencia SQL para eliminar un producto por su id. */
    private static final String SQL_DELETE = "DELETE FROM products WHERE id = ?";

    /** Origen de datos que provee las conexiones JDBC hacia MySQL. */
    private final DataSource dataSource;

    /**
     * Constructor con inyeccion del {@link DataSource}.
     *
     * @param dataSource origen de datos configurado en {@code DatabaseConfig}
     */
    public ProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserta un producto nuevo en la base de datos.
     *
     * <p>Usa {@code RETURN_GENERATED_KEYS} para recuperar el id autoincremental
     * generado por MySQL y asignarlo al objeto devuelto.</p>
     *
     * @param product datos del producto a crear (sin id)
     * @return el producto creado con su id asignado
     */
    public Product save(Product product) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            // Asigna cada parametro (?) de la sentencia INSERT en orden
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.setString(4, product.getCategory());
            statement.setInt(5, product.getStock());
            statement.setString(6, product.getImage());

            // Ejecuta la insercion
            statement.executeUpdate();

            // Recupera el id generado automaticamente por MySQL
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el producto: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta todos los productos almacenados.
     *
     * @return lista de productos (vacia si no hay registros)
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            // Recorre cada fila del resultado y la convierte en un objeto Product
            while (resultSet.next()) {
                products.add(mapRowToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los productos: " + e.getMessage(), e);
        }
    }

    /**
     * Consulta un producto por su identificador.
     *
     * @param id identificador del producto a buscar
     * @return un {@link Optional} con el producto si existe, o vacio si no
     */
    public Optional<Product> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {

            statement.setLong(1, id);

            // Ejecuta la consulta y mapea la unica fila esperada
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToProduct(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el producto: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id      identificador del producto a modificar
     * @param product nuevos datos del producto
     * @return {@code true} si se actualizo alguna fila; {@code false} si el id no existe
     */
    public boolean update(Long id, Product product) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            // Asigna los nuevos valores y, al final, el id del registro a modificar
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.setString(4, product.getCategory());
            statement.setInt(5, product.getStock());
            statement.setString(6, product.getImage());
            statement.setLong(7, id);

            // executeUpdate devuelve el numero de filas afectadas
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el producto: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto a eliminar
     * @return {@code true} si se elimino alguna fila; {@code false} si el id no existe
     */
    public boolean deleteById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setLong(1, id);

            // executeUpdate devuelve el numero de filas eliminadas
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el producto: " + e.getMessage(), e);
        }
    }

    /**
     * Convierte la fila actual de un {@link ResultSet} en un objeto {@link Product}.
     *
     * <p>Metodo auxiliar reutilizado por las consultas para mapear columnas de la
     * tabla a los atributos de la entidad.</p>
     *
     * @param resultSet resultado posicionado en la fila a mapear
     * @return el producto construido a partir de la fila
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private Product mapRowToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getLong("id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setCategory(resultSet.getString("category"));
        product.setStock(resultSet.getInt("stock"));
        product.setImage(resultSet.getString("image"));
        return product;
    }
}
