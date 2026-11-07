# Documentación por módulo y componente

Entradas, salidas y errores de cada módulo de BeautyShop. Los ejemplos JSON son **respuestas reales** obtenidas en la ejecución local del 25/10/2026 contra el backend en `http://localhost:8090` (MySQL 9.5.0). Los nombres de campo son los reales del código.

> Ver también: [MANUAL_TECNICO.md](MANUAL_TECNICO.md) · [CONFIGURACION.md](CONFIGURACION.md) · [INFORME_PRUEBAS.md](INFORME_PRUEBAS.md)

**Formato de error común del backend principal:** `{"error": "mensaje"}` con estado `400` (datos inválidos), `404` (recurso inexistente) o `500` (error inesperado). Lo produce `GlobalExceptionHandler`.

| Módulo | Carpeta / paquete | Puerto / URL base |
|--------|-------------------|-------------------|
| [1. Productos](#1-productos) | `com.beautyshop.{controller,service,repository,model}` | `http://localhost:8090/api/products` |
| [2. Autenticación](#2-autenticación-backend-principal) | `com.beautyshop.auth` | `http://localhost:8090/api/auth` |
| [3. Carrito](#3-carrito) | `com.beautyshop.cart` | `http://localhost:8090/api/cart` |
| [4. Pedidos](#4-pedidos) | `com.beautyshop.order` | `http://localhost:8090/api/orders` |
| [5. Frontend](#5-frontend-react) | `frontend/` | `http://localhost:4300` (dev) |
| [6. auth-service](#6-auth-service-microservicio-de-autenticación) | `auth-service/` | `http://localhost:8080/api/auth` |
| [7. Servlets / JSP](#7-servlets--jsp) | `web-servlets-module/` | `http://localhost:8080/beautyshop-web/` |
| [8. JDBC demo](#8-jdbc-demo) | `jdbc-demo/` | consola |

---

## 1. Productos

**Responsabilidad:** CRUD del catálogo de productos con JDBC puro.

| Componente | Responsabilidad |
|-----------|-----------------|
| `ProductController` | Expone `/api/products`, devuelve `201/200/204/404` |
| `ProductService` | Valida nombre, precio y stock; delega en el repositorio |
| `ProductRepository` | SQL sobre la tabla `products` (`save`, `findAll`, `findById`, `update`, `deleteById`) |
| `Product` | Modelo: `id`, `name`, `description`, `price`, `category`, `stock`, `image` |

### Entradas y salidas

| Método | Ruta | Entrada | Salida correcta | Errores |
|--------|------|---------|-----------------|---------|
| `POST` | `/api/products` | Body JSON `{name, description, price, category, stock, image}` | `201` con el producto y su `id` | `400` nombre vacío, precio < 0, stock < 0 |
| `GET` | `/api/products` | — | `200` lista de productos | — |
| `GET` | `/api/products/{id}` | `id` en la ruta | `200` producto | `404` (cuerpo vacío) |
| `PUT` | `/api/products/{id}` | `id` + mismo body que `POST` | `200` producto actualizado | `400` validación, `404` (cuerpo vacío) |
| `GET` | `/images/{archivo}` | Nombre del archivo guardado en `image` (por ejemplo `labial-rojo.jpg`) | `200` con la foto (`image/jpeg`); la sirve Spring Boot desde `src/main/resources/static/images/` | `404` si el archivo no existe (la web y la app muestran una imagen de reemplazo) |
| `DELETE` | `/api/products/{id}` | `id` | `204` sin cuerpo | `404` (cuerpo vacío) |

**Ejemplo real — `GET /api/products/1` → `200`:**

```json
{"id":1,"name":"Labial Mate Rojo Pasion","description":"Labial de larga duracion acabado mate","price":25900.00,"category":"labios","stock":40,"image":"labial-rojo.jpg"}
```

**Ejemplo real — `POST /api/products` → `201`:**

Petición: `{"name":"Producto Humo","description":"prueba","price":1000,"category":"otros","stock":5,"image":"x.jpg"}`

```json
{"id":14,"name":"Producto Humo","description":"prueba","price":1000,"category":"otros","stock":5,"image":"x.jpg"}
```

**Error real — `POST` con `{"name":"","price":-1,"stock":1}` → `400`:** `{"error":"El nombre del producto es obligatorio."}`
Otros mensajes del servicio: `"El precio debe ser un valor positivo."`, `"El stock no puede ser negativo."`.

Verificado además: `PUT /api/products/14` → `200`; `PUT /api/products/9999` → `404`; `DELETE /api/products/14` → `204`; segundo `DELETE` → `404`.

---

## 2. Autenticación (backend principal)

**Responsabilidad:** registrar usuarios y validar credenciales contra la tabla `users` (contraseña con SHA-256).

| Componente | Responsabilidad |
|-----------|-----------------|
| `AuthController` | `POST /api/auth/register` y `POST /api/auth/login` |
| `AuthService` | Valida campos, evita correos duplicados, cifra la contraseña, oculta el hash en la respuesta |
| `UserRepository` | `save`, `existsByEmail`, `findByCredentials`, `existsById` |
| `PasswordUtil` | `hashPassword` (SHA-256 en hexadecimal) |
| `User` | Modelo: `id`, `name`, `email`, `password` |

| Método | Ruta | Entrada (JSON) | Salida correcta | Errores |
|--------|------|----------------|-----------------|---------|
| `POST` | `/api/auth/register` | `{name, email, password}` | `201` usuario con `password: null` | `400` campos vacíos o correo repetido |
| `POST` | `/api/auth/login` | `{email, password}` | `200` datos del usuario | `401` credenciales incorrectas; `400` campos vacíos |

**Ejemplo real — registro → `201`:**

```json
{"id":13,"name":"Usuaria Humo","email":"smoke_1792944193@correo.com","password":null}
```

**Ejemplo real — login correcto → `200`:** misma forma, `{"id":13,"name":"Usuaria Humo","email":"smoke_1792944193@correo.com","password":null}`. **No se devuelve token** (ver límites en el manual técnico).

**Errores reales:**

| Caso | Estado | Cuerpo |
|------|--------|--------|
| Correo ya registrado | `400` | `{"error":"El correo ya esta registrado."}` |
| Nombre vacío (todo vacío) | `400` | `{"error":"El nombre es obligatorio."}` |
| Contraseña incorrecta | `401` | `{"error":"Correo o contrasena incorrectos."}` |

Otros mensajes: `"El correo es obligatorio."`, `"La contrasena es obligatoria."`, `"Debes ingresar correo y contrasena."`.

---

## 3. Carrito

**Responsabilidad:** carrito por usuario guardado en la BD (`cart_items`). El precio unitario siempre se toma del producto; el total es la suma de `precioUnitario × cantidad`.

| Componente | Responsabilidad |
|-----------|-----------------|
| `CartController` | Rutas `/api/cart/{userId}...` |
| `CartService` | Valida usuario, producto y cantidad (> 0); suma cantidades si el producto ya estaba; calcula el total |
| `CartRepository` | `insert`, `findByUserAndProduct`, `findByUser`, `updateQuantity`, `delete`, `deleteByUser` |
| `CartItem` | Línea: `id`, `idUsuario`, `idProducto`, `cantidad`, `precioUnitario` |
| `CartResponse` | Salida: `idUsuario`, `items`, `total` |

| Método | Ruta | Entrada | Salida correcta | Errores |
|--------|------|---------|-----------------|---------|
| `GET` | `/api/cart/{userId}` | `userId` | `200` `CartResponse` | `404` usuario no existe |
| `POST` | `/api/cart/{userId}/items` | Body `{idProducto, cantidad}` | `201` `CartResponse` | `400` cantidad ≤ 0 o nula; `404` usuario o producto no existe |
| `PUT` | `/api/cart/{userId}/items/{productId}` | Body `{cantidad}` | `200` `CartResponse` | `400` cantidad inválida; `404` el producto no está en el carrito |
| `DELETE` | `/api/cart/{userId}/items/{productId}` | — | `200` `CartResponse` | `404` el producto no está en el carrito |

**Ejemplo real — `POST /api/cart/13/items` con `{"idProducto":1,"cantidad":2}` → `201`:**

```json
{"idUsuario":13,"items":[{"id":11,"idUsuario":13,"idProducto":1,"cantidad":2,"precioUnitario":25900.00}],"total":51800.00}
```

**Ejemplo real — `PUT /api/cart/13/items/1` con `{"cantidad":3}` → `200`:**

```json
{"idUsuario":13,"items":[{"id":11,"idUsuario":13,"idProducto":1,"cantidad":3,"precioUnitario":25900.00},{"id":12,"idUsuario":13,"idProducto":3,"cantidad":1,"precioUnitario":68500.00}],"total":146200.00}
```

**Errores reales:**

| Caso | Estado | Cuerpo |
|------|--------|--------|
| Cantidad 0 | `400` | `{"error":"La cantidad debe ser mayor que 0."}` |
| Producto 999 inexistente | `404` | `{"error":"El producto 999 no existe."}` |
| Usuario 99999 inexistente | `404` | `{"error":"El usuario 99999 no existe."}` |
| Quitar un producto que no está | `404` | `{"error":"El producto 3 no esta en el carrito."}` |

---

## 4. Pedidos

**Responsabilidad:** convertir el carrito en un pedido (estado inicial `pendiente`), listar el historial, consultar el detalle y cambiar el estado. Al crear el pedido el carrito queda vacío. **No descuenta stock.**

| Componente | Responsabilidad |
|-----------|-----------------|
| `OrderController` | Rutas `/api/orders...` |
| `OrderService` | Usa `CartService.getCart` para el total, copia las líneas al pedido, vacía el carrito, valida estados |
| `OrderRepository` | `saveOrder`, `saveOrderItem`, `findByUser`, `findById`, `findItemsByOrder`, `updateStatus` |
| `Order` | Cabecera: `idPedido`, `idUsuario`, `fecha`, `estado`, `total` |
| `OrderItem` | Línea: `id`, `idPedido`, `idProducto`, `cantidad`, `precioUnitario` |
| `OrderDetail` | Salida: `order` + `items` |

Estados válidos: `pendiente`, `pagado`, `enviado`, `entregado`, `cancelado` (se normalizan a minúscula y sin espacios).

| Método | Ruta | Entrada | Salida correcta | Errores |
|--------|------|---------|-----------------|---------|
| `POST` | `/api/orders/{userId}` | `userId` (usa el carrito actual) | `201` `OrderDetail` | `400` carrito vacío; `404` usuario no existe |
| `GET` | `/api/orders/{userId}` | `userId` | `200` lista de `Order` (vacía si no hay) | `404` usuario no existe |
| `GET` | `/api/orders/detail/{orderId}` | `orderId` | `200` `OrderDetail` | `404` pedido no existe |
| `PATCH` | `/api/orders/{orderId}/status` | Body `{"estado": "pagado"}` | `200` `Order` actualizado | `400` estado inválido o nulo; `404` pedido no existe |

**Ejemplo real — `POST /api/orders/13` → `201`:**

```json
{"order":{"idPedido":9,"idUsuario":13,"fecha":"2026-10-25T11:03:19.479665","estado":"pendiente","total":77700.00},"items":[{"id":null,"idPedido":9,"idProducto":1,"cantidad":3,"precioUnitario":25900.00}]}
```

**Ejemplo real — `GET /api/orders/13` → `200`:**

```json
[{"idPedido":9,"idUsuario":13,"fecha":"2026-10-25T11:03:19","estado":"pendiente","total":77700.00}]
```

**Ejemplo real — `PATCH /api/orders/9/status` con `{"estado":"pagado"}` → `200`:**

```json
{"idPedido":9,"idUsuario":13,"fecha":"2026-10-25T11:03:19","estado":"pagado","total":77700.00}
```

**Errores reales:**

| Caso | Estado | Cuerpo |
|------|--------|--------|
| Crear pedido con carrito vacío | `400` | `{"error":"El carrito esta vacio; no se puede crear el pedido."}` |
| Estado `xx` | `400` | `{"error":"Estado invalido. Validos: [entregado, cancelado, pendiente, enviado, pagado]"}` |
| Pedido 9999 inexistente | `404` | `{"error":"El pedido 9999 no existe."}` |

`GET /api/orders/detail/9` devuelve `200` con `order` y los `items` (esta vez con `id` asignado por la BD).

---

## 5. Frontend (React)

**Responsabilidad:** interfaz de la tienda. Consume la API del backend a través de `/api` (proxy de Vite → `127.0.0.1:8090`).

| Componente | Entrada | Salida / efecto |
|-----------|---------|-----------------|
| `pages/Home` | Productos de `GET /products`; texto del buscador (`SearchBar`) | Hero + lista filtrada por nombre; botón «Agregar» |
| `pages/Catalogo` | `GET /products` | Listado completo de productos |
| `pages/Login` / `LoginForm` | Correo, contraseña (valida formato de correo) | `POST /auth/login`; guarda usuario en `localStorage`; va a `/` |
| `pages/Register` / `RegisterForm` | Nombre, correo, contraseña (todos obligatorios) | `POST /auth/register`; va a `/login` |
| `pages/Cart` / `CartSummary` | Estado de `CartContext` | Cambiar cantidad, quitar, «Finalizar compra» → `POST /orders/{userId}` → `/pedidos` |
| `pages/Orders` / `OrderHistory` | `GET /orders/{userId}` | Tarjetas «Pedido #N» con fecha, estado y total |
| `pages/Checkout` | Nombre, correo, dirección, tarjeta, vencimiento `MM/AA`, CVV (datos ficticios) | Valida formato, crea el pedido y va a `/pedidos` |
| `Navbar`, `Footer`, `Toast`, `ProductCard`, `ProductList` | props | Presentación y avisos |
| `context/AuthContext` | acciones `login`, `register`, `logout` | `currentUser` persistido en `localStorage` |
| `context/CartContext` | usuario actual | `items`, `total`, `totalCount`, `changeQuantity`, `removeItem`, `refreshCart` (llama a `/cart`) |
| `services/*.js` | parámetros de cada llamada | Datos de respuesta del backend; los errores se leen de `error.response.data.error` |

Mensajes de validación visibles (verificados en pruebas e2e): «El correo no tiene un formato valido.» y «Todos los campos son obligatorios.». Capturas en [DESPLIEGUE.md](DESPLIEGUE.md).

---

## 6. auth-service (microservicio de autenticación)

**Responsabilidad:** servicio autocontenido de registro/login con usuarios **en memoria** (sin BD). Spring Boot 3.2.5, puerto `8080`. Los campos están en español.

| Componente | Responsabilidad |
|-----------|-----------------|
| `AuthController` | `POST /api/auth/register`, `POST /api/auth/login` |
| `AuthService` | Valida, cifra con SHA-256, autentica |
| `UsuarioRepository` | `ConcurrentHashMap` indexado por correo |
| `GlobalExceptionHandler` | Traduce errores a `400` con `{"mensaje": ...}` |
| `Usuario` / `RespuestaAuth` | Entrada `{nombre, correo, contrasena}` / salida `{mensaje, nombre, correo}` |

| Método | Ruta | Entrada | Salida | Errores |
|--------|------|---------|--------|---------|
| `POST` | `/api/auth/register` | `{nombre, correo, contrasena}` | `201` | `400` |
| `POST` | `/api/auth/login` | `{correo, contrasena}` | `200` | `401` |

**Ejemplos reales:**

| Caso | Estado | Cuerpo |
|------|--------|--------|
| Registro correcto | `201` | `{"mensaje":"Usuario registrado exitosamente","nombre":"Ana","correo":"ejemplo_1792944230@correo.com"}` |
| Correo repetido | `400` | `{"mensaje":"El correo ya esta registrado."}` |
| Campos vacíos | `400` | `{"mensaje":"Todos los campos son obligatorios."}` |
| Login correcto | `200` | `{"mensaje":"Autenticacion satisfactoria","nombre":"Ana","correo":"ejemplo_1792944230@correo.com"}` |
| Contraseña incorrecta | `401` | `{"mensaje":"Autenticacion fallida: credenciales invalidas"}` |

---

## 7. Servlets / JSP

**Responsabilidad:** registro e inicio de sesión con Servlets y JSP puros contra la misma tabla `users`. WAR `beautyshop-web.war`, contexto `/beautyshop-web`, Tomcat 10.1.

| Componente | Entrada | Salida |
|-----------|---------|--------|
| `login.html` / `register.html` | Formularios HTML | Envían `POST` a `/login` y `/register` |
| `RegisterServlet` (`/register`) | Parámetros de formulario `name`, `email`, `password`; `GET` redirige a `register.html` | Éxito: `302` a `login.html?registered=true`. Error: reenvía a `error.jsp` (`Todos los campos son obligatorios.`, `El correo ya esta registrado.`) |
| `LoginServlet` (`/login`) | Parámetros `email`, `password`; `GET` redirige a `login.html` | Éxito: `welcome.jsp` («Hola, {nombre}!») y sesión con `userName`, `userEmail`. Error: `error.jsp` (`Correo o contrasena incorrectos.`, `Debes ingresar correo y contrasena.`) |
| `UserDao` | `User` / correo + hash | JDBC con `PreparedStatement` sobre `users` |
| `DatabaseConnection` | variables `BEAUTYSHOP_DB_USER`, `BEAUTYSHOP_DB_PASSWORD` (opcionales) | `Connection` JDBC |

Comprobado en el despliegue local (Tomcat 10.1.61): registro `302 → login.html?registered=true`; login correcto muestra `Hola, Servlet User!`; contraseña incorrecta muestra `Correo o contrasena incorrectos.`; campos vacíos muestran `Debes ingresar correo y contrasena.`; correo repetido muestra `El correo ya esta registrado.`. Los errores de formulario responden `200` con la página `error.jsp`.

---

## 8. JDBC demo

**Responsabilidad:** programa de consola que muestra el uso de JDBC puro (`Class.forName`, `DriverManager`, `Statement`) con insertar, actualizar y eliminar sobre `products`. Sin entradas; salida por consola.

Salida real de la ejecución (`./mvnw -q compile exec:java`):

```
[3] Driver JDBC cargado con Class.forName: com.mysql.cj.jdbc.Driver
>> INSERTAR: 1 fila insertada (id = 15)
   CONSULTAR -> id=15, name=Labial Demo JDBC, price=19900.00, stock=5
>> ACTUALIZAR: 1 fila actualizada (id = 15)
   CONSULTAR -> id=15, name=Labial Demo Actualizado, price=24900.00, stock=12
>> ELIMINAR: 1 fila eliminada (id = 15)
   CONSULTAR -> el producto con id=15 ya no existe.
```

---

## Documentación interactiva (Swagger / OpenAPI)

Con el backend en ejecución: Swagger UI en `http://localhost:8090/swagger-ui.html` (redirige a `/swagger-ui/index.html`) y la especificación en `http://localhost:8090/v3/api-docs`. Muestra los 4 grupos (Autenticacion, Carrito, Pedidos, Productos) y los esquemas `Product`, `CartItem`, `CartResponse`, `Order`, `OrderDetail`, `OrderItem`, `User`. Captura: [07_swagger_ui.png](capturas/07_swagger_ui.png).
