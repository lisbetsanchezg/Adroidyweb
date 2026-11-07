package com.beautyshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicacion BeautyShop.
 *
 * <p>Punto de entrada del backend. La anotacion {@code @SpringBootApplication}
 * habilita la autoconfiguracion de Spring, el escaneo de componentes dentro del
 * paquete {@code com.beautyshop} y arranca el servidor web embebido (Tomcat).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@SpringBootApplication
public class BeautyShopApplication {

    /**
     * Metodo de arranque de la aplicacion.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SpringApplication.run(BeautyShopApplication.class, args);
    }
}
