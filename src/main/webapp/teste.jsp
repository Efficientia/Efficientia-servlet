<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.efficientia.efficientia.DAO.impl.PecuaristaDAO" %>
<%@ page import="com.efficientia.efficientia.DAO.impl.MotoristaDAO" %>
<%@ page import="com.efficientia.efficientia.DAO.impl.CaminhaoDAO" %>
<%@ page import="com.efficientia.efficientia.DAO.impl.EmpresaDAO" %>
<%@ page import="com.efficientia.efficientia.DAO.impl.TrajetoDAO" %>
<%@ page import="com.efficientia.efficientia.model.PecuaristaModel" %>
<%@ page import="com.efficientia.efficientia.model.MotoristaModel" %>
<%@ page import="com.efficientia.efficientia.model.CaminhaoModel" %>
<%@ page import="com.efficientia.efficientia.model.EmpresaModel" %>
<%@ page import="com.efficientia.efficientia.model.TrajetoModel" %>
<%@ page import="com.efficientia.efficientia.model.StatusTrajeto" %>

<%
    // Instanciação dos DAOs e carregamento das listas de dados
    PecuaristaDAO pecuaristaDAO = new PecuaristaDAO();
    MotoristaDAO motoristaDAO = new MotoristaDAO();
    CaminhaoDAO caminhaoDAO = new CaminhaoDAO();
    EmpresaDAO empresaDAO = new EmpresaDAO();
    TrajetoDAO trajetoDAO = new TrajetoDAO();

    List<PecuaristaModel> pecuaristas = null;
    List<MotoristaModel> motoristas = null;
    List<CaminhaoModel> caminhoes = null;
    List<EmpresaModel> empresas = null;
    List<TrajetoModel> trajetos = null;

    try { pecuaristas = pecuaristaDAO.listar(); } catch (Exception e) { pecuaristas = new java.util.ArrayList<>(); }
    try { motoristas = motoristaDAO.listar(); } catch (Exception e) { motoristas = new java.util.ArrayList<>(); }
    try { caminhoes = caminhaoDAO.listar(); } catch (Exception e) { caminhoes = new java.util.ArrayList<>(); }
    try { empresas = empresaDAO.listar(); } catch (Exception e) { empresas = new java.util.ArrayList<>(); }
    try { trajetos = trajetoDAO.listar(); } catch (Exception e) { trajetos = new java.util.ArrayList<>(); }

    request.setAttribute("pecuaristas", pecuaristas);
    request.setAttribute("motoristas", motoristas);
    request.setAttribute("caminhoes", caminhoes);
    request.setAttribute("empresas", empresas);
    request.setAttribute("trajetos", trajetos);
    request.setAttribute("statusTrajetos", StatusTrajeto.values());

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter dtfInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    request.setAttribute("dtfDateTime", dtfDateTime);
    request.setAttribute("dtfInput", dtfInput);
%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Painel CRUD de Servlets | Efficientia</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">

    <!-- CSS Unificado -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teste.css">
</head>
<body>

