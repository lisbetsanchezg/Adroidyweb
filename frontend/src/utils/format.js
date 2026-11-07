/**
 * Formatea un numero como precio en pesos colombianos (COP).
 *
 * @param {number} amount valor a formatear
 * @returns {string} valor formateado, por ejemplo "$18.000"
 */
export function formatCurrency(amount) {
  return Number(amount).toLocaleString('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0
  });
}

/**
 * Construye la URL de la foto de un producto. El backend guarda solo el nombre
 * del archivo (por ejemplo "labial-rojo.jpg") y sirve la foto en /images/<archivo>.
 * Si el valor ya es una URL absoluta, se usa tal cual.
 *
 * @param {string} image valor del campo image del producto
 * @returns {string|null} URL de la foto, o null si el producto no tiene imagen
 */
export function productImageUrl(image) {
  if (!image || !image.trim()) {
    return null;
  }
  if (/^https?:\/\//i.test(image)) {
    return image;
  }
  return `/images/${encodeURIComponent(image.trim())}`;
}
