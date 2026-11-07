# Informe de pruebas por módulo

Fecha de ejecución: **25/10/2026**. Ambiente: macOS arm64, JDK 17.0.17, MySQL 9.5.0, Node 22.23.2 (ver [AMBIENTES.md](AMBIENTES.md)). Todos los resultados de este informe provienen de ejecuciones reales; lo que no se ejecutó está marcado como **NO EJECUTADO**.

## 1. Resumen

| Tipo de prueba | Comando | Total | Aprobadas | Fallidas | Resultado |
|----------------|---------|-------|-----------|----------|-----------|
| Unitarias — backend principal | `./mvnw test` | 48 | 48 | 0 | ✅ `BUILD SUCCESS` |
| Unitarias — auth-service | `cd auth-service && ./mvnw test` | 8 | 8 | 0 | ✅ `BUILD SUCCESS` |
| End-to-end (Playwright, Chromium) | `npx playwright test` | 7 | 7 | 0 | ✅ (ver nota de la primera corrida) |
| Colección Postman (Newman) | `newman run ...` | 5 solicitudes / 8 aserciones | 8 | 0 | ✅ |
| Humo manual de la API (curl) | ver §4 | 33 llamadas | — | — | ✅ códigos esperados |
| Despliegue WAR en Tomcat (manual) | ver §5 | 5 comprobaciones | 5 | 0 | ✅ |
| JDBC demo (consola) | `./mvnw -q compile exec:java` | 1 ejecución | 1 | 0 | ✅ |

Desglose de las unitarias del backend (`target/surefire-reports`): `PasswordUtilTest` 4, `ProductServiceTest` 8, `CartServiceTest` 16, `OrderServiceTest` 14, `AuthServiceTest` 6 → **48**.

> **Nota sobre las pruebas e2e:** la primera corrida dio 5 aprobadas y 2 fallidas (`home.spec.js`) porque la tabla `products` tenía los 5 productos duplicados (el script de datos de ejemplo no era idempotente y se había ejecutado dos veces; el texto «Rubor en Polvo Coral» aparecía dos veces). Se corrigió `database/products.sql` para que solo inserte si la tabla está vacía, se eliminaron los duplicados y la segunda corrida dio **7 de 7 aprobadas**. No se modificó ninguna prueba.

## 2. Pruebas unitarias por módulo

Las pruebas usan repositorios falsos en memoria (sin base de datos ni Mockito). Resultado de todas: **aprobada**.

### 2.1 Productos — `ProductServiceTest` (8)

| # | Prueba | Qué verifica | Resultado |
|---|--------|--------------|-----------|
| 1 | `createProduct guarda un producto valido` | Delega en el repositorio y devuelve el producto con `id` | ✅ |
| 2 | `createProduct rechaza nombre vacio` | `IllegalArgumentException` con nombre en blanco | ✅ |
| 3 | `createProduct rechaza precio negativo` | `IllegalArgumentException` con precio < 0 | ✅ |
| 4 | `createProduct rechaza stock negativo` | `IllegalArgumentException` con stock < 0 | ✅ |
| 5 | `getAllProducts devuelve la lista del repositorio` | Devuelve los 2 productos del repositorio | ✅ |
| 6 | `updateProduct devuelve el producto cuando existe` | Devuelve el producto con el `id` indicado | ✅ |
| 7 | `updateProduct devuelve vacio cuando no existe` | `Optional` vacío si no se actualizó nada | ✅ |
| 8 | `deleteProduct delega en el repositorio` | Devuelve `true` cuando el repositorio elimina | ✅ |

### 2.2 Autenticación — `AuthServiceTest` (6) y `PasswordUtilTest` (4)