<div class="container">
    <!-- Cabeçalho Principal -->
    <header class="header">
        <div class="header-title-box">
            <h1>Efficientia <span>| Gestão Integrada</span></h1>
            <p>Painel Unificado de Testes e Gerenciamento CRUD dos Servlets</p>
        </div>
        <a href="${pageContext.request.contextPath}/" class="btn-voltar">
            &larr; Página Principal
        </a>
    </header>

    <!-- Navegação Principal entre Servlets -->
    <nav class="servlet-nav" aria-label="Selecione o Servlet">
        <button type="button" class="servlet-nav-btn active" id="navBtn-pecuarista" onclick="switchServlet('pecuarista')">
            <div class="servlet-nav-info">
                <span class="servlet-nav-icon">🐄</span>
                <div>
                    <span class="servlet-nav-title">Pecuarista</span>
                    <span class="servlet-nav-sub">/pecuarista</span>
                </div>
            </div>
            <span class="servlet-count-badge">${not empty pecuaristas ? pecuaristas.size() : 0}</span>
        </button>

        <button type="button" class="servlet-nav-btn" id="navBtn-motorista" onclick="switchServlet('motorista')">
            <div class="servlet-nav-info">
                <span class="servlet-nav-icon">🚚</span>
                <div>
                    <span class="servlet-nav-title">Motorista</span>
                    <span class="servlet-nav-sub">/motorista</span>
                </div>
            </div>
            <span class="servlet-count-badge">${not empty motoristas ? motoristas.size() : 0}</span>
        </button>

        <button type="button" class="servlet-nav-btn" id="navBtn-caminhao" onclick="switchServlet('caminhao')">
            <div class="servlet-nav-info">
                <span class="servlet-nav-icon">🛻</span>
                <div>
                    <span class="servlet-nav-title">Caminhão</span>
                    <span class="servlet-nav-sub">/caminhao</span>
                </div>
            </div>
            <span class="servlet-count-badge">${not empty caminhoes ? caminhoes.size() : 0}</span>
        </button>

        <button type="button" class="servlet-nav-btn" id="navBtn-trajeto" onclick="switchServlet('trajeto')">
            <div class="servlet-nav-info">
                <span class="servlet-nav-icon">🗺️</span>
                <div>
                    <span class="servlet-nav-title">Trajeto</span>
                    <span class="servlet-nav-sub">/trajeto</span>
                </div>
            </div>
            <span class="servlet-count-badge">${not empty trajetos ? trajetos.size() : 0}</span>
        </button>
    </nav>


    <!-- ==================================================================== -->
    <!-- SEÇÃO 1: PECUARISTA SERVLET -->
    <!-- ==================================================================== -->
    <section id="section-pecuarista" class="servlet-section active">
        <!-- Cabeçalho de Abas Internas -->
        <div class="tabs-header">
            <button type="button" id="tabBtnCadastrar-pecuarista" class="tab-button active" onclick="switchTab('pecuarista', 'cadastrar')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                Cadastrar Novo Pecuarista
            </button>
            <button type="button" id="tabBtnExcluir-pecuarista" class="tab-button tab-danger" onclick="switchTab('pecuarista', 'excluir')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                Excluir Pecuarista
            </button>
        </div>

        <div class="card">
            <!-- ABA 1: CADASTRO PECUARISTA -->
            <div id="tabCadastrar-pecuarista" class="tab-content active">
                <div class="card-header">
                    <h2 class="title-green">Cadastrar Novo Pecuarista</h2>
                    <p>Preencha os dados abaixo para cadastrar um novo pecuarista via PecuaristaServlet.</p>
                </div>

                <form action="${pageContext.request.contextPath}/pecuarista" method="post" id="formCadastrarPecuarista" onsubmit="return handleFormSubmit(event, 'Pecuarista cadastrado com sucesso!')">
                    <input type="hidden" name="acao" value="cadastrar">

                    <div class="form-grid">
                        <div class="form-group form-group-full">
                            <label for="pec-nome">Nome Completo *</label>
                            <input type="text" id="pec-nome" name="nome" placeholder="Ex: Roberto Carlos de Oliveira" required>
                        </div>

                        <div class="form-group">
                            <label for="pec-cpf">
                                CPF *
                                <span class="helper-text">11 dígitos (apenas números)</span>
                            </label>
                            <input type="text" id="pec-cpf" name="cpf" maxlength="11" placeholder="Ex: 12345678901" pattern="[0-9]{11}" required title="O CPF deve conter exatamente 11 números">
                        </div>

                        <div class="form-group">
                            <label for="pec-dataNascimento">Data de Nascimento *</label>
                            <input type="date" id="pec-dataNascimento" name="dataNascimento" required>
                        </div>

                        <div class="form-group">
                            <label for="pec-email">E-mail *</label>
                            <input type="email" id="pec-email" name="email" placeholder="roberto@fazenda.com" required>
                        </div>

                        <div class="form-group">
                            <label for="pec-telefone">
                                Telefone / Celular
                                <span class="helper-text">Até 11 dígitos com DDD</span>
                            </label>
                            <input type="tel" id="pec-telefone" name="telefone" maxlength="11" placeholder="Ex: 11987654321" pattern="[0-9]{10,11}">
                        </div>

                        <div class="form-group">
                            <label for="pec-senha">Senha de Acesso *</label>
                            <input type="password" id="pec-senha" name="senha" placeholder="Crie uma senha de acesso" required>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="reset" class="btn btn-secondary">Limpar Formulário</button>
                        <button type="submit" class="btn btn-primary">Cadastrar Pecuarista</button>
                    </div>
                </form>
            </div>

            <!-- ABA 2: EXCLUSÃO PECUARISTA -->
            <div id="tabExcluir-pecuarista" class="tab-content">
                <div class="card-header">
                    <h2 class="title-red">Excluir Pecuarista</h2>
                    <p>Selecione um pecuarista cadastrado para realizar a exclusão segura no banco de dados.</p>
                </div>

                <c:choose>
                    <c:when test="${empty pecuaristas}">
                        <div class="empty-state">
                            <p>Não há pecuaristas cadastrados disponíveis para exclusão.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form id="formAbaExcluirPecuarista" onsubmit="return handleSelectExcluirSubmit(event, 'selectExcluirPecuarista', '/pecuarista')">
                            <div class="form-grid">
                                <div class="form-group form-group-full">
                                    <label for="selectExcluirPecuarista">Selecione o Pecuarista para Excluir *</label>
                                    <select id="selectExcluirPecuarista" name="id" class="danger-input" required>
                                        <option value="" disabled selected>Escolha um pecuarista...</option>
                                        <c:forEach var="pec" items="${pecuaristas}">
                                            <option value="${pec.id}" data-nome="${pec.nome}">
                                                ID #${pec.id} - ${pec.nome} (CPF: ${pec.cpf})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-actions">
                                <button type="button" class="btn btn-secondary" onclick="switchTab('pecuarista', 'cadastrar')">Cancelar</button>
                                <button type="submit" class="btn btn-danger">
                                    <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                    </svg>
                                    Excluir Pecuarista
                                </button>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- LISTAGEM PECUARISTAS -->
        <div class="list-section-header">
            <h2>Pecuaristas Cadastrados</h2>
            <span class="badge-count">${not empty pecuaristas ? pecuaristas.size() : 0} registros</span>
        </div>

        <div class="table-responsive">
            <c:choose>
                <c:when test="${empty pecuaristas}">
                    <div class="empty-state">
                        <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        <p>Nenhum pecuarista cadastrado até o momento.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nome</th>
                                <th>CPF</th>
                                <th>E-mail</th>
                                <th>Telefone</th>
                                <th>Data Nasc.</th>
                                <th>Assinatura</th>
                                <th style="text-align: center;">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="pec" items="${pecuaristas}">
                                <tr>
                                    <td><strong>#${pec.id}</strong></td>
                                    <td><c:out value="${pec.nome}" default="-" /></td>
                                    <td><c:out value="${pec.cpf}" default="-" /></td>
                                    <td><c:out value="${pec.email}" default="-" /></td>
                                    <td><c:out value="${pec.telefone}" default="-" /></td>
                                    <td>${pec.dataNascimento != null ? pec.dataNascimento : '-'}</td>
                                    <td><span class="text-code"><c:out value="${pec.assinatura}" default="-" /></span></td>
                                    <td style="text-align: center;">
                                        <div style="display: flex; align-items: center; justify-content: center; gap: 0.4rem;">
                                            <button type="button" class="btn-sm-edit" onclick="abrirModalEdicaoPecuarista(${pec.id}, '<c:out value="${pec.nome}" />', '<c:out value="${pec.cpf}" />', '${pec.dataNascimento}', '<c:out value="${pec.email}" />', '<c:out value="${pec.telefone}" />', '<c:out value="${pec.assinatura}" />')">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                                </svg>
                                                Editar
                                            </button>
                                            <button type="button" class="btn-sm-danger" onclick="abrirConfirmacaoExcluir('/pecuarista', ${pec.id}, '<c:out value="${pec.nome}" /> (CPF: <c:out value="${pec.cpf}" />)')">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                                Excluir
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </section>


    <!-- ==================================================================== -->
    <!-- SEÇÃO 2: MOTORISTA SERVLET -->
    <!-- ==================================================================== -->
    <section id="section-motorista" class="servlet-section">
        <!-- Cabeçalho de Abas Internas -->
        <div class="tabs-header">
            <button type="button" id="tabBtnCadastrar-motorista" class="tab-button active" onclick="switchTab('motorista', 'cadastrar')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                Cadastrar Novo Motorista
            </button>
            <button type="button" id="tabBtnExcluir-motorista" class="tab-button tab-danger" onclick="switchTab('motorista', 'excluir')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                Excluir Motorista
            </button>
        </div>

        <div class="card">
            <!-- ABA 1: CADASTRO MOTORISTA -->
            <div id="tabCadastrar-motorista" class="tab-content active">
                <div class="card-header">
                    <h2 class="title-green">Cadastrar Novo Motorista</h2>
                    <p>Preencha os dados abaixo para cadastrar um novo motorista via MotoristaServlet.</p>
                </div>

                <form action="${pageContext.request.contextPath}/motorista" method="post" id="formCadastrarMotorista" onsubmit="return handleFormSubmit(event, 'Motorista cadastrado com sucesso!')">
                    <input type="hidden" name="acao" value="cadastrar">

                    <div class="form-grid">
                        <div class="form-group">
                            <label for="mot-nome">Nome Completo *</label>
                            <input type="text" id="mot-nome" name="nome" placeholder="Ex: Marcos Silveira Souza" required>
                        </div>

                        <div class="form-group">
                            <label for="mot-idEmpresa">
                                Empresa Vinculada
                                <span class="helper-text">Selecione a transportadora</span>
                            </label>
                            <select id="mot-idEmpresa" name="idEmpresa">
                                <option value="">Sem empresa associada</option>
                                <c:forEach var="emp" items="${empresas}">
                                    <option value="${emp.id}">#${emp.id} - ${emp.nome} (CNPJ: ${emp.cnpj})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="mot-dataNascimento">Data de Nascimento *</label>
                            <input type="date" id="mot-dataNascimento" name="dataNascimento" required>
                        </div>

                        <div class="form-group">
                            <label for="mot-email">E-mail *</label>
                            <input type="email" id="mot-email" name="email" placeholder="marcos@transportes.com" required>
                        </div>

                        <div class="form-group">
                            <label for="mot-telefone">
                                Telefone / Celular
                                <span class="helper-text">Até 11 dígitos com DDD</span>
                            </label>
                            <input type="tel" id="mot-telefone" name="telefone" maxlength="11" placeholder="Ex: 11987654321" pattern="[0-9]{10,11}">
                        </div>

                        <div class="form-group form-group-full">
                            <label for="mot-senha">Senha de Acesso *</label>
                            <input type="password" id="mot-senha" name="senha" placeholder="Crie uma senha de acesso" required>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="reset" class="btn btn-secondary">Limpar Formulário</button>
                        <button type="submit" class="btn btn-primary">Cadastrar Motorista</button>
                    </div>
                </form>
            </div>

            <!-- ABA 2: EXCLUSÃO MOTORISTA -->
            <div id="tabExcluir-motorista" class="tab-content">
                <div class="card-header">
                    <h2 class="title-red">Excluir Motorista</h2>
                    <p>Selecione um motorista cadastrado para realizar a exclusão no banco de dados.</p>
                </div>

                <c:choose>
                    <c:when test="${empty motoristas}">
                        <div class="empty-state">
                            <p>Não há motoristas cadastrados disponíveis para exclusão.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form id="formAbaExcluirMotorista" onsubmit="return handleSelectExcluirSubmit(event, 'selectExcluirMotorista', '/motorista')">
                            <div class="form-grid">
                                <div class="form-group form-group-full">
                                    <label for="selectExcluirMotorista">Selecione o Motorista para Excluir *</label>
                                    <select id="selectExcluirMotorista" name="id" class="danger-input" required>
                                        <option value="" disabled selected>Escolha um motorista...</option>
                                        <c:forEach var="mot" items="${motoristas}">
                                            <option value="${mot.id}" data-nome="${mot.nome}">
                                                ID #${mot.id} - ${mot.nome} (E-mail: ${mot.email})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-actions">
                                <button type="button" class="btn btn-secondary" onclick="switchTab('motorista', 'cadastrar')">Cancelar</button>
                                <button type="submit" class="btn btn-danger">
                                    <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                    </svg>
                                    Excluir Motorista
                                </button>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- LISTAGEM MOTORISTAS -->
        <div class="list-section-header">
            <h2>Motoristas Cadastrados</h2>
            <span class="badge-count">${not empty motoristas ? motoristas.size() : 0} registros</span>
        </div>

        <div class="table-responsive">
            <c:choose>
                <c:when test="${empty motoristas}">
                    <div class="empty-state">
                        <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        <p>Nenhum motorista cadastrado até o momento.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nome</th>
                                <th>Empresa</th>
                                <th>E-mail</th>
                                <th>Telefone</th>
                                <th>Data Nasc.</th>
                                <th>Assinatura</th>
                                <th style="text-align: center;">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="mot" items="${motoristas}">
                                <tr>
                                    <td><strong>#${mot.id}</strong></td>
                                    <td><c:out value="${mot.nome}" default="-" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${mot.empresaModel != null}">
                                                <span class="text-code"><c:out value="${mot.empresaModel.nome}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: 0.85rem;">Avulso</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${mot.email}" default="-" /></td>
                                    <td><c:out value="${mot.telefone}" default="-" /></td>
                                    <td>${mot.dataNascimento != null ? mot.dataNascimento : '-'}</td>
                                    <td><span class="text-code"><c:out value="${mot.assinatura}" default="-" /></span></td>
                                    <td style="text-align: center;">
                                        <div style="display: flex; align-items: center; justify-content: center; gap: 0.4rem;">
                                            <button type="button" class="btn-sm-edit" onclick="abrirModalEdicaoMotorista(${mot.id}, '${mot.empresaModel != null ? mot.empresaModel.id : ''}', '<c:out value="${mot.nome}" />', '${mot.dataNascimento}', '<c:out value="${mot.email}" />', '<c:out value="${mot.telefone}" />', '<c:out value="${mot.assinatura}" />')">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                                </svg>
                                                Editar
                                            </button>
                                            <button type="button" class="btn-sm-danger" onclick="abrirConfirmacaoExcluir('/motorista', ${mot.id}, '<c:out value="${mot.nome}" /> (E-mail: <c:out value="${mot.email}" />)')">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                                Excluir
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </section>


    <!-- ==================================================================== -->
    <!-- SEÇÃO 3: CAMINHÃO SERVLET -->
    <!-- ==================================================================== -->
    <section id="section-caminhao" class="servlet-section">
        <!-- Cabeçalho de Abas Internas -->
        <div class="tabs-header">
            <button type="button" id="tabBtnCadastrar-caminhao" class="tab-button active" onclick="switchTab('caminhao', 'cadastrar')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                Cadastrar Novo Caminhão
            </button>
            <button type="button" id="tabBtnExcluir-caminhao" class="tab-button tab-danger" onclick="switchTab('caminhao', 'excluir')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                Excluir Caminhão
            </button>
        </div>

        <div class="card">
            <!-- ABA 1: CADASTRO CAMINHÃO -->
            <div id="tabCadastrar-caminhao" class="tab-content active">
                <div class="card-header">
                    <h2 class="title-green">Cadastrar Novo Caminhão</h2>
                    <p>Preencha os dados abaixo para cadastrar um caminhão via CaminhaoServlet.</p>
                </div>

                <form action="${pageContext.request.contextPath}/caminhao" method="post" id="formCadastrarCaminhao" onsubmit="return handleFormSubmit(event, 'Caminhão cadastrado com sucesso!')">
                    <input type="hidden" name="acao" value="cadastrar">

                    <div class="form-grid">
                        <div class="form-group">
                            <label for="cam-placaCavalo">
                                Placa do Cavalo *
                                <span class="helper-text">Ex: ABC1D23 ou ABC-1234</span>
                            </label>
                            <input type="text" id="cam-placaCavalo" name="placaCavalo" class="input-plate" maxlength="8" placeholder="Ex: ABC1D23" required>
                        </div>

                        <div class="form-group">
                            <label for="cam-placaCarreta">
                                Placa da Carreta *
                                <span class="helper-text">Ex: XYZ9W87 ou XYZ-9876</span>
                            </label>
                            <input type="text" id="cam-placaCarreta" name="placaCarreta" class="input-plate" maxlength="8" placeholder="Ex: XYZ9W87" required>
                        </div>

                        <div class="form-group form-group-full">
                            <label for="cam-capacidadeMaxima">
                                Capacidade Máxima *
                                <span class="helper-text">Carga suportada em kg ou unidades</span>
                            </label>
                            <input type="number" id="cam-capacidadeMaxima" name="capacidadeMaxima" min="1" placeholder="Ex: 45000" required>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="reset" class="btn btn-secondary">Limpar Formulário</button>
                        <button type="submit" class="btn btn-primary">Cadastrar Caminhão</button>
                    </div>
                </form>
            </div>

            <!-- ABA 2: EXCLUSÃO CAMINHÃO -->
            <div id="tabExcluir-caminhao" class="tab-content">
                <div class="card-header">
                    <h2 class="title-red">Excluir Caminhão</h2>
                    <p>Selecione um caminhão cadastrado para realizar a exclusão no banco de dados.</p>
                </div>

                <c:choose>
                    <c:when test="${empty caminhoes}">
                        <div class="empty-state">
                            <p>Não há caminhões cadastrados disponíveis para exclusão.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form id="formAbaExcluirCaminhao" onsubmit="return handleSelectExcluirSubmit(event, 'selectExcluirCaminhao', '/caminhao')">
                            <div class="form-grid">
                                <div class="form-group form-group-full">
                                    <label for="selectExcluirCaminhao">Selecione o Caminhão para Excluir *</label>
                                    <select id="selectExcluirCaminhao" name="id" class="danger-input" required>
                                        <option value="" disabled selected>Escolha um caminhão...</option>
                                        <c:forEach var="cam" items="${caminhoes}">
                                            <option value="${cam.id}" data-nome="Cavalo: ${cam.placaCavalo} | Carreta: ${cam.placaCarreta}">
                                                ID #${cam.id} - Cavalo: ${cam.placaCavalo} | Carreta: ${cam.placaCarreta} (Capacidade: ${cam.capacidadeMaxima})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-actions">
                                <button type="button" class="btn btn-secondary" onclick="switchTab('caminhao', 'cadastrar')">Cancelar</button>
                                <button type="submit" class="btn btn-danger">
                                    <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                    </svg>
                                    Excluir Caminhão
                                </button>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- LISTAGEM CAMINHÕES -->
        <div class="list-section-header">
            <h2>Caminhões Cadastrados</h2>
            <span class="badge-count">${not empty caminhoes ? caminhoes.size() : 0} registros</span>
        </div>

        <div class="table-responsive">
            <c:choose>
                <c:when test="${empty caminhoes}">
                    <div class="empty-state">
                        <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        <p>Nenhum caminhão cadastrado até o momento.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Placa Cavalo</th>
                                <th>Placa Carreta</th>
                                <th>Capacidade Máxima</th>
                                <th style="text-align: center;">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cam" items="${caminhoes}">
                                <tr>
                                    <td><strong>#${cam.id}</strong></td>
                                    <td><span class="text-code"><c:out value="${cam.placaCavalo}" /></span></td>
                                    <td><span class="text-code"><c:out value="${cam.placaCarreta}" /></span></td>
                                    <td><strong><c:out value="${cam.capacidadeMaxima}" /></strong></td>
                                    <td style="text-align: center;">
                                        <div style="display: flex; align-items: center; justify-content: center; gap: 0.4rem;">
                                            <button type="button" class="btn-sm-edit" onclick="abrirModalEdicaoCaminhao(${cam.id}, '<c:out value="${cam.placaCavalo}" />', '<c:out value="${cam.placaCarreta}" />', ${cam.capacidadeMaxima})">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                                </svg>
                                                Editar
                                            </button>
                                            <button type="button" class="btn-sm-danger" onclick="abrirConfirmacaoExcluir('/caminhao', ${cam.id}, 'Cavalo: <c:out value="${cam.placaCavalo}" /> | Carreta: <c:out value="${cam.placaCarreta}" />')">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                                Excluir
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </section>


    <!-- ==================================================================== -->
    <!-- SEÇÃO 4: TRAJETO SERVLET -->
    <!-- ==================================================================== -->
    <section id="section-trajeto" class="servlet-section">
        <!-- Cabeçalho de Abas Internas -->
        <div class="tabs-header">
            <button type="button" id="tabBtnCadastrar-trajeto" class="tab-button active" onclick="switchTab('trajeto', 'cadastrar')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                Cadastrar Novo Trajeto
            </button>
            <button type="button" id="tabBtnExcluir-trajeto" class="tab-button tab-danger" onclick="switchTab('trajeto', 'excluir')">
                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                Excluir Trajeto
            </button>
        </div>

        <div class="card">
            <!-- ABA 1: CADASTRO TRAJETO (ESTILO RELATÓRIO DE EMBARQUE E DESEMBARQUE) -->
            <div id="tabCadastrar-trajeto" class="tab-content active" style="background: transparent; padding: 0;">
                <form action="${pageContext.request.contextPath}/trajeto" method="post" id="formCadastrarTrajeto" onsubmit="return handleFormSubmit(event, 'Relatório cadastrado com sucesso!')">
                    <input type="hidden" name="acao" value="cadastrar">
                    <input type="hidden" name="status" value="EM_ANDAMENTO">

                    <div class="doc-sheet-wrapper">
                        <div class="doc-sheet">
                            
                            <!-- CABEÇALHO DO DOCUMENTO -->
                            <div class="doc-header">
                                <div class="doc-logo-box">
                                    <span class="doc-logo-pill">(( EFFICIENTIA ))</span>
                                </div>

                                <div class="doc-vias-box">
                                    1ª Via Branca - TRP<br>
                                    2ª Via Rosa - Frigorífico<br>
                                    3ª Via Azul - Pecuarista
                                </div>

                                <div class="doc-title-box">
                                    <h1>Relatório de Embarque e Desembarque</h1>
                                </div>

                                <div class="doc-number-box">
                                    33235
                                </div>
                            </div>

                            <!-- LINHA 1: PECUARISTA -->
                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Nome pecuarista:</span>
                                    <select name="idPecuarista" class="doc-cell-select" required>
                                        <option value="" disabled selected>Selecione o pecuarista...</option>
                                        <c:forEach var="pec" items="${pecuaristas}">
                                            <option value="${pec.id}">${pec.nome} (CPF: ${pec.cpf})</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <!-- LINHA 2: PLACAS CAVALO E CARRETA -->
                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Caminhão (Placa cavalo):</span>
                                    <select id="traj-select-caminhao" name="idCaminhao" class="doc-cell-select" required onchange="atualizarPlacasTraj()">
                                        <option value="" disabled selected>Selecione o caminhão...</option>
                                        <c:forEach var="cam" items="${caminhoes}">
                                            <option value="${cam.id}" data-carreta="${cam.placaCarreta}">
                                                Cavalo: ${cam.placaCavalo} | Carreta: ${cam.placaCarreta}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Placa carreta:</span>
                                    <input type="text" id="traj-placa-carreta" class="doc-cell-input" readonly placeholder="Preenchimento automático">
                                </div>
                            </div>

                            <!-- LINHA 3: DATAS E HORÁRIOS DE EMBARQUE -->
                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Data e Hora embarque:</span>
                                    <input type="datetime-local" id="traj-horarioEmbarque" name="horarioEmbarque" class="doc-cell-input" required>
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Horário de saída da propriedade:</span>
                                    <input type="datetime-local" id="traj-dataHoraInicio" name="dataHoraInicio" class="doc-cell-input">
                                </div>
                            </div>

                            <!-- LINHA 4: MOTORISTA E DOCUMENTAÇÃO -->
                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1.2;">
                                    <span class="doc-cell-label">Código/ Motorista:</span>
                                    <select id="traj-idMotorista" name="idMotorista" class="doc-cell-select" required>
                                        <option value="" disabled selected>Selecione o motorista...</option>
                                        <c:forEach var="mot" items="${motoristas}">
                                            <option value="${mot.id}">Cód. #${mot.id} - ${mot.nome}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="doc-cell" style="flex: 0.9;">
                                    <span class="doc-cell-label">Nº GTA:</span>
                                    <input type="text" id="traj-numeroGTA" name="numeroGTA" class="doc-cell-input" placeholder="Ex: GTA-98741" required>
                                </div>
                                <div class="doc-cell" style="flex: 0.9;">
                                    <span class="doc-cell-label">Nº nota fiscal:</span>
                                    <input type="text" id="traj-numeroNotaFiscal" name="numeroNotaFiscal" class="doc-cell-input" placeholder="Ex: NF-45892" required>
                                </div>
                            </div>

                            <!-- LINHA 5: QUILOMETRAGEM -->
                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Km saída do embarcadouro:</span>
                                    <input type="number" id="traj-kmSaida" name="kmSaida" class="doc-cell-input" placeholder="Ex: 120000">
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Km chegada desembarcadouro:</span>
                                    <input type="number" id="traj-kmChegada" name="kmChegada" class="doc-cell-input" placeholder="Ex: 120450">
                                </div>
                            </div>

                            <!-- SEÇÃO 2: ANIMAIS / EMBARQUE -->
                            <div class="doc-section-header">ANIMAIS / EMBARQUE</div>
                            
                            <div class="doc-row" style="background: #fafafa;">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Machos:</span>
                                    <input type="number" id="traj-qtdMacho" name="qtdMacho" class="doc-cell-input calc-total" min="0" value="0" oninput="calcularTotalAnimaisTraj()">
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Fêmeas:</span>
                                    <input type="number" id="traj-qtdFemea" name="qtdFemea" class="doc-cell-input calc-total" min="0" value="0" oninput="calcularTotalAnimaisTraj()">
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Marrucos:</span>
                                    <input type="number" id="traj-qtdMarruco" name="qtdMarruco" class="doc-cell-input calc-total" min="0" value="0" oninput="calcularTotalAnimaisTraj()">
                                </div>
                                <div class="doc-cell" style="flex: 1; background: #f3f4f6;">
                                    <span class="doc-cell-label">Total:</span>
                                    <strong id="traj-totalAnimais" style="font-size: 1rem; color: #111827; padding-left: 0.5rem;">0</strong>
                                </div>
                            </div>

                            <!-- CHECKLIST DE EMBARQUE -->
                            <div class="doc-section-header" style="font-size: 0.76rem; background: #f3f4f6;">INFORMAÇÕES EMBARQUE</div>

                            <div class="doc-checks-grid">
                                <div class="doc-checks-col">
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Animal sangrando
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Animal mancando
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Gaiola muito cheia
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox" checked> Nenhum
                                    </label>
                                </div>

                                <div class="doc-checks-col">
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Excesso de magreza ou debilitado
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Animal sujo e/ou com sinal de pisoteio
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Chute/ paulada/ uso de ferrão
                                    </label>
                                    <div style="display: flex; align-items: center; gap: 0.3rem;">
                                        <label class="doc-check-item">
                                            <input type="checkbox"> Outro:
                                        </label>
                                        <input type="text" class="doc-cell-input" style="font-size: 0.75rem;">
                                    </div>
                                </div>

                                <div class="doc-checks-col">
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Cutucões fortes
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Tentativa de quebrar a cauda
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Arraste
                                    </label>
                                </div>
                            </div>

                            <!-- ASSINATURA RESPONSÁVEL DA FAZENDA -->
                            <div class="doc-signatures-section" style="padding-bottom: 0.5rem;">
                                <div style="max-width: 420px; margin: 1.2rem auto 0.2rem auto;">
                                    <input type="text" class="doc-sign-input" placeholder="Assinatura da fazenda">
                                    <div class="doc-sign-line">Assinatura do responsável (fazenda)</div>
                                </div>
                            </div>

                            <!-- SEÇÃO 3: INFORMAÇÕES VIAGEM -->
                            <div class="doc-section-header">INFORMAÇÕES VIAGEM</div>

                            <div class="doc-row" style="flex-wrap: wrap;">
                                <div class="doc-cell" style="gap: 0.8rem; flex: 1.5; border-right: 1px solid #000;">
                                    <span class="doc-cell-label">Parada imprevista?</span>
                                    <label class="doc-check-item"><input type="radio" name="parada_imp_teste" value="nao" checked> Não</label>
                                    <label class="doc-check-item"><input type="radio" name="parada_imp_teste" value="sim"> Sim</label>
                                    
                                    <span style="font-size: 0.72rem; color: #4b5563; margin-left: 0.5rem;">Motivo:</span>
                                    <label class="doc-check-item"><input type="checkbox"> Transbordo</label>
                                    <label class="doc-check-item"><input type="checkbox"> Acidente na pista</label>
                                    <label class="doc-check-item"><input type="checkbox"> Atoleiro</label>
                                    <label class="doc-check-item"><input type="checkbox"> Problema mecânico</label>
                                    <label class="doc-check-item"><input type="checkbox"> Nenhum</label>
                                </div>
                            </div>

                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Data e Hora de chegada na unidade:</span>
                                    <input type="datetime-local" id="traj-dataHoraFim" name="dataHoraFim" class="doc-cell-input">
                                </div>
                            </div>

                            <!-- SEÇÃO 4: INFORMAÇÕES DESEMBARQUE -->
                            <div class="doc-section-header">INFORMAÇÕES DESEMBARQUE</div>

                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Horário do desembarque:</span>
                                    <input type="datetime-local" id="traj-horarioDesembarque" name="horarioDesembarque" class="doc-cell-input">
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Nº curral:</span>
                                    <input type="text" id="traj-numeroCurral" name="numeroCurral" class="doc-cell-input" placeholder="Ex: Curral C-04">
                                </div>
                            </div>

                            <div class="doc-checks-grid" style="grid-template-columns: 1.4fr 1fr;">
                                <div class="doc-checks-col">
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Gaiola com buraco, estrado solto e/ou com ponta levantada
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Uso abusivo do choque (na cara, no umbigo, genitálias e/ou ânus)
                                    </label>
                                    <div style="display: flex; align-items: center; gap: 0.3rem;">
                                        <label class="doc-check-item">
                                            <input type="checkbox"> Outros atos de abuso:
                                        </label>
                                        <input type="text" class="doc-cell-input" style="font-size: 0.75rem;">
                                    </div>
                                </div>

                                <div class="doc-checks-col">
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Porteiras não abrem totalmente
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox"> Problema mecânico no veículo
                                    </label>
                                    <label class="doc-check-item">
                                        <input type="checkbox" checked> Nenhum
                                    </label>
                                </div>
                            </div>

                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1; gap: 1rem;">
                                    <span class="doc-cell-label">A sirene de ré do veículo funcionou ao encostar no desembarcadouro?</span>
                                    <label class="doc-check-item"><input type="radio" name="sirene_re_teste" value="sim" checked> Sim</label>
                                    <label class="doc-check-item"><input type="radio" name="sirene_re_teste" value="nao"> Não</label>
                                </div>
                            </div>

                            <!-- SEÇÃO 5: INFORMAÇÕES ANIMAIS -->
                            <div class="doc-section-header">INFORMAÇÕES ANIMAIS</div>

                            <div class="doc-row" style="background: #fafafa;">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Em pé:</span>
                                    <input type="number" class="doc-cell-input" min="0" value="0">
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Deitado:</span>
                                    <input type="number" class="doc-cell-input" min="0" value="0">
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Morto:</span>
                                    <input type="number" class="doc-cell-input" min="0" value="0">
                                </div>
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Emergência:</span>
                                    <input type="number" class="doc-cell-input" min="0" value="0">
                                </div>
                                <div class="doc-cell" style="flex: 1; background: #f3f4f6;">
                                    <span class="doc-cell-label">Total:</span>
                                    <strong id="traj-totalDesembarque">0</strong>
                                </div>
                            </div>

                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Em caso de emergência, descrever o motivo:</span>
                                    <input type="text" class="doc-cell-input" placeholder="Descreva aqui se houver emergência...">
                                </div>
                            </div>

                            <div class="doc-row">
                                <div class="doc-cell" style="flex: 1;">
                                    <span class="doc-cell-label">Comentários (opcional):</span>
                                    <input type="text" class="doc-cell-input" placeholder="Observações gerais sobre a viagem...">
                                </div>
                            </div>

                            <!-- SEÇÃO 6: ASSINATURAS -->
                            <div class="doc-signatures-section">
                                <!-- Linha 1: Assinatura do Motorista -->
                                <div style="max-width: 440px; margin: 1.5rem auto 0.5rem auto;">
                                    <input type="text" id="traj-assinaturaMotorista" name="assinaturaMotorista" class="doc-sign-input" placeholder="Assinatura do motorista">
                                    <div class="doc-sign-line">Assinatura do entregador responsável (motorista)</div>
                                </div>

                                <!-- Linha 2: Manobrista e Curraleiro -->
                                <div class="doc-sign-duo">
                                    <div>
                                        <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                            <span style="font-size: 0.72rem; font-weight: 700;">Nome Manobrista:</span>
                                            <input type="text" id="traj-nomeManobrista" name="nomeManobrista" class="doc-cell-input" placeholder="Nome do manobrista">
                                        </div>
                                        <input type="text" id="traj-assinaturaManobrista" name="assinaturaManobrista" class="doc-sign-input" placeholder="Assinatura">
                                        <div class="doc-sign-line">Assinatura do entregador responsável (manobrista)</div>
                                    </div>

                                    <div>
                                        <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                            <span style="font-size: 0.72rem; font-weight: 700;">Nome Curraleiro:</span>
                                            <input type="text" id="traj-nomeCurraleiro" name="nomeCurraleiro" class="doc-cell-input" placeholder="Nome do curraleiro">
                                        </div>
                                        <input type="text" id="traj-assinaturaCurraleiro" name="assinaturaCurraleiro" class="doc-sign-input" placeholder="Assinatura">
                                        <div class="doc-sign-line">Assinatura do recebedor (curraleiro) responsável</div>
                                    </div>
                                </div>
                            </div>

                            <!-- OBSERVAÇÃO DO RODAPÉ -->
                            <div class="doc-footer-obs">
                                Obs: O proprietário do veículo é responsável pela documentação dos animais. Após a viagem, entregue imediatamente este comprovante.
                            </div>

                        </div>
                    </div>

                    <div class="doc-actions-bar">
                        <button type="reset" class="btn btn-secondary">Limpar Formulário</button>
                        <div style="display: flex; gap: 0.8rem;">
                            <button type="button" class="btn btn-secondary" onclick="window.print()">
                                🖨️ Imprimir
                            </button>
                            <button type="submit" class="btn btn-primary">
                                📝 Emitir / Cadastrar Relatório
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- ABA 2: EXCLUSÃO TRAJETO -->
            <div id="tabExcluir-trajeto" class="tab-content">
                <div class="card-header">
                    <h2 class="title-red">Excluir Trajeto</h2>
                    <p>Selecione um trajeto cadastrado para realizar a exclusão no banco de dados.</p>
                </div>

                <c:choose>
                    <c:when test="${empty trajetos}">
                        <div class="empty-state">
                            <p>Não há trajetos cadastrados disponíveis para exclusão.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form id="formAbaExcluirTrajeto" onsubmit="return handleSelectExcluirSubmit(event, 'selectExcluirTrajeto', '/trajeto')">
                            <div class="form-grid">
                                <div class="form-group form-group-full">
                                    <label for="selectExcluirTrajeto">Selecione o Trajeto para Excluir *</label>
                                    <select id="selectExcluirTrajeto" name="id" class="danger-input" required>
                                        <option value="" disabled selected>Escolha um trajeto...</option>
                                        <c:forEach var="t" items="${trajetos}">
                                            <option value="${t.id}" data-nome="Trajeto #${t.id} - ${t.motoristaModel != null ? t.motoristaModel.nome : 'Motorista'}">
                                                ID #${t.id} - Status: ${t.status} | Motorista: ${t.motoristaModel != null ? t.motoristaModel.nome : 'N/A'} (GTA: ${not empty t.numeroGTA ? t.numeroGTA : 'S/N'})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-actions">
                                <button type="button" class="btn btn-secondary" onclick="switchTab('trajeto', 'cadastrar')">Cancelar</button>
                                <button type="submit" class="btn btn-danger">
                                    <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                    </svg>
                                    Excluir Trajeto
                                </button>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- LISTAGEM TRAJETOS -->
        <div class="list-section-header">
            <h2>Trajetos / Viagens Cadastradas</h2>
            <span class="badge-count">${not empty trajetos ? trajetos.size() : 0} registros</span>
        </div>

        <div class="table-responsive">
            <c:choose>
                <c:when test="${empty trajetos}">
                    <div class="empty-state">
                        <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                        </svg>
                        <p>Nenhum trajeto cadastrado até o momento.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Status</th>
                                <th>Motorista</th>
                                <th>Caminhão</th>
                                <th>Pecuarista</th>
                                <th>Datas / Horários</th>
                                <th>Carga (M/F/Mar)</th>
                                <th>Doc (GTA/NF)</th>
                                <th style="text-align: center;">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${trajetos}">
                                <tr>
                                    <td><strong>#${t.id}</strong></td>
                                    <td>
                                        <span class="badge-status ${t.status == 'CONCLUIDA' ? 'badge-status-concluida' : 'badge-status-andamento'}">
                                            <c:out value="${t.status}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.motoristaModel != null}">
                                                <strong><c:out value="${t.motoristaModel.nome}" /></strong>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.caminhaoModel != null}">
                                                <span class="text-code"><c:out value="${t.caminhaoModel.placaCavalo}" /></span>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.pecuarista != null}">
                                                <c:out value="${t.pecuarista.nome}" />
                                            </c:when>
                                            <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div style="font-size: 0.82rem; line-height: 1.4;">
                                            <div>Início: <strong>${t.dataHoraInicio != null ? dtfDateTime.format(t.dataHoraInicio) : '-'}</strong></div>
                                            <div>Fim: <strong>${t.dataHoraFim != null ? dtfDateTime.format(t.dataHoraFim) : '-'}</strong></div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="carga-badge-box">
                                            <span>M: <strong>${t.qtdMacho}</strong></span>
                                            <span>F: <strong>${t.qtdFemea}</strong></span>
                                            <span>Mar: <strong>${t.qtdMarruco}</strong></span>
                                            <span class="badge-total-animais">Total: ${t.qtdMacho + t.qtdFemea + t.qtdMarruco}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <div style="font-size: 0.8rem; line-height: 1.3;">
                                            <div>GTA: <span class="text-code">${not empty t.numeroGTA ? t.numeroGTA : '-'}</span></div>
                                            <div>NF: <span class="text-code">${not empty t.numeroNotaFiscal ? t.numeroNotaFiscal : '-'}</span></div>
                                        </div>
                                    </td>
                                    <td style="text-align: center;">
                                        <div style="display: flex; align-items: center; justify-content: center; gap: 0.4rem;">
                                            <button type="button" class="btn-sm-edit" onclick="abrirModalEdicaoTrajeto(
                                                ${t.id},
                                                ${t.motoristaModel != null ? t.motoristaModel.id : 'null'},
                                                ${t.caminhaoModel != null ? t.caminhaoModel.id : 'null'},
                                                ${t.pecuarista != null ? t.pecuarista.id : 'null'},
                                                '${t.status != null ? t.status.name() : ''}',
                                                '${t.dataHoraInicio != null ? dtfInput.format(t.dataHoraInicio) : ''}',
                                                '${t.dataHoraFim != null ? dtfInput.format(t.dataHoraFim) : ''}',
                                                ${t.kmSaida},
                                                ${t.kmChegada},
                                                '<c:out value="${t.numeroGTA}" />',
                                                '<c:out value="${t.numeroNotaFiscal}" />',
                                                '${t.horarioEmbarque != null ? dtfInput.format(t.horarioEmbarque) : ''}',
                                                ${t.qtdMacho},
                                                ${t.qtdFemea},
                                                ${t.qtdMarruco},
                                                '${t.horarioDesembarque != null ? dtfInput.format(t.horarioDesembarque) : ''}',
                                                '<c:out value="${t.numeroCurral}" />',
                                                '<c:out value="${t.nomeCurraleiro}" />',
                                                '<c:out value="${t.nomeManobrista}" />',
                                                '<c:out value="${t.assinaturaCurraleiro}" />',
                                                '<c:out value="${t.assinaturaManobrista}" />',
                                                '<c:out value="${t.assinaturaMotorista}" />'
                                            )">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                                </svg>
                                                Editar
                                            </button>
                                            <button type="button" class="btn-sm-danger" onclick="abrirConfirmacaoExcluir('/trajeto', ${t.id}, 'Trajeto #${t.id} (GTA: <c:out value="${t.numeroGTA}" />)')">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                                Excluir
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

