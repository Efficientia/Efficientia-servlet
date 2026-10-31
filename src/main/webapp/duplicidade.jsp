<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aviso de Duplicidade | Efficientia</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap" rel="stylesheet">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/Favicon_64x64.png" type="image/x-icon">

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
            --danger-glow: rgba(239, 68, 68, 0.25);
            --warning: #f59e0b;
            --warning-glow: rgba(245, 158, 11, 0.25);
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
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 2rem 1.5rem;
            position: relative;
            overflow-x: hidden;
        }

        /* Fundo com efeito radial */
        body::before {
            content: '';
            position: absolute;
            top: 20%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 600px;
            height: 600px;
            background: radial-gradient(circle, rgba(239, 68, 68, 0.08) 0%, transparent 70%);
            pointer-events: none;
            z-index: 0;
        }

        .container {
            width: 100%;
            max-width: 620px;
            position: relative;
            z-index: 1;
        }

        /* Topo com Logo */
        .header-logo {
            text-align: center;
            margin-bottom: 2rem;
        }

        .header-logo img {
            max-height: 48px;
            width: auto;
        }

        /* Card de Erro */
        .error-card {
            background-color: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 2.75rem 2.25rem;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.45);
            text-align: center;
            position: relative;
        }

        .error-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, var(--danger), var(--warning));
            border-radius: var(--radius-lg) var(--radius-lg) 0 0;
        }

        /* Ícone central */
        .icon-wrapper {
            width: 72px;
            height: 72px;
            border-radius: 50%;
            background: rgba(239, 68, 68, 0.12);
            border: 1px solid rgba(239, 68, 68, 0.3);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 1.5rem;
            color: var(--danger);
            box-shadow: 0 0 25px var(--danger-glow);
            animation: pulseIcon 2.5s infinite;
        }

        @keyframes pulseIcon {
            0%, 100% {
                transform: scale(1);
                box-shadow: 0 0 25px var(--danger-glow);
            }
            50% {
                transform: scale(1.05);
                box-shadow: 0 0 35px rgba(239, 68, 68, 0.4);
            }
        }

        .title {
            font-family: 'Manrope', sans-serif;
            font-size: 1.65rem;
            font-weight: 800;
            letter-spacing: -0.02em;
            margin-bottom: 0.75rem;
            color: var(--text-main);
        }

        .subtitle {
            color: var(--text-muted);
            font-size: 1rem;
            line-height: 1.55;
            margin-bottom: 1.75rem;
        }

        /* Detalhes do Erro */
        .details-box {
            background-color: var(--surface-alt);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            padding: 1.25rem 1.5rem;
            margin-bottom: 2rem;
            text-align: left;
        }

        .details-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.5rem 0;
            font-size: 0.92rem;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
        }

        .details-row:last-child {
            border-bottom: none;
            padding-bottom: 0;
        }

        .details-row:first-child {
            padding-top: 0;
        }

        .details-label {
            color: var(--text-muted);
            font-weight: 500;
        }

        .details-value {
            color: var(--text-main);
            font-weight: 600;
            word-break: break-all;
            text-align: right;
        }

        .details-badge {
            display: inline-block;
            background: rgba(239, 68, 68, 0.15);
            color: #fca5a5;
            border: 1px solid rgba(239, 68, 68, 0.35);
            padding: 0.2rem 0.6rem;
            border-radius: 6px;
            font-size: 0.8rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.04em;
        }

        /* Botões de Ação */
        .actions-group {
            display: flex;
            gap: 1rem;
            justify-content: center;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            padding: 0.85rem 1.6rem;
            border-radius: var(--radius-md);
            font-family: 'Manrope', sans-serif;
            font-size: 0.95rem;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s ease;
            border: none;
        }

        .btn-primary {
            background-color: var(--primary-green);
            color: #08110b;
        }

        .btn-primary:hover {
            background-color: var(--primary-hover);
            transform: translateY(-2px);
            box-shadow: 0 6px 20px var(--accent-glow);
        }

        .btn-secondary {
            background: transparent;
            color: var(--text-muted);
            border: 1px solid var(--border-color);
        }

        .btn-secondary:hover {
            color: var(--text-main);
            border-color: var(--text-muted);
            background-color: var(--surface-alt);
        }

        @media (max-width: 600px) {
            .error-card {
                padding: 2rem 1.5rem;
            }
            .actions-group {
                flex-direction: column;
            }
            .btn {
                width: 100%;
            }
            .details-row {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.25rem;
            }
            .details-value {
                text-align: left;
            }
        }
    </style>
