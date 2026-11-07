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

/**
 * Servlet encargado del registro de usuarios de BeautyShop.
 *
 * <p>Mapeado a la URL {@code /register} mediante la anotacion
 * {@link WebServlet}. Recibe los datos del formulario {@code register.html},
 * cifra la contrasena y guarda el usuario en MySQL a traves del
 * {@link UserDao}.</p>
 *
 * @author Lisbet Sanchez
 * @version 1.0.0
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    /** Version para la serializacion del servlet. */
    private static final long serialVersionUID = 1L;

    /** DAO para el acceso a datos de usuarios. */
    private final UserDao userDao = new UserDao();

    /**
     * Atiende las peticiones GET a {@code /register}.
     *
     * <p>Redirige al formulario HTML de registro. Asi, si el usuario navega
     * directamente a la URL, ve el formulario en lugar de un error.</p>
     *
     * @param request  peticion HTTP
     * @param response respuesta HTTP
     * @throws IOException si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/register.html");
    }

    /**
     * Atiende las peticiones POST a {@code /register}.
     *
     * <p>Lee los datos del formulario (nombre, correo, contrasena), valida que
     * el correo no este repetido, cifra la contrasena y registra al usuario.
     * En caso de exito redirige al login; ante un error reenvia a la pagina de
     * error.</p>
     *
     * @param request  peticion HTTP con los parametros del formulario
     * @param response respuesta HTTP
     * @throws ServletException si ocurre un error al reenviar a la vista
     * @throws IOException      si ocurre un error de entrada/salida
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Recupera los parametros enviados por el formulario de registro
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validacion basica de campos obligatorios
        if (isBlank(name) || isBlank(email) || isBlank(password)) {
            forwardToError(request, response, "Todos los campos son obligatorios.");
            return;
        }

        try {
            // Evita registrar dos veces el mismo correo
            if (userDao.existsByEmail(email)) {
                forwardToError(request, response, "El correo ya esta registrado.");
                return;
            }

            // Cifra la contrasena antes de persistirla
            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = new User(name, email, hashedPassword);

            boolean registered = userDao.registerUser(user);
            if (registered) {
                // Registro exitoso: enviamos al login con un indicador
                response.sendRedirect(request.getContextPath() + "/login.html?registered=true");
            } else {
                forwardToError(request, response, "No se pudo completar el registro.");
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
