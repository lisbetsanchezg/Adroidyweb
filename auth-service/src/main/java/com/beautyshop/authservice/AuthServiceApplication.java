package com.beautyshop.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del servicio de autenticacion (caso de practica AA5-EV01).
 *
 * <p>Arranca un servicio REST independiente de registro e inicio de sesion.
 * Los usuarios se guardan en memoria, de modo que el servicio es autocontenido
 * y no requiere base de datos para probarse con Postman.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@SpringBootApplication
public class AuthServiceApplication {

    /**
     * Punto de entrada de la aplicacion.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
