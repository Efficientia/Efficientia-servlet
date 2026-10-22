<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Efficientia - Trocar senha</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/area-restrita.css?v=acesso-retornos-2">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:ital,wght@0,100..700;1,100..700&family=Manrope:wght@200..800&display=swap" rel="stylesheet">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/Favicon_64x64.png" type="image/x-icon">
</head>
<body class="pagina-acesso">
    <main class="conteudo">
        <div class="lateral-esquerda">
            <a href="${pageContext.request.contextPath}/">
                <img class="logo-efficientia" src="${pageContext.request.contextPath}/assets/logo_efficientia.png" alt="Logo Efficientia">
            </a>
        </div>
        
        <div class="direita">
            <div class="menu-login">
                <section class="formulario-acesso" aria-labelledby="titulo-recuperacao">
                    <h1 class="titulo-login" id="titulo-recuperacao">Trocar senha</h1>
                    <p class="mensagem-recuperacao">
                        A troca de senha está indisponível. Para redefinir seu acesso, entre em contato com o responsável pela administração do sistema.
                    </p>
                    <a class="botao-acesso voltar-login" href="${pageContext.request.contextPath}/login">Voltar para o login</a>
                </section>
            </div>
        </div>
    </main>
</body>
</html>
