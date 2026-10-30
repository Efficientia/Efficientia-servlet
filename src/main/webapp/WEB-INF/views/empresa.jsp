<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  Tela de Gestão de Empresas (Design System) acoplada ao EmpresaServlet.
  Atributos: empresaModels (lista), empresaModel (edição, opcional), termoBusca.
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="paginaAtiva" value="empresa" />
<c:set var="paginaServlet" value="/empresa" />
<c:set var="tituloPagina" value="Gestão de Empresas" />
<c:set var="editando" value="${not empty empresaModel}" />
<c:set var="total" value="${fn:length(empresaModels)}" />
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
            <span>Empresa</span>
          </nav>
          <div class="category-badge">GESTÃO DE CADASTROS</div>
          <h1 class="page-title">Empresa</h1>
          <p class="page-subtitle">Preencha os dados abaixo para cadastrar uma nova empresa no sistema.</p>
        </section>

        <section class="card-form-section">
          <div class="form-card">
            <div class="card-header-styled">
              <div class="card-title-bar"></div>
              <div>
                <h2 class="card-title" id="formCardTitle">
                  <c:choose>
                    <c:when test="${editando}">Editar Empresa #${empresaModel.id}</c:when>
                    <c:otherwise>Cadastrar Nova Empresa</c:otherwise>
                  </c:choose>
                </h2>
                <p class="card-subtitle">
                  <c:choose>
                    <c:when test="${editando}">Altere os dados necessários e salve.</c:when>
                    <c:otherwise>Preencha os dados abaixo para cadastrar uma nova empresa.</c:otherwise>
                  </c:choose>
                </p>
              </div>
            </div>

            <form id="empresaForm" class="empresa-form cadastro-form"
                  action="${ctx}/empresa" method="post" novalidate
                  data-modo="${editando ? 'edicao' : 'cadastro'}">
              <c:if test="${editando}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id" value="${empresaModel.id}">
                <input type="hidden" name="codigo" value="<c:out value='${empresaModel.codigo}' />">
              </c:if>

              <div class="form-group full-width">
                <label for="razaoSocialInput">Razão Social / Nome <span class="required-star">*</span></label>
                <div class="input-wrapper">
                  <input type="text" id="razaoSocialInput" name="nome" placeholder="Ex: TransBoi"
                         maxlength="100" required
                         value="<c:out value='${empresaModel.nome}' />">
                </div>
              </div>

              <div class="form-group half-width">
                <div class="label-with-hint">
                  <label for="cnpjInput">CNPJ <span class="required-star">*</span></label>
                  <span class="field-hint">14 dígitos (apenas números)</span>
                </div>
                <div class="input-wrapper">
                  <input type="text" id="cnpjInput" name="cnpj" inputmode="numeric"
                         placeholder="Ex: 12345678912345" maxlength="18" required
                         data-mask="digits" data-maxlen="14"
                         value="<c:out value='${empresaModel.cnpj}' />">
                </div>
              </div>

              <div class="form-actions-row">
                <c:choose>
                  <c:when test="${editando}">
                    <a class="btn btn-secondary-outline" id="btnLimpar" href="${ctx}/empresa">Cancelar edição</a>
                  </c:when>
                  <c:otherwise>
                    <button type="button" class="btn btn-secondary-outline" id="btnLimpar">Limpar Formulário</button>
                  </c:otherwise>
                </c:choose>
                <button type="submit" class="btn btn-primary-green" id="btnSubmit">
                  ${editando ? 'Salvar Alterações' : 'Cadastrar Empresa'}
                </button>
              </div>
            </form>
          </div>
        </section>

        <section class="table-section">
          <div class="table-header-controls">
            <h2 class="section-title-white">Empresas Cadastradas</h2>

            <form class="table-search-form" action="${ctx}/empresa" method="get">
              <div class="table-search-group">
                <div class="table-search-input-wrapper">
                  <input type="text" id="tableSearchInput" class="table-search-input" name="busca"
                         placeholder="Buscar por Razão Social ou Código..."
                         value="<c:out value='${termoBusca}' />">
                </div>
                <button type="submit" class="btn-table-search" id="btnBuscarTabela">Buscar</button>
                <c:if test="${not empty termoBusca}">
                  <a class="btn-table-search" href="${ctx}/empresa">Limpar</a>
                </c:if>
                <div class="badge-total-records" id="totalRecordsBadge">
                  ${total} ${total == 1 ? 'registro' : 'registros'}
                </div>
              </div>
            </form>
          </div>

          <div class="table-card">
            <div class="table-responsive">
              <table class="custom-data-table" id="empresasTable">
                <thead>
                  <tr>
                    <th scope="col" class="th-id">ID</th>
                    <th scope="col" class="th-razao">RAZÃO SOCIAL / NOME</th>
                    <th scope="col" class="th-cnpj">CNPJ</th>
                    <th scope="col" class="th-data">CÓDIGO</th>
                    <th scope="col" class="th-acoes">AÇÕES</th>
                  </tr>
                </thead>
                <tbody id="tableBody">
                  <c:forEach items="${empresaModels}" var="emp">
                    <tr>
                      <td class="td-id">#${emp.id}</td>
                      <td class="td-razao"><c:out value="${emp.nome}" /></td>
                      <td class="td-cnpj"><c:out value="${emp.cnpj}" /></td>
                      <td class="td-data"><c:out value="${emp.codigo}" /></td>
                      <td class="td-acoes">
                        <div class="action-buttons-cell">
                          <a class="btn-action-edit" href="${ctx}/empresa?acao=editar&id=${emp.id}" title="Editar empresa">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                            Editar
                          </a>
                          <form class="inline-form" action="${ctx}/empresa" method="post"
                                data-confirm="Deseja realmente excluir a empresa &quot;<c:out value='${emp.nome}' />&quot; (#${emp.id})?">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id" value="${emp.id}">
                            <button type="submit" class="btn-action-delete" title="Excluir empresa">
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
                <p>Nenhuma empresa encontrada.</p>
              </div>
            </c:if>
          </div>
        </section>

      </div>
    </main>
  </div>

  <%@ include file="/WEB-INF/views/fragments/toasts.jspf" %>
  <script src="${ctx}/js/painel.js?v=20261030"></script>
</body>
</html>