</div>


<!-- ==================================================================== -->
<!-- MODAL DE CONFIRMAÇÃO DE EXCLUSÃO (GENÉRICO) -->
<!-- ==================================================================== -->
<div id="modalConfirmacao" class="modal-overlay">
    <div class="modal-card">
        <div class="modal-icon-box">
            <svg width="28" height="28" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
        </div>

        <h3 class="modal-title">Realmente deseja excluir?</h3>
        <p class="modal-desc">Esta ação não poderá ser desfeita no banco de dados.</p>
        <div id="modalDetalhe" class="modal-detail"></div>

        <form id="formConfirmModal" method="post" onsubmit="return handleFormSubmit(event, 'Registro excluído com sucesso!')">
            <input type="hidden" name="acao" value="excluir">
            <input type="hidden" id="modalInputId" name="id" value="">

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="fecharModal('modalConfirmacao')">Cancelar</button>
                <button type="submit" class="btn btn-danger">Sim, Excluir</button>
            </div>
        </form>
    </div>
</div>


<!-- ==================================================================== -->
<!-- MODAL DE EDIÇÃO: PECUARISTA (Idêntico a editar.jsp) -->
<!-- ==================================================================== -->
<div id="modalEdicaoPecuarista" class="modal-overlay">
    <div class="modal-card modal-card-lg">
        <div class="modal-header-edit">
            <h3>
                Editar Pecuarista
                <span id="badgeEditPecuaristaId" class="badge-id">#0</span>
            </h3>
            <button type="button" class="modal-close-btn" onclick="fecharModal('modalEdicaoPecuarista')">&times;</button>
        </div>

        <form id="formEditarPecuarista" action="${pageContext.request.contextPath}/pecuarista" method="post" onsubmit="return handleFormSubmit(event, 'Pecuarista atualizado com sucesso!')">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" id="editPec-id" name="id" value="">

            <div class="form-grid">
                <div class="form-group form-group-full">
                    <label for="editPec-nome">Nome Completo *</label>
                    <input type="text" id="editPec-nome" name="nome" required>
                </div>

                <div class="form-group">
                    <label for="editPec-cpf">CPF * <span class="helper-text">11 números</span></label>
                    <input type="text" id="editPec-cpf" name="cpf" maxlength="11" pattern="[0-9]{11}" required>
                </div>

                <div class="form-group">
                    <label for="editPec-dataNascimento">Data de Nascimento *</label>
                    <input type="date" id="editPec-dataNascimento" name="dataNascimento" required>
                </div>

                <div class="form-group">
                    <label for="editPec-email">E-mail *</label>
                    <input type="email" id="editPec-email" name="email" required>
                </div>

                <div class="form-group">
                    <label for="editPec-telefone">Telefone</label>
                    <input type="tel" id="editPec-telefone" name="telefone" maxlength="11" pattern="[0-9]{10,11}">
                </div>

                <div class="form-group">
                    <label for="editPec-assinatura">Assinatura <span class="helper-text">(vinda do banco de dados)</span></label>
                    <input type="text" id="editPec-assinatura" name="assinatura" readonly style="opacity: 0.75; cursor: not-allowed;">
                </div>

                <div class="form-group">
                    <label for="editPec-senha">Senha de Acesso *</label>
                    <input type="password" id="editPec-senha" name="senha" required>
                </div>
            </div>

            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="fecharModal('modalEdicaoPecuarista')">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar Alterações</button>
            </div>
        </form>
    </div>
