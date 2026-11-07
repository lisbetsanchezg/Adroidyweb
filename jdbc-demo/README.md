# JDBC Demo — GA7-220501096-AA2-EV01

Mini proyecto **independiente** que demuestra la conexión a **MySQL** mediante
**JDBC puro** y ejecuta las operaciones de **insertar, actualizar y eliminar**
sobre la tabla `products`.

**Estudiante:** Lisbet Sánchez · **Ficha:** 228118

## Contenido
```
jdbc-demo/
├── pom.xml                                  # driver mysql-connector-j (PUNTO 1)
├── mvnw, mvnw.cmd, .mvn/                     # Maven Wrapper (correr sin instalar Maven)
├── database/products.sql                    # tabla products
└── src/main/java/com/beautyshop/jdbc/
    └── ProductJdbcDemo.java                 # la demostración (PUNTOS 2 a 6)
```

## Puntos que muestra el video
1. **Driver JDBC en las librerías** → dependencia `mysql-connector-j` en `pom.xml`.
2. **Paquete `java.sql.*`** → `import java.sql.*;` en `ProductJdbcDemo`.
3. **`Class.forName`** → carga del driver.
4. **`Connection` con `DriverManager`** → `DriverManager.getConnection(...)`.
5. **`Statement` desde el `Connection`** → `connection.createStatement()`.
6. **Ejecutar** insertar / actualizar / eliminar.

## Cómo ejecutar

### 1. Preparar la base de datos (una sola vez)
Con MySQL en ejecución:
```bash
mysql -u root -p < database/products.sql
```

### 2. Ajustar credenciales (si aplica)
En `ProductJdbcDemo.java`, revisa las constantes `URL`, `USER`, `PASSWORD`.
Por defecto: `localhost:3306`, usuario `root`, contraseña vacía.

### 3. Ejecutar el programa
```bash
./mvnw -q compile exec:java
```
(En Windows: `mvnw.cmd -q compile exec:java`)

**Salida esperada:**
```
[3] Driver JDBC cargado con Class.forName: com.mysql.cj.jdbc.Driver
[4] Connection creada con DriverManager ...
[5] Statement creado desde el Connection.
>> INSERTAR: 1 fila insertada (id = N)
   CONSULTAR -> id=N, name=Labial Demo JDBC, price=19900.00, stock=5
>> ACTUALIZAR: 1 fila actualizada (id = N)
   CONSULTAR -> id=N, name=Labial Demo Actualizado, price=24900.00, stock=12
>> ELIMINAR: 1 fila eliminada (id = N)
   CONSULTAR -> el producto con id=N ya no existe.
```
