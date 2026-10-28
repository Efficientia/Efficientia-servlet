<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  Tela de Gestão de Caminhões (Design System) acoplada ao CaminhaoServlet.
  Atributos: caminhaoModels (lista), empresaModels (FK), caminhaoModel (edição, opcional), termoBusca.
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="paginaAtiva" value="caminhao" />
<c:set var="paginaServlet" value="/caminhao" />
<c:set var="tituloPagina" value="Gestão de Caminhões" />
<c:set var="editando" value="${not empty caminhaoModel}" />
<c:set var="total" value="${fn:length(caminhaoModels)}" />
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
            <span>Caminhão</span>
          </nav>
          <div class="category-badge">GESTÃO DE CADASTROS</div>
          <h1 class="page-title">Caminhão</h1>
          <p class="page-subtitle">Preencha os dados abaixo para cadastrar um caminhão.</p>
        </section>

        <section class="card-form-section">
          <div class="form-card">
            <div class="card-header-styled">
              <div class="card-title-bar"></div>
              <div>
                <h2 class="card-title" id="formCardTitle">
                  <c:choose>
                    <c:when test="${editando}">Editar Caminhão #${caminhaoModel.id}</c:when>
                    <c:otherwise>Cadastrar Novo Caminhão</c:otherwise>
                  </c:choose>
                </h2>
                <p class="card-subtitle">
                  <c:choose>
                    <c:when test="${editando}">Altere os dados necessários e salve.</c:when>
                    <c:otherwise>Preencha os dados abaixo para cadastrar um novo caminhão.</c:otherwise>
                  </c:choose>
                </p>
              </div>
            </div>

            <form id="caminhaoForm" class="caminhao-form cadastro-form"
                  action="${ctx}/caminhao" method="post" novalidate
                  data-modo="${editando ? 'edicao' : 'cadastro'}">
              <c:if test="${editando}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id" value="${caminhaoModel.id}">
              </c:if>

              <div class="form-row two-cols">
                <div class="form-group">
                  <div class="label-with-hint">
                    <label for="placaCavaloInput">Placa Cavalo <span class="required-star">*</span></label>
                    <span class="field-hint">Ex: ABC1D23 ou ABC-1234</span>
                  </div>
                  <div class="input-wrapper">
                    <input type="text" id="placaCavaloInput" name="placaCavalo" placeholder="Ex: ABC1D23"
                           maxlength="8" required data-mask="placa" style="text-transform: uppercase;"
                           value="<c:out value='${caminhaoModel.placaCavalo}' />">
                  </div>
                </div>

                <div class="form-group">
                  <div class="label-with-hint">
                    <label for="placaCarretaInput">Placa da Carreta <span class="required-star">*</span></label>
                    <span class="field-hint">Ex: XYZ9W87 ou XYZ-9876</span>
                  </div>
                  <div class="input-wrapper">
                    <input type="text" id="placaCarretaInput" name="placaCarreta" placeholder="Ex: XYZ9W87"
                           maxlength="8" required data-mask="placa" style="text-transform: uppercase;"
                           value="<c:out value='${caminhaoModel.placaCarreta}' />">
                  </div>
                </div>
              </div>

              <div class="form-group full-width">
                <div class="label-with-hint">
                  <label for="empresaInput">Empresa Proprietária <span class="required-star">*</span></label>
                  <span class="field-hint">Empresa à qual o caminhão pertence</span>
                </div>
                <div class="input-wrapper">
                  <select id="empresaInput" name="idEmpresa" required>
                    <option value="" disabled ${editando ? '' : 'selected'}>Selecione uma empresa...</option>
                    <c:forEach items="${empresaModels}" var="emp">
                      <option value="${emp.id}" ${editando and caminhaoModel.empresa.id == emp.id ? 'selected' : ''}>
                        <c:out value="${emp.nome}" /> <c:out value="${emp.codigo}" />
                      </option>
                    </c:forEach>
                  </select>
                </div>
              </div>

              <div class="form-group full-width">
                <div class="label-with-hint">
                  <label for="capacidadeInput">Capacidade Máxima <span class="required-star">*</span></label>
                  <span class="field-hint">Carga suportada em kg ou unidades</span>
                </div>
                <div class="input-wrapper">
                  <input type="number" id="capacidadeInput" name="capacidadeMaxima" placeholder="Ex: 45000"
                         min="1" required
                         value="<c:out value='${caminhaoModel.capacidadeMaxima}' />">
                </div>
              </div>

              <div class="form-actions-row">
                <c:choose>
                  <c:when test="${editando}">
                    <a class="btn btn-secondary-outline" id="btnLimpar" href="${ctx}/caminhao">Cancelar edição</a>
                  </c:when>
                  <c:otherwise>
                    <button type="button" class="btn btn-secondary-outline" id="btnLimpar">Limpar Formulário</button>
                  </c:otherwise>
                </c:choose>
                <button type="submit" class="btn btn-primary-green" id="btnSubmit">
                  ${editando ? 'Salvar Alterações' : 'Cadastrar caminhão'}
                </button>
              </div>
            </form>
          </div>
        </section>

        <section class="table-section">
          <div class="table-header-controls">
            <h2 class="section-title-white">Caminhões Cadastrados</h2>

            <form class="table-search-form" action="${ctx}/caminhao" method="get">
              <div class="table-search-group">
                <div class="table-search-input-wrapper">
                  <input type="text" id="tableSearchInput" class="table-search-input" name="busca"
                         placeholder="Buscar por Placa..."
                         value="<c:out value='${termoBusca}' />">
                </div>
                <button type="submit" class="btn-table-search" id="btnBuscarTabela">Buscar</button>
                <c:if test="${not empty termoBusca}">
                  <a class="btn-table-search" href="${ctx}/caminhao">Limpar</a>
                </c:if>
                <div class="badge-total-records" id="totalRecordsBadge">
                  ${total} ${total == 1 ? 'registro' : 'registros'}
                </div>
              </div>
            </form>
          </div>

          <div class="table-card">
            <div class="table-responsive">
              <table class="custom-data-table" id="caminhoesTable">
                <thead>
                  <tr>
                    <th scope="col" class="th-id">ID</th>
                    <th scope="col" class="th-empresa">EMPRESA</th>
                    <th scope="col" class="th-cavalo">PLACA CAVALO</th>
                    <th scope="col" class="th-carreta">PLACA CARRETA</th>
                    <th scope="col" class="th-capacidade">CAPACIDADE MÁXIMA</th>
                    <th scope="col" class="th-acoes">AÇÕES</th>
                  </tr>
                </thead>
                <tbody id="tableBody">
                  <c:forEach items="${caminhaoModels}" var="c">
                    <tr>
                      <td class="td-id">#${c.id}</td>
                      <td class="td-empresa"><c:out value="${c.empresa.nome}" default="Sem empresa" /></td>
                      <td class="td-cavalo"><c:out value="${c.placaCavalo}" /></td>
                      <td class="td-carreta"><c:out value="${c.placaCarreta}" /></td>
                      <td class="td-capacidade"><c:out value="${c.capacidadeMaxima}" /></td>
                      <td class="td-acoes">
                        <div class="action-buttons-cell">
                          <a class="btn-action-edit" href="${ctx}/caminhao?acao=editar&id=${c.id}" title="Editar caminhão">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                            Editar
                          </a>
                          <form class="inline-form" action="${ctx}/caminhao" method="post"
                                data-confirm="Deseja realmente excluir o caminhão &quot;<c:out value='${c.placaCavalo}' />&quot; (#${c.id})?">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id" value="${c.id}">
                            <button type="submit" class="btn-action-delete" title="Excluir caminhão">
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
                <p>Nenhum caminhão encontrado.</p>
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
