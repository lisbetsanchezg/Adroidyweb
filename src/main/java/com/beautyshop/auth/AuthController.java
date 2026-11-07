package com.beautyshop.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautyshop.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST de autenticacion para el frontend.
 *
 * <p>Expone los endpoints de registro e inicio de sesion. Ruta base:
 * {@code /api/auth}. Devuelve JSON (nunca paginas JSP) para ser consumido por
 * el frontend React.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticacion", description = "Registro e inicio de sesion de usuarios")
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
     * @param user datos de registro (nombre, correo, contrasena) en el cuerpo JSON
     * @return el usuario creado (sin contrasena) con HTTP 201 (CREATED);
     *         HTTP 400 si el correo ya existe (manejado globalmente)
     */
    @Operation(summary = "Registrar usuario",
            description = "Crea un usuario nuevo con nombre, correo y contrasena. "
                    + "Devuelve el usuario creado sin la contrasena.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o el correo ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User created = authService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Inicia sesion validando las credenciales contra la base de datos.
     *
     * <p>Endpoint: {@code POST /api/auth/login}</p>
     *
     * @param credentials objeto con el correo y la contrasena
     * @return el usuario autenticado con HTTP 200 (OK), o HTTP 401 (UNAUTHORIZED)
     *         con un mensaje si las credenciales son incorrectas
     */
    @Operation(summary = "Iniciar sesion",
            description = "Valida el correo y la contrasena contra la base de datos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticacion satisfactoria; devuelve el usuario"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User credentials) {
        return authService.login(credentials.getEmail(), credentials.getPassword())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Correo o contrasena incorrectos.")));
    }
}
