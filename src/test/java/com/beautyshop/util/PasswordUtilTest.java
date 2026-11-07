package com.beautyshop.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de {@link PasswordUtil}.
 *
 * <p>Verifican que el hash SHA-256 sea determinista, de la longitud correcta
 * y coincida con un valor conocido (vector de prueba).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
class PasswordUtilTest {

    /** El mismo texto debe producir siempre el mismo hash (determinismo). */
    @Test
    @DisplayName("El hash es determinista para la misma entrada")
    void hashPasswordIsDeterministic() {
        String first = PasswordUtil.hashPassword("clave123");
        String second = PasswordUtil.hashPassword("clave123");
        assertEquals(first, second);
    }

    /** Entradas distintas deben producir hashes distintos. */
    @Test
    @DisplayName("Entradas diferentes producen hashes diferentes")
    void hashPasswordDiffersForDifferentInput() {
        assertNotEquals(PasswordUtil.hashPassword("clave123"), PasswordUtil.hashPassword("clave124"));
    }

    /** El hash SHA-256 en hexadecimal siempre tiene 64 caracteres. */
    @Test
    @DisplayName("El hash tiene 64 caracteres hexadecimales")
    void hashPasswordHasCorrectLength() {
        assertEquals(64, PasswordUtil.hashPassword("cualquiera").length());
    }

    /** Comparacion contra un vector conocido: SHA-256 de "1234". */
    @Test
    @DisplayName("El hash coincide con el valor conocido de SHA-256")
    void hashPasswordMatchesKnownVector() {
        String expected = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4";
        assertEquals(expected, PasswordUtil.hashPassword("1234"));
    }
}
