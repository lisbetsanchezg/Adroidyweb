-- =====================================================================
-- BeautyShop - Tabla 'users' para el modulo de autenticacion del backend
-- Motor: MySQL   |   Usada por el backend Spring Boot (API REST /api/auth)
-- Es la MISMA tabla que usa el modulo de servlets, por lo que un usuario
-- registrado en cualquiera de los dos modulos sirve en el otro.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS beautyshop
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE beautyshop;

-- ---------------------------------------------------------------------
-- Tabla de usuarios
-- La contrasena se almacena como hash (SHA-256), nunca en texto plano.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del usuario',
    name       VARCHAR(120) NOT NULL              COMMENT 'Nombre del usuario',
    email      VARCHAR(150) NOT NULL              COMMENT 'Correo electronico (unico)',
    password   VARCHAR(255) NOT NULL              COMMENT 'Hash de la contrasena',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro',
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Usuarios registrados de BeautyShop';

-- ---------------------------------------------------------------------
-- Usuario demo para las pruebas e2e (correo demo@beautyshop.com, contrasena demo1234).
-- INSERT IGNORE evita el error si ya existe (el correo es unico).
-- ---------------------------------------------------------------------
INSERT IGNORE INTO users (name, email, password) VALUES
('Lisbet Pruebas', 'demo@beautyshop.com', '0ead2060b65992dca4769af601a1b3a35ef38cfad2c2c465bb160ea764157c5d');
