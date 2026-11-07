# BeautyShop 💄

Tienda online de maquillaje. Proyecto formativo del **SENA — Análisis y Desarrollo de Software (ADSO)**.

## Tecnologías

| Capa            | Tecnología                          |
|-----------------|-------------------------------------|
| Backend         | Java 17 + Spring Boot 3.2           |
| Acceso a datos  | JDBC puro (`Connection`, `PreparedStatement`) — sin ORM |
| Base de datos   | MySQL                               |
| Frontend        | React + Vite (`frontend/`)          |
| Control de versiones | Git + GitHub                   |

## Estructura del proyecto

```
BeautyShop/
├── pom.xml                          # Configuración Maven y dependencias
├── database/
│   └── products.sql                 # Script de la base de datos y tabla products
├── src/main/java/com/beautyshop/
│   ├── BeautyShopApplication.java   # Clase principal
│   ├── config/                      # Configuración (conexión a MySQL)
│   ├── controller/                  # Controladores REST (@RestController)
│   ├── service/                     # Lógica de negocio (@Service)
│   ├── repository/                  # Acceso a datos con JDBC (@Repository)
│   └── model/                       # Entidades (Product, ...)
└── src/main/resources/
    └── application.properties       # Configuración de la app y datasource
```

## Módulo de Productos (evidencia AA2-EV01)

CRUD completo de productos con JDBC puro.

### Endpoints REST

| Método | Ruta                  | Descripción                  |
|--------|-----------------------|------------------------------|
| POST   | `/api/products`       | Crear un producto            |
| GET    | `/api/products`       | Listar todos los productos   |
| GET    | `/api/products/{id}`  | Consultar un producto por id |
| PUT    | `/api/products/{id}`  | Actualizar un producto       |
| DELETE | `/api/products/{id}`  | Eliminar un producto         |

### Modelo de datos (Product)

`id`, `name`, `description`, `price`, `category`, `stock`, `image`.

## Puesta en marcha

### 1. Base de datos

Ejecuta los scripts en MySQL, en este orden (se pueden repetir sin duplicar datos):

```bash
mysql -u root -p < database/products.sql
mysql -u root -p < database/users.sql
mysql -u root -p < database/cart_orders.sql
```

### 2. Configurar credenciales

Edita `src/main/resources/application.properties` con tu usuario y contraseña de MySQL.

### 3. Ejecutar el backend

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8090/api/products` (Swagger UI en `http://localhost:8090/swagger-ui.html`).

### Ejemplo de creación (curl)

```bash
curl -X POST http://localhost:8090/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Labial Mate","description":"Larga duración","price":25900,"category":"labios","stock":40,"image":"labial.jpg"}'
```

## Frontend (React + Vite)

Interfaz de la tienda en `frontend/`. Consume la API de productos del backend.

### Estructura

```
frontend/src/
├── components/    # Navbar, ProductCard, LoginDrawer, Toast
├── pages/         # Home, Catalogo, Cart, Login, Checkout
├── services/      # api.js (axios), productService, authService
├── context/       # CartContext, ToastContext (carrito con localStorage)
├── utils/         # format.js (precios en COP)
└── index.css      # Sistema de diseño (tokens rosa, estilos compartidos)
```

### Puesta en marcha

```bash
cd frontend
npm install
npm run dev
```

Abre `http://localhost:4300`. El proxy de Vite redirige `/api` al backend en el puerto 8090 (evita CORS). **Ten el backend y MySQL en ejecución** para ver los productos en el catálogo.

> Los puertos por defecto son **4300** (frontend) y **8090** (backend) para no chocar con los típicos 3000/8080. Se configuran en `frontend/vite.config.js` y `src/main/resources/application.properties`.

## Estándares de codificación

- Variables y métodos en `camelCase`; métodos como verbos.
- Constantes en `UPPER_SNAKE_CASE` (`static final`).
- Clases e interfaces en `PascalCase`, un archivo `.java` por clase pública.
- Indentación de 4 espacios, llaves en la misma línea (Google Java Style), máx. 120 caracteres por línea.
- Javadoc en clases, métodos públicos y endpoints.

## Pruebas

```bash
./mvnw test                         # unitarias del backend (48 pruebas, sin MySQL)
cd auth-service && ./mvnw test      # unitarias de auth-service (8 pruebas)
cd frontend && npx playwright test  # e2e (7 pruebas; requiere backend y MySQL)
```

Pruebas unitarias del backend (`src/test/java/com/beautyshop/`): `PasswordUtilTest`, `ProductServiceTest`,
`AuthServiceTest`, `CartServiceTest` (carrito) y `OrderServiceTest` (pedidos). Resultados en
[docs/INFORME_PRUEBAS.md](docs/INFORME_PRUEBAS.md).

## Documentación

| Documento | Contenido |
|-----------|-----------|
| [docs/MANUAL_TECNICO.md](docs/MANUAL_TECNICO.md) | Arquitectura, diagramas, librerías, patrones, seguridad, estándares |
| [docs/REQUISITOS.md](docs/REQUISITOS.md) | Requisitos funcionales y no funcionales, historias de usuario, casos de uso y matriz de trazabilidad |
| [docs/ACTA_APROBACION_REQUISITOS.md](docs/ACTA_APROBACION_REQUISITOS.md) | Plantilla de acta de aprobación de requisitos (por firmar) |
| [docs/MODULOS.md](docs/MODULOS.md) | Entradas, salidas y errores de cada módulo y componente |
| [docs/CONFIGURACION.md](docs/CONFIGURACION.md) | Servidores, puertos, base de datos, modelo entidad–relación, cómo ejecutar cada módulo |
| [docs/AMBIENTES.md](docs/AMBIENTES.md) | Ambiente de desarrollo y de pruebas con versiones reales |
| [docs/INFORME_PRUEBAS.md](docs/INFORME_PRUEBAS.md) | Resultado de las pruebas unitarias, e2e y Postman por módulo |
| [docs/DESPLIEGUE.md](docs/DESPLIEGUE.md) | Despliegue local, URLs y capturas |
| [docs/CREDITOS_IMAGENES.md](docs/CREDITOS_IMAGENES.md) | Origen y licencia de las fotos de productos, y cómo agregar una nueva |
| [ENLACE_VIDEO_SUSTENTACION.md](ENLACE_VIDEO_SUSTENTACION.md) | Enlace del video de sustentación |

Los ejecutables (`beautyshop-backend.jar`, `auth-service.jar`, `beautyshop-web.war` y `frontend-dist/`) están en
[`ejecutables/`](ejecutables/).
