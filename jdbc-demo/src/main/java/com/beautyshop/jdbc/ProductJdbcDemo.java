package com.beautyshop.jdbc;

// PUNTO 2 del video: se importa el paquete java.sql.*
// (Connection, DriverManager, Statement, ResultSet, SQLException, etc.)
import java.sql.*;

/**
 * Demostracion de conexion a MySQL mediante JDBC puro para la evidencia
 * GA7-220501096-AA2-EV01 "Codificacion de modulos del software".
 *
 * <p>Esta clase es un programa independiente (tiene metodo {@code main}) que
 * muestra, de forma explicita y en un solo lugar, los elementos que pide el
 * video de la evidencia y ejecuta las operaciones de INSERTAR, ACTUALIZAR y
 * ELIMINAR sobre la tabla {@code products}.</p>
 *
 * <p>Guia de los puntos del video (busque los comentarios "PUNTO n"):</p>
 * <ol>
 *   <li>Ubicacion del driver JDBC: dependencia {@code mysql-connector-j} en el
 *       {@code pom.xml} (visible en las librerias/Maven Dependencies del proyecto).</li>
 *   <li>Paquete {@code java.sql.*}: en el import de arriba.</li>
 *   <li>Inicializacion del driver con {@code Class.forName}.</li>
 *   <li>Creacion del {@code Connection} con {@code DriverManager}.</li>
 *   <li>Creacion del {@code Statement} a partir del {@code Connection}.</li>
 *   <li>Ejecucion del programa (insertar, actualizar, eliminar).</li>
 * </ol>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class ProductJdbcDemo {

    /** URL de conexion JDBC hacia la base de datos MySQL. */
    private static final String URL =
            "jdbc:mysql://localhost:3306/beautyshop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    /** Usuario de la base de datos. */
    private static final String USER = "root";

    /** Contrasena de la base de datos (vacia en la instalacion local). */
    private static final String PASSWORD = "";

    /** Nombre completo de la clase del driver JDBC de MySQL. */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Metodo principal: ejecuta la demostracion de CRUD con JDBC.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("=== Demostracion JDBC - Modulo de Productos (AA2-EV01) ===\n");

        try {
            // PUNTO 3 del video: se inicializa (carga) el driver JDBC con Class.forName
            Class.forName(DRIVER);
            System.out.println("[3] Driver JDBC cargado con Class.forName: " + DRIVER);

            // PUNTO 4 del video: se crea el objeto Connection por medio de DriverManager.
            // try-with-resources cierra la conexion automaticamente al terminar.
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                System.out.println("[4] Connection creada con DriverManager hacia: " + URL + "\n");

                // PUNTO 5 del video: se crea el objeto Statement a partir del Connection.
                try (Statement statement = connection.createStatement()) {
                    System.out.println("[5] Statement creado desde el Connection.\n");

                    // PUNTO 6 del video: se ejecuta insertar, actualizar y eliminar.
                    long nuevoId = insertarProducto(statement);
                    consultarProducto(statement, nuevoId);

                    actualizarProducto(statement, nuevoId);
                    consultarProducto(statement, nuevoId);

                    eliminarProducto(statement, nuevoId);
                    consultarProducto(statement, nuevoId);
                }
            }

            System.out.println("\n=== Demostracion finalizada correctamente ===");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontro el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        }
    }

    /**
     * INSERTAR: agrega un producto de prueba y devuelve el id generado.
     *
     * @param statement objeto Statement activo
     * @return el id autogenerado del producto insertado
     * @throws SQLException si ocurre un error al ejecutar la sentencia
     */
    private static long insertarProducto(Statement statement) throws SQLException {
        String sqlInsert = "INSERT INTO products (name, description, price, category, stock, image) "
                + "VALUES ('Labial Demo JDBC', 'Producto de prueba', 19900, 'labios', 5, 'demo.jpg')";

        // executeUpdate devuelve el numero de filas afectadas
        int filas = statement.executeUpdate(sqlInsert, Statement.RETURN_GENERATED_KEYS);

        long idGenerado = 0;
        try (ResultSet llaves = statement.getGeneratedKeys()) {
            if (llaves.next()) {
                idGenerado = llaves.getLong(1);
            }
        }
        System.out.println(">> INSERTAR: " + filas + " fila insertada (id = " + idGenerado + ")");
        return idGenerado;
    }

    /**
     * ACTUALIZAR: cambia el nombre, el precio y el stock del producto indicado.
     *
     * @param statement objeto Statement activo
     * @param id        identificador del producto a actualizar
     * @throws SQLException si ocurre un error al ejecutar la sentencia
     */
    private static void actualizarProducto(Statement statement, long id) throws SQLException {
        String sqlUpdate = "UPDATE products SET name = 'Labial Demo Actualizado', "
                + "price = 24900, stock = 12 WHERE id = " + id;

        int filas = statement.executeUpdate(sqlUpdate);
        System.out.println(">> ACTUALIZAR: " + filas + " fila actualizada (id = " + id + ")");
    }

    /**
     * ELIMINAR: borra el producto de prueba.
     *
     * @param statement objeto Statement activo
     * @param id        identificador del producto a eliminar
     * @throws SQLException si ocurre un error al ejecutar la sentencia
     */
    private static void eliminarProducto(Statement statement, long id) throws SQLException {
        String sqlDelete = "DELETE FROM products WHERE id = " + id;

        int filas = statement.executeUpdate(sqlDelete);
        System.out.println(">> ELIMINAR: " + filas + " fila eliminada (id = " + id + ")");
    }

    /**
     * CONSULTAR: muestra el producto (o indica que ya no existe).
     *
     * @param statement objeto Statement activo
     * @param id        identificador del producto a consultar
     * @throws SQLException si ocurre un error al ejecutar la sentencia
     */
    private static void consultarProducto(Statement statement, long id) throws SQLException {
        String sqlSelect = "SELECT id, name, price, stock FROM products WHERE id = " + id;

        try (ResultSet resultSet = statement.executeQuery(sqlSelect)) {
            if (resultSet.next()) {
                System.out.println("   CONSULTAR -> id=" + resultSet.getLong("id")
                        + ", name=" + resultSet.getString("name")
                        + ", price=" + resultSet.getBigDecimal("price")
                        + ", stock=" + resultSet.getInt("stock"));
            } else {
                System.out.println("   CONSULTAR -> el producto con id=" + id + " ya no existe.");
            }
        }
    }
}
