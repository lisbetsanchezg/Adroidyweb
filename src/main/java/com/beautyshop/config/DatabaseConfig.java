package com.beautyshop.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Configuracion de la conexion a la base de datos MySQL.
 *
 * <p>Construye de forma explicita el {@link DataSource} a partir de las
 * propiedades definidas en {@code application.properties}. El resto de la
 * aplicacion obtiene conexiones JDBC ({@code java.sql.Connection}) a traves de
 * este {@code DataSource}, manteniendo asi el acceso a datos con JDBC puro
 * (sin ningun ORM como Hibernate).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Configuration
public class DatabaseConfig {

    /** URL de conexion JDBC hacia el servidor MySQL. */
    @Value("${spring.datasource.url}")
    private String url;

    /** Usuario de la base de datos. */
    @Value("${spring.datasource.username}")
    private String username;

    /** Contrasena de la base de datos. */
    @Value("${spring.datasource.password}")
    private String password;

    /** Nombre completo de la clase del driver JDBC de MySQL. */
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * Crea y expone el {@link DataSource} como bean de Spring.
     *
     * <p>El {@code DataSource} centraliza los datos de conexion; los repositorios
     * lo inyectan y llaman a {@code getConnection()} para obtener una conexion
     * JDBC lista para ejecutar sentencias SQL.</p>
     *
     * @return el origen de datos configurado hacia MySQL
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