</div>


<!-- ==================================================================== -->
<!-- MODAL DE EDIÇÃO: MOTORISTA -->
<!-- ==================================================================== -->
<div id="modalEdicaoMotorista" class="modal-overlay">
    <div class="modal-card modal-card-lg">
        <div class="modal-header-edit">
            <h3>
                Editar Motorista
                <span id="badgeEditMotoristaId" class="badge-id">#0</span>
            </h3>
            <button type="button" class="modal-close-btn" onclick="fecharModal('modalEdicaoMotorista')">&times;</button>
        </div>

        <form id="formEditarMotorista" action="${pageContext.request.contextPath}/motorista" method="post" onsubmit="return handleFormSubmit(event, 'Motorista atualizado com sucesso!')">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" id="editMot-id" name="id" value="">

            <div class="form-grid">
                <div class="form-group">
                    <label for="editMot-nome">Nome Completo *</label>
                    <input type="text" id="editMot-nome" name="nome" required>
                </div>

                <div class="form-group">
                    <label for="editMot-idEmpresa">Empresa Vinculada</label>
                    <select id="editMot-idEmpresa" name="idEmpresa">
                        <option value="">Sem empresa associada</option>
                        <c:forEach var="emp" items="${empresas}">
                            <option value="${emp.id}">#${emp.id} - ${emp.nome} (CNPJ: ${emp.cnpj})</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="editMot-dataNascimento">Data de Nascimento *</label>
                    <input type="date" id="editMot-dataNascimento" name="dataNascimento" required>
                </div>

                <div class="form-group">
                    <label for="editMot-email">E-mail *</label>
                    <input type="email" id="editMot-email" name="email" required>
                </div>

                <div class="form-group">
                    <label for="editMot-telefone">Telefone</label>
                    <input type="tel" id="editMot-telefone" name="telefone" maxlength="11" pattern="[0-9]{10,11}">
                </div>

                <div class="form-group">
                    <label for="editMot-assinatura">Assinatura <span class="helper-text">(vinda do banco de dados)</span></label>
                    <input type="text" id="editMot-assinatura" name="assinatura" readonly style="opacity: 0.75; cursor: not-allowed;">
                </div>

                <div class="form-group form-group-full">
                    <label for="editMot-senha">Senha de Acesso *</label>
                    <input type="password" id="editMot-senha" name="senha" required>
                </div>
            </div>

            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="fecharModal('modalEdicaoMotorista')">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar Alterações</button>
            </div>
        </form>
    </div>
