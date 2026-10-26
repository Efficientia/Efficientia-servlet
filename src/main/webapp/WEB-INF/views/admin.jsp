<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  Tela de Gestão de Administradores (Design System) acoplada ao AdminServlet.
  Atributos: adminModels (lista), empresas (FK), adminModel (edição, opcional), termoBusca.
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="paginaAtiva" value="admin" />
<c:set var="paginaServlet" value="/admin" />
<c:set var="tituloPagina" value="Gestão de Administradores" />
<c:set var="editando" value="${not empty adminModel}" />
<c:set var="total" value="${fn:length(adminModels)}" />
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

      <div class="content-container">

        <section class="page-title-section">
          <nav class="breadcrumb-trail" aria-label="Breadcrumb">
            <span>Gestão de transporte</span>
            <span class="separator">/</span>
            <span>Administrador</span>
          </nav>
          <div class="category-badge">GESTÃO DE CADASTROS</div>
          <h1 class="page-title">Administrador</h1>
          <p class="page-subtitle">Preencha os dados do administrador do sistema.</p>
        </section>

        <section class="card-form-section">
          <div class="form-card">
            <div class="card-header-styled">
              <div class="card-title-bar"></div>
              <div>
                <h2 class="card-title" id="formCardTitle">
                  <c:choose>
                    <c:when test="${editando}">Editar Administrador #${adminModel.id}</c:when>
                    <c:otherwise>Cadastrar Novo Administrador</c:otherwise>
                  </c:choose>
                </h2>
                <p class="card-subtitle">
                  <c:choose>
                    <c:when test="${editando}">Altere os dados necessários. Deixe a senha em branco para mantê-la.</c:when>
                    <c:otherwise>Preencha os dados abaixo para cadastrar um novo administrador.</c:otherwise>
                  </c:choose>
                </p>
              </div>
            </div>

            <form id="adminForm" class="admin-form cadastro-form"
                  action="${ctx}/admin" method="post" novalidate
                  data-modo="${editando ? 'edicao' : 'cadastro'}">
              <c:if test="${editando}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id" value="${adminModel.id}">
              </c:if>

              <div class="form-group full-width">
                <label for="nomeInput">Nome Completo <span class="required-star">*</span></label>
                <div class="input-wrapper">
                  <input type="text" id="nomeInput" name="nome" maxlength="100"
                         placeholder="Ex: Bruno Ferrão da Silva" required
                         value="<c:out value='${adminModel.nome}' />">
                </div>
              </div>

              <div class="form-row two-cols">
                <div class="form-group">
                  <label for="emailInput">E-mail <span class="required-star">*</span></label>
                  <div class="input-wrapper">
                    <input type="email" id="emailInput" name="email" maxlength="254"
                           placeholder="robert@fazenda.com" required
                           value="<c:out value='${adminModel.email}' />">
                  </div>
                </div>

                <div class="form-group">
                  <label for="senhaInput">Senha de acesso
                    <c:if test="${not editando}"><span class="required-star">*</span></c:if>
                  </label>
                  <div class="input-wrapper has-icon-right">
                    <input type="password" id="senhaInput" name="senha" maxlength="128"
                           autocomplete="new-password"
                           placeholder="${editando ? 'Em branco mantém a atual' : 'Crie uma senha de acesso'}"
                           ${editando ? '' : 'required'}>
                    <span class="input-icon-right eye-toggle" id="togglePasswordBtn" role="button"
                          aria-label="Mostrar ou ocultar senha" tabindex="0">
                      <img src="${ctx}/assets/icons/olho.svg" alt="">
                    </span>
                  </div>
                </div>
              </div>

              <div class="form-group full-width">
                <label for="empresaInput">Empresa Vinculada</label>
                <div class="input-wrapper">
                  <select id="empresaInput" name="idEmpresa">
                    <option value="">Sem vínculo com alguma empresa</option>
                    <c:forEach items="${empresas}" var="emp">
                      <option value="${emp.id}" ${editando and adminModel.empresa.id == emp.id ? 'selected' : ''}>
                        <c:out value="${emp.nome}" />
                      </option>
                    </c:forEach>
                  </select>
                </div>
              </div>

              <div class="form-actions-row">
                <c:choose>
                  <c:when test="${editando}">
                    <a class="btn btn-secondary-outline" id="btnLimpar" href="${ctx}/admin">Cancelar edição</a>
                  </c:when>
                  <c:otherwise>
                    <button type="button" class="btn btn-secondary-outline" id="btnLimpar">Limpar Formulário</button>
                  </c:otherwise>
                </c:choose>
                <button type="submit" class="btn btn-primary-green" id="btnSubmit">
                  ${editando ? 'Salvar Alterações' : 'Cadastrar Admin'}
                </button>
              </div>
            </form>
          </div>
        </section>

        <section class="table-section">
          <div class="table-header-controls">
            <h2 class="section-title-white">Administradores Cadastrados</h2>

            <form class="table-search-form" action="${ctx}/admin" method="get">
              <div class="table-search-group">
                <div class="table-search-input-wrapper">
                  <input type="text" id="tableSearchInput" class="table-search-input" name="busca"
                         placeholder="Buscar por Nome ou E-mail..."
                         value="<c:out value='${termoBusca}' />">
                </div>
                <button type="submit" class="btn-table-search" id="btnBuscarTabela">Buscar</button>
                <c:if test="${not empty termoBusca}">
                  <a class="btn-table-search" href="${ctx}/admin">Limpar</a>
                </c:if>
                <div class="badge-total-records" id="totalRecordsBadge">
                  ${total} ${total == 1 ? 'registro' : 'registros'}
                </div>
              </div>
            </form>
          </div>

          <div class="table-card">
            <div class="table-responsive">
              <table class="custom-data-table" id="adminsTable">
                <thead>
                  <tr>
                    <th scope="col" class="th-id">ID</th>
                    <th scope="col" class="th-nome">NOME</th>
                    <th scope="col" class="th-email">E-MAIL</th>
                    <th scope="col" class="th-empresa">EMPRESA</th>
                    <th scope="col" class="th-acoes">AÇÕES</th>
                  </tr>
                </thead>
                <tbody id="tableBody">
                  <c:forEach items="${adminModels}" var="a">
                    <tr>
                      <td class="td-id">#${a.id}</td>
                      <td class="td-nome"><c:out value="${a.nome}" /></td>
                      <td class="td-email"><c:out value="${a.email}" /></td>
                      <td class="td-empresa"><c:out value="${a.empresa.nome}" default="Sem vínculo" /></td>
                      <td class="td-acoes">
                        <div class="action-buttons-cell">
                          <a class="btn-action-edit" href="${ctx}/admin?acao=editar&id=${a.id}" title="Editar administrador">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                            Editar
                          </a>
                          <form class="inline-form" action="${ctx}/admin" method="post"
                                data-confirm="Deseja realmente excluir o administrador &quot;<c:out value='${a.nome}' />&quot; (#${a.id})?">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id" value="${a.id}">
                            <button type="submit" class="btn-action-delete" title="Excluir administrador">
                              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                              Excluir
                            </button>
                          </form>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
            <c:if test="${total == 0}">
              <div class="table-empty-state" id="tableEmptyState">
                <p>Nenhum administrador encontrado.</p>
              </div>
            </c:if>
          </div>
        </section>

      </div>
    </main>
  </div>

  <%@ include file="/WEB-INF/views/fragments/toasts.jspf" %>
  <script src="${ctx}/js/painel.js"></script>
</body>
</html>
