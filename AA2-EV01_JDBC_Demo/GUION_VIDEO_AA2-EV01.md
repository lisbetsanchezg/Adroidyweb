# Guion del video — GA7-220501096-AA2-EV01

**Evidencia:** Codificación de módulos del software (conexión a BD con JDBC).
**Estudiante:** Lisbet Sánchez · **Ficha:** 228118

En el video debes mostrar los 6 puntos. Todo está reunido en la clase de
demostración **`src/main/java/com/beautyshop/jdbc/ProductJdbcDemo.java`**
(busca los comentarios `PUNTO n`).

---

## Qué mostrar (los 6 puntos)

| # | Qué pide | Dónde mostrarlo |
|---|----------|-----------------|
| 1 | **Ubicación del driver JDBC en las librerías** | En `pom.xml`, la dependencia `mysql-connector-j`. En el IDE, ábrela en **"Maven Dependencies" / "External Libraries"** y muestra el `mysql-connector-j-8.3.0.jar`. |
| 2 | **Paquete `java.sql.*`** | Arriba de `ProductJdbcDemo.java`: `import java.sql.*;` (comentario `PUNTO 2`). |
| 3 | **Inicialización del driver con `Class.forName`** | En `main()`: `Class.forName(DRIVER)` (comentario `PUNTO 3`). |
| 4 | **`Connection` creado con `DriverManager`** | `DriverManager.getConnection(URL, USER, PASSWORD)` (comentario `PUNTO 4`). |
| 5 | **`Statement` creado desde el `Connection`** | `connection.createStatement()` (comentario `PUNTO 5`). |
| 6 | **Ejecutar: insertar, actualizar y eliminar** | Corre el programa (ver abajo). La consola muestra INSERTAR → ACTUALIZAR → ELIMINAR con sus consultas. |

---

## Cómo ejecutar el programa (punto 6)

**Requisito:** MySQL en ejecución y la base `beautyshop` con la tabla `products`
creada (script `database/products.sql`).

### Opción A — desde el IDE (recomendado para el video)
1. Abre `ProductJdbcDemo.java`.
2. Clic derecho → **Run 'ProductJdbcDemo.main()'** (IntelliJ) o **Run As → Java Application** (Eclipse/NetBeans).
3. Se ve en la consola cómo inserta, consulta, actualiza y elimina.

### Opción B — desde la terminal
```bash
./mvnw -q -DskipTests compile
java -cp "target/classes:$(find ~/.m2 -name 'mysql-connector-j-*.jar' | head -1)" com.beautyshop.jdbc.ProductJdbcDemo
```

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

---

## Otros requisitos de la evidencia (ya cumplidos)

- **Versionamiento (Git):** el proyecto usa Git. Sube el repo a GitHub y pega el
  enlace en `repositorio.txt` y en la entrega.
- **Estándares de codificación:**
  - Clases en `PascalCase` (`ProductJdbcDemo`, `ProductRepository`).
  - Métodos/variables en `camelCase` (`insertarProducto`, `nuevoId`).
  - Constantes en `UPPER_SNAKE_CASE` (`URL`, `DRIVER`).
  - Paquetes en minúscula (`com.beautyshop.jdbc`, `com.beautyshop.repository`).
- **CRUD completo:** inserción, consulta, actualización y eliminación (en la demo
  y también en el módulo real `ProductRepository` + `ProductController`).

> Nota: el módulo REST real (`ProductRepository`) usa `PreparedStatement` y el
> `DataSource` de Spring (más seguro y profesional). La clase `ProductJdbcDemo`
> usa `Class.forName` + `DriverManager` + `Statement` porque son los elementos
> que el video pide señalar explícitamente.

---

## Checklist antes de grabar
- [ ] MySQL corriendo y `products.sql` ejecutado.
- [ ] Repo subido a GitHub; enlace listo.
- [ ] Programa compila y corre (Opción A o B).
- [ ] Guion a la mano para señalar los puntos 1 a 6.
