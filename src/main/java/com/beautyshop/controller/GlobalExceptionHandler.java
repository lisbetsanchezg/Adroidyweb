package com.beautyshop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.beautyshop.exception.ResourceNotFoundException;

/**
 * Manejador global de excepciones para los controladores REST.
 *
 * <p>Centraliza el tratamiento de errores para devolver codigos HTTP y mensajes
 * consistentes al cliente (frontend React), en lugar del error 500 por defecto.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja los errores de validacion de datos de entrada.
     *
     * <p>Se dispara cuando el servicio lanza {@link IllegalArgumentException}
     * (por ejemplo, nombre vacio o precio negativo) y responde con HTTP 400.</p>
     *
     * @param exception excepcion capturada
     * @return respuesta HTTP 400 (BAD REQUEST) con el mensaje de error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidation(IllegalArgumentException exception) {
        Map<String, String> body = new HashMap<>();
        body.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Maneja los recursos no encontrados (usuario, producto, pedido, etc.).
     *
     * <p>Se dispara cuando el servicio lanza {@link ResourceNotFoundException}
     * y responde con HTTP 404.</p>
     *
     * @param exception excepcion capturada
     * @return respuesta HTTP 404 (NOT FOUND) con el mensaje de error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException exception) {
        Map<String, String> body = new HashMap<>();
        body.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Maneja errores inesperados de acceso a datos u otros no controlados.
     *
     * @param exception excepcion capturada
     * @return respuesta HTTP 500 (INTERNAL SERVER ERROR) con el mensaje de error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException exception) {
        Map<String, String> body = new HashMap<>();
        body.put("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
