package com.beautyshop.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.beautyshop.authservice.model.RespuestaAuth;

/**
 * Manejador global de excepciones del servicio de autenticacion.
 *
 * <p>Traduce los errores de validacion a respuestas HTTP 400 con un mensaje
 * claro en JSON.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja los errores de validacion (campos vacios o correo duplicado).
     *
     * @param exception excepcion capturada
     * @return respuesta HTTP 400 (BAD REQUEST) con el mensaje de error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaAuth> handleValidation(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RespuestaAuth(exception.getMessage()));
    }
}
