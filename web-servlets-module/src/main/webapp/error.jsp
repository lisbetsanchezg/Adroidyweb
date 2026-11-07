<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--
    error.jsp - Muestra un mensaje de error enviado por los servlets
    (atributo "errorMessage" de la peticion) durante el registro o el login.
--%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BeautyShop - Error</title>
    <style>
        body { font-family: Arial, sans-serif; background: #fdf0f5; display: flex;
               justify-content: center; align-items: center; min-height: 100vh; margin: 0; }
        .card { background: #fff; padding: 40px; border-radius: 12px; text-align: center;
                box-shadow: 0 4px 16px rgba(0,0,0,.1); width: 360px; }
        h1 { color: #c62828; }
        p { color: #444; }
        a { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #c2185b;
            color: #fff; text-decoration: none; border-radius: 6px; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Ups! Algo salio mal</h1>
        <%-- Muestra el mensaje de error; si no hay, muestra uno generico --%>
        <p>${not empty errorMessage ? errorMessage : 'Ocurrio un error inesperado.'}</p>
        <a href="login.html">Volver</a>
    </div>
</body>
</html>
