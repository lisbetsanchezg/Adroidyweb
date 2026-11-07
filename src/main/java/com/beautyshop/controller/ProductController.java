package com.beautyshop.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautyshop.model.Product;
import com.beautyshop.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST del modulo de productos.
 *
 * <p>Expone los endpoints HTTP para las operaciones CRUD sobre productos.
 * Ruta base: {@code /api/products}. Delega toda la logica en
 * {@link ProductService} y devuelve respuestas con el codigo HTTP adecuado.</p>
 *
 * <p>{@code @CrossOrigin} permite el consumo desde el frontend React
 * (por ejemplo, http://localhost:3000).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Productos", description = "CRUD de productos de la tienda")
public class ProductController {

    /** Servicio con la logica de negocio de productos. */
    private final ProductService productService;

    /**
     * Constructor con inyeccion del servicio.
     *
     * @param productService servicio de productos
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Crea un producto nuevo.
     *
     * <p>Endpoint: {@code POST /api/products}</p>
     *
     * @param product datos del producto enviados en el cuerpo de la peticion (JSON)
     * @return el producto creado con codigo HTTP 201 (CREATED)
     */
    @Operation(summary = "Crear producto",
            description = "Registra un producto nuevo (nombre, descripcion, precio, categoria, stock, imagen).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos del producto invalidos")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Lista todos los productos.
     *
     * <p>Endpoint: {@code GET /api/products}</p>
     *
     * @return lista de productos con codigo HTTP 200 (OK)
     */
    @Operation(summary = "Listar productos", description = "Devuelve todos los productos registrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos")
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Consulta un producto por su identificador.
     *
     * <p>Endpoint: {@code GET /api/products/{id}}</p>
     *
     * @param id identificador del producto
     * @return el producto con HTTP 200 (OK) si existe, o HTTP 404 (NOT FOUND) si no
     */
    @Operation(summary = "Consultar producto por id",
            description = "Devuelve un producto por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Identificador del producto") @PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza un producto existente.
     *
     * <p>Endpoint: {@code PUT /api/products/{id}}</p>
     *
     * @param id      identificador del producto a modificar
     * @param product nuevos datos del producto (JSON)
     * @return el producto actualizado con HTTP 200 (OK), o HTTP 404 (NOT FOUND) si no existe
     */
    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos del producto invalidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Identificador del producto") @PathVariable Long id,
            @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un producto por su identificador.
     *
     * <p>Endpoint: {@code DELETE /api/products/{id}}</p>
     *
     * @param id identificador del producto a eliminar
     * @return HTTP 204 (NO CONTENT) si se elimino, o HTTP 404 (NOT FOUND) si no existia
     */
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Identificador del producto") @PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
