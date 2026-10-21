<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Efficientia - Trocar senha</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/area-restrita.css?v=acesso-figma-final-2">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:ital,wght@0,100..700;1,100..700&family=Manrope:wght@200..800&display=swap" rel="stylesheet">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/Favicon_64x64.png" type="image/x-icon">
</head>
<body class="pagina-acesso pagina-trocar-senha">
    <main class="conteudo" aria-describedby="aviso-troca-senha">
        <div class="lateral-esquerda">
            <a href="${pageContext.request.contextPath}/">
                <img class="logo-efficientia" src="${pageContext.request.contextPath}/assets/logo_efficientia.png" alt="Logo Efficientia">
            </a>
        </div>
        
        <div class="direita">
            <div class="menu-login">
                <section class="formulario-acesso" aria-labelledby="titulo-troca-senha">
                    <h1 class="titulo-login" id="titulo-troca-senha">Trocar senha</h1>
                    <div class="grupo-input">
                        <label for="email">Email</label>
                        <input type="email" id="email" placeholder="Digite seu email" disabled>
                    </div>
                    <div class="grupo-input">
                        <label for="senha">Senha</label>
                        <input type="password" id="senha" placeholder="Digite sua senha" disabled>
                    </div>
                    <p class="retorno-login">Lembrou a senha? <a href="${pageContext.request.contextPath}/login">Faça login</a></p>
                    <button class="botao-acesso" type="button" disabled aria-describedby="aviso-troca-senha">Alterar senha</button>
                </section>
            </div>
        </div>
    </main>
    <p class="aviso-troca-senha" id="aviso-troca-senha">Troca de senha em preparação. Por enquanto, procure o responsável pela administração do sistema.</p>
</body>
</html>
