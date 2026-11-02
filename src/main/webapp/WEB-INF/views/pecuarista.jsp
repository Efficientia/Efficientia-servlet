<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  Tela de Gestão de Pecuaristas (Design System) acoplada ao PecuaristaServlet.
  Atributos: pecuaristaModels (lista), pecuaristaModel (edição, opcional), termoBusca.
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="paginaAtiva" value="pecuarista" />
<c:set var="paginaServlet" value="/pecuarista" />
<c:set var="tituloPagina" value="Gestão de Pecuaristas" />
<c:set var="editando" value="${not empty pecuaristaModel}" />
<c:set var="total" value="${fn:length(pecuaristaModels)}" />
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
            <span>Pecuaristas</span>
          </nav>
          <div class="category-badge">GESTÃO DE CADASTROS</div>
          <h1 class="page-title">Pecuarista</h1>
          <p class="page-subtitle">Preencha os dados abaixo para cadastrar um novo pecuarista no sistema.</p>
        </section>

        <!-- Formulário: cadastro (acao padrão) ou edição (acao=atualizar) -->
        <section class="card-form-section">
          <div class="form-card">
            <div class="card-header-styled">
              <div class="card-title-bar"></div>
              <div>
                <h2 class="card-title" id="formCardTitle">
                  <c:choose>
                    <c:when test="${editando}">Editar Pecuarista #${pecuaristaModel.id}</c:when>
                    <c:otherwise>Cadastrar Novo Pecuarista</c:otherwise>
                  </c:choose>
                </h2>
                <p class="card-subtitle">
                  <c:choose>
                    <c:when test="${editando}">Altere os dados necessários. Deixe a senha em branco para mantê-la.</c:when>
                    <c:otherwise>Preencha os dados abaixo para cadastrar um novo pecuarista.</c:otherwise>
                  </c:choose>
                </p>
              </div>
            </div>

            <form id="pecuaristaForm" class="pecuarista-form cadastro-form"
                  action="${ctx}/pecuarista" method="post" novalidate
                  data-modo="${editando ? 'edicao' : 'cadastro'}">
              <c:if test="${editando}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id" value="${pecuaristaModel.id}">
              </c:if>
              <input type="hidden" id="dataNascimentoHidden" name="dataNascimento"
                     value="<c:out value='${pecuaristaModel.dataNascimento}' />">

              <div class="form-group full-width">
                <label for="nomeInput">Nome Completo <span class="required-star">*</span></label>
                <div class="input-wrapper">
                  <input type="text" id="nomeInput" name="nome" maxlength="100"
                         placeholder="Ex: Raíssa Marconato Marques" required
                         value="<c:out value='${pecuaristaModel.nome}' />">
                </div>
              </div>

              <div class="form-row two-cols">
                <div class="form-group">
                  <div class="label-with-hint">
                    <label for="cpfInput">CPF <span class="required-star">*</span></label>
                    <span class="field-hint">11 dígitos (apenas números)</span>
                  </div>
                  <div class="input-wrapper">
                    <input type="text" id="cpfInput" name="cpf" inputmode="numeric"
                           placeholder="Ex: 12345678901" maxlength="14" required
                           data-mask="digits" data-maxlen="11"
                           value="<c:out value='${pecuaristaModel.cpf}' />">
                  </div>
                </div>

                <div class="form-group">
                  <label for="nascimentoInput">Data de Nascimento <span class="required-star">*</span></label>
                  <div class="input-wrapper has-icon-right">
                    <input type="text" id="nascimentoInput" placeholder="DD/MM/AAAA"
                           maxlength="10" inputmode="numeric" required autocomplete="off">
                    <span class="input-icon-right" aria-hidden="true">
                      <img src="${ctx}/assets/icons/calendario.svg" alt="">
                    </span>
                  </div>
                </div>
              </div>

              <div class="form-row two-cols">
                <div class="form-group">
                  <label for="emailInput">E-mail <span class="required-star">*</span></label>
                  <div class="input-wrapper">
                    <input type="email" id="emailInput" name="email" maxlength="254"
                           placeholder="roberto@fazenda.com" required
                           value="<c:out value='${pecuaristaModel.email}' />">
                  </div>
                </div>

                <div class="form-group">
                  <div class="label-with-hint">
                    <label for="telefoneInput">Telefone / Celular <span class="required-star">*</span></label>
                    <span class="field-hint">Até 11 dígitos com DDD</span>
                  </div>
                  <div class="input-wrapper has-icon-right">
                    <input type="tel" id="telefoneInput" name="telefone" inputmode="numeric"
                           placeholder="Ex: 11987654321" maxlength="15" required
                           data-mask="digits" data-maxlen="11"
                           value="<c:out value='${pecuaristaModel.telefone}' />">
                    <span class="input-icon-right" aria-hidden="true">
                      <img src="${ctx}/assets/icons/telefone.svg" alt="">
                    </span>
                  </div>
                </div>
              </div>

              <div class="form-group full-width">
                <label for="senhaInput">Senha de Acesso
                  <c:if test="${not editando}"><span class="required-star">*</span></c:if>
                </label>
                <div class="input-wrapper has-icon-right">
                  <input type="password" id="senhaInput" name="senha" maxlength="128"
                         autocomplete="new-password"
                         placeholder="${editando ? 'Deixe em branco para manter a senha atual' : 'Crie uma senha de acesso'}"
                         ${editando ? '' : 'required'}>
                  <span class="input-icon-right eye-toggle" id="togglePasswordBtn" role="button"
                        aria-label="Mostrar ou ocultar senha" tabindex="0">
                    <img src="${ctx}/assets/icons/olho.svg" alt="">
                  </span>
                </div>
              </div>

              <div class="form-actions-row">
                <c:choose>
                  <c:when test="${editando}">
                    <a class="btn btn-secondary-outline" id="btnLimpar" href="${ctx}/pecuarista">Cancelar edição</a>
                  </c:when>
                  <c:otherwise>
                    <button type="button" class="btn btn-secondary-outline" id="btnLimpar">Limpar Formulário</button>
                  </c:otherwise>
                </c:choose>
                <button type="submit" class="btn btn-primary-green" id="btnSubmit">
                  ${editando ? 'Salvar Alterações' : 'Cadastrar pecuarista'}
                </button>
              </div>
            </form>
          </div>
        </section>

        <!-- Tabela: dados vindos do banco via PecuaristaServlet -->
        <section class="table-section">
          <div class="table-header-controls">
            <h2 class="section-title-white">Pecuaristas Cadastrados</h2>

            <form class="table-search-form" action="${ctx}/pecuarista" method="get">
              <div class="table-search-group">
                <div class="table-search-input-wrapper">
                  <input type="text" id="tableSearchInput" class="table-search-input" name="busca"
                         placeholder="Buscar por Nome, CPF, Email ou Tel..."
                         value="<c:out value='${termoBusca}' />">
                </div>
                <button type="submit" class="btn-table-search" id="btnBuscarTabela">Buscar</button>
                <c:if test="${not empty termoBusca}">
                  <a class="btn-table-search" href="${ctx}/pecuarista">Limpar</a>
                </c:if>
                <div class="badge-total-records" id="totalRecordsBadge">
                  ${total} ${total == 1 ? 'registro' : 'registros'}
                </div>
              </div>
            </form>
          </div>

          <div class="table-card">
            <div class="table-responsive">
              <table class="custom-data-table" id="pecuaristasTable">
                <thead>
                  <tr>
                    <th scope="col" class="th-id">ID</th>
                    <th scope="col" class="th-nome">NOME</th>
                    <th scope="col" class="th-cpf">CPF</th>
                    <th scope="col" class="th-email">E-MAIL</th>
                    <th scope="col" class="th-telefone">TELEFONE</th>
                    <th scope="col" class="th-nasc">DATA DE NASC.</th>
                    <th scope="col" class="th-acoes">AÇÕES</th>
                  </tr>
                </thead>
                <tbody id="tableBody">
                  <c:forEach items="${pecuaristaModels}" var="p">
                    <tr>
                      <td class="td-id">#${p.id}</td>
                      <td class="td-nome"><c:out value="${p.nome}" /></td>
                      <td class="td-cpf"><c:out value="${p.cpf}" /></td>
                      <td class="td-email"><c:out value="${p.email}" /></td>
                      <td class="td-telefone"><c:out value="${p.telefone}" /></td>
                      <td class="td-nasc"><c:out value="${p.dataNascimento}" /></td>
                      <td class="td-acoes">
                        <div class="action-buttons-cell">
                          <a class="btn-action-edit" href="${ctx}/pecuarista?acao=editar&id=${p.id}" title="Editar pecuarista">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                            Editar
                          </a>
                          <form class="inline-form" action="${ctx}/pecuarista" method="post"
                                data-confirm="Deseja realmente excluir o pecuarista &quot;<c:out value='${p.nome}' />&quot; (#${p.id})?">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit" class="btn-action-delete" title="Excluir pecuarista">
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
                <p>Nenhum pecuarista encontrado.</p>
              </div>
            </c:if>
          </div>
        </section>

      </div>
    </main>
  </div>

  <%@ include file="/WEB-INF/views/fragments/toasts.jspf" %>
  <script src="${ctx}/js/painel.js?v=20261101"></script>
</body>
</html>
