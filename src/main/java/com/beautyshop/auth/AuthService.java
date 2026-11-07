package com.beautyshop.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.beautyshop.model.User;
import com.beautyshop.repository.UserRepository;
import com.beautyshop.util.PasswordUtil;

/**
 * Servicio con la logica de negocio de autenticacion (registro e inicio de sesion).
 *
 * <p>Valida los datos, cifra la contrasena antes de persistirla y delega el
 * acceso a datos en {@link UserRepository}. Nunca devuelve la contrasena en
 * los objetos de respuesta.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Service
public class AuthService {

    /** Repositorio de acceso a datos de usuarios. */
    private final UserRepository userRepository;

    /**
     * Constructor con inyeccion del repositorio.
     *
     * @param userRepository repositorio de usuarios
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registra un usuario nuevo tras validar los datos y evitar correos duplicados.
     *
     * @param user datos de registro (nombre, correo, contrasena en texto plano)
     * @return el usuario creado sin la contrasena
     * @throws IllegalArgumentException si los datos son invalidos o el correo ya existe
     */
    public User register(User user) {
        validateRegistration(user);

        // Evita registrar dos veces el mismo correo
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El correo ya esta registrado.");
        }

        // Cifra la contrasena antes de guardarla
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        User saved = userRepository.save(user);

        // No exponer el hash de la contrasena en la respuesta
        saved.setPassword(null);
        return saved;
    }

    /**
     * Valida las credenciales de inicio de sesion contra la base de datos.
     *
     * @param email    correo ingresado
     * @param password contrasena en texto plano ingresada
     * @return un {@link Optional} con el usuario autenticado si las credenciales son validas
     */
    public Optional<User> login(String email, String password) {
        if (isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("Debes ingresar correo y contrasena.");
        }

        // Cifra la contrasena para compararla con la almacenada
        String hashedPassword = PasswordUtil.hashPassword(password);
        return userRepository.findByCredentials(email, hashedPassword);
    }

    /**
     * Valida los datos obligatorios para el registro.
     *
     * @param user usuario a validar
     * @throws IllegalArgumentException si algun dato es invalido
     */
    private void validateRegistration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Los datos del usuario son obligatorios.");
        }
        if (isBlank(user.getName())) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (isBlank(user.getEmail())) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }
        if (isBlank(user.getPassword())) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }
    }

    /**
     * Indica si una cadena es nula o esta vacia (solo espacios).
     *
     * @param value cadena a evaluar
     * @return {@code true} si es nula o vacia
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