| # | Prueba | Qué verifica | Resultado |
|---|--------|--------------|-----------|
| 1 | `register cifra la contrasena y no la devuelve` | La contraseña se guarda con hash y la respuesta no la incluye | ✅ |
| 2 | `register rechaza datos invalidos` | Nombre en blanco → `IllegalArgumentException` | ✅ |
| 3 | `register rechaza correos duplicados` | Correo existente → `IllegalArgumentException` | ✅ |
| 4 | `login exitoso devuelve el usuario` | Credenciales correctas → usuario | ✅ |
| 5 | `login falla con contrasena incorrecta` | Contraseña errónea → `Optional` vacío | ✅ |
| 6 | `login rechaza campos vacios` | Correo y contraseña vacíos → `IllegalArgumentException` | ✅ |
| 7 | `El hash es determinista para la misma entrada` | Misma entrada → mismo hash | ✅ |
| 8 | `Entradas diferentes producen hashes diferentes` | Entradas distintas → hashes distintos | ✅ |
| 9 | `El hash tiene 64 caracteres hexadecimales` | Longitud 64 | ✅ |
| 10 | `El hash coincide con el valor conocido de SHA-256` | `"1234"` → `03ac6742…46f4` | ✅ |

### 2.3 Carrito — `CartServiceTest` (16)

| # | Prueba | Qué verifica | Resultado |
|---|--------|--------------|-----------|
| 1 | `addItem agrega una linea nueva con el precio del producto` | Ignora el precio enviado por el cliente; total = 2 × 18 000 = 36 000 | ✅ |
| 2 | `addItem suma la cantidad si el producto ya estaba en el carrito` | 2 + 3 = 5 unidades en una sola línea; total 90 000 | ✅ |
| 3 | `addItem rechaza cantidad cero` | `IllegalArgumentException` | ✅ |
| 4 | `addItem rechaza cantidad negativa` | `IllegalArgumentException` | ✅ |
| 5 | `addItem rechaza cantidad nula` | `IllegalArgumentException` | ✅ |
| 6 | `addItem lanza ResourceNotFoundException si el producto no existe` | Producto inexistente → 404 | ✅ |
| 7 | `addItem lanza ResourceNotFoundException si el usuario no existe` | Usuario inexistente → 404 | ✅ |
| 8 | `updateQuantity reemplaza la cantidad y recalcula el total` | 2 → 4 unidades; total 72 000 | ✅ |
| 9 | `updateQuantity rechaza cantidad menor o igual a cero` | `IllegalArgumentException` | ✅ |
| 10 | `updateQuantity lanza ResourceNotFoundException si el producto no esta en el carrito` | 404 | ✅ |
| 11 | `removeItem quita la linea y deja el total en cero` | Carrito vacío, total 0 | ✅ |
| 12 | `removeItem lanza ResourceNotFoundException si el producto no esta en el carrito` | 404 | ✅ |
| 13 | `getCart devuelve carrito vacio con total cero` | Lista vacía, total 0 | ✅ |
| 14 | `getCart lanza ResourceNotFoundException si el usuario no existe` | 404 | ✅ |
| 15 | `calculateTotal suma precio unitario por cantidad` | 2 × 10 000 + 3 × 2 500,50 = 27 501,50 | ✅ |
| 16 | `clearCart elimina todas las lineas del usuario` | Carrito vacío tras `clearCart` | ✅ |

### 2.4 Pedidos — `OrderServiceTest` (14)

| # | Prueba | Qué verifica | Resultado |
|---|--------|--------------|-----------|
| 1 | `createOrder crea el pedido pendiente con el total del carrito` | Estado `pendiente`; total 25 000; `idUsuario` correcto | ✅ |
| 2 | `createOrder copia las lineas del carrito al pedido` | 2 líneas con cantidad y precio unitario | ✅ |
| 3 | `createOrder vacia el carrito` | Carrito sin líneas tras crear el pedido | ✅ |
| 4 | `createOrder rechaza carrito vacio` | `IllegalArgumentException` y no se guarda ningún pedido | ✅ |
| 5 | `createOrder lanza ResourceNotFoundException si el usuario no existe` | 404 | ✅ |
| 6 | `getOrdersByUser devuelve los pedidos del usuario` | 1 pedido en el historial | ✅ |
| 7 | `getOrdersByUser devuelve lista vacia si no hay pedidos` | Lista vacía | ✅ |
| 8 | `getOrdersByUser lanza ResourceNotFoundException si el usuario no existe` | 404 | ✅ |
| 9 | `getOrderDetail devuelve cabecera y lineas` | Cabecera + 2 líneas | ✅ |
| 10 | `getOrderDetail lanza ResourceNotFoundException si el pedido no existe` | 404 | ✅ |
| 11 | `updateStatus normaliza y guarda un estado valido` | `"  PAGADO "` → `pagado` | ✅ |
| 12 | `updateStatus rechaza un estado invalido` | `IllegalArgumentException` | ✅ |
| 13 | `updateStatus rechaza estado nulo` | `IllegalArgumentException` | ✅ |
| 14 | `updateStatus lanza ResourceNotFoundException si el pedido no existe` | 404 | ✅ |

