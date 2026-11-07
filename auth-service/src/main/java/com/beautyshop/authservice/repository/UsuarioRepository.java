package com.beautyshop.authservice.repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.beautyshop.authservice.model.Usuario;

/**
 * Repositorio de usuarios en memoria.
 *
 * <p>Almacena los usuarios en un {@link ConcurrentHashMap} indexado por correo.
 * Al ser un caso de practica, no se usa base de datos: los datos viven mientras
 * el servicio este en ejecucion.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Repository
public class UsuarioRepository {

    /** Almacen en memoria: correo -> usuario. */
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    /**
     * Indica si ya existe un usuario con el correo indicado.
     *
     * @param correo correo a verificar
     * @return {@code true} si el correo ya esta registrado
     */
    public boolean existePorCorreo(String correo) {
        return usuarios.containsKey(correo);
    }

    /**
     * Guarda un usuario nuevo en memoria.
     *
     * @param usuario usuario a guardar
     * @return el usuario guardado
     */
    public Usuario guardar(Usuario usuario) {
        usuarios.put(usuario.getCorreo(), usuario);
        return usuario;
    }

    /**
     * Busca un usuario por su correo.
     *
     * @param correo correo del usuario
     * @return un {@link Optional} con el usuario si existe
     */
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return Optional.ofNullable(usuarios.get(correo));
    }
}
