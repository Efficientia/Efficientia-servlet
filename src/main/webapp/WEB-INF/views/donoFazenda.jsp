<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="java.util.List" %>
        <%@ page import="com.efficientia.efficientia.model.DonoFazendaModel" %>
            <%@ page import="java.time.format.DateTimeFormatter" %>

                <!DOCTYPE html>
                <html lang="pt-BR">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Cadastro - Dono de Fazenda | Efficientia</title>

                    <!-- Google Fonts -->
                    <link rel="preconnect" href="https://fonts.googleapis.com">
                    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                    <link
                        href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;500;600;700&family=Manrope:wght@500;600;700;800&display=swap"
                        rel="stylesheet">

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

                        /* Card do Formulário */
                        .card {
                            background-color: var(--surface-color);
                            border: 1px solid var(--border-color);
                            border-radius: var(--radius-lg);
                            padding: 2rem;
                            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
                            margin-bottom: 3rem;
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

                        .card-header h2::before {
                            content: '';
                            display: inline-block;
                            width: 4px;
                            height: 1.3rem;
                            background-color: var(--primary-green);
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

                        input::placeholder {
                            color: #64748b;
                        }

                        /* Botões do Formulário */
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

                        /* Seção da Tabela / Listagem */
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
                            padding: 1rem 1.2rem;
                            border-bottom: 1px solid var(--border-color);
                            color: var(--text-main);
                        }

                        tbody tr:last-child td {
                            border-bottom: none;
                        }

                        tbody tr:hover {
                            background-color: rgba(255, 255, 255, 0.02);
                        }

                        .tag-plano {
                            display: inline-block;
                            padding: 0.25rem 0.65rem;
                            border-radius: 6px;
                            background: rgba(86, 216, 103, 0.12);
                            color: var(--primary-green);
                            font-size: 0.82rem;
                            font-weight: 600;
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
                                <p>Módulo de Gerenciamento de Donos de Fazenda</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/" class="btn-voltar">
                                &larr; Página Principal
                            </a>
                        </header>

                        <!-- Formulário de Inserção -->
                        <div class="card">
                            <div class="card-header">
                                <h2>Cadastrar Novo Dono de Fazenda</h2>
                                <p>Preencha os dados abaixo para cadastrar um novo proprietário no sistema.</p>
                            </div>

                            <form action="${pageContext.request.contextPath}/donoFazenda" method="post"
                                id="formDonoFazenda">
                                <div class="form-grid">
                                    <!-- Nome -->
                                    <div class="form-group form-group-full">
                                        <label for="nome">Nome Completo *</label>
                                        <input type="text" id="nome" name="nome"
                                            placeholder="Ex: Roberto Carlos de Oliveira" required>
                                    </div>

                                    <!-- CPF -->
                                    <div class="form-group">
                                        <label for="cpf">
                                            CPF *
                                            <span class="helper-text">11 dígitos (apenas números)</span>
                                        </label>
                                        <input type="text" id="cpf" name="cpf" maxlength="11"
                                            placeholder="Ex: 12345678901" pattern="[0-9]{11}" required
                                            title="O CPF deve conter exatamente 11 números">
                                    </div>

                                    <!-- Data de Nascimento -->
                                    <div class="form-group">
                                        <label for="dataNascimento">Data de Nascimento *</label>
                                        <input type="date" id="dataNascimento" name="dataNascimento" required>
                                    </div>

                                    <!-- E-mail -->
                                    <div class="form-group">
                                        <label for="email">E-mail *</label>
                                        <input type="email" id="email" name="email" placeholder="roberto@fazenda.com"
                                            required>
                                    </div>

                                    <!-- Telefone -->
                                    <div class="form-group">
                                        <label for="telefone">
                                            Telefone / Celular
                                            <span class="helper-text">Até 11 dígitos com DDD</span>
                                        </label>
                                        <input type="tel" id="telefone" name="telefone" maxlength="11"
                                            placeholder="Ex: 11987654321" pattern="[0-9]{10,11}"
                                            title="Digite o DDD e o número (10 ou 11 dígitos numéricos)">
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
                                        <input type="password" id="senha" name="senha"
                                            placeholder="Crie uma senha de acesso" required>
                                    </div>
                                </div>

                                <div class="form-actions">
                                    <button type="reset" class="btn btn-secondary">Limpar Formulário</button>
                                    <button type="submit" class="btn btn-primary">Cadastrar Dono de Fazenda</button>
                                </div>
                            </form>
                        </div>

                        <!-- Lista de Cadastros -->
                        <% Object objeto=request.getAttribute("donoFazendaModels"); List<DonoFazendaModel> lista = null;
                            if (objeto instanceof List
                            <?>) {
            @SuppressWarnings("unchecked")
            List<DonoFazendaModel> castedList = (List<DonoFazendaModel>) objeto;
            lista = castedList;
        }
        int total = (lista != null) ? lista.size() : 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    %>

    <div class="list-section-header">
        <h2>Donos de Fazenda Cadastrados</h2>
        <span class="badge-count"><%= total %> <%= (total == 1 ? "registro" : "registros") %></span>
    </div>

    <div class="table-responsive">
        <% if (lista == null || lista.isEmpty()) { %>
            <div class="empty-state">
                <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <p>Nenhum dono de fazenda cadastrado até o momento.</p>
            </div>
        <% } else { %>
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
                    </tr>
                </thead>
                <tbody>
                    <% for (DonoFazendaModel dono : lista) { %>
                        <tr>
                            <td><strong>#<%= dono.getId() %></strong></td>
                            <td><%= dono.getNome() != null ? dono.getNome() : "-" %></td>
                            <td><%= dono.getCpf() != null ? dono.getCpf() : "-" %></td>
                            <td><%= dono.getEmail() != null ? dono.getEmail() : "-" %></td>
                            <td><%= dono.getTelefone() != null ? dono.getTelefone() : "-" %></td>
                            <td>
                                <%= dono.getDataNascimento() != null ? dono.getDataNascimento().format(dtf) : "-" %>
                            </td>
                            <td>
                                <span style="font-family: monospace; font-size: 0.88rem; background: var(--surface-alt); padding: 0.25rem 0.6rem; border-radius: 4px; border: 1px solid var(--border-color);"><%= dono.getAssinatura() != null ? dono.getAssinatura() : "-" %></span>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
</div>

<script>
    // Limpeza de caracteres não numéricos em CPF e Telefone para evitar que passem de 11 caracteres
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