package com.beautyshop.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautyshop.authservice.model.RespuestaAuth;
import com.beautyshop.authservice.model.Usuario;
import com.beautyshop.authservice.service.AuthService;

/**
 * Controlador REST del servicio de autenticacion (AA5-EV01).
 *
 * <p>Ruta base: {@code /api/auth}. Expone el registro y el inicio de sesion,
 * devolviendo mensajes de "Autenticacion satisfactoria" o de error segun el
 * resultado, con los codigos HTTP adecuados (201, 200, 400, 401).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    /** Servicio con la logica de autenticacion. */
    private final AuthService authService;

    /**
     * Constructor con inyeccion del servicio.
     *
     * @param authService servicio de autenticacion
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un usuario nuevo.
     *
     * <p>Endpoint: {@code POST /api/auth/register}</p>
     *
     * @param usuario datos de registro (nombre, correo, contrasena) en JSON
     * @return HTTP 201 con el mensaje y los datos del usuario (sin contrasena);
     *         HTTP 400 si faltan datos o el correo ya existe (manejado globalmente)
     */
    @PostMapping("/register")
    public ResponseEntity<RespuestaAuth> registrar(@RequestBody Usuario usuario) {
        Usuario creado = authService.registrar(usuario);
        RespuestaAuth respuesta = new RespuestaAuth(
                "Usuario registrado exitosamente", creado.getNombre(), creado.getCorreo());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * Inicia sesion validando las credenciales.
     *
     * <p>Endpoint: {@code POST /api/auth/login}</p>
     *
     * @param credenciales objeto con el correo y la contrasena
     * @return HTTP 200 con "Autenticacion satisfactoria" si son validas;
     *         HTTP 401 con "Autenticacion fallida" si no lo son
     */
    @PostMapping("/login")
    public ResponseEntity<RespuestaAuth> iniciarSesion(@RequestBody Usuario credenciales) {
        return authService.autenticar(credenciales.getCorreo(), credenciales.getContrasena())
                .map(usuario -> ResponseEntity.ok(
                        new RespuestaAuth("Autenticacion satisfactoria",
                                usuario.getNombre(), usuario.getCorreo())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new RespuestaAuth("Autenticacion fallida: credenciales invalidas")));
    }
}
