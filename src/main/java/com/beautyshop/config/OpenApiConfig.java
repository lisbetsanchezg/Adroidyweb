package com.beautyshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuracion de la documentacion OpenAPI (Swagger) del proyecto BeautyShop.
 *
 * <p>Define la informacion general de la API (titulo, version, descripcion) que
 * se muestra en {@code /swagger-ui.html}. Los endpoints quedan disponibles en
 * {@code /v3/api-docs} (especificacion JSON) y {@code /swagger-ui.html} (UI).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Construye la metadata general de la API de BeautyShop.
     *
     * @return la configuracion {@link OpenAPI} con la informacion del proyecto
     */
    @Bean
    public OpenAPI beautyShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BeautyShop API")
                        .description("API REST de la tienda de maquillaje BeautyShop: "
                                + "productos, carrito de compras, gestion de pedidos y autenticacion. "
                                + "Proyecto formativo SENA - ADSO.")
                        .version("1.0.0")
                        .contact(new Contact().name("BeautyShop").email("soporte@beautyshop.com"))
                        .license(new License().name("Uso educativo")));
    }
}
