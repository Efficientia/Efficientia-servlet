<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Efficientia - Cadastro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/area-restrita.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:ital,wght@0,100..700;1,100..700&family=Manrope:wght@200..800&display=swap" rel="stylesheet">
</head>
<body>
    <div class="conteudo">
        <div class="lateral-esquerda">
            <a href="${pageContext.request.contextPath}/"><img class="logo-efficientia" src="${pageContext.request.contextPath}/assets/logo_efficientia.png" alt="Logo Efficientia"></a>
        </div>
        <div class="direita">
            <div class="menu-login">
                <form action="${pageContext.request.contextPath}/cadastro" method="post">
                    <h1 class="titulo-login">Cadastrar</h1>
                    <c:if test="${not empty erro}"><p class="alerta-erro" role="alert"><c:out value="${erro}" /></p></c:if>
                    <div class="grupo-input">
                        <label for="nome">Nome</label>
                        <input type="text" id="nome" name="nome" value="<c:out value='${nomeInformado}' />" placeholder="Nome" maxlength="100" autocomplete="name" required>
                    </div>
                    <div class="grupo-input">
                        <label for="email">E-mail</label>
                        <input type="email" id="email" name="email" value="<c:out value='${emailInformado}' />" placeholder="Digite seu email" maxlength="254" autocomplete="email" required>
                    </div>
                    <div class="grupo-input">
                        <label for="senha">Senha (15 a 128 caracteres)</label>
                        <input type="password" id="senha" name="senha" placeholder="Digite uma senha" minlength="15" maxlength="128" autocomplete="new-password" required>
                    </div>
                    <p class="link-cadastro">Já possui uma conta?<a href="${pageContext.request.contextPath}/login">Faça login</a></p>
                    <button type="submit">Cadastrar</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