</div>


<!-- ==================================================================== -->
<!-- MODAL DE EDIÇÃO: CAMINHÃO -->
<!-- ==================================================================== -->
<div id="modalEdicaoCaminhao" class="modal-overlay">
    <div class="modal-card modal-card-lg">
        <div class="modal-header-edit">
            <h3>
                Editar Caminhão
                <span id="badgeEditCaminhaoId" class="badge-id">#0</span>
            </h3>
            <button type="button" class="modal-close-btn" onclick="fecharModal('modalEdicaoCaminhao')">&times;</button>
        </div>

        <form id="formEditarCaminhao" action="${pageContext.request.contextPath}/caminhao" method="post" onsubmit="return handleFormSubmit(event, 'Caminhão atualizado com sucesso!')">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" id="editCam-id" name="id" value="">

            <div class="form-grid">
                <div class="form-group">
                    <label for="editCam-placaCavalo">Placa do Cavalo *</label>
                    <input type="text" id="editCam-placaCavalo" name="placaCavalo" class="input-plate" maxlength="8" required>
                </div>

                <div class="form-group">
                    <label for="editCam-placaCarreta">Placa da Carreta *</label>
                    <input type="text" id="editCam-placaCarreta" name="placaCarreta" class="input-plate" maxlength="8" required>
                </div>

                <div class="form-group form-group-full">
                    <label for="editCam-capacidadeMaxima">Capacidade Máxima *</label>
                    <input type="number" id="editCam-capacidadeMaxima" name="capacidadeMaxima" min="1" required>
                </div>
            </div>

            <div class="form-actions">
                <button type="button" class="btn btn-secondary" onclick="fecharModal('modalEdicaoCaminhao')">Cancelar</button>
                <button type="submit" class="btn btn-primary">Salvar Alterações</button>
            </div>
        </form>
    </div>
