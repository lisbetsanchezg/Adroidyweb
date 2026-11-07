-- =====================================================================
-- BeautyShop - Tablas del carrito de compras y la gestion de pedidos
-- Motor: MySQL   |   Evidencia: AA3-EV01
-- Requiere las tablas 'users' y 'products' ya existentes.
-- =====================================================================

USE beautyshop;

-- ---------------------------------------------------------------------
-- Carrito de compras: una fila por producto dentro del carrito de un usuario.
-- La combinacion (user_id, product_id) es unica para evitar lineas duplicadas.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la linea de carrito',
    user_id    BIGINT         NOT NULL              COMMENT 'Usuario dueno del carrito',
    product_id BIGINT         NOT NULL              COMMENT 'Producto agregado',
    quantity   INT            NOT NULL              COMMENT 'Cantidad de unidades',
    unit_price DECIMAL(10, 2) NOT NULL              COMMENT 'Precio unitario al agregar',
    PRIMARY KEY (id),
    UNIQUE KEY uk_cart_user_product (user_id, product_id),
    CONSTRAINT fk_cart_user    FOREIGN KEY (user_id)    REFERENCES users (id)    ON DELETE CASCADE,
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lineas del carrito de compras';

-- ---------------------------------------------------------------------
-- Pedidos: cabecera de cada pedido realizado por un usuario.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id         BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'Identificador del pedido',
    user_id    BIGINT         NOT NULL              COMMENT 'Usuario que realizo el pedido',
    order_date DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha del pedido',
    status     VARCHAR(20)    NOT NULL DEFAULT 'pendiente' COMMENT 'Estado del pedido',
    total      DECIMAL(12, 2) NOT NULL              COMMENT 'Total del pedido',
    PRIMARY KEY (id),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Cabecera de los pedidos';

-- ---------------------------------------------------------------------
-- Detalle de pedidos: una fila por producto dentro de un pedido.
-- Guarda el precio unitario historico (el que tenia el producto al comprar).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id         BIGINT         NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la linea de pedido',
    order_id   BIGINT         NOT NULL              COMMENT 'Pedido al que pertenece',
    product_id BIGINT         NOT NULL              COMMENT 'Producto pedido',
    quantity   INT            NOT NULL              COMMENT 'Cantidad de unidades',
    unit_price DECIMAL(10, 2) NOT NULL              COMMENT 'Precio unitario al momento de la compra',
    PRIMARY KEY (id),
    CONSTRAINT fk_orderitem_order   FOREIGN KEY (order_id)   REFERENCES orders (id)   ON DELETE CASCADE,
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Detalle (lineas) de los pedidos';
