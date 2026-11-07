import './Footer.css';

/**
 * Pie de pagina comun de la aplicacion.
 * Muestra la marca, unos enlaces informativos y el ano actual.
 */
function Footer() {
  const year = new Date().getFullYear();

  return (
    <footer className="site-footer">
      <div className="site-footer-brand">Beauty Shop 💄</div>
      <p className="site-footer-text">
        Maquillaje y skincare premium. Proyecto formativo SENA - ADSO.
      </p>
      <p className="site-footer-copy">© {year} Beauty Shop. Todos los derechos reservados.</p>
    </footer>
  );
}

export default Footer;
