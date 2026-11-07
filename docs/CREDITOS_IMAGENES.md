# Créditos de las fotos de productos

Las fotos del catálogo están en `src/main/resources/static/images/`. El backend las sirve en
`GET /images/<archivo>`, con el mismo nombre de archivo que guarda el campo `image` de la tabla `products`.
La tienda web (`frontend/src/utils/format.js`, `productImageUrl`) y la app móvil construyen la URL a partir de ese nombre.

Todas son de [Pexels](https://www.pexels.com) y se usan bajo la [licencia de Pexels](https://www.pexels.com/license/)
(uso gratuito, sin atribución obligatoria). Se redujeron a 800 px en su lado mayor.

| Archivo | Producto | ID en Pexels | Archivo original |
|---------|----------|--------------|------------------|
| `labial-rojo.jpg` | Labial Mate Rojo Pasion | 90297 | https://images.pexels.com/photos/90297/pexels-photo-90297.jpeg |
| `base-natural.jpg` | Base Liquida Natural | 28959839 | https://images.pexels.com/photos/28959839/pexels-photo-28959839.jpeg |
| `paleta-nude.jpg` | Paleta de Sombras Nude | 1339340 | https://images.pexels.com/photos/1339340/pexels-photo-1339340.jpeg |
| `mascara-volumen.jpg` | Mascara de Pestanas Volumen | 8558532 | https://images.pexels.com/photos/8558532/pexels-photo-8558532.jpeg |
| `rubor-coral.jpg` | Rubor en Polvo Coral | 2417855 | https://images.pexels.com/photos/2417855/pexels-photo-2417855.jpeg |

La página de cada foto en Pexels es `https://www.pexels.com/photo/<ID>/`.

Las fotos son ilustrativas: no corresponden a los productos reales de la tienda.

## Agregar la foto de un producto nuevo

1. Copia la imagen (JPG o PNG) en `src/main/resources/static/images/`.
2. Guarda el nombre del archivo en el campo `image` del producto (por ejemplo `"image": "delineador-negro.jpg"`).
3. Vuelve a compilar el backend. Si el archivo no existe, la web y la app muestran una imagen de reemplazo.
