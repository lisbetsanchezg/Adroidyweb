import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import Footer from './components/Footer.jsx';
import Home from './pages/Home.jsx';
import Catalogo from './pages/Catalogo.jsx';
import Cart from './pages/Cart.jsx';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import Orders from './pages/Orders.jsx';
import Checkout from './pages/Checkout.jsx';

/**
 * Componente raiz de la aplicacion.
 * Define la barra de navegacion, el pie de pagina comun y las rutas
 * de las paginas principales mediante React Router.
 */
function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/catalogo" element={<Catalogo />} />
        <Route path="/carrito" element={<Cart />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Register />} />
        <Route path="/pedidos" element={<Orders />} />
        <Route path="/pago" element={<Checkout />} />
      </Routes>
      <Footer />
    </>
  );
}

export default App;
