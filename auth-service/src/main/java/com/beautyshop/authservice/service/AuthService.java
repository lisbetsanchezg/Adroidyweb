package com.beautyshop.authservice.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.beautyshop.authservice.model.Usuario;
import com.beautyshop.authservice.repository.UsuarioRepository;
import com.beautyshop.authservice.util.PasswordUtil;

/**
 * Servicio con la logica de negocio de registro e inicio de sesion.
 *
 * <p>Valida los datos, cifra la contrasena antes de guardarla y verifica las
 * credenciales en el inicio de sesion.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Service
public class AuthService {

    /** Repositorio de usuarios en memoria. */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyeccion del repositorio.
     *
     * @param usuarioRepository repositorio de usuarios
     */
    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un usuario nuevo tras validar los datos y evitar correos duplicados.
     *
     * @param usuario datos de registro (nombre, correo, contrasena)
     * @return el usuario registrado (con la contrasena ya cifrada)
     * @throws IllegalArgumentException si faltan datos o el correo ya existe
     */
    public Usuario registrar(Usuario usuario) {
        if (usuario == null || isBlank(usuario.getNombre())
                || isBlank(usuario.getCorreo()) || isBlank(usuario.getContrasena())) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }
        if (usuarioRepository.existePorCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo ya esta registrado.");
        }

        // Cifra la contrasena antes de guardarla
        usuario.setContrasena(PasswordUtil.hashPassword(usuario.getContrasena()));
        return usuarioRepository.guardar(usuario);
    }

    /**
     * Verifica las credenciales de inicio de sesion.
     *
     * @param correo     correo ingresado
     * @param contrasena contrasena en texto plano ingresada
     * @return un {@link Optional} con el usuario si las credenciales son validas
     */
    public Optional<Usuario> autenticar(String correo, String contrasena) {
        if (isBlank(correo) || isBlank(contrasena)) {
            return Optional.empty();
        }

        String hash = PasswordUtil.hashPassword(contrasena);
        return usuarioRepository.buscarPorCorreo(correo)
                .filter(usuario -> usuario.getContrasena().equals(hash));
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