</div>


<!-- ==================================================================== -->
<!-- MODAL DE EDIÇÃO: TRAJETO (ESTILO RELATÓRIO DE EMBARQUE E DESEMBARQUE) -->
<!-- ==================================================================== -->
<div id="modalEdicaoTrajeto" class="modal-overlay">
    <div class="modal-card modal-card-lg" style="max-width: 980px; max-height: 92vh; overflow-y: auto; padding: 1.25rem;">
        <div class="modal-header-edit">
            <h3>
                Relatório de Embarque e Desembarque
                <span id="badgeEditTrajetoId" class="badge-id" style="color: #dc2626; font-family: 'Courier New', monospace; font-size: 1.4rem;">#0</span>
            </h3>
            <button type="button" class="modal-close-btn" onclick="fecharModal('modalEdicaoTrajeto')">&times;</button>
        </div>

        <form id="formEditarTrajeto" action="${pageContext.request.contextPath}/trajeto" method="post" onsubmit="return handleFormSubmit(event, 'Relatório atualizado com sucesso!')">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" id="editTraj-id" name="id" value="">
            <input type="hidden" id="editTraj-status" name="status" value="EM_ANDAMENTO">

            <div class="doc-sheet-wrapper" style="margin: 0.5rem 0;">
                <div class="doc-sheet" style="box-shadow: none;">
                    
                    <!-- CABEÇALHO DO DOCUMENTO -->
                    <div class="doc-header">
                        <div class="doc-logo-box">
                            <span class="doc-logo-pill">(( EFFICIENTIA ))</span>
                        </div>

                        <div class="doc-vias-box">
                            1ª Via Branca - TRP<br>
                            2ª Via Rosa - Frigorífico<br>
                            3ª Via Azul - Pecuarista
                        </div>

                        <div class="doc-title-box">
                            <h1>Relatório de Embarque e Desembarque</h1>
                        </div>

                        <div class="doc-number-box" id="doc-modal-num">
                            33235
                        </div>
                    </div>

                    <!-- LINHA 1: PECUARISTA -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Nome pecuarista:</span>
                            <select id="editTraj-idPecuarista" name="idPecuarista" class="doc-cell-select" required>
                                <option value="">Selecione o pecuarista...</option>
                                <c:forEach var="pec" items="${pecuaristas}">
                                    <option value="${pec.id}">${pec.nome} (CPF: ${pec.cpf})</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <!-- LINHA 2: PLACAS CAVALO E CARRETA -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Caminhão (Placa cavalo):</span>
                            <select id="editTraj-idCaminhao" name="idCaminhao" class="doc-cell-select" required onchange="atualizarPlacasEditTraj()">
                                <option value="">Selecione o caminhão...</option>
                                <c:forEach var="cam" items="${caminhoes}">
                                    <option value="${cam.id}" data-carreta="${cam.placaCarreta}">
                                        Cavalo: ${cam.placaCavalo} | Carreta: ${cam.placaCarreta}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Placa carreta:</span>
                            <input type="text" id="editTraj-placaCarreta" class="doc-cell-input" readonly placeholder="Preenchimento automático">
                        </div>
                    </div>

                    <!-- LINHA 3: DATAS E HORÁRIOS DE EMBARQUE -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Data e Hora embarque:</span>
                            <input type="datetime-local" id="editTraj-horarioEmbarque" name="horarioEmbarque" class="doc-cell-input" required>
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Horário de saída da propriedade:</span>
                            <input type="datetime-local" id="editTraj-dataHoraInicio" name="dataHoraInicio" class="doc-cell-input">
                        </div>
                    </div>

                    <!-- LINHA 4: MOTORISTA E DOCUMENTAÇÃO -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1.2;">
                            <span class="doc-cell-label">Código/ Motorista:</span>
                            <select id="editTraj-idMotorista" name="idMotorista" class="doc-cell-select" required>
                                <option value="">Selecione o motorista...</option>
                                <c:forEach var="mot" items="${motoristas}">
                                    <option value="${mot.id}">Cód. #${mot.id} - ${mot.nome}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="doc-cell" style="flex: 0.9;">
                            <span class="doc-cell-label">Nº GTA:</span>
                            <input type="text" id="editTraj-numeroGTA" name="numeroGTA" class="doc-cell-input" required>
                        </div>
                        <div class="doc-cell" style="flex: 0.9;">
                            <span class="doc-cell-label">Nº nota fiscal:</span>
                            <input type="text" id="editTraj-numeroNotaFiscal" name="numeroNotaFiscal" class="doc-cell-input" required>
                        </div>
                    </div>

                    <!-- LINHA 5: QUILOMETRAGEM -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Km saída do embarcadouro:</span>
                            <input type="number" id="editTraj-kmSaida" name="kmSaida" class="doc-cell-input">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Km chegada desembarcadouro:</span>
                            <input type="number" id="editTraj-kmChegada" name="kmChegada" class="doc-cell-input">
                        </div>
                    </div>

                    <!-- SEÇÃO 2: ANIMAIS / EMBARQUE -->
                    <div class="doc-section-header">ANIMAIS / EMBARQUE</div>
                    
                    <div class="doc-row" style="background: #fafafa;">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Machos:</span>
                            <input type="number" id="editTraj-qtdMacho" name="qtdMacho" class="doc-cell-input calc-total" min="0" oninput="calcularTotalAnimaisEdit()">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Fêmeas:</span>
                            <input type="number" id="editTraj-qtdFemea" name="qtdFemea" class="doc-cell-input calc-total" min="0" oninput="calcularTotalAnimaisEdit()">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Marrucos:</span>
                            <input type="number" id="editTraj-qtdMarruco" name="qtdMarruco" class="doc-cell-input calc-total" min="0" oninput="calcularTotalAnimaisEdit()">
                        </div>
                        <div class="doc-cell" style="flex: 1; background: #f3f4f6;">
                            <span class="doc-cell-label">Total:</span>
                            <strong id="editTraj-totalAnimais" style="font-size: 1rem; color: #111827; padding-left: 0.5rem;">0</strong>
                        </div>
                    </div>

                    <!-- CHECKLIST DE EMBARQUE -->
                    <div class="doc-section-header" style="font-size: 0.76rem; background: #f3f4f6;">INFORMAÇÕES EMBARQUE</div>

                    <div class="doc-checks-grid">
                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Animal sangrando
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Animal mancando
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Gaiola muito cheia
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox" checked> Nenhum
                            </label>
                        </div>

                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Excesso de magreza ou debilitado
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Animal sujo e/ou com sinal de pisoteio
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Chute/ paulada/ uso de ferrão
                            </label>
                            <div style="display: flex; align-items: center; gap: 0.3rem;">
                                <label class="doc-check-item">
                                    <input type="checkbox"> Outro:
                                </label>
                                <input type="text" class="doc-cell-input" style="font-size: 0.75rem;">
                            </div>
                        </div>

                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Cutucões fortes
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Tentativa de quebrar a cauda
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Arraste
                            </label>
                        </div>
                    </div>

                    <!-- ASSINATURA RESPONSÁVEL DA FAZENDA -->
                    <div class="doc-signatures-section" style="padding-bottom: 0.5rem;">
                        <div style="max-width: 420px; margin: 1.2rem auto 0.2rem auto;">
                            <input type="text" class="doc-sign-input" placeholder="Assinatura da fazenda">
                            <div class="doc-sign-line">Assinatura do responsável (fazenda)</div>
                        </div>
                    </div>

                    <!-- SEÇÃO 3: INFORMAÇÕES VIAGEM -->
                    <div class="doc-section-header">INFORMAÇÕES VIAGEM</div>

                    <div class="doc-row" style="flex-wrap: wrap;">
                        <div class="doc-cell" style="gap: 0.8rem; flex: 1.5; border-right: 1px solid #000;">
                            <span class="doc-cell-label">Parada imprevista?</span>
                            <label class="doc-check-item"><input type="radio" name="edit_parada_imp" value="nao" checked> Não</label>
                            <label class="doc-check-item"><input type="radio" name="edit_parada_imp" value="sim"> Sim</label>
                            
                            <span style="font-size: 0.72rem; color: #4b5563; margin-left: 0.5rem;">Motivo:</span>
                            <label class="doc-check-item"><input type="checkbox"> Transbordo</label>
                            <label class="doc-check-item"><input type="checkbox"> Acidente na pista</label>
                            <label class="doc-check-item"><input type="checkbox"> Atoleiro</label>
                            <label class="doc-check-item"><input type="checkbox"> Problema mecânico</label>
                            <label class="doc-check-item"><input type="checkbox"> Nenhum</label>
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Data e Hora de chegada na unidade:</span>
                            <input type="datetime-local" id="editTraj-dataHoraFim" name="dataHoraFim" class="doc-cell-input">
                        </div>
                    </div>

                    <!-- SEÇÃO 4: INFORMAÇÕES DESEMBARQUE -->
                    <div class="doc-section-header">INFORMAÇÕES DESEMBARQUE</div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Horário do desembarque:</span>
                            <input type="datetime-local" id="editTraj-horarioDesembarque" name="horarioDesembarque" class="doc-cell-input">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Nº curral:</span>
                            <input type="text" id="editTraj-numeroCurral" name="numeroCurral" class="doc-cell-input">
                        </div>
                    </div>

                    <div class="doc-checks-grid" style="grid-template-columns: 1.4fr 1fr;">
                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Gaiola com buraco, estrado solto e/ou com ponta levantada
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Uso abusivo do choque (na cara, no umbigo, genitálias e/ou ânus)
                            </label>
                            <div style="display: flex; align-items: center; gap: 0.3rem;">
                                <label class="doc-check-item">
                                    <input type="checkbox"> Outros atos de abuso:
                                </label>
                                <input type="text" class="doc-cell-input" style="font-size: 0.75rem;">
                            </div>
                        </div>

                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Porteiras não abrem totalmente
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Problema mecânico no veículo
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox" checked> Nenhum
                            </label>
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1; gap: 1rem;">
                            <span class="doc-cell-label">A sirene de ré do veículo funcionou ao encostar no desembarcadouro?</span>
                            <label class="doc-check-item"><input type="radio" name="edit_sirene_re" value="sim" checked> Sim</label>
                            <label class="doc-check-item"><input type="radio" name="edit_sirene_re" value="nao"> Não</label>
                        </div>
                    </div>

                    <!-- SEÇÃO 5: INFORMAÇÕES ANIMAIS -->
                    <div class="doc-section-header">INFORMAÇÕES ANIMAIS</div>

                    <div class="doc-row" style="background: #fafafa;">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Em pé:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Deitado:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Morto:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Emergência:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1; background: #f3f4f6;">
                            <span class="doc-cell-label">Total:</span>
                            <strong id="editTraj-totalDesembarque">0</strong>
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Em caso de emergência, descrever o motivo:</span>
                            <input type="text" class="doc-cell-input" placeholder="Descreva aqui se houver emergência...">
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Comentários (opcional):</span>
                            <input type="text" class="doc-cell-input" placeholder="Observações gerais sobre a viagem...">
                        </div>
                    </div>

                    <!-- SEÇÃO 6: ASSINATURAS -->
                    <div class="doc-signatures-section">
                        <!-- Linha 1: Assinatura do Motorista -->
                        <div style="max-width: 440px; margin: 1.5rem auto 0.5rem auto;">
                            <input type="text" id="editTraj-assinaturaMotorista" name="assinaturaMotorista" class="doc-sign-input" placeholder="Assinatura do motorista">
                            <div class="doc-sign-line">Assinatura do entregador responsável (motorista)</div>
                        </div>

                        <!-- Linha 2: Manobrista e Curraleiro -->
                        <div class="doc-sign-duo">
                            <div>
                                <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                    <span style="font-size: 0.72rem; font-weight: 700;">Nome Manobrista:</span>
                                    <input type="text" id="editTraj-nomeManobrista" name="nomeManobrista" class="doc-cell-input" placeholder="Nome do manobrista">
                                </div>
                                <input type="text" id="editTraj-assinaturaManobrista" name="assinaturaManobrista" class="doc-sign-input" placeholder="Assinatura">
                                <div class="doc-sign-line">Assinatura do entregador responsável (manobrista)</div>
                            </div>

                            <div>
                                <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                    <span style="font-size: 0.72rem; font-weight: 700;">Nome Curraleiro:</span>
                                    <input type="text" id="editTraj-nomeCurraleiro" name="nomeCurraleiro" class="doc-cell-input" placeholder="Nome do curraleiro">
                                </div>
                                <input type="text" id="editTraj-assinaturaCurraleiro" name="assinaturaCurraleiro" class="doc-sign-input" placeholder="Assinatura">
                                <div class="doc-sign-line">Assinatura do recebedor (curraleiro) responsável</div>
                            </div>
                        </div>
                    </div>

                    <!-- OBSERVAÇÃO DO RODAPÉ -->
                    <div class="doc-footer-obs">
                        Obs: O proprietário do veículo é responsável pela documentação dos animais. Após a viagem, entregue imediatamente este comprovante.
                    </div>

                </div>
            </div>

            <div class="doc-actions-bar">
                <button type="button" class="btn btn-secondary" onclick="fecharModal('modalEdicaoTrajeto')">Cancelar</button>
                <div style="display: flex; gap: 0.8rem;">
                    <button type="button" class="btn btn-secondary" onclick="window.print()">
                        🖨️ Imprimir
                    </button>
                    <button type="submit" class="btn btn-primary">
                        💾 Salvar Alterações
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>


