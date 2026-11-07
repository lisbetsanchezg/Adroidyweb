package com.beautyshop.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.beautyshop.exception.ResourceNotFoundException;
import com.beautyshop.model.Product;
import com.beautyshop.repository.ProductRepository;
import com.beautyshop.repository.UserRepository;

/**
 * Servicio con la logica de negocio del carrito de compras.
 *
 * <p>Valida usuario y producto, resuelve el precio unitario desde el producto
 * real y calcula el total del carrito (suma de precio unitario * cantidad).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@Service
public class CartService {

    /** Repositorio de acceso a datos del carrito. */
    private final CartRepository cartRepository;

    /** Repositorio de productos, para validar existencia y obtener el precio. */
    private final ProductRepository productRepository;

    /** Repositorio de usuarios, para validar la existencia del usuario. */
    private final UserRepository userRepository;

    /**
     * Constructor con inyeccion de dependencias.
     *
     * @param cartRepository    repositorio del carrito
     * @param productRepository repositorio de productos
     * @param userRepository    repositorio de usuarios
     */
    public CartService(CartRepository cartRepository, ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Agrega un producto al carrito. Si el producto ya estaba, suma la cantidad.
     *
     * <p>El precio unitario se toma del producto real (no del cliente). Valida
     * que el usuario y el producto existan y que la cantidad sea positiva.</p>
     *
     * @param userId  identificador del usuario
     * @param request datos de la linea (idProducto y cantidad)
     * @return el carrito actualizado con su total
     */
    public CartResponse addItem(Long userId, CartItem request) {
        requireUser(userId);
        validateQuantity(request.getCantidad());

        Long productId = request.getIdProducto();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto " + productId + " no existe."));

        Optional<CartItem> existing = cartRepository.findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            // Ya estaba en el carrito: se suma la cantidad solicitada
            int nuevaCantidad = existing.get().getCantidad() + request.getCantidad();
            cartRepository.updateQuantity(userId, productId, nuevaCantidad);
        } else {
            // Linea nueva: el precio unitario se toma del producto real
            CartItem item = new CartItem(userId, productId, request.getCantidad(), product.getPrice());
            cartRepository.insert(item);
        }
        return getCart(userId);
    }

    /**
     * Actualiza la cantidad de un producto que ya esta en el carrito.
     *
     * @param userId    identificador del usuario
     * @param productId identificador del producto
     * @param cantidad  nueva cantidad (debe ser mayor que 0)
     * @return el carrito actualizado con su total
     */
    public CartResponse updateQuantity(Long userId, Long productId, Integer cantidad) {
        requireUser(userId);
        validateQuantity(cantidad);

        cartRepository.findByUserAndProduct(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto " + productId + " no esta en el carrito."));

        cartRepository.updateQuantity(userId, productId, cantidad);
        return getCart(userId);
    }

    /**
     * Quita un producto del carrito.
     *
     * @param userId    identificador del usuario
     * @param productId identificador del producto
     * @return el carrito actualizado con su total
     */
    public CartResponse removeItem(Long userId, Long productId) {
        requireUser(userId);

        boolean removed = cartRepository.delete(userId, productId);
        if (!removed) {
            throw new ResourceNotFoundException(
                    "El producto " + productId + " no esta en el carrito.");
        }
        return getCart(userId);
    }

    /**
     * Obtiene el contenido del carrito de un usuario con el total calculado.
     *
     * @param userId identificador del usuario
     * @return el carrito con sus lineas y el total
     */
    public CartResponse getCart(Long userId) {
        requireUser(userId);
        List<CartItem> items = cartRepository.findByUser(userId);
        return new CartResponse(userId, items, calculateTotal(items));
    }

    /**
     * Vacia por completo el carrito de un usuario.
     *
     * @param userId identificador del usuario
     */
    public void clearCart(Long userId) {
        cartRepository.deleteByUser(userId);
    }

    /**
     * Calcula el total del carrito sumando precio unitario * cantidad de cada linea.
     *
     * @param items lineas del carrito
     * @return el total
     */
    public BigDecimal calculateTotal(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }
        return total;
    }

    /**
     * Verifica que el usuario exista; si no, lanza una excepcion 404.
     *
     * @param userId identificador del usuario
     */
    private void requireUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("El usuario " + userId + " no existe.");
        }
    }

    /**
     * Valida que la cantidad sea un numero positivo.
     *
     * @param cantidad cantidad a validar
     */
    private void validateQuantity(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }
    }
}
