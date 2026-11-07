# Acta de aprobación de requisitos

> **PLANTILLA PARA DILIGENCIAR Y FIRMAR.** Este documento no está aprobado. La columna «Aprobado (Sí/No)», las observaciones, los compromisos y las firmas se llenan en la reunión de aprobación. El detalle de cada requisito está en [REQUISITOS.md](REQUISITOS.md).

Evidencias **GA8-220501096-AA1-EV01** y **AA1-EV02**.

## 1. Datos generales

| Campo | Información |
|-------|-------------|
| Proyecto | BeautyShop — tienda en línea de maquillaje |
| Programa | Análisis y Desarrollo de Software (ADSO) — SENA |
| Ficha | 228118 |
| Fecha | ____ / ____ / ________ |
| Lugar | ________________________________________ |
| Hora de inicio / fin | ________ / ________ |
| Documento revisado | [docs/REQUISITOS.md](REQUISITOS.md) |

## 2. Participantes

| Nombre completo | Documento de identidad | Rol | Firma |
|-----------------|------------------------|-----|-------|
| | | Aprendiz | |
| | | Instructor(a) o cliente | |
| | | | |

## 3. Objetivo

Revisar los requisitos funcionales y no funcionales de BeautyShop, dejar constancia de cuáles se aprueban y registrar las observaciones y compromisos para ajustarlos, antes de continuar con el desarrollo y las pruebas.

## 4. Requisitos funcionales

Marque «Sí» o «No» en cada fila durante la reunión.

| ID | Nombre | Aprobado (Sí/No) |
|----|--------|------------------|
| RF-01 | Registro de usuario | |
| RF-02 | Inicio de sesión | |
| RF-03 | Validación de los formularios web | |
| RF-04 | Cierre de sesión | |
| RF-05 | Registro e inicio de sesión con Servlets/JSP | |
| RF-06 | Servicio de autenticación independiente | |
| RF-07 | Listar productos | |
| RF-08 | Consultar un producto por id | |
| RF-09 | Buscar productos por nombre | |
| RF-10 | Catálogo con foto del producto | |
| RF-11 | Administrar productos (API) | |
| RF-12 | Agregar producto al carrito | |
| RF-13 | Ver el carrito y su total | |
| RF-14 | Cambiar la cantidad de un producto | |
| RF-15 | Quitar un producto del carrito | |
| RF-16 | Carrito solo con sesión iniciada | |
| RF-17 | Crear pedido desde el carrito | |
| RF-18 | Historial de pedidos | |
| RF-19 | Detalle de un pedido | |
| RF-20 | Cambiar el estado de un pedido (API) | |
| RF-21 | Pago simulado | |
| RF-22 | Autenticación en la app | |
| RF-23 | Catálogo, búsqueda, filtro y detalle (app) | |
| RF-24 | Carrito en la app | |
| RF-25 | Pedidos en la app | |

## 5. Requisitos no funcionales

| ID | Nombre | Aprobado (Sí/No) |
|----|--------|------------------|
| RNF-01 | Contraseñas con hash SHA-256 y nunca devueltas | |
| RNF-02 | Validación de entradas | |
| RNF-03 | Consultas SQL con `PreparedStatement` | |
| RNF-04 | Formato uniforme de errores del backend | |
| RNF-05 | Seguridad en la app móvil | |
| RNF-06 | Usabilidad: español, pesos colombianos y mensajes claros | |
| RNF-07 | Tienda web adaptable a pantallas pequeñas | |
| RNF-08 | Portabilidad: JDK 17, Android 8.0 (minSdk 26) o superior, MySQL | |
| RNF-09 | Mantenibilidad: capas y estándares de código | |
| RNF-10 | Compatibilidad con navegadores (probado en Chromium) | |
| RNF-11 | Rendimiento en ejecución local | |
| RNF-12 | Documentación de la API con Swagger/OpenAPI | |

## 6. Limitaciones conocidas que se ponen en conocimiento

Estas limitaciones están descritas en la [sección 7 de REQUISITOS.md](REQUISITOS.md#7-limitaciones-conocidas). Se registra si los participantes las conocen y las aceptan.

| # | Limitación | Conocida y aceptada (Sí/No) |
|---|-----------|-----------------------------|
| L-01 | Sin JWT ni Spring Security: los endpoints no exigen credenciales | |
| L-02 | No existen roles; el administrador solo usa la API | |
| L-03 | Contraseñas con SHA-256 sin sal | |
| L-04 | El pedido no descuenta el stock | |
| L-05 | El pago es simulado | |
| L-06 | Los estados del pedido no tienen reglas de transición | |
| L-07 | Reglas de contraseña distintas en backend, web y app | |
| L-08 | `auth-service` guarda usuarios solo en memoria | |
| L-09 | Conexiones a la base de datos sin pool | |
| L-10 | Cobertura de pruebas parcial | |
| L-11 | Funciones no ofrecidas (recuperar contraseña, editar perfil, etc.) | |

## 7. Observaciones

| # | Requisito (ID) | Observación |
|---|----------------|-------------|
| 1 | | |
| 2 | | |
| 3 | | |
| 4 | | |

## 8. Compromisos

| # | Compromiso | Responsable | Fecha límite |
|---|------------|-------------|--------------|
| 1 | | | ____ / ____ / ________ |
| 2 | | | ____ / ____ / ________ |
| 3 | | | ____ / ____ / ________ |

## 9. Firmas

Con su firma, los participantes dejan constancia de haber revisado los requisitos y de que las marcas de la columna «Aprobado (Sí/No)» y las observaciones corresponden a lo acordado en la reunión.

| Rol | Nombre | Firma | Fecha |
|-----|--------|-------|-------|
| Aprendiz | | | ____ / ____ / ________ |
| Instructor(a) o cliente | | | ____ / ____ / ________ |
