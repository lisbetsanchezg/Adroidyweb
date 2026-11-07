# Ambientes de desarrollo y de pruebas

Versiones consultadas directamente en el equipo el 25/10/2026.

> Ver también: [CONFIGURACION.md](CONFIGURACION.md) · [INFORME_PRUEBAS.md](INFORME_PRUEBAS.md) · [DESPLIEGUE.md](DESPLIEGUE.md)

## 1. Ambiente de desarrollo

| Componente | Versión / dato | Cómo se consultó |
|-----------|----------------|------------------|
| Sistema operativo | macOS 27.0.1, arquitectura arm64 (Apple Silicon) | `sw_vers`, `uname -m` |
| JDK | OpenJDK 17.0.17 (Homebrew `openjdk@17`) | `java -version` |
| Maven | 3.9.9 (descargado por el Maven Wrapper `./mvnw`; no hace falta instalar Maven) | `./mvnw -v` |
| Spring Boot | 3.2.5 | `pom.xml` |
| Node.js / npm | 22.23.2 / 10.9.8 | `node -v`, `npm -v` |
| Vite | 5.4.21 | `node_modules` |
| Servidor de base de datos | MySQL Server 9.5.0 (Homebrew) en `localhost:3306` | `SELECT @@version;` |
| Cliente de línea de comandos | `mysql` de Homebrew | `mysql --version` |
| Servidor de aplicaciones (módulo Servlets) | Apache Tomcat 10.1.61 (Homebrew `tomcat@10`) | `catalina.sh version` |
| Control de versiones | Git 2.53.0 | `git --version` |
| Editores / IDE instalados en el equipo | Visual Studio Code Insiders, Xcode, Android Studio (para la app móvil) | carpeta `/Applications` |
| Navegador de pruebas | Chromium (Headless Shell 151.0.7922.34, instalado por Playwright) | `npx playwright install chromium` |

> Nota: en este equipo conviven los binarios de MySQL y de MariaDB de Homebrew; el servidor que atendió el proyecto reporta `9.5.0` (MySQL).

### Preparar el ambiente de desarrollo

```bash
# 1. JDK 17 (macOS con Homebrew)
brew install openjdk@17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17

# 2. MySQL y datos
brew install mysql && brew services start mysql
mysql -u root < database/products.sql
mysql -u root < database/users.sql
mysql -u root < database/cart_orders.sql

# 3. Backend
./mvnw spring-boot:run            # http://localhost:8090

# 4. Frontend
cd frontend && npm ci && npm run dev   # http://localhost:4300
```

Detalle de puertos y credenciales en [CONFIGURACION.md](CONFIGURACION.md).

## 2. Ambiente de pruebas

| Tipo de prueba | Herramienta (versión) | Ubicación | Requiere |
|----------------|----------------------|-----------|----------|
| Unitarias del backend | JUnit Jupiter 5.10.2 (`spring-boot-starter-test`), repositorios falsos en memoria | `src/test/java/com/beautyshop/` | Solo JDK 17 (sin MySQL, sin servidor) |
| Unitarias de `auth-service` | JUnit Jupiter 5.10.2 | `auth-service/src/test/java/` | Solo JDK 17 |
| End-to-end (navegador) | Playwright `@playwright/test` 1.62.0, Chromium | `frontend/e2e/` (`auth.spec.js`, `home.spec.js`, `shop.spec.js`) | Backend en 8090 + MySQL con scripts cargados (incluye usuario demo) |
| API (colección) | Postman (`postman/BeautyShop_Auth.postman_collection.json` + `BeautyShop_local.postman_environment.json`); ejecutada por línea de comandos con Newman 6.2.3 | `postman/` | `auth-service` en el puerto 8080 |
| Manuales / humo | `curl` sobre la API, Swagger UI, navegador | — | Backend en 8090 |

### Datos de prueba

- Usuario demo para e2e (lo crea `database/users.sql`): `demo@beautyshop.com` / `demo1234`.
- Las pruebas e2e de registro crean un usuario con correo único por ejecución (`e2e_<marca de tiempo>@mail.com`).
- La base debe tener los 5 productos de ejemplo **sin duplicar** (`Rubor en Polvo Coral` debe aparecer una sola vez); los scripts SQL ya son idempotentes para evitarlo.

### Cómo correr cada tipo de prueba

**Unitarias del backend** (no necesitan MySQL):

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
./mvnw test
```

Resultado esperado: `Tests run: 48, Failures: 0, Errors: 0, Skipped: 0` / `BUILD SUCCESS`. Los informes quedan en `target/surefire-reports/`.

**Unitarias de auth-service:**

```bash
cd auth-service && ./mvnw test
```

Resultado esperado: `Tests run: 8, Failures: 0, Errors: 0, Skipped: 0`.

**End-to-end con Playwright:**

```bash
# backend y MySQL en marcha
cd frontend
npm ci
npx playwright install chromium     # solo la primera vez
npx playwright test                 # levanta `npm run dev` si el frontend no está activo
npx playwright show-report          # informe HTML
```

Resultado esperado: `7 passed`. La configuración está en `frontend/playwright.config.js` (1 worker, `baseURL http://127.0.0.1:4300`, capturas solo en fallos).

**Colección Postman (auth-service):**

```bash
java -jar ejecutables/auth-service.jar &      # puerto 8080
npx newman run postman/BeautyShop_Auth.postman_collection.json \
  -e postman/BeautyShop_local.postman_environment.json \
  --env-var userCorreo=postman_$(date +%s)@correo.com --env-var contrasena=Clave1234
```

La colección usa las variables `baseUrl`, `userCorreo` y `contrasena`; en Postman (interfaz) importa la colección y el entorno `BeautyShop_local`.

**Pruebas manuales de la API del backend principal:** abre `http://localhost:8090/swagger-ui.html` y usa «Try it out», o consulta los ejemplos `curl` de [MODULOS.md](MODULOS.md).

Los resultados reales de cada tipo de prueba están en [INFORME_PRUEBAS.md](INFORME_PRUEBAS.md).
