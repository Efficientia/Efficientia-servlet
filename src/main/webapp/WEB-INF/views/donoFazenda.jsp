<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.util.List" %>
<%@ page import="com.efficientia.efficientia.model.DonoFazendaModel" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gerenciamento - Dono de Fazenda | Efficientia</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">

    <style>
        :root {
            --bg-color: #0b111e;
            --surface-color: #131c2e;
            --surface-alt: #1a273f;
            --border-color: #243552;
            --primary-green: #56D867;
            --primary-hover: #48be57;
            --text-main: #f8fafc;
            --text-muted: #94a3b8;
            --accent-glow: rgba(86, 216, 103, 0.2);
            --danger: #ef4444;
            --danger-hover: #dc2626;
            --danger-glow: rgba(239, 68, 68, 0.25);
            --radius-md: 10px;
            --radius-lg: 16px;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background-color: var(--bg-color);
            color: var(--text-main);
            font-family: 'IBM Plex Sans', sans-serif;
            min-height: 100vh;
            padding: 2.5rem 1.5rem;
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .container {
            width: 100%;
            max-width: 1100px;
        }

        /* Cabeçalho */
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1.5rem;
            border-bottom: 1px solid var(--border-color);
        }

        .header-title-box {
            display: flex;
            flex-direction: column;
            gap: 0.3rem;
        }

        .header h1 {
            font-family: 'Manrope', sans-serif;
            font-size: 1.85rem;
            font-weight: 800;
            letter-spacing: -0.02em;
        }

        .header h1 span {
            color: var(--primary-green);
        }

        .header p {
            color: var(--text-muted);
            font-size: 0.95rem;
        }

        .btn-voltar {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.6rem 1.1rem;
            background: var(--surface-alt);
            color: var(--text-main);
            text-decoration: none;
            border-radius: var(--radius-md);
            border: 1px solid var(--border-color);
            font-size: 0.9rem;
            font-weight: 500;
            transition: all 0.2s ease;
        }

        .btn-voltar:hover {
            border-color: var(--primary-green);
            color: var(--primary-green);
        }

        /* Abas (Tabs) */
        .tabs-header {
            display: flex;
            gap: 0.75rem;
            margin-bottom: -1px;
            position: relative;
            z-index: 1;
        }

        .tab-button {
            display: inline-flex;
            align-items: center;
            gap: 0.6rem;
            background: var(--surface-alt);
            color: var(--text-muted);
            border: 1px solid var(--border-color);
            border-bottom: none;
            border-radius: var(--radius-md) var(--radius-md) 0 0;
            padding: 0.85rem 1.6rem;
            font-family: 'Manrope', sans-serif;
            font-size: 0.95rem;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .tab-button:hover {
            color: var(--text-main);
            background: #1f304f;
        }

        .tab-button.active {
            background-color: var(--surface-color);
            color: var(--primary-green);
            border-color: var(--border-color);
            border-top: 3px solid var(--primary-green);
            padding-top: calc(0.85rem - 2px);
        }

        .tab-button.tab-danger.active {
            color: var(--danger);
            border-top: 3px solid var(--danger);
        }

        /* Card de Conteúdo */
        .card {
            background-color: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: 0 var(--radius-lg) var(--radius-lg) var(--radius-lg);
            padding: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
            margin-bottom: 3rem;
        }

        .tab-content {
            display: none;
        }

        .tab-content.active {
            display: block;
            animation: fadeIn 0.25s ease-in-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(4px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .card-header {
            margin-bottom: 1.75rem;
        }

        .card-header h2 {
            font-family: 'Manrope', sans-serif;
            font-size: 1.35rem;
            font-weight: 700;
            color: var(--text-main);
            display: flex;
            align-items: center;
            gap: 0.6rem;
        }

        .card-header h2.title-green::before {
            content: '';
            display: inline-block;
            width: 4px;
            height: 1.3rem;
            background-color: var(--primary-green);
            border-radius: 4px;
        }

        .card-header h2.title-red::before {
            content: '';
            display: inline-block;
            width: 4px;
            height: 1.3rem;
            background-color: var(--danger);
            border-radius: 4px;
        }

        .card-header p {
            color: var(--text-muted);
            font-size: 0.9rem;
            margin-top: 0.3rem;
        }

        /* Form Grid */
        .form-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 1.25rem 1.5rem;
        }

        .form-group-full {
            grid-column: span 2;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 0.45rem;
        }

        label {
            font-size: 0.88rem;
            font-weight: 600;
            color: var(--text-main);
            display: flex;
            justify-content: space-between;
        }

        .helper-text {
            font-size: 0.78rem;
            color: var(--text-muted);
            font-weight: 400;
        }

        input[type="text"],
        input[type="number"],
        input[type="email"],
        input[type="tel"],
        input[type="date"],
        input[type="password"],
        select {
            width: 100%;
            background-color: var(--surface-alt);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 0.75rem 1rem;
            color: var(--text-main);
            font-family: 'IBM Plex Sans', sans-serif;
            font-size: 0.95rem;
            transition: all 0.2s ease;
            outline: none;
        }

        input:focus,
        select:focus {
            border-color: var(--primary-green);
            box-shadow: 0 0 0 3px var(--accent-glow);
        }

        .danger-input:focus {
            border-color: var(--danger) !important;
            box-shadow: 0 0 0 3px var(--danger-glow) !important;
        }

        input::placeholder {
            color: #64748b;
        }

        /* Botões */
        .form-actions {
            margin-top: 1.8rem;
            display: flex;
            justify-content: flex-end;
            gap: 1rem;
        }

        .btn {
            cursor: pointer;
            padding: 0.8rem 1.6rem;
            border-radius: var(--radius-md);
            font-weight: 600;
            font-size: 0.95rem;
            transition: all 0.2s ease;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            border: none;
            text-decoration: none;
        }

        .btn-primary {
            background-color: var(--primary-green);
            color: #08110b;
        }

        .btn-primary:hover {
            background-color: var(--primary-hover);
            transform: translateY(-1px);
            box-shadow: 0 4px 14px var(--accent-glow);
        }

        .btn-danger {
            background-color: var(--danger);
            color: #ffffff;
        }

        .btn-danger:hover {
            background-color: var(--danger-hover);
            transform: translateY(-1px);
            box-shadow: 0 4px 14px var(--danger-glow);
        }

        .btn-secondary {
            background: transparent;
            color: var(--text-muted);
            border: 1px solid var(--border-color);
        }

        .btn-secondary:hover {
            color: var(--text-main);
            border-color: var(--text-muted);
        }

        .btn-sm-danger {
            background: rgba(239, 68, 68, 0.12);
            color: #f87171;
            border: 1px solid rgba(239, 68, 68, 0.3);
            padding: 0.35rem 0.75rem;
            font-size: 0.82rem;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
            transition: all 0.15s ease;
        }

        .btn-sm-danger:hover {
            background: var(--danger);
            color: #ffffff;
            border-color: var(--danger);
        }

        .btn-sm-edit {
            background: rgba(86, 216, 103, 0.12);
            color: var(--primary-green);
            border: 1px solid rgba(86, 216, 103, 0.3);
            padding: 0.35rem 0.75rem;
            font-size: 0.82rem;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
            text-decoration: none;
            transition: all 0.15s ease;
        }

        .btn-sm-edit:hover {
            background: var(--primary-green);
            color: #08110b;
            border-color: var(--primary-green);
        }

        /* Seção de Listagem */
        .list-section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.2rem;
        }

        .list-section-header h2 {
            font-family: 'Manrope', sans-serif;
            font-size: 1.35rem;
            font-weight: 700;
        }

        .badge-count {
            background: var(--surface-alt);
            color: var(--primary-green);
            border: 1px solid var(--border-color);
            padding: 0.35rem 0.85rem;
            border-radius: 9999px;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .table-responsive {
            width: 100%;
            overflow-x: auto;
            border-radius: var(--radius-lg);
            border: 1px solid var(--border-color);
            background-color: var(--surface-color);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
            font-size: 0.92rem;
        }

        thead {
            background-color: var(--surface-alt);
            border-bottom: 1px solid var(--border-color);
        }

        th {
            padding: 1rem 1.2rem;
            font-weight: 600;
            color: var(--text-muted);
            text-transform: uppercase;
            font-size: 0.76rem;
            letter-spacing: 0.05em;
        }

        td {
            padding: 0.95rem 1.2rem;
            border-bottom: 1px solid var(--border-color);
            color: var(--text-main);
            vertical-align: middle;
        }

        tbody tr:last-child td {
            border-bottom: none;
        }

        tbody tr:hover {
            background-color: rgba(255, 255, 255, 0.02);
        }

        .text-code {
            font-family: monospace;
            font-size: 0.86rem;
            background: var(--surface-alt);
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            border: 1px solid var(--border-color);
        }

        .empty-state {
            padding: 3.5rem 1.5rem;
            text-align: center;
            color: var(--text-muted);
        }

        .empty-state svg {
            margin-bottom: 1rem;
            opacity: 0.4;
        }

        .empty-state p {
            font-size: 1rem;
        }

        /* Modal de Confirmação */
        .modal-overlay {
            position: fixed;
            inset: 0;
            background: rgba(4, 8, 16, 0.75);
            backdrop-filter: blur(4px);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 999;
            padding: 1.5rem;
        }

        .modal-overlay.open {
            display: flex;
            animation: fadeIn 0.15s ease-out;
        }

        .modal-card {
            background-color: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            width: 100%;
            max-width: 460px;
            padding: 2rem;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5);
            text-align: center;
        }

        .modal-icon-box {
            width: 56px;
            height: 56px;
            border-radius: 50%;
            background: rgba(239, 68, 68, 0.12);
            color: var(--danger);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 1.2rem;
        }

        .modal-title {
            font-family: 'Manrope', sans-serif;
            font-size: 1.3rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
        }

        .modal-desc {
            color: var(--text-muted);
            font-size: 0.95rem;
            margin-bottom: 0.5rem;
        }

        .modal-detail {
            background: var(--surface-alt);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 0.75rem 1rem;
            font-size: 0.9rem;
            color: var(--text-main);
            margin-bottom: 1.5rem;
            word-break: break-word;
        }

        .modal-footer {
            display: flex;
            justify-content: center;
            gap: 0.8rem;
        }

        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }
            .form-group-full {
                grid-column: span 1;
            }
            .header {
                flex-direction: column;
                align-items: flex-start;
                gap: 1rem;
            }
            .form-actions {
                flex-direction: column-reverse;
            }
            .btn {
                width: 100%;
            }
            .tabs-header {
                flex-direction: column;
                gap: 0.25rem;
            }
            .tab-button {
                border-radius: var(--radius-md);
                border-bottom: 1px solid var(--border-color);
            }
            .card {
                border-radius: var(--radius-lg);
            }
        }
    </style>
