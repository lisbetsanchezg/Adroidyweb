-- =====================================================================
-- BeautyShop - Script de la base de datos y la tabla 'products'
-- Motor: MySQL
-- Evidencia: AA2-EV01 "Codificacion de modulos del software"
-- =====================================================================

-- Crea la base de datos si no existe y la selecciona para trabajar
CREATE DATABASE IF NOT EXISTS beautyshop
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE beautyshop;

-- ---------------------------------------------------------------------
-- Tabla de productos de maquillaje
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id          BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del producto',
    name        VARCHAR(150)    NOT NULL              COMMENT 'Nombre del producto',
    description TEXT                                  COMMENT 'Descripcion detallada',
    price       DECIMAL(10, 2)  NOT NULL              COMMENT 'Precio de venta',
    category    VARCHAR(80)                           COMMENT 'Categoria (labios, ojos, rostro, etc.)',
    stock       INT             NOT NULL DEFAULT 0    COMMENT 'Unidades disponibles en inventario',
    image       VARCHAR(255)                          COMMENT 'Ruta o URL de la imagen',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Productos de la tienda BeautyShop';

-- ---------------------------------------------------------------------
-- Datos de ejemplo (opcional, para probar la API)
-- ---------------------------------------------------------------------
-- Solo se insertan si la tabla esta vacia, para poder ejecutar el script
-- varias veces sin duplicar los productos.
INSERT INTO products (name, description, price, category, stock, image)
SELECT * FROM (
    SELECT 'Labial Mate Rojo Pasion' AS name, 'Labial de larga duracion acabado mate' AS description,
           25900.00 AS price, 'labios' AS category, 40 AS stock, 'labial-rojo.jpg' AS image
    UNION ALL SELECT 'Base Liquida Natural', 'Cobertura media, acabado natural',
           45000.00, 'rostro', 25, 'base-natural.jpg'
    UNION ALL SELECT 'Paleta de Sombras Nude', '12 tonos nude de alta pigmentacion',
           68500.00, 'ojos', 15, 'paleta-nude.jpg'
    UNION ALL SELECT 'Mascara de Pestanas Volumen', 'Efecto volumen y alargamiento',
           32000.00, 'ojos', 30, 'mascara-volumen.jpg'
    UNION ALL SELECT 'Rubor en Polvo Coral', 'Rubor de acabado satinado',
           28000.00, 'rostro', 20, 'rubor-coral.jpg'
) AS semilla
WHERE NOT EXISTS (SELECT 1 FROM products);
