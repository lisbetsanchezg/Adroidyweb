package com.beautyshop.web.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.beautyshop.web.dao.UserDao;
import com.beautyshop.web.model.User;
import com.beautyshop.web.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet encargado del inicio de sesion de usuarios de BeautyShop.
 *
 * <p>Mapeado a la URL {@code /login} mediante {@link WebServlet}. Recibe los
 * datos del formulario {@code login.html}, valida las credenciales contra la
 * base de datos y redirige a {@code welcome.jsp} (exito) o {@code error.jsp}
 * (fallo).</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    /** Version para la serializacion del servlet. */
    private static final long serialVersionUID = 1L;

    /** DAO para el acceso a datos de usuarios. */
    private final UserDao userDao = new UserDao();

    /**
     * Atiende las peticiones GET a {@code /login}.
     *
     * <p>Redirige al formulario HTML de inicio de sesion.</p>
     *
     * @param request  peticion HTTP
     * @param response respuesta HTTP
     * @throws IOException si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    /**
     * Atiende las peticiones POST a {@code /login}.
     *
     * <p>Lee el correo y la contrasena, cifra la contrasena con el mismo
     * algoritmo del registro y busca una coincidencia en la base de datos.
     * Si las credenciales son validas, guarda el nombre del usuario en la
     * sesion y reenvia a {@code welcome.jsp}; de lo contrario, reenvia a
     * {@code error.jsp}.</p>
     *
     * @param request  peticion HTTP con los parametros del formulario
     * @param response respuesta HTTP
     * @throws ServletException si ocurre un error al reenviar a la vista
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Recupera las credenciales enviadas por el formulario de login
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validacion basica de campos obligatorios
        if (isBlank(email) || isBlank(password)) {
            forwardToError(request, response, "Debes ingresar correo y contrasena.");
            return;
        }

        try {
            // Cifra la contrasena para compararla con la almacenada
            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = userDao.findByCredentials(email, hashedPassword);

            if (user != null) {
                // Credenciales validas: guarda el usuario en la sesion
                HttpSession session = request.getSession();
                session.setAttribute("userName", user.getName());
                session.setAttribute("userEmail", user.getEmail());

                // Reenvia a la pagina de bienvenida
                request.getRequestDispatcher("/welcome.jsp").forward(request, response);
            } else {
                forwardToError(request, response, "Correo o contrasena incorrectos.");
            }
        } catch (SQLException e) {
            forwardToError(request, response, "Error de base de datos: " + e.getMessage());
        }
    }

    /**
     * Reenvia la peticion a la pagina de error mostrando un mensaje.
     *
     * @param request  peticion HTTP
     * @param response respuesta HTTP
     * @param message  mensaje de error a mostrar
     * @throws ServletException si ocurre un error al reenviar
     * @throws IOException      si ocurre un error de entrada/salida
     */
    private void forwardToError(HttpServletRequest request, HttpServletResponse response,
                                String message) throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    /**
     * Indica si una cadena es nula o esta vacia (solo espacios).
     *
     * @param value cadena a evaluar
     * @return {@code true} si es nula o vacia
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