</head>
<body>

<div class="container">
    <div class="header-logo">
        <a href="${pageContext.request.contextPath}/">
            <img src="${pageContext.request.contextPath}/assets/logo_efficientia.png" alt="Efficientia Logo">
        </a>
    </div>

    <div class="error-card">
        <div class="icon-wrapper">
            <svg width="36" height="36" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
        </div>

        <h1 class="title">
            <c:choose>
                <c:when test="${not empty param.tipoErro}">Registro Duplicado Detectado</c:when>
                <c:otherwise>Falha no Processamento</c:otherwise>
            </c:choose>
        </h1>

        <p class="subtitle">
            <c:choose>
                <c:when test="${param.tipoErro == 'cpf_duplicado'}">
                    Já existe um cadastro no sistema com o <strong>CPF</strong> informado.
                </c:when>
                <c:when test="${param.tipoErro == 'cnpj_duplicado'}">
                    Já existe uma empresa cadastrada com o <strong>CNPJ</strong> informado.
                </c:when>
                <c:when test="${param.tipoErro == 'email_duplicado'}">
                    Já existe um usuário no sistema utilizando este <strong>E-mail</strong>.
                </c:when>
                <c:when test="${param.tipoErro == 'placa_duplicada'}">
                    Já existe um veículo registrado com a <strong>Placa</strong> informada.
                </c:when>
                <c:when test="${param.tipoErro == 'codigo_duplicado'}">
                    Já existe um registro com este <strong>Código</strong> corporativo.
                </c:when>
                <c:when test="${param.tipoErro == 'documento_fiscal_duplicado'}">
                    Já existe um trajeto com este <strong>Documento Fiscal (Guia GTA / NF-e)</strong>.
                </c:when>
                <c:when test="${not empty param.mensagem}">
                    <c:out value="${param.mensagem}" />
                </c:when>
                <c:otherwise>
                    Os dados informados entram em conflito com registros já existentes na base de dados.
                </c:otherwise>
            </c:choose>
        </p>

        <div class="details-box">
            <c:if test="${not empty param.entidade}">
                <div class="details-row">
                    <span class="details-label">Entidade de Negócio:</span>
                    <span class="details-value"><c:out value="${param.entidade}" /></span>
                </div>
            </c:if>

            <c:if test="${not empty param.campo}">
                <div class="details-row">
                    <span class="details-label">Campo em Conflito:</span>
                    <span class="details-value"><c:out value="${param.campo}" /></span>
                </div>
            </c:if>

            <c:if test="${not empty param.valor}">
                <div class="details-row">
                    <span class="details-label">Valor Informado:</span>
                    <span class="details-value"><c:out value="${param.valor}" /></span>
                </div>
            </c:if>

            <div class="details-row">
                <span class="details-label">Regra de Integridade:</span>
                <span class="details-badge">Restrição UNIQUE</span>
            </div>
        </div>

        <div class="actions-group">
            <c:choose>
                <c:when test="${not empty param.voltar}">
                    <a href="<c:out value='${param.voltar}' />" class="btn btn-primary">
                        &larr; Voltar e Corrigir
                    </a>
                </c:when>
                <c:otherwise>
                    <button type="button" class="btn btn-primary" onclick="if (window.history.length > 1) { window.history.back(); } else { window.location.href = '${pageContext.request.contextPath}/'; }">
                        &larr; Voltar e Corrigir
                    </button>
                </c:otherwise>
            </c:choose>

            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                Página Inicial
            </a>
        </div>
    </div>
</div>

</body>
</html>
