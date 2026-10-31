<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Tela Inicial da Área Restrita (Visão Geral / Dashboard).
  Ponto de entrada exibido imediatamente após o login do Administrador.
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="paginaAtiva" value="home" />
<c:set var="paginaServlet" value="/home" />
<c:set var="tituloPagina" value="Visão Geral" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
  <div class="app-layout">
    <%@ include file="/WEB-INF/views/fragments/sidebar.jspf" %>

    <main class="main-content">
      <%@ include file="/WEB-INF/views/fragments/waves.jspf" %>
      <%@ include file="/WEB-INF/views/fragments/topbar.jspf" %>

      <div class="content-container home-content-wrapper">

        <!-- Seção de Boas-vindas com mascote e dica -->
        <section class="home-welcome-section">
          <div class="welcome-text-col">
            <span class="welcome-greeting">Olá, <c:out value="${sessionScope.adminLogado.nome}" default="Administrador" />!</span>
            <h1 class="welcome-heading">Seja bem-vindo (a)!</h1>
            <p class="welcome-desc">Aqui você acompanha as informações e relatórios da sua operação de formas simples e eficiente</p>
          </div>

          <div class="welcome-mascot-slot">
            <img src="${ctx}/assets/images/muuriarearestrita.png" alt="Mascote Efficientia">
          </div>

          <!-- Card de Dica Superior -->
          <div class="tip-card-widget">
            <div class="tip-bulb-icon" aria-hidden="true">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                   stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 18h6"></path>
                <path d="M10 22h4"></path>
                <path d="M12 2a7 7 0 0 0-7 7c0 2.5 1.5 4.5 3 6h8c1.5-1.5 3-3.5 3-6a7 7 0 0 0-7-7z"></path>
              </svg>
            </div>
            <div class="tip-text-block">
              <p class="tip-message">Mais organização<br>para melhores<br>decisões.</p>
              <span class="tip-indicator-bar"></span>
            </div>
          </div>
        </section>

        <!-- Layout Principal: Cards à esquerda e espaço livre na lateral direita -->
        <div class="home-main-layout">

          <!-- Coluna com os 5 Cards de Módulos Administrativos -->
          <section class="home-cards-list" id="homeCardsContainer">

            <!-- Card 1: Motoristas -->
            <article class="home-card" data-card-title="motoristas">
              <div class="card-image-slot">
                <img src="${ctx}/assets/images/motorista.png" alt="Motoristas">
              </div>
              <div class="home-card-content">
                <div class="home-card-header">
                  <h2 class="home-card-title">
                    Seus motoristas,<br>
                    <span class="highlight-green">sob controle.</span>
                  </h2>
                  <div class="home-card-badge">
                    <img src="${ctx}/assets/icons/motoristas.svg" alt="" class="badge-icon">
                    <span>GESTÃO DE MOTORISTAS</span>
                  </div>
                </div>
                <div class="home-card-footer">
                  <p class="home-card-desc">
                    Gerencie e cadastre os motoristas da sua empresa e mantenha suas informações sempre atualizadas.
                  </p>
                  <a href="${ctx}/motorista" class="btn-ver-agora">
                    <span>Ver agora</span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </a>
                </div>
              </div>
            </article>

            <!-- Card 2: Administradores -->
            <article class="home-card" data-card-title="administradores admin">
              <div class="card-image-slot">
                <img src="${ctx}/assets/images/administrador.png" alt="Administradores">
              </div>
              <div class="home-card-content">
                <div class="home-card-header">
                  <h2 class="home-card-title">
                    Informação organizada,<br>
                    gestão <span class="highlight-green">inteligente.</span>
                  </h2>
                  <div class="home-card-badge">
                    <img src="${ctx}/assets/icons/administradores.svg" alt="" class="badge-icon">
                    <span>GESTÃO DE ADMINISTRADORES</span>
                  </div>
                </div>
                <div class="home-card-footer">
                  <p class="home-card-desc">
                    Gerencie e cadastre os dados dos administradores da sua empresa e mantenha suas informações sempre organizadas.
                  </p>
                  <a href="${ctx}/admin" class="btn-ver-agora">
                    <span>Ver agora</span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </a>
                </div>
              </div>
            </article>

            <!-- Card 3: Pecuaristas -->
            <article class="home-card" data-card-title="pecuaristas pasto gado">
              <div class="card-image-slot">
                <img src="${ctx}/assets/images/pecuarista.png" alt="Pecuaristas">
              </div>
              <div class="home-card-content">
                <div class="home-card-header">
                  <h2 class="home-card-title">
                    Do pasto à gestão,<br>
                    tudo <span class="highlight-green">conectado.</span>
                  </h2>
                  <div class="home-card-badge">
                    <img src="${ctx}/assets/icons/pecuaristas.svg" alt="" class="badge-icon">
                    <span>GESTÃO DE PECUARISTAS</span>
                  </div>
                </div>
                <div class="home-card-footer">
                  <p class="home-card-desc">
                    Gerencie e cadastre os dados dos pecuaristas e mantenha as informações sempre organizadas e atualizadas.
                  </p>
                  <a href="${ctx}/pecuarista" class="btn-ver-agora">
                    <span>Ver agora</span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </a>
                </div>
              </div>
            </article>

            <!-- Card 4: Caminhões -->
            <article class="home-card" data-card-title="caminhoes frota transporte">
              <div class="card-image-slot">
                <img src="${ctx}/assets/images/caminhao.png" alt="Caminhões">
              </div>
              <div class="home-card-content">
                <div class="home-card-header">
                  <h2 class="home-card-title">
                    Transporte <span class="highlight-green">inteligente,</span><br>
                    gestão integrada.
                  </h2>
                  <div class="home-card-badge">
                    <img src="${ctx}/assets/icons/caminhoes.svg" alt="" class="badge-icon">
                    <span>GESTÃO DE CAMINHÕES</span>
                  </div>
                </div>
                <div class="home-card-footer">
                  <p class="home-card-desc">
                    Gerencie e cadastre os caminhões da sua empresa e mantenha as informações da frota sempre organizadas.
                  </p>
                  <a href="${ctx}/caminhao" class="btn-ver-agora">
                    <span>Ver agora</span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </a>
                </div>
              </div>
            </article>

            <!-- Card 5: Empresas -->
            <article class="home-card" data-card-title="empresas gestao">
              <div class="card-image-slot">
                <img src="${ctx}/assets/images/escritorio.png" alt="Empresas">
              </div>
              <div class="home-card-content">
                <div class="home-card-header">
                  <h2 class="home-card-title">
                    Empresas organizadas,<br>
                    gestão <span class="highlight-green">simplificada.</span>
                  </h2>
                  <div class="home-card-badge">
                    <img src="${ctx}/assets/icons/empresas.svg" alt="" class="badge-icon">
                    <span>GESTÃO DE EMPRESAS</span>
                  </div>
                </div>
                <div class="home-card-footer">
                  <p class="home-card-desc">
                    Gerencie e registre os dados das empresas cadastradas e mantenha as informações sempre organizadas.
                  </p>
                  <a href="${ctx}/empresa" class="btn-ver-agora">
                    <span>Ver agora</span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M9 18l6-6-6-6" />
                    </svg>
                  </a>
                </div>
              </div>
            </article>

          </section>

          <!-- Palco 3D na lateral direita (visível em desktop, oculto em telas menores) -->
          <aside class="home-right-spacer home-3d-stage-wrapper" aria-hidden="true">
            <div id="home3dCanvasContainer" class="home-3d-canvas-container"></div>
          </aside>

        </div>

      </div>
    </main>
  </div>

  <%@ include file="/WEB-INF/views/fragments/toasts.jspf" %>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js"></script>
  <script type="importmap">
    {
      "imports": {
        "three": "https://unpkg.com/three@0.160.0/build/three.module.js"
      }
    }
  </script>
  <script src="${ctx}/js/painel.js?v=20261030b"></script>
  <script type="module" src="${ctx}/js/home-animacao-3d.js?v=20261030b"></script>
</body>
</html>