</head>
<body>

<%
    Object objeto = request.getAttribute("donoFazendaModels");
    List<DonoFazendaModel> lista = null;
    if (objeto instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<DonoFazendaModel> castedList = (List<DonoFazendaModel>) objeto;
        lista = castedList;
    }
    int total = (lista != null) ? lista.size() : 0;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>

<div class="container">
    <!-- Cabeçalho -->
    <header class="header">
        <div class="header-title-box">
            <h1>Efficientia <span>| Gestão Rural</span></h1>
            <p>Módulo de Gerenciamento de Donos de Fazenda</p>
        </div>
        <a href="${pageContext.request.contextPath}/" class="btn-voltar">
            &larr; Página Principal
        </a>
    </header>

    <!-- Cabeçalho das Abas -->
    <div class="tabs-header">
        <button type="button" id="tabBtnCadastrar" class="tab-button active" onclick="switchTab('cadastrar')">
            <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Cadastrar Novo Dono
        </button>
        <button type="button" id="tabBtnExcluir" class="tab-button tab-danger" onclick="switchTab('excluir')">
            <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
            Excluir Dono
        </button>
    </div>

    <!-- Card Principal -->
    <div class="card">
        <!-- ============================================== -->
        <!-- ABA 1: FORMULÁRIO DE CADASTRO -->
        <!-- ============================================== -->
        <div id="tabCadastrar" class="tab-content active">
            <div class="card-header">
                <h2 class="title-green">Cadastrar Novo Dono de Fazenda</h2>
                <p>Preencha os dados abaixo para cadastrar um novo proprietário no sistema.</p>
            </div>

            <form action="${pageContext.request.contextPath}/donoFazenda" method="post" id="formDonoFazenda">
                <!-- Ação oculta padrão (slide 99) -->
                <input type="hidden" name="acao" value="cadastrar">

                <div class="form-grid">
                    <!-- Nome -->
                    <div class="form-group form-group-full">
                        <label for="nome">Nome Completo *</label>
                        <input type="text" id="nome" name="nome" placeholder="Ex: Roberto Carlos de Oliveira" required>
                    </div>

                    <!-- CPF -->
                    <div class="form-group">
                        <label for="cpf">
                            CPF *
                            <span class="helper-text">11 dígitos (apenas números)</span>
                        </label>
                        <input type="text" id="cpf" name="cpf" maxlength="11" placeholder="Ex: 12345678901" pattern="[0-9]{11}" required title="O CPF deve conter exatamente 11 números">
                    </div>

                    <!-- Data de Nascimento -->
                    <div class="form-group">
                        <label for="dataNascimento">Data de Nascimento *</label>
                        <input type="date" id="dataNascimento" name="dataNascimento" required>
                    </div>

                    <!-- E-mail -->
                    <div class="form-group">
                        <label for="email">E-mail *</label>
                        <input type="email" id="email" name="email" placeholder="roberto@fazenda.com" required>
                    </div>

                    <!-- Telefone -->
                    <div class="form-group">
                        <label for="telefone">
                            Telefone / Celular
                            <span class="helper-text">Até 11 dígitos com DDD</span>
                        </label>
                        <input type="tel" id="telefone" name="telefone" maxlength="11" placeholder="Ex: 11987654321" pattern="[0-9]{10,11}" title="Digite o DDD e o número (10 ou 11 dígitos numéricos)">
                    </div>

                    <!-- Assinatura -->
                    <div class="form-group">
                        <label for="assinatura">
                            Assinatura *
                            <span class="helper-text">Texto normal (futuro hash)</span>
                        </label>
                        <input type="text" id="assinatura" name="assinatura" placeholder="Digite a assinatura" required>
                    </div>

                    <!-- Senha -->
                    <div class="form-group">
                        <label for="senha">Senha de Acesso *</label>
                        <input type="password" id="senha" name="senha" placeholder="Crie uma senha de acesso" required>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="reset" class="btn btn-secondary">Limpar Formulário</button>
                    <button type="submit" class="btn btn-primary">Cadastrar Dono de Fazenda</button>
                </div>
            </form>
        </div>

        <!-- ============================================== -->
        <!-- ABA 2: EXCLUSÃO DE DONO DE FAZENDA -->
        <!-- ============================================== -->
        <div id="tabExcluir" class="tab-content">
            <div class="card-header">
                <h2 class="title-red">Excluir Dono de Fazenda</h2>
                <p>Selecione um proprietário cadastrado ou informe o ID para realizar a exclusão.</p>
            </div>

            <c:choose>
                <c:when test="${empty donoFazendaModels}">
                    <div class="empty-state">
                        <p>Não há proprietários cadastrados disponíveis para exclusão.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <form id="formAbaExcluir" action="${pageContext.request.contextPath}/donoFazenda" method="post" onsubmit="return handleAbaExcluirSubmit(event)">
                        <input type="hidden" name="acao" value="excluir">

                        <div class="form-grid">
                            <div class="form-group form-group-full">
                                <label for="selectExcluirDono">Selecione o Dono de Fazenda para Excluir *</label>
                                <select id="selectExcluirDono" name="id" class="danger-input" required>
                                    <option value="" disabled selected>Escolha um proprietário...</option>
                                    <c:forEach var="dono" items="${donoFazendaModels}">
                                        <option value="${dono.id}" data-nome="${dono.nome}">
                                            ID #${dono.id} - ${dono.nome} (CPF: ${dono.cpf})
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-actions">
                            <button type="button" class="btn btn-secondary" onclick="switchTab('cadastrar')">Cancelar</button>
                            <button type="submit" class="btn btn-danger">
                                <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                </svg>
                                Excluir Dono de Fazenda
                            </button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- ============================================== -->
    <!-- LISTA DE CADASTROS COM AÇÕES -->
    <!-- ============================================== -->
    <div class="list-section-header">
        <h2>Donos de Fazenda Cadastrados</h2>
        <span class="badge-count">${not empty donoFazendaModels ? donoFazendaModels.size() : 0} registros</span>
    </div>

    <div class="table-responsive">
        <c:choose>
            <c:when test="${empty donoFazendaModels}">
                <div class="empty-state">
                    <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    <p>Nenhum dono de fazenda cadastrado até o momento.</p>
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
                        <c:forEach var="dono" items="${donoFazendaModels}">
                            <tr>
                                <td><strong>#<c:out value="${dono.id}" /></strong></td>
                                <td><c:out value="${dono.nome}" default="-" /></td>
                                <td><c:out value="${dono.cpf}" default="-" /></td>
                                <td><c:out value="${dono.email}" default="-" /></td>
                                <td><c:out value="${dono.telefone}" default="-" /></td>
                                <td><c:out value="${dono.dataNascimento}" default="-" /></td>
                                <td>
                                    <span class="text-code"><c:out value="${dono.assinatura}" default="-" /></span>
                                </td>
                                <td style="text-align: center;">
                                    <div style="display: flex; align-items: center; justify-content: center; gap: 0.4rem;">
                                        <a href="${pageContext.request.contextPath}/donoFazenda?acao=editar&id=${dono.id}" class="btn-sm-edit">
                                            <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                            </svg>
                                            Editar
                                        </a>
                                        <form action="${pageContext.request.contextPath}/donoFazenda" method="post" style="display: inline-block; margin: 0;">
                                            <input type="hidden" name="acao" value="excluir">
                                            <input type="hidden" name="id" value="${dono.id}">
                                            <button type="submit" class="btn-sm-danger" onclick="return confirm('Deseja excluir este dono de fazenda?')">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                </svg>
                                                Excluir
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- ============================================== -->
<!-- MODAL DE CONFIRMAÇÃO DE EXCLUSÃO -->
<!-- ============================================== -->
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

        <form id="formConfirmModal" action="${pageContext.request.contextPath}/donoFazenda" method="post">
            <input type="hidden" name="acao" value="excluir">
            <input type="hidden" id="modalInputId" name="id" value="">

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="fecharConfirmacaoExcluir()">Cancelar</button>
                <button type="submit" class="btn btn-danger">Sim, Excluir</button>
            </div>
        </form>
    </div>
