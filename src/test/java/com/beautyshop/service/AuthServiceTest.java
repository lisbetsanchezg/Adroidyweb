package com.beautyshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.beautyshop.auth.AuthService;
import com.beautyshop.model.User;
import com.beautyshop.repository.UserRepository;
import com.beautyshop.util.PasswordUtil;

/**
 * Pruebas unitarias de {@link AuthService}.
 *
 * <p>Usan un {@link FakeUserRepository} en memoria (sin base de datos ni
 * Mockito) para validar las reglas de registro e inicio de sesion.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
class AuthServiceTest {

    /** Repositorio falso configurable para cada prueba. */
    private FakeUserRepository fakeRepository;

    /** Servicio bajo prueba. */
    private AuthService authService;

    /** Prepara un servicio nuevo antes de cada prueba. */
    @BeforeEach
    void setUp() {
        fakeRepository = new FakeUserRepository();
        authService = new AuthService(fakeRepository);
    }

    /** El registro valido cifra la contrasena y no la expone en la respuesta. */
    @Test
    @DisplayName("register cifra la contrasena y no la devuelve")
    void registerHashesPasswordAndHidesIt() {
        User user = new User("Lisbet Sanchez", "ana@mail.com", "1234");

        User result = authService.register(user);

        // La contrasena almacenada es el hash, no el texto plano
        assertEquals(PasswordUtil.hashPassword("1234"), fakeRepository.savedPassword);
        // La respuesta no expone la contrasena y trae el id asignado
        assertNull(result.getPassword());
        assertNotNull(result.getId());
    }

    /** El registro con un nombre vacio es rechazado. */
    @Test
    @DisplayName("register rechaza datos invalidos")
    void registerRejectsBlankName() {
        User user = new User("  ", "ana@mail.com", "1234");
        assertThrows(IllegalArgumentException.class, () -> authService.register(user));
    }

    /** El registro con un correo ya existente es rechazado. */
    @Test
    @DisplayName("register rechaza correos duplicados")
    void registerRejectsDuplicateEmail() {
        fakeRepository.existing = true;
        User user = new User("Lisbet Sanchez", "ana@mail.com", "1234");
        assertThrows(IllegalArgumentException.class, () -> authService.register(user));
    }

    /** El login con credenciales correctas devuelve el usuario. */
    @Test
    @DisplayName("login exitoso devuelve el usuario")
    void loginSucceedsWithValidCredentials() {
        // El fake tiene un usuario cuya contrasena es "secret"
        fakeRepository.registerCredentials("ana@mail.com", "secret");

        Optional<User> result = authService.login("ana@mail.com", "secret");

        assertTrue(result.isPresent());
        assertEquals("ana@mail.com", result.get().getEmail());
    }

    /** El login con contrasena incorrecta no devuelve usuario. */
    @Test
    @DisplayName("login falla con contrasena incorrecta")
    void loginFailsWithWrongPassword() {
        fakeRepository.registerCredentials("ana@mail.com", "secret");

        Optional<User> result = authService.login("ana@mail.com", "otra");

        assertFalse(result.isPresent());
    }

    /** El login con campos vacios es rechazado. */
    @Test
    @DisplayName("login rechaza campos vacios")
    void loginRejectsBlankFields() {
        assertThrows(IllegalArgumentException.class, () -> authService.login("", ""));
    }

    /**
     * Implementacion falsa de {@link UserRepository} que no accede a la base de
     * datos. Extiende la clase real pasando un DataSource nulo y sobrescribe los
     * metodos usados por el servicio.
     */
    private static class FakeUserRepository extends UserRepository {

        /** Indica si el correo consultado ya existe. */
        private boolean existing = false;

        /** Contrasena (hash) capturada en el ultimo save. */
        private String savedPassword;

        /** Correo con credenciales validas registradas para el login. */
        private String storedEmail;

        /** Hash de la contrasena valida registrada para el login. */
        private String storedHash;

        FakeUserRepository() {
            super(null);
        }

        /**
         * Registra unas credenciales validas para las pruebas de login.
         *
         * @param email         correo valido
         * @param plainPassword contrasena en texto plano
         */
        void registerCredentials(String email, String plainPassword) {
            this.storedEmail = email;
            this.storedHash = PasswordUtil.hashPassword(plainPassword);
        }

        @Override
        public boolean existsByEmail(String email) {
            return existing;
        }

        @Override
        public User save(User user) {
            savedPassword = user.getPassword();
            user.setId(1L);
            return user;
        }

        @Override
        public Optional<User> findByCredentials(String email, String hashedPassword) {
            if (email.equals(storedEmail) && hashedPassword.equals(storedHash)) {
                User user = new User();
                user.setId(1L);
                user.setName("Lisbet Sanchez");
                user.setEmail(email);
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }
}
