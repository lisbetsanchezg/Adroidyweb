<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
    welcome.jsp - Pagina de bienvenida tras un inicio de sesion exitoso.
    Muestra el nombre del usuario autenticado guardado en la sesion por LoginServlet.
    Se usa un scriptlet para proteger la pagina y EL (${...}) para pintar los datos.
--%>
<%
    // Si no hay sesion iniciada (acceso directo a la JSP), vuelve al login
    Object userName = session.getAttribute("userName");
    if (userName == null) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BeautyShop - Bienvenida</title>
    <style>
        body { font-family: Arial, sans-serif; background: #fdf0f5; display: flex;
               justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .card { background: #fff; padding: 40px; border-radius: 12px; text-align: center;
                box-shadow: 0 4px 16px rgba(0,0,0,.1); width: 360px; }
        h1 { color: #c2185b; }
        p { color: #444; }
        a { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #c2185b;
            color: #fff; text-decoration: none; border-radius: 6px; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Hola, ${sessionScope.userName}! 💄</h1>
        <p>Bienvenido(a) a BeautyShop.</p>
        <p>Tu correo registrado es: <strong>${sessionScope.userEmail}</strong></p>
        <a href="login.html">Cerrar sesion</a>
    </div>
</body>
</html>