<!-- Container para Toasts Dinâmicos -->
<div id="toastContainer" class="toast-container" aria-live="polite"></div>


<!-- ==================================================================== -->
<!-- SCRIPTS DE COMPORTAMENTO E OPERAÇÕES CRUD -->
<!-- ==================================================================== -->
<script>
    const CTX = '${pageContext.request.contextPath}';
    let currentServlet = 'pecuarista';

    // Inicialização ao carregar a página: verifica parâmetro na URL
    window.addEventListener('DOMContentLoaded', () => {
        const urlParams = new URLSearchParams(window.location.search);
        const servletParam = urlParams.get('servlet');
        if (servletParam && ['pecuarista', 'motorista', 'caminhao', 'trajeto'].includes(servletParam)) {
            switchServlet(servletParam);
        }

        // Sanitização de CPF e Telefone (apenas números)
        ['pec-cpf', 'pec-telefone', 'mot-telefone', 'editPec-cpf', 'editPec-telefone', 'editMot-telefone'].forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.addEventListener('input', (e) => {
                    e.target.value = e.target.value.replace(/\D/g, '');
                });
            }
        });

        // Fechamento de modal ao clicar fora
        document.querySelectorAll('.modal-overlay').forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    modal.classList.remove('open');
                }
            });
        });
    });

    // ── Alternância entre Servlets ──
    function switchServlet(servletName) {
        currentServlet = servletName;

        // Atualiza botões do topo
        document.querySelectorAll('.servlet-nav-btn').forEach(btn => btn.classList.remove('active'));
        const activeNavBtn = document.getElementById('navBtn-' + servletName);
        if (activeNavBtn) activeNavBtn.classList.add('active');

        // Atualiza seções
        document.querySelectorAll('.servlet-section').forEach(sec => sec.classList.remove('active'));
        const activeSec = document.getElementById('section-' + servletName);
        if (activeSec) activeSec.classList.add('active');
    }

    // ── Alternância de Abas Internas (Cadastrar / Excluir) ──
    function switchTab(servlet, tabName) {
        const tabCadastrar = document.getElementById('tabCadastrar-' + servlet);
        const tabExcluir = document.getElementById('tabExcluir-' + servlet);
        const btnCadastrar = document.getElementById('tabBtnCadastrar-' + servlet);
        const btnExcluir = document.getElementById('tabBtnExcluir-' + servlet);

        if (!tabCadastrar || !tabExcluir) return;

        if (tabName === 'cadastrar') {
            tabCadastrar.classList.add('active');
            tabExcluir.classList.remove('active');
            btnCadastrar.classList.add('active');
            btnExcluir.classList.remove('active');
        } else {
            tabCadastrar.classList.remove('active');
            tabExcluir.classList.add('active');
            btnCadastrar.classList.remove('active');
            btnExcluir.classList.add('active');
        }
    }

    // ── Controle de Modais ──
    function abrirModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.add('open');
    }

    function fecharModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.remove('open');
    }

    // ── Abertura do Modal de Confirmação de Exclusão ──
    function abrirConfirmacaoExcluir(servletPath, id, detalheTexto) {
        const form = document.getElementById('formConfirmModal');
        form.action = CTX + servletPath;
        document.getElementById('modalInputId').value = id;
        document.getElementById('modalDetalhe').textContent = 'Registro #' + id + (detalheTexto ? ' - ' + detalheTexto : '');
        abrirModal('modalConfirmacao');
    }

    // ── Submissão do select na aba Excluir ──
    function handleSelectExcluirSubmit(event, selectId, servletPath) {
        event.preventDefault();
        const select = document.getElementById(selectId);
        if (!select || !select.value) return false;

        const id = select.value;
        const selectedOption = select.options[select.selectedIndex];
        const nome = selectedOption.getAttribute('data-nome') || '';

        abrirConfirmacaoExcluir(servletPath, id, nome);
        return false;
    }

    // ── Abertura dos Modais de Edição com preenchimento de campos ──
    function abrirModalEdicaoPecuarista(id, nome, cpf, dataNasc, email, telefone, assinatura) {
        document.getElementById('editPec-id').value = id;
        document.getElementById('badgeEditPecuaristaId').textContent = '#' + id;
        document.getElementById('editPec-nome').value = nome || '';
        document.getElementById('editPec-cpf').value = cpf || '';
        document.getElementById('editPec-dataNascimento').value = dataNasc || '';
        document.getElementById('editPec-email').value = email || '';
        document.getElementById('editPec-telefone').value = telefone || '';
        document.getElementById('editPec-assinatura').value = assinatura || '';
        document.getElementById('editPec-senha').value = '';
        abrirModal('modalEdicaoPecuarista');
    }

    function abrirModalEdicaoMotorista(id, idEmpresa, nome, dataNasc, email, telefone, assinatura) {
        document.getElementById('editMot-id').value = id;
        document.getElementById('badgeEditMotoristaId').textContent = '#' + id;
        document.getElementById('editMot-nome').value = nome || '';
        document.getElementById('editMot-idEmpresa').value = idEmpresa || '';
        document.getElementById('editMot-dataNascimento').value = dataNasc || '';
        document.getElementById('editMot-email').value = email || '';
        document.getElementById('editMot-telefone').value = telefone || '';
        document.getElementById('editMot-assinatura').value = assinatura || '';
        document.getElementById('editMot-senha').value = '';
        abrirModal('modalEdicaoMotorista');
    }

    function abrirModalEdicaoCaminhao(id, placaCavalo, placaCarreta, capacidadeMaxima) {
        document.getElementById('editCam-id').value = id;
        document.getElementById('badgeEditCaminhaoId').textContent = '#' + id;
        document.getElementById('editCam-placaCavalo').value = placaCavalo || '';
        document.getElementById('editCam-placaCarreta').value = placaCarreta || '';
        document.getElementById('editCam-capacidadeMaxima').value = capacidadeMaxima || '';
        abrirModal('modalEdicaoCaminhao');
    }

    function abrirModalEdicaoTrajeto(id, idMotorista, idCaminhao, idPecuarista, status, dataInicio, dataFim, kmSaida, kmChegada, gta, nf, embarque, macho, femea, marruco, desembarque, curral, curraleiro, manobrista, assCurraleiro, assManobrista, assMotorista) {
        document.getElementById('editTraj-id').value = id;
        document.getElementById('badgeEditTrajetoId').textContent = '#' + id;
        const modalNumEl = document.getElementById('doc-modal-num');
        if (modalNumEl) modalNumEl.textContent = id || '33235';

        document.getElementById('editTraj-idMotorista').value = (idMotorista !== null && idMotorista !== 'null') ? idMotorista : '';
        document.getElementById('editTraj-idCaminhao').value = (idCaminhao !== null && idCaminhao !== 'null') ? idCaminhao : '';
        document.getElementById('editTraj-idPecuarista').value = (idPecuarista !== null && idPecuarista !== 'null') ? idPecuarista : '';
        document.getElementById('editTraj-status').value = status || 'EM_ANDAMENTO';
        document.getElementById('editTraj-dataHoraInicio').value = dataInicio || '';
        document.getElementById('editTraj-dataHoraFim').value = dataFim || '';
        document.getElementById('editTraj-kmSaida').value = (kmSaida && kmSaida !== 'null') ? kmSaida : '';
        document.getElementById('editTraj-kmChegada').value = (kmChegada && kmChegada !== 'null') ? kmChegada : '';
        document.getElementById('editTraj-numeroGTA').value = gta || '';
        document.getElementById('editTraj-numeroNotaFiscal').value = nf || '';
        document.getElementById('editTraj-horarioEmbarque').value = embarque || '';
        document.getElementById('editTraj-qtdMacho').value = macho || 0;
        document.getElementById('editTraj-qtdFemea').value = femea || 0;
        document.getElementById('editTraj-qtdMarruco').value = marruco || 0;
        document.getElementById('editTraj-horarioDesembarque').value = desembarque || '';
        document.getElementById('editTraj-numeroCurral').value = curral || '';
        document.getElementById('editTraj-nomeCurraleiro').value = curraleiro || '';
        document.getElementById('editTraj-nomeManobrista').value = manobrista || '';
        document.getElementById('editTraj-assinaturaCurraleiro').value = assCurraleiro || '';
        document.getElementById('editTraj-assinaturaManobrista').value = assManobrista || '';
        document.getElementById('editTraj-assinaturaMotorista').value = assMotorista || '';

        atualizarPlacasEditTraj();
        calcularTotalAnimaisEdit();
        abrirModal('modalEdicaoTrajeto');
    }

    // Funções utilitárias do formulário estilo Documento JBS / Efficientia
    function calcularTotalAnimaisTraj() {
        const m = parseInt(document.getElementById('traj-qtdMacho').value) || 0;
        const f = parseInt(document.getElementById('traj-qtdFemea').value) || 0;
        const mar = parseInt(document.getElementById('traj-qtdMarruco').value) || 0;
        const totalEl = document.getElementById('traj-totalAnimais');
        if (totalEl) totalEl.textContent = m + f + mar;
    }

    function atualizarPlacasTraj() {
        const select = document.getElementById('traj-select-caminhao');
        const carretaInput = document.getElementById('traj-placa-carreta');
        if (!select || !carretaInput) return;
        const opt = select.options[select.selectedIndex];
        if (opt && opt.getAttribute('data-carreta')) {
            carretaInput.value = opt.getAttribute('data-carreta');
        } else {
            carretaInput.value = '';
        }
    }

    function calcularTotalAnimaisEdit() {
        const m = parseInt(document.getElementById('editTraj-qtdMacho').value) || 0;
        const f = parseInt(document.getElementById('editTraj-qtdFemea').value) || 0;
        const mar = parseInt(document.getElementById('editTraj-qtdMarruco').value) || 0;
        const totalEl = document.getElementById('editTraj-totalAnimais');
        if (totalEl) totalEl.textContent = m + f + mar;
    }

    function atualizarPlacasEditTraj() {
        const select = document.getElementById('editTraj-idCaminhao');
        const carretaInput = document.getElementById('editTraj-placaCarreta');
        if (!select || !carretaInput) return;
        const opt = select.options[select.selectedIndex];
        if (opt && opt.getAttribute('data-carreta')) {
            carretaInput.value = opt.getAttribute('data-carreta');
        } else {
            carretaInput.value = '';
        }
    }

    // ── Tratamento AJAX assíncrono para os formulários CRUD dos Servlets ──
    async function handleFormSubmit(event, successMsg) {
        event.preventDefault();
        const form = event.target;
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalContent = submitBtn ? submitBtn.innerHTML : '';

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = 'Processando...';
        }

        const formData = new URLSearchParams(new FormData(form));

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
                },
                body: formData.toString()
            });

            // Se o Servlet processou e respondeu (inclusive com redirect 302 seguido de 200)
            if (response.ok || response.status === 302 || response.type === 'opaqueredirect') {
                showToast(successMsg, 'toast-success');
                fecharModal('modalConfirmacao');
                fecharModal('modalEdicaoPecuarista');
                fecharModal('modalEdicaoMotorista');
                fecharModal('modalEdicaoCaminhao');
                fecharModal('modalEdicaoTrajeto');

                // Recarrega a página mantendo a aba do servlet ativa
                setTimeout(() => {
                    window.location.href = CTX + '/teste.jsp?servlet=' + currentServlet;
                }, 600);
            } else {
                showToast('Erro ao processar (HTTP ' + response.status + ')', 'toast-danger');
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = originalContent;
                }
            }
        } catch (error) {
            console.error('Erro na requisição ao Servlet:', error);
            showToast('Erro de comunicação: ' + error.message, 'toast-danger');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalContent;
            }
        }

        return false;
    }

    // ── Sistema de Toast Notifications ──
    function showToast(message, typeClass) {
        const container = document.getElementById('toastContainer');
        const toast = document.createElement('div');
        toast.className = 'toast ' + (typeClass || 'toast-info');

        let icon = 'ℹ️';
        if (typeClass === 'toast-success') icon = '✅';
        if (typeClass === 'toast-danger') icon = '❌';

        toast.innerHTML = '<span style="font-size: 1.1rem;">' + icon + '</span> <span>' + message + '</span>';
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(30px)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
</script>

</body>
</html>
