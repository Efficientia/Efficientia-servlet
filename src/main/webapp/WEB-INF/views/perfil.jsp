<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="paginaAtiva" value="perfil" />
<c:set var="paginaServlet" value="/perfil" />
<c:set var="tituloPagina" value="Meu perfil" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
  <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/css/perfil.css?v=20261101b">
  <script src="${ctx}/js/perfil.js?v=20261101" defer></script>
</head>
<body class="perfil-page">
  <a class="perfil-pular" href="#perfil-conteudo">Ir para o conteúdo</a>
  <div class="app-layout">
    <%@ include file="/WEB-INF/views/fragments/sidebar.jspf" %>
    <main class="main-content" id="perfil-conteudo">
      <%@ include file="/WEB-INF/views/fragments/topbar.jspf" %>
      <div class="content-container perfil-content">
        <div class="perfil-titulo">
          <h1>Meu perfil</h1>
          <p>Consulte e atualize as informações básicas da sua conta.</p>
        </div>
        <section class="perfil-card" aria-labelledby="perfil-informacoes">
          <div class="perfil-identificacao">
            <div class="perfil-avatar" aria-hidden="true"><c:out value="${inicialPerfil}" /></div>
            <div class="perfil-identidade">
              <p class="perfil-nome"><c:out value="${identificacaoPerfil}" /></p>
              <p class="perfil-papel">Conta de administrador</p>
            </div>
          </div>
          <form action="${ctx}/perfil" method="post" id="perfil-form">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
            <h2 id="perfil-informacoes">Informações da conta</h2>
            <c:if test="${not empty erroPerfil}">
              <div class="perfil-mensagem perfil-erro" role="alert" tabindex="-1"><c:out value="${erroPerfil}" /></div>
            </c:if>
            <c:if test="${not empty sucessoPerfil}">
              <div class="perfil-mensagem perfil-sucesso" role="status" tabindex="-1"><c:out value="${sucessoPerfil}" /></div>
            </c:if>
            <div class="perfil-campos">
              <div class="perfil-campo">
                <label for="perfil-nome">Nome</label>
                <input id="perfil-nome" name="nome" type="text" autocomplete="name" maxlength="150" required
                       value="<c:out value='${nomePerfil}' />" aria-invalid="${not empty erroNome}"
                       aria-describedby="perfil-erro-nome">
                <span class="perfil-erro-campo" id="perfil-erro-nome"><c:out value="${erroNome}" /></span>
              </div>
              <div class="perfil-campo">
                <label for="perfil-email">E-mail</label>
                <input id="perfil-email" name="email" type="email" autocomplete="email" maxlength="254" required
                       value="<c:out value='${emailPerfil}' />" aria-invalid="${not empty erroEmail}"
                       aria-describedby="perfil-erro-email">
                <span class="perfil-erro-campo" id="perfil-erro-email"><c:out value="${erroEmail}" /></span>
              </div>
              <div class="perfil-campo perfil-empresa">
                <label for="perfil-empresa">Empresa vinculada</label>
                <output id="perfil-empresa" aria-describedby="perfil-empresa-ajuda"><c:out value="${empresaPerfil}" /></output>
                <p id="perfil-empresa-ajuda">Gerenciada pela administração</p>
              </div>
              <div class="perfil-acoes">
                <button type="submit" class="perfil-salvar" ${perfilIndisponivel ? 'disabled' : ''}>Salvar alterações</button>
              </div>
            </div>
          </form>
        </section>
      </div>
    </main>
  </div>
</body>
</html>
