-- =====================================================================
-- BeautyShop - Tabla 'users' para el modulo de registro e inicio de sesion
-- Motor: MySQL   |   Evidencia: AA2-EV02 (Servlets + JSP)
-- Se usa la misma base de datos 'beautyshop' del backend.
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
