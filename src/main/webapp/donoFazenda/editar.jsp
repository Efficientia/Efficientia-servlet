<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.efficientia.efficientia.model.DonoFazendaModel" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Dono de Fazenda | Efficientia</title>

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
            max-width: 900px;
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

        /* Card */
        .card {
            background-color: var(--surface-color);
            border: 1px solid var(--border-color);
            border-radius: var(--radius-lg);
            padding: 2rem;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
        }

        .card-header {
            margin-bottom: 1.75rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 0.8rem;
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

        .card-header h2::before {
            content: '';
            display: inline-block;
            width: 4px;
            height: 1.3rem;
            background-color: var(--primary-green);
            border-radius: 4px;
        }

        .badge-id {
            background: var(--surface-alt);
            color: var(--primary-green);
            border: 1px solid var(--border-color);
            padding: 0.35rem 0.85rem;
            border-radius: 9999px;
            font-size: 0.85rem;
            font-weight: 600;
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
        input[type="email"],
        input[type="tel"],
        input[type="date"],
        input[type="password"] {
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

        input:focus {
            border-color: var(--primary-green);
            box-shadow: 0 0 0 3px var(--accent-glow);
        }

        input::placeholder {
            color: #64748b;
        }

        /* Botões */
        .form-actions {
            margin-top: 2rem;
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

        .btn-secondary {
            background: transparent;
            color: var(--text-muted);
            border: 1px solid var(--border-color);
        }

        .btn-secondary:hover {
            color: var(--text-main);
            border-color: var(--text-muted);
        }

        .empty-card {
            text-align: center;
            padding: 3rem 1.5rem;
            color: var(--text-muted);
        }

        .empty-card p {
            margin-bottom: 1.5rem;
            font-size: 1.05rem;
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
        }
    </style>
</head>
<body>

<div class="container">
    <!-- Cabeçalho -->
    <header class="header">
        <div class="header-title-box">
            <h1>Efficientia <span>| Gestão Rural</span></h1>
            <p>Edição de Cadastro de Dono de Fazenda</p>
        </div>
        <a href="${pageContext.request.contextPath}/donoFazenda" class="btn-voltar">
            &larr; Voltar para a Lista
        </a>
    </header>

    <div class="card">
        <c:choose>
            <c:when test="${empty donoFazendaModel}">
                <div class="empty-card">
                    <p>Proprietário não encontrado ou ID inválido.</p>
                    <a href="${pageContext.request.contextPath}/donoFazenda" class="btn btn-secondary">
                        Voltar para Donos de Fazenda
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card-header">
                    <h2>Editar Dono de Fazenda</h2>
                    <span class="badge-id">ID #${donoFazendaModel.id}</span>
                </div>

                <form action="${pageContext.request.contextPath}/donoFazenda" method="post" id="formEditarDono">
                    <!-- Ação e ID ocultos para o doPost (slide 110) -->
                    <input type="hidden" name="acao" value="atualizar">
                    <input type="hidden" name="id" value="${donoFazendaModel.id}">

                    <div class="form-grid">
                        <!-- Nome -->
                        <div class="form-group form-group-full">
                            <label for="nome">Nome Completo *</label>
                            <input type="text" id="nome" name="nome" value="<c:out value='${donoFazendaModel.nome}' />" placeholder="Ex: Roberto Carlos de Oliveira" required>
                        </div>

                        <!-- CPF -->
                        <div class="form-group">
                            <label for="cpf">
                                CPF *
                                <span class="helper-text">11 dígitos (apenas números)</span>
                            </label>
                            <input type="text" id="cpf" name="cpf" maxlength="11" value="<c:out value='${donoFazendaModel.cpf}' />" pattern="[0-9]{11}" required title="O CPF deve conter exatamente 11 números">
                        </div>

                        <!-- Data de Nascimento -->
                        <div class="form-group">
                            <label for="dataNascimento">Data de Nascimento *</label>
                            <input type="date" id="dataNascimento" name="dataNascimento" value="${donoFazendaModel.dataNascimento}" required>
                        </div>

                        <!-- E-mail -->
                        <div class="form-group">
                            <label for="email">E-mail *</label>
                            <input type="email" id="email" name="email" value="<c:out value='${donoFazendaModel.email}' />" placeholder="roberto@fazenda.com" required>
                        </div>

                        <!-- Telefone -->
                        <div class="form-group">
                            <label for="telefone">
                                Telefone / Celular
                                <span class="helper-text">Até 11 dígitos com DDD</span>
                            </label>
                            <input type="tel" id="telefone" name="telefone" maxlength="11" value="<c:out value='${donoFazendaModel.telefone}' />" pattern="[0-9]{10,11}" title="Digite o DDD e o número (10 ou 11 dígitos numéricos)">
                        </div>

                        <!-- Assinatura -->
                        <div class="form-group">
                            <label for="assinatura">
                                Assinatura *
                                <span class="helper-text">Texto normal (futuro hash)</span>
                            </label>
                            <input type="text" id="assinatura" name="assinatura" value="<c:out value='${donoFazendaModel.assinatura}' />" placeholder="Digite a assinatura" required>
                        </div>

                        <!-- Senha -->
                        <div class="form-group">
                            <label for="senha">Senha de Acesso *</label>
                            <input type="password" id="senha" name="senha" value="<c:out value='${donoFazendaModel.senha}' />" placeholder="Digite uma nova senha ou mantenha a atual" required>
                        </div>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/donoFazenda" class="btn btn-secondary">Cancelar</a>
                        <button type="submit" class="btn btn-primary">
                            <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                            </svg>
                            Salvar Alterações
                        </button>
                    </div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    // Limpeza de caracteres não numéricos em CPF e Telefone
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
