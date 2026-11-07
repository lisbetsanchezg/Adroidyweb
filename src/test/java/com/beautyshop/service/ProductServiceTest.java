package com.beautyshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.beautyshop.model.Product;
import com.beautyshop.repository.ProductRepository;

/**
 * Pruebas unitarias de {@link ProductService}.
 *
 * <p>Usan un {@link FakeProductRepository} en memoria (sin base de datos ni
 * Mockito) para validar las reglas de negocio de los productos.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
class ProductServiceTest {

    /** Repositorio falso configurable para cada prueba. */
    private FakeProductRepository fakeRepository;

    /** Servicio bajo prueba. */
    private ProductService productService;

    /** Construye un producto valido de ejemplo. */
    private Product validProduct() {
        return new Product(null, "Labial Matte", "Larga duracion",
                new BigDecimal("18000"), "labios", 40, "labial.jpg");
    }

    /** Prepara un servicio nuevo antes de cada prueba. */
    @BeforeEach
    void setUp() {
        fakeRepository = new FakeProductRepository();
        productService = new ProductService(fakeRepository);
    }

    /** Crear un producto valido delega en el repositorio y devuelve el resultado. */
    @Test
    @DisplayName("createProduct guarda un producto valido")
    void createProductSavesValidProduct() {
        Product created = productService.createProduct(validProduct());
        assertEquals(1L, created.getId());
        assertTrue(fakeRepository.saveCalled);
    }

    /** Crear un producto sin nombre es rechazado. */
    @Test
    @DisplayName("createProduct rechaza nombre vacio")
    void createProductRejectsBlankName() {
        Product product = validProduct();
        product.setName("  ");
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(product));
    }

    /** Crear un producto con precio negativo es rechazado. */
    @Test
    @DisplayName("createProduct rechaza precio negativo")
    void createProductRejectsNegativePrice() {
        Product product = validProduct();
        product.setPrice(new BigDecimal("-1"));
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(product));
    }

    /** Crear un producto con stock negativo es rechazado. */
    @Test
    @DisplayName("createProduct rechaza stock negativo")
    void createProductRejectsNegativeStock() {
        Product product = validProduct();
        product.setStock(-5);
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(product));
    }

    /** Listar productos devuelve lo que entrega el repositorio. */
    @Test
    @DisplayName("getAllProducts devuelve la lista del repositorio")
    void getAllProductsReturnsList() {
        assertEquals(2, productService.getAllProducts().size());
    }

    /** Actualizar un producto existente devuelve el producto con su id. */
    @Test
    @DisplayName("updateProduct devuelve el producto cuando existe")
    void updateProductReturnsProductWhenExists() {
        fakeRepository.updateResult = true;

        Optional<Product> result = productService.updateProduct(7L, validProduct());

        assertTrue(result.isPresent());
        assertEquals(7L, result.get().getId());
    }

    /** Actualizar un producto inexistente devuelve un Optional vacio. */
    @Test
    @DisplayName("updateProduct devuelve vacio cuando no existe")
    void updateProductReturnsEmptyWhenMissing() {
        fakeRepository.updateResult = false;

        Optional<Product> result = productService.updateProduct(99L, validProduct());

        assertFalse(result.isPresent());
    }

    /** Eliminar un producto delega en el repositorio. */
    @Test
    @DisplayName("deleteProduct delega en el repositorio")
    void deleteProductDelegates() {
        fakeRepository.deleteResult = true;
        assertTrue(productService.deleteProduct(3L));
    }

    /**
     * Implementacion falsa de {@link ProductRepository} que no accede a la base
     * de datos. Extiende la clase real con un DataSource nulo y sobrescribe los
     * metodos usados por el servicio.
     */
    private static class FakeProductRepository extends ProductRepository {

        /** Indica si se llamo a save. */
        private boolean saveCalled = false;

        /** Resultado configurable de update. */
        private boolean updateResult = false;

        /** Resultado configurable de deleteById. */
        private boolean deleteResult = false;

        FakeProductRepository() {
            super(null);
        }

        @Override
        public Product save(Product product) {
            saveCalled = true;
            product.setId(1L);
            return product;
        }

        @Override
        public List<Product> findAll() {
            return List.of(new Product(), new Product());
        }

        @Override
        public Optional<Product> findById(Long id) {
            return Optional.of(new Product());
        }

        @Override
        public boolean update(Long id, Product product) {
            return updateResult;
        }

        @Override
        public boolean deleteById(Long id) {
            return deleteResult;
        }
    }
}
