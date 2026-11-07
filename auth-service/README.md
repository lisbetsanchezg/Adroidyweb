# Auth Service — Registro e inicio de sesión (AA5-EV01)

Servicio REST **independiente** de registro e inicio de sesión, como caso de práctica del SENA (evidencia **AA5-EV01**). Se prueba con la colección de Postman de **AA5-EV02**.

## Características
- **Spring Boot** (Java 17), REST puro.
- **Almacenamiento en memoria** (sin base de datos) → autocontenido y fácil de probar.
- Contraseñas cifradas con **SHA-256** (no se guardan ni se devuelven en texto plano).
- Mensajes en español: **"Autenticación satisfactoria"** / error.

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registra un usuario (`nombre`, `correo`, `contrasena`) |
| POST | `/api/auth/login` | Inicia sesión (`correo`, `contrasena`) |

### Registro — `POST /api/auth/register`
```json
{ "nombre": "Lisbet Sánchez", "correo": "lisbet@correo.com", "contrasena": "Clave123*" }
```
- **201** → `{ "mensaje": "Usuario registrado exitosamente", "nombre": "...", "correo": "..." }`
- **400** → correo duplicado o campos vacíos.

### Login — `POST /api/auth/login`
```json
{ "correo": "lisbet@correo.com", "contrasena": "Clave123*" }
```
- **200** → `{ "mensaje": "Autenticación satisfactoria", "nombre": "...", "correo": "..." }`
- **401** → `{ "mensaje": "Autenticacion fallida: credenciales invalidas" }`

## Ejecutar

```bash
./mvnw spring-boot:run
```

Queda en `http://localhost:8080`. 

> **Puerto:** por defecto usa el 8080 (el que pide la evidencia). Si ese puerto está ocupado, arráncalo en otro con:
> `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`

## Probar con Postman
Importa la colección y el environment de la carpeta `postman/` (evidencia AA5-EV02) y ejecútalos con el Runner, o por CLI con Newman:

```bash
npx newman run ../postman/BeautyShop_Auth.postman_collection.json -e ../postman/BeautyShop_local.postman_environment.json
```
