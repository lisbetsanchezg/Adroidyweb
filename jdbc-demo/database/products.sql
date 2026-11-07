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
INSERT INTO products (name, description, price, category, stock, image) VALUES
('Labial Mate Rojo Pasion', 'Labial de larga duracion acabado mate', 25900.00, 'labios', 40, 'labial-rojo.jpg'),
('Base Liquida Natural', 'Cobertura media, acabado natural', 45000.00, 'rostro', 25, 'base-natural.jpg'),
('Paleta de Sombras Nude', '12 tonos nude de alta pigmentacion', 68500.00, 'ojos', 15, 'paleta-nude.jpg'),
('Mascara de Pestanas Volumen', 'Efecto volumen y alargamiento', 32000.00, 'ojos', 30, 'mascara-volumen.jpg'),
('Rubor en Polvo Coral', 'Rubor de acabado satinado', 28000.00, 'rostro', 20, 'rubor-coral.jpg');
