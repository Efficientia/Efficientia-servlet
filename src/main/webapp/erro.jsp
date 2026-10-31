<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#0F51AE">
    <title>Ops, algo deu errado! | Efficientia</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/erro.css?v=1">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@400&family=Manrope:wght@700&display=swap" rel="stylesheet">
</head>
<body>
<main class="error-page" aria-labelledby="titulo-erro">
    <div class="error-decoration error-decoration--top-large" aria-hidden="true">
        <img src="${pageContext.request.contextPath}/assets/error-glow-top-large.svg" alt="">
    </div>
    <div class="error-decoration error-decoration--top-small" aria-hidden="true">
        <img src="${pageContext.request.contextPath}/assets/error-glow-ellipse-159.svg" alt="">
    </div>
    <div class="error-decoration error-decoration--bottom-large" aria-hidden="true">
        <img src="${pageContext.request.contextPath}/assets/error-glow-bottom-large.svg" alt="">
    </div>
    <div class="error-decoration error-decoration--bottom-small" aria-hidden="true">
        <img src="${pageContext.request.contextPath}/assets/error-glow-ellipse-161.svg" alt="">
    </div>
    <div class="error-decoration error-decoration--bottom-small" aria-hidden="true">
        <img src="${pageContext.request.contextPath}/assets/error-glow-ellipse-161.svg" alt="">
    </div>

    <div class="error-content">
        <h1 id="titulo-erro">Ops, algo deu errado!</h1>
        <p>tente novamente mais tarde</p>
    </div>
</main>
</body>
</html>