> Las pruebas se escribieron sobre el comportamiento que el código **realmente** implementa. `OrderService` no descuenta stock ni valida transiciones entre estados (solo que el estado sea uno de los 5 permitidos), por lo que no existen pruebas de esas reglas; están listadas como límites en el [manual técnico](MANUAL_TECNICO.md#8-mecanismos-de-seguridad-reales-y-límites).

### 2.5 auth-service — `AuthServiceTest` (8)

| # | Prueba | Qué verifica | Resultado |
|---|--------|--------------|-----------|
| 1 | `registrar guarda la contrasena cifrada` | Contraseña distinta del texto original y de 64 caracteres | ✅ |
| 2 | `registrar rechaza campos vacios` | Nombre, correo o contraseña vacíos/nulos → `IllegalArgumentException` | ✅ |
| 3 | `registrar rechaza usuario nulo` | `IllegalArgumentException` | ✅ |
| 4 | `registrar rechaza correo duplicado` | `IllegalArgumentException` | ✅ |
| 5 | `autenticar devuelve el usuario con credenciales correctas` | Devuelve el usuario registrado | ✅ |
| 6 | `autenticar falla con contrasena incorrecta` | `Optional` vacío | ✅ |
| 7 | `autenticar falla con correo no registrado` | `Optional` vacío | ✅ |
| 8 | `autenticar devuelve vacio con datos en blanco` | Sin excepción, `Optional` vacío | ✅ |

## 3. Pruebas end-to-end y de colección

### 3.1 Frontend — Playwright (Chromium), segunda corrida: 7 de 7

| Módulo | Archivo | Prueba | Qué verifica | Resultado |
|--------|---------|--------|--------------|-----------|
| Autenticación | `auth.spec.js` | el login valida el formato del correo | Mensaje «El correo no tiene un formato valido.» | ✅ |
| Autenticación | `auth.spec.js` | el registro exige todos los campos | Mensaje «Todos los campos son obligatorios.» | ✅ |
| Autenticación | `auth.spec.js` | registra un usuario nuevo y luego inicia sesion | Registro → redirige a `/login` → login → aparece «Salir» | ✅ |
| Productos / Home | `home.spec.js` | muestra el hero y carga productos desde la API | Título del hero y producto «Rubor en Polvo Coral» | ✅ (tras corregir datos duplicados; 1.ª corrida ❌) |
| Productos / Home | `home.spec.js` | el buscador filtra los productos por nombre | Filtra «rubor» y oculta «Labial Mate Rojo Pasion» | ✅ (tras corregir datos duplicados; 1.ª corrida ❌) |
| Carrito y pedidos | `shop.spec.js` | login, agregar al carrito, finalizar y ver el pedido | Flujo completo hasta `/pedidos` con «Pedido #N» | ✅ |
| Carrito | `shop.spec.js` | el carrito pide iniciar sesion si no hay usuario | Mensaje «Inicia sesion para ver y gestionar tu carrito.» | ✅ |

### 3.2 Postman / Newman — colección `BeautyShop Auth` (auth-service, puerto 8080)

Resultado: 5 solicitudes, 5 scripts de prueba, **8 aserciones, 0 fallidas**.

| Solicitud | Aserciones | Estado HTTP | Resultado |
|-----------|-----------|-------------|-----------|
| Registro exitoso | código 201; el body contiene correo; el body NO contiene contraseña | 201 | ✅ |
| Registro con correo duplicado | código 400 | 400 | ✅ |
| Login exitoso | código 200; el body contiene «satisfactoria» | 200 | ✅ |
| Login con contraseña incorrecta | código 401 | 401 | ✅ |
| Login con usuario inexistente | código 401 | 401 | ✅ |

La colección solo cubre el servicio `auth-service`; **no existe colección Postman para el backend principal** (carrito, pedidos, productos) — esa API se verificó con curl (§4) y con Swagger.

## 4. Humo manual de la API del backend principal (curl, `http://localhost:8090`)

| Módulo | Llamada | Esperado | Obtenido |
|--------|---------|----------|----------|
| Productos | `GET /api/products/1` | 200 | 200 |
| Productos | `GET /api/products/999` | 404 | 404 |
| Productos | `POST /api/products` válido | 201 | 201 |
| Productos | `POST /api/products` con nombre vacío | 400 | 400 |
| Productos | `PUT /api/products/14` | 200 | 200 |
| Productos | `PUT /api/products/9999` | 404 | 404 |
| Productos | `DELETE /api/products/14` (dos veces) | 204 y 404 | 204 y 404 |
| Autenticación | `POST /api/auth/register` | 201 | 201 |
| Autenticación | registro repetido | 400 | 400 |
| Autenticación | registro con datos vacíos | 400 | 400 |
| Autenticación | `POST /api/auth/login` correcto | 200 | 200 |
| Autenticación | login con contraseña incorrecta | 401 | 401 |
| Carrito | `GET /api/cart/13` (vacío) | 200 | 200 |
| Carrito | `POST /api/cart/13/items` (×2 productos) | 201 | 201 |
| Carrito | agregar con cantidad 0 | 400 | 400 |
| Carrito | agregar producto inexistente | 404 | 404 |
| Carrito | `GET /api/cart/99999` | 404 | 404 |
| Carrito | `PUT /api/cart/13/items/1` | 200 | 200 |
| Carrito | `DELETE /api/cart/13/items/3` (dos veces) | 200 y 404 | 200 y 404 |
| Pedidos | `POST /api/orders/13` | 201 | 201 |
| Pedidos | crear pedido con carrito vacío | 400 | 400 |
| Pedidos | `GET /api/orders/13` | 200 | 200 |
| Pedidos | `GET /api/orders/detail/9` | 200 | 200 |
| Pedidos | `GET /api/orders/detail/9999` | 404 | 404 |
| Pedidos | `PATCH /api/orders/9/status` `pagado` | 200 | 200 |
| Pedidos | `PATCH` con estado inválido | 400 | 400 |
| Pedidos | `PATCH /api/orders/9999/status` | 404 | 404 |
| Swagger | `GET /swagger-ui.html` | 302 → `/swagger-ui/index.html` | 302 |
| Swagger | `GET /v3/api-docs` | 200 | 200 |

Respuestas completas en [MODULOS.md](MODULOS.md). Las 33 llamadas incluyen las repeticiones indicadas en la tabla y una comprobación de CORS (`OPTIONS /api/products` con `Origin: http://localhost:4300` → `200` con `Access-Control-Allow-Origin: *`).

Además, el flujo completo (catálogo → login → agregar al carrito → finalizar compra → historial) se ejecutó en el navegador y quedó documentado con capturas en [DESPLIEGUE.md](DESPLIEGUE.md).

## 5. Módulo Servlets/JSP desplegado en Tomcat 10.1.61 (manual)

| Comprobación | Esperado | Obtenido |
|--------------|----------|----------|
| `GET /beautyshop-web/` y `/login.html` | 200 | 200 |
| `POST /register` con datos válidos | 302 a `login.html?registered=true` | 302 a `login.html?registered=true` |
| `POST /register` con correo repetido | página de error «El correo ya esta registrado.» | ✅ |
| `POST /login` correcto | `welcome.jsp` con «Hola, Servlet User!» | ✅ |
| `POST /login` con contraseña incorrecta / vacío | «Correo o contrasena incorrectos.» / «Debes ingresar correo y contrasena.» | ✅ |

## 6. Pruebas no ejecutadas

- **No existen pruebas unitarias** para los controladores REST, los repositorios JDBC (requerirían una BD real) ni el módulo `web-servlets-module`; no se escribieron en esta evidencia.
- **No hay pruebas unitarias de componentes React** (solo e2e).
- **No se ejecutó prueba de carga ni de seguridad automatizada.**
- **Postman para el backend principal: NO EXISTE / NO EJECUTADO.**
