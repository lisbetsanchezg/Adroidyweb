package com.beautyshop.model;

import java.math.BigDecimal;

/**
 * Entidad que representa un producto de maquillaje de la tienda BeautyShop.
 *
 * <p>Refleja una fila de la tabla {@code products} de la base de datos. Se usa
 * como objeto de transferencia entre las capas repository, service y controller.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
public class Product {

    /** Identificador unico del producto (clave primaria autoincremental). */
    private Long id;

    /** Nombre comercial del producto. */
    private String name;

    /** Descripcion detallada del producto. */
    private String description;

    /** Precio de venta. Se usa BigDecimal para evitar errores de redondeo con dinero. */
    private BigDecimal price;

    /** Categoria a la que pertenece (labios, ojos, rostro, etc.). */
    private String category;

    /** Cantidad de unidades disponibles en inventario. */
    private Integer stock;

    /** Ruta o URL de la imagen del producto. */
    private String image;

    /**
     * Constructor vacio requerido para crear instancias y poblarlas por setters
     * (por ejemplo al leer un {@code ResultSet} en el repositorio).
     */
    public Product() {
    }

    /**
     * Constructor completo con todos los campos del producto.
     *
     * @param id          identificador unico
     * @param name        nombre del producto
     * @param description descripcion del producto
     * @param price       precio de venta
     * @param category    categoria del producto
     * @param stock       unidades disponibles
     * @param image       ruta o URL de la imagen
     */
    public Product(Long id, String name, String description, BigDecimal price,
                   String category, Integer stock, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.image = image;
    }

    /**
     * Devuelve el identificador del producto.
     *
     * @return el id
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador del producto.
     *
     * @param id el id a establecer
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * @return el nombre
     */
    public String getName() {
        return name;
    }

    /**
     * Asigna el nombre del producto.
     *
     * @param name el nombre a establecer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Devuelve la descripcion del producto.
     *
     * @return la descripcion
     */
    public String getDescription() {
        return description;
    }

    /**
     * Asigna la descripcion del producto.
     *
     * @param description la descripcion a establecer
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Devuelve el precio del producto.
     *
     * @return el precio
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Asigna el precio del producto.
     *
     * @param price el precio a establecer
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Devuelve la categoria del producto.
     *
     * @return la categoria
     */
    public String getCategory() {
        return category;
    }

    /**
     * Asigna la categoria del producto.
     *
     * @param category la categoria a establecer
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Devuelve el stock disponible del producto.
     *
     * @return el stock
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Asigna el stock disponible del producto.
     *
     * @param stock el stock a establecer
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * Devuelve la ruta o URL de la imagen del producto.
     *
     * @return la imagen
     */
    public String getImage() {
        return image;
    }

    /**
     * Asigna la ruta o URL de la imagen del producto.
     *
     * @param image la imagen a establecer
     */
    public void setImage(String image) {
        this.image = image;
    }
}
