# BeautyShop — Módulo Web (Servlets + JSP)

Evidencia **AA2-EV02**. Módulo independiente de **registro e inicio de sesión** de usuarios, construido con **Servlets y JSP puros** (sin Spring), dentro del repositorio BeautyShop.

## Tecnologías

| Elemento         | Tecnología                                  |
|------------------|---------------------------------------------|
| Lenguaje         | Java 17                                      |
| Web              | Jakarta Servlet 6.0 + JSP 3.1 (namespace `jakarta.*`) |
| Empaquetado      | Maven `war`                                  |
| Servidor         | Apache Tomcat **10.1+**                       |
| Base de datos    | MySQL (JDBC puro)                            |

> **Tomcat 9 o anterior:** usa el namespace `javax.*`. Cambia en el `pom.xml` la dependencia por `javax.servlet:javax.servlet-api:4.0.1`, y en las clases reemplaza los `import jakarta.servlet.*` por `import javax.servlet.*`.

## Estructura

```
web-servlets-module/
├── pom.xml
├── database/
│   └── users.sql                     # Tabla users
└── src/main/
    ├── java/com/beautyshop/web/
    │   ├── config/DatabaseConnection.java   # Conexión JDBC (DriverManager)
    │   ├── model/User.java
    │   ├── dao/UserDao.java                 # Acceso a datos (JDBC)
    │   ├── util/PasswordUtil.java           # Hash SHA-256
    │   └── servlet/
    │       ├── RegisterServlet.java         # /register (doGet + doPost)
    │       └── LoginServlet.java            # /login (doGet + doPost)
    └── webapp/
        ├── register.html
        ├── login.html
        ├── welcome.jsp                      # Muestra el usuario autenticado
        ├── error.jsp
        └── WEB-INF/web.xml
```

## Flujo

1. `register.html` → POST `/register` → `RegisterServlet` guarda el usuario en MySQL → redirige a `login.html`.
2. `login.html` → POST `/login` → `LoginServlet` valida credenciales → `welcome.jsp` (éxito) o `error.jsp` (fallo).

## Puesta en marcha

### 1. Base de datos

```bash
mysql -u root -p < database/users.sql
```

Ajusta las credenciales en `config/DatabaseConnection.java` si tu MySQL usa otro usuario/contraseña.

### 2. Empaquetar el WAR

```bash
mvn clean package
```

Genera `target/beautyshop-web.war`.

### 3. Desplegar en Tomcat

Copia `beautyshop-web.war` en la carpeta `webapps/` de Tomcat 10.1 y arráncalo. La app queda en:

```
http://localhost:8080/beautyshop-web/
```

(Se abre `login.html` por el `welcome-file`. Regístrate primero desde el enlace "Regístrate".)
