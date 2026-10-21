<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Efficientia - Login de Administrador</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/area-restrita.css?v=acesso-retornos-2">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:ital,wght@0,100..700;1,100..700&family=Manrope:wght@200..800&display=swap" rel="stylesheet">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/Favicon_64x64.png" type="image/x-icon">
</head>
<body class="pagina-acesso pagina-login">
    <main class="conteudo">
        <div class="lateral-esquerda">
            <a href="${pageContext.request.contextPath}/">
                <img class="logo-efficientia" src="${pageContext.request.contextPath}/assets/logo_efficientia.png" alt="Logo Efficientia">
            </a>
        </div>
        
        <div class="direita">
            <div class="menu-login">
                <form action="${pageContext.request.contextPath}/login" method="post">
                    <h1 class="titulo-login">Entrar</h1>

                    <!-- Mensagens de validação e segurança -->
                    <c:if test="${param.erro == 'nao_autorizado'}">
                        <div class="alerta-erro" role="alert">
                            Acesso restrito. Faça login para acessar o painel.
                        </div>
                    </c:if>

                    <c:if test="${not empty erro}">
                        <div class="alerta-erro" role="alert">
                            <c:out value="${erro}" />
                        </div>
                    </c:if>

                    <c:if test="${param.msg == 'desconectado'}">
                        <div class="alerta-sucesso" role="status">
                            Sessão encerrada com sucesso.
                        </div>
                    </c:if>

                    <div class="grupo-input">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="<c:out value='${emailInformado}' />" placeholder="Digite seu email" maxlength="254" required autocomplete="email">
                    </div>

                    <div class="grupo-input">
                        <label for="senha">Senha</label>
                        <input type="password" id="senha" name="senha" placeholder="Digite sua senha" maxlength="128" required autocomplete="current-password">
                    </div>
                    
                    <div class="esqueceu-senha">
                        <a href="${pageContext.request.contextPath}/esqueci-senha.jsp">Esqueci minha senha</a>
                    </div>
                    
                    <button type="submit">Entrar</button>
                </form>
                <a class="botao-acesso voltar-inicio" href="${pageContext.request.contextPath}/index.html#inicio">Voltar para o início</a>
            </div>
        </div>
    </main>
</body>
</html>
