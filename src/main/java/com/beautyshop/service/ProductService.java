package com.beautyshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.beautyshop.model.Product;
import com.beautyshop.repository.ProductRepository;

/**
 * Servicio con la logica de negocio de los productos.
 *
 * <p>Actua como intermediario entre el controlador REST y el repositorio de
 * acceso a datos. Aqui se aplican validaciones y reglas de negocio antes de
 * delegar la persistencia en {@link ProductRepository}.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Service
public class ProductService {

    /** Repositorio de acceso a datos de productos. */
    private final ProductRepository productRepository;

    /**
     * Constructor con inyeccion del repositorio.
     *
     * @param productRepository repositorio de productos
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Crea un producto nuevo tras validar sus datos.
     *
     * @param product datos del producto a crear
     * @return el producto creado con su id asignado
     */
    public Product createProduct(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }

    /**
     * Devuelve todos los productos registrados.
     *
     * @return lista de productos
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto
     * @return un {@link Optional} con el producto si existe, o vacio si no
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Actualiza un producto existente tras validar sus datos.
     *
     * @param id      identificador del producto a modificar
     * @param product nuevos datos del producto
     * @return el producto actualizado si existia; en caso contrario un {@link Optional} vacio
     */
    public Optional<Product> updateProduct(Long id, Product product) {
        validateProduct(product);
        boolean updated = productRepository.update(id, product);
        if (updated) {
            product.setId(id);
            return Optional.of(product);
        }
        return Optional.empty();
    }

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto a eliminar
     * @return {@code true} si se elimino; {@code false} si no existia
     */
    public boolean deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

    /**
     * Valida las reglas de negocio basicas de un producto.
     *
     * @param product producto a validar
     * @throws IllegalArgumentException si algun dato obligatorio es invalido
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new IllegalArgumentException("El precio debe ser un valor positivo.");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }
}