</div>

<script>
    // Alternância de Abas
    function switchTab(tabName) {
        const tabCadastrar = document.getElementById('tabCadastrar');
        const tabExcluir = document.getElementById('tabExcluir');
        const btnCadastrar = document.getElementById('tabBtnCadastrar');
        const btnExcluir = document.getElementById('tabBtnExcluir');

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

    // Modal de Confirmação
    function abrirConfirmacaoExcluir(id, nome) {
        document.getElementById('modalInputId').value = id;
        document.getElementById('modalDetalhe').textContent = 'Registro #' + id + (nome ? ' - ' + nome : '');
        document.getElementById('modalConfirmacao').classList.add('open');
    }

    function fecharConfirmacaoExcluir() {
        document.getElementById('modalConfirmacao').classList.remove('open');
    }

    // Fechar ao clicar fora do modal
    document.getElementById('modalConfirmacao').addEventListener('click', function(e) {
        if (e.target === this) {
            fecharConfirmacaoExcluir();
        }
    });

    // Submissão da Aba Excluir com confirmação
    function handleAbaExcluirSubmit(event) {
        event.preventDefault();
        const select = document.getElementById('selectExcluirDono');
        const id = select.value;
        if (!id) return false;

        const selectedOption = select.options[select.selectedIndex];
        const nome = selectedOption.getAttribute('data-nome') || '';

        abrirConfirmacaoExcluir(id, nome);
        return false;
    }

    // Sanitização de CPF e Telefone (apenas números)
    const cpfInput = document.getElementById('cpf');
    const telInput = document.getElementById('telefone');

    [cpfInput, telInput].forEach(input => {
        if (!input) return;
        input.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/\D/g, '');
        });
    });
</script>

</body>
</html>