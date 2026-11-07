package com.beautyshop.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para el manejo seguro de contrasenas.
 *
 * <p>Genera un hash SHA-256 de la contrasena para no almacenarla en texto
 * plano. Usa el mismo algoritmo que el modulo de servlets, de modo que ambos
 * modulos comparten la tabla {@code users}: un usuario registrado en uno puede
 * autenticarse en el otro. En produccion se recomienda BCrypt.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public final class PasswordUtil {

    /** Algoritmo de hash utilizado. */
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Constructor privado: clase de utilidad, no se instancia.
     */
    private PasswordUtil() {
    }

    /**
     * Calcula el hash SHA-256 de una contrasena en texto plano.
     *
     * @param plainPassword contrasena en texto plano
     * @return representacion hexadecimal del hash
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            // Convierte el arreglo de bytes a una cadena hexadecimal legible
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No se pudo generar el hash de la contrasena", e);
        }
    }
}
