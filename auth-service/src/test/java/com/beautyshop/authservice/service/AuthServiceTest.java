package com.beautyshop.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.beautyshop.authservice.model.Usuario;
import com.beautyshop.authservice.repository.UsuarioRepository;

/**
 * Pruebas unitarias de {@link AuthService} del microservicio de autenticacion.
 *
 * <p>Usan el repositorio en memoria real ({@link UsuarioRepository}), que no
 * necesita base de datos, para validar el registro y el inicio de sesion.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
class AuthServiceTest {

    /** Servicio bajo prueba. */
    private AuthService authService;

    /** Prepara un servicio con un repositorio vacio antes de cada prueba. */
    @BeforeEach
    void setUp() {
        authService = new AuthService(new UsuarioRepository());
    }

    /** Registrar guarda la contrasena cifrada, no en texto plano. */
    @Test
    @DisplayName("registrar guarda la contrasena cifrada")
    void registrarHashesPassword() {
        Usuario registrado = authService.registrar(new Usuario("Ana", "ana@correo.com", "Clave123"));

        assertNotEquals("Clave123", registrado.getContrasena());
        assertEquals(64, registrado.getContrasena().length());
    }

    /** Registrar con un campo vacio es rechazado. */
    @Test
    @DisplayName("registrar rechaza campos vacios")
    void registrarRejectsBlankFields() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.registrar(new Usuario(" ", "ana@correo.com", "Clave123")));
        assertThrows(IllegalArgumentException.class,
                () -> authService.registrar(new Usuario("Ana", "", "Clave123")));
        assertThrows(IllegalArgumentException.class,
                () -> authService.registrar(new Usuario("Ana", "ana@correo.com", null)));
    }

    /** Registrar un usuario nulo es rechazado. */
    @Test
    @DisplayName("registrar rechaza usuario nulo")
    void registrarRejectsNullUser() {
        assertThrows(IllegalArgumentException.class, () -> authService.registrar(null));
    }

    /** No se permite registrar dos veces el mismo correo. */
    @Test
    @DisplayName("registrar rechaza correo duplicado")
    void registrarRejectsDuplicateEmail() {
        authService.registrar(new Usuario("Ana", "ana@correo.com", "Clave123"));

        assertThrows(IllegalArgumentException.class,
                () -> authService.registrar(new Usuario("Otra Ana", "ana@correo.com", "Otra456")));
    }

    /** Con credenciales correctas el inicio de sesion devuelve el usuario. */
    @Test
    @DisplayName("autenticar devuelve el usuario con credenciales correctas")
    void autenticarSucceedsWithValidCredentials() {
        authService.registrar(new Usuario("Ana", "ana@correo.com", "Clave123"));

        Optional<Usuario> result = authService.autenticar("ana@correo.com", "Clave123");

        assertTrue(result.isPresent());
        assertEquals("Ana", result.get().getNombre());
    }

    /** Con contrasena incorrecta el inicio de sesion falla. */
    @Test
    @DisplayName("autenticar falla con contrasena incorrecta")
    void autenticarFailsWithWrongPassword() {
        authService.registrar(new Usuario("Ana", "ana@correo.com", "Clave123"));

        assertFalse(authService.autenticar("ana@correo.com", "Incorrecta").isPresent());
    }

    /** Con un correo no registrado el inicio de sesion falla. */
    @Test
    @DisplayName("autenticar falla con correo no registrado")
    void autenticarFailsWithUnknownEmail() {
        assertFalse(authService.autenticar("nadie@correo.com", "Clave123").isPresent());
    }

    /** Con datos vacios el inicio de sesion devuelve vacio sin lanzar error. */
    @Test
    @DisplayName("autenticar devuelve vacio con datos en blanco")
    void autenticarReturnsEmptyWithBlankInput() {
        assertFalse(authService.autenticar("", "Clave123").isPresent());
        assertFalse(authService.autenticar("ana@correo.com", null).isPresent());
    }
}
