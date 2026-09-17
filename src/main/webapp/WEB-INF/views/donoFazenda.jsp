<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Teste Dono Fazenda</title>
</head>

<body>

<h1>Teste do DonoFazendaServlet</h1>

<p>O Servlet conseguiu encaminhar para esta página!</p>

<hr>

<h2>Donos de Fazenda cadastrados</h2>

<%
    Object objeto = request.getAttribute("donoFazendaModels");

    if (objeto == null) {
%>

<p>Nenhum atributo "donoFazendaModels" foi recebido.</p>

<%
} else {
%>

<p>
    O atributo <strong>donoFazendaModels</strong>
    foi recebido pelo Servlet.
</p>

<pre>
<%= objeto %>
        </pre>

<%
    }
%>

</body>
</html>