package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.PecuaristaDAO;
import com.efficientia.efficientia.exception.DadoDuplicadoException;
import com.efficientia.efficientia.model.PecuaristaModel;
import com.efficientia.efficientia.util.ValidadorRegex;
import com.efficientia.efficientia.util.WebErroUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet responsável pelo controle de requisições relacionadas à entidade Pecuarista.
 *
 * Mapeado no endpoint '/pecuarista', atua como Controller na arquitetura MVC do sistema Efficientia,
 * gerenciando operações de listagem, consulta, cadastro, atualização e exclusão
 * dos produtores rurais / pecuaristas que originam os lotes e animais transportados.
 */
@WebServlet(name = "PecuaristaServlet", value = "/pecuarista")
public class PecuaristaServlet extends HttpServlet {

    private PecuaristaDAO dao;

    // ==================== INICIALIZAÇÃO ====================

    /**
     * Inicializa os Data Access Objects (DAOs) necessários durante o ciclo de vida do servlet.
     */
    @Override
    public void init() {
        dao = new PecuaristaDAO();
    }

    // ==================== REQUISIÇÕES GET ====================

    /**
     * Processa requisições HTTP GET para consulta e exibição de pecuaristas.
     * Suporta a ação 'editar' para carregar os dados de um pecuarista específico,
     * bem como buscas por CPF, e-mail, telefone, busca textual unificada ou listagem geral.
     *
     * @param req  objeto {@link HttpServletRequest} contendo os parâmetros da requisição
     * @param resp objeto {@link HttpServletResponse} para direcionamento e resposta HTTP
     * @throws ServletException caso ocorra erro no despacho para a visão JSP
     * @throws IOException      caso ocorra erro de entrada/saída durante o encaminhamento
     */
    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String acao = req.getParameter("acao");

        // Edição: busca o pecuarista pelo ID e o disponibiliza para o formulário em modo de edição,
        // seguindo o fluxo normal de listagem (a mesma JSP exibe formulário + tabela)
        if ("editar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                PecuaristaModel pecuaristaModel = dao.buscar(id);

                if (pecuaristaModel != null) {
                    req.setAttribute("pecuaristaModel", pecuaristaModel);
                    req.setAttribute("pecuarista", pecuaristaModel);
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar pecuarista para edição: " + e.getMessage());
            }
        }

        // Busca flexível: CPF, e-mail, telefone, termo unificado (nome/CPF/e-mail/telefone) ou listagem geral
        String cpf = obterParametro(req, "cpf");
        String email = obterParametro(req, "email");
        String telefone = obterParametro(req, "telefone");
        String busca = obterParametro(req, "busca", "nome", "q", "pesquisa");
        List<PecuaristaModel> pecuaristaModels;

        // 1. Busca por CPF
        if (cpf != null && !cpf.isBlank()) {
            pecuaristaModels = dao.buscarPorCpf(cpf);
            req.setAttribute("termoBusca", cpf);
        // 2. Busca por e-mail
        } else if (email != null && !email.isBlank()) {
            pecuaristaModels = dao.buscarPorEmail(email);
            req.setAttribute("termoBusca", email);
        // 3. Busca por telefone
        } else if (telefone != null && !telefone.isBlank()) {
            pecuaristaModels = dao.buscarPorTelefone(telefone);
            req.setAttribute("termoBusca", telefone);
        // 4. Busca textual unificada com desduplicação por ID via LinkedHashSet
        } else if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();
            Set<Integer> ids = new LinkedHashSet<>();
            pecuaristaModels = new ArrayList<>();

            for (PecuaristaModel p : dao.buscarPorNome(termo)) {
                if (ids.add(p.getId())) pecuaristaModels.add(p);
            }
            for (PecuaristaModel p : dao.buscarPorCpf(termo)) {
                if (ids.add(p.getId())) pecuaristaModels.add(p);
            }
            for (PecuaristaModel p : dao.buscarPorEmail(termo)) {
                if (ids.add(p.getId())) pecuaristaModels.add(p);
            }
            for (PecuaristaModel p : dao.buscarPorTelefone(termo)) {
                if (ids.add(p.getId())) pecuaristaModels.add(p);
            }
            req.setAttribute("termoBusca", busca);
        // 5. Listagem geral de todos os pecuaristas cadastrados
        } else {
            pecuaristaModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }

        // Define os atributos na requisição e encaminha para a visão principal
        req.setAttribute("pecuaristaModels", pecuaristaModels);
        req.setAttribute("pecuaristas", pecuaristaModels);

        req.getRequestDispatcher(
                "/WEB-INF/views/pecuarista.jsp"
        ).forward(req, resp);
    }

    // ==================== REQUISIÇÕES POST ====================

    /**
     * Processa requisições HTTP POST para operações de modificação (CUD: Cadastro, Atualização e Exclusão).
     * Aplica o padrão Post-Redirect-Get (PRG) para evitar submissões duplicadas por recarregamento acidental.
     *
     * @param req  objeto {@link HttpServletRequest} contendo os dados enviados no formulário
     * @param resp objeto {@link HttpServletResponse} para redirecionamento após a operação
     * @throws IOException caso ocorra erro no redirecionamento HTTP
     */
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        // Garante suporte adequado a caracteres UTF-8
        req.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // Exclusão: remove o pecuarista a partir do identificador informado
        if ("excluir".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                boolean excluido = dao.excluir(id);
                if (!excluido) {
                    WebErroUtil.redirecionarErroGeral(req, resp, "Não foi possível excluir o pecuarista. Registro não encontrado.", "/pecuarista");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao excluir pecuarista: " + e.getMessage());
                WebErroUtil.redirecionarErroGeral(req, resp, "Erro ao excluir pecuarista: " + e.getMessage(), "/pecuarista");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/pecuarista?sucesso=excluido");
            return;
        }

        // Inserção simplificada de pecuarista
        if ("inserirSimples".equals(acao)) {
            String redirectUrl = req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista";
            try {
                String nome = obterParametro(req, "nome");
                String cpf = obterParametro(req, "cpf");
                String email = obterParametro(req, "email");
                String senha = obterParametro(req, "senha");

                if (!ValidadorRegex.isCpfValido(cpf)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=cpf_invalido");
                    return;
                }
                if (email != null && !email.isBlank() && !ValidadorRegex.isEmailValido(email)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=email_invalido");
                    return;
                }

                String cpfSanitizado = ValidadorRegex.apenasDigitos(cpf);
                boolean inserido = dao.inserirSimples(nome, cpfSanitizado, email, senha);
                if (!inserido) {
                    WebErroUtil.redirecionarErroGeral(req, resp, "Não foi possível cadastrar o pecuarista simplificado.", redirectUrl);
                    return;
                }
            } catch (DadoDuplicadoException e) {
                WebErroUtil.redirecionarErroDuplicidade(req, resp, e, redirectUrl);
                return;
            } catch (Exception e) {
                System.out.println("Erro ao inserir pecuarista simples: " + e.getMessage());
                WebErroUtil.redirecionarErroGeral(req, resp, "Erro ao cadastrar pecuarista simples: " + e.getMessage(), redirectUrl);
                return;
            }
            resp.sendRedirect(req.getContextPath() + redirectUrl);
            return;
        }

        // Atualização específica: modifica somente o CPF do pecuarista
        if ("atualizarCpf".equals(acao)) {
            String redirectUrl = req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista";
            try {
                int id = parseInt(req.getParameter("id"), 0);
                String novoCpf = obterParametro(req, "cpf", "novoCpf");
                if (!ValidadorRegex.isCpfValido(novoCpf)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=cpf_invalido");
                    return;
                }
                String cpfSanitizado = ValidadorRegex.apenasDigitos(novoCpf);
                boolean atualizado = dao.atualizarCpf(id, cpfSanitizado);
                if (!atualizado) {
                    WebErroUtil.redirecionarErroGeral(req, resp, "Não foi possível atualizar o CPF do pecuarista.", redirectUrl);
                    return;
                }
            } catch (DadoDuplicadoException e) {
                WebErroUtil.redirecionarErroDuplicidade(req, resp, e, redirectUrl);
                return;
            } catch (Exception e) {
                System.out.println("Erro ao atualizar CPF do pecuarista: " + e.getMessage());
                WebErroUtil.redirecionarErroGeral(req, resp, "Erro ao atualizar CPF do pecuarista: " + e.getMessage(), redirectUrl);
                return;
            }
            resp.sendRedirect(req.getContextPath() + redirectUrl);
            return;
        }

        // Atualização específica: modifica somente o nome do pecuarista
        if ("atualizarNome".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                String novoNome = obterParametro(req, "nome", "novoNome");
                dao.atualizarNome(id, novoNome);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar nome do pecuarista: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista"));
            return;
        }

        // Atualização específica: modifica somente o e-mail do pecuarista com validação REGEX
        if ("atualizarEmail".equals(acao)) {
            String redirectUrl = req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista";
            try {
                int id = parseInt(req.getParameter("id"), 0);
                String novoEmail = obterParametro(req, "email", "novoEmail");
                if (novoEmail != null && !novoEmail.isBlank() && !ValidadorRegex.isEmailValido(novoEmail)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=email_invalido");
                    return;
                }
                boolean atualizado = dao.atualizarEmail(id, novoEmail);
                if (!atualizado) {
                    WebErroUtil.redirecionarErroGeral(req, resp, "Não foi possível atualizar o e-mail do pecuarista.", redirectUrl);
                    return;
                }
            } catch (DadoDuplicadoException e) {
                WebErroUtil.redirecionarErroDuplicidade(req, resp, e, redirectUrl);
                return;
            } catch (Exception e) {
                System.out.println("Erro ao atualizar email do pecuarista: " + e.getMessage());
                WebErroUtil.redirecionarErroGeral(req, resp, "Erro ao atualizar email do pecuarista: " + e.getMessage(), redirectUrl);
                return;
            }
            resp.sendRedirect(req.getContextPath() + redirectUrl);
            return;
        }

        // Atualização específica: modifica somente o telefone/celular do pecuarista com validação REGEX
        if ("atualizarTelefone".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                String novoTelefone = obterParametro(req, "telefone", "novoTelefone");
                if (novoTelefone != null && !novoTelefone.isBlank() && !ValidadorRegex.isCelularValido(novoTelefone)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=telefone_invalido");
                    return;
                }
                dao.atualizarTelefone(id, novoTelefone);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar telefone do pecuarista: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista"));
            return;
        }

        // Atualização específica: modifica a senha do pecuarista
        if ("atualizarSenha".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                String novaSenha = obterParametro(req, "senha", "novaSenha");
                dao.atualizarSenha(id, novaSenha);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar senha do pecuarista: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista"));
            return;
        }

        // Atualização específica: modifica a data de nascimento do pecuarista
        if ("atualizarDataNascimento".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                LocalDate novaData = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento", "novaData"));
                dao.atualizarDataNascimento(id, novaData);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar data de nascimento do pecuarista: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista"));
            return;
        }

        // Atualização específica: modifica a assinatura digital do pecuarista
        if ("atualizarAssinatura".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                String novaAssinatura = obterParametro(req, "assinatura", "novaAssinatura");
                dao.atualizarAssinatura(id, novaAssinatura);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar assinatura do pecuarista: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/pecuarista"));
            return;
        }

        // Atualização: modifica os dados do pecuarista existente
        if ("atualizar".equals(acao)) {
            int id = parseInt(req.getParameter("id"), 0);
            try {
                String cpf = obterParametro(req, "cpf");
                LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
                String nome = obterParametro(req, "nome");
                String senha = obterParametro(req, "senha");
                String email = obterParametro(req, "email");
                String telefone = obterParametro(req, "telefone");

                // Senha em branco: mantém a senha atual (a tela de edição não pré-carrega a senha)
                if (senha == null) {
                    PecuaristaModel atual = dao.buscar(id);
                    senha = atual != null ? atual.getSenha() : null;
                }

                // Validação defensiva com REGEX (Demanda de Sistemas Operacionais & Lógica)
                if (!ValidadorRegex.isCpfValido(cpf)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?acao=editar&id=" + id + "&erro=cpf_invalido");
                    return;
                }
                if (telefone != null && !telefone.isBlank() && !ValidadorRegex.isCelularValido(telefone)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?acao=editar&id=" + id + "&erro=telefone_invalido");
                    return;
                }
                if (email != null && !email.isBlank() && !ValidadorRegex.isEmailValido(email)) {
                    resp.sendRedirect(req.getContextPath() + "/pecuarista?acao=editar&id=" + id + "&erro=email_invalido");
                    return;
                }

                // Sanitização: assegura 11 dígitos para cumprimento do CHECK (length(cpf) = 11) do PostgreSQL
                String cpfSanitizado = ValidadorRegex.apenasDigitos(cpf);

                PecuaristaModel pecuaristaModel = new PecuaristaModel(
                        id,
                        cpfSanitizado,
                        dataNascimento,
                        nome,
                        senha,
                        email,
                        telefone
                );

                boolean atualizado = dao.atualizar(pecuaristaModel, id);
                if (!atualizado) {
                    WebErroUtil.redirecionarErroGeral(req, resp, "Não foi possível atualizar o pecuarista. Verifique os dados informados.", "/pecuarista?acao=editar&id=" + id);
                    return;
                }
            } catch (DadoDuplicadoException e) {
                WebErroUtil.redirecionarErroDuplicidade(req, resp, e, "/pecuarista?acao=editar&id=" + id);
                return;
            } catch (Exception e) {
                System.out.println("Erro ao atualizar pecuarista: " + e.getMessage());
                WebErroUtil.redirecionarErroGeral(req, resp, "Erro ao atualizar pecuarista: " + e.getMessage(), "/pecuarista?acao=editar&id=" + id);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/pecuarista?sucesso=atualizado");
            return;
        }

        // Cadastro: persiste um novo produtor rural / pecuarista no sistema
        String cpf = obterParametro(req, "cpf");
        LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
        String nome = obterParametro(req, "nome");
        String senha = obterParametro(req, "senha");
        String email = obterParametro(req, "email");
        String telefone = obterParametro(req, "telefone");

        // Validação defensiva com REGEX (Demanda de Sistemas Operacionais & Lógica)
        if (!ValidadorRegex.isCpfValido(cpf)) {
            resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=cpf_invalido");
            return;
        }
        if (telefone != null && !telefone.isBlank() && !ValidadorRegex.isCelularValido(telefone)) {
            resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=telefone_invalido");
            return;
        }
        if (email != null && !email.isBlank() && !ValidadorRegex.isEmailValido(email)) {
            resp.sendRedirect(req.getContextPath() + "/pecuarista?erro=email_invalido");
            return;
        }

        // Sanitização: assegura 11 dígitos para cumprimento do CHECK do PostgreSQL
        String cpfSanitizado = ValidadorRegex.apenasDigitos(cpf);

        PecuaristaModel novoPecuarista = new PecuaristaModel(
                cpfSanitizado,
                dataNascimento,
                nome,
                senha,
                email,
                telefone
        );

        try {
            boolean inserido = dao.inserir(novoPecuarista);
            if (!inserido) {
                WebErroUtil.redirecionarErroGeral(req, resp, "Não foi possível cadastrar o pecuarista. Verifique os dados informados.", "/pecuarista");
                return;
            }
        } catch (DadoDuplicadoException e) {
            WebErroUtil.redirecionarErroDuplicidade(req, resp, e, "/pecuarista");
            return;
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar pecuarista: " + e.getMessage());
            WebErroUtil.redirecionarErroGeral(req, resp, "Erro ao cadastrar pecuarista: " + e.getMessage(), "/pecuarista");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/pecuarista?sucesso=cadastrado");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Obtém o primeiro valor não nulo e não em branco correspondente aos nomes informados nos parâmetros da requisição.
     * Fornece resiliência contra variações de parâmetros (suporte a camelCase e snake_case).
     *
     * @param req   requisição HTTP
     * @param nomes nomes alternativos de parâmetros aceitos
     * @return valor do parâmetro ou null caso não encontrado
     */
    private String obterParametro(HttpServletRequest req, String... nomes) {
        for (String nome : nomes) {
            String valor = req.getParameter(nome);
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }

    /**
     * Converte com segurança uma string para {@link LocalDate}, retornando null em caso de falha ou valor vazio.
     * Previne erros na camada de controle causados por datas inválidas.
     *
     * @param texto texto contendo a data no formato ISO (AAAA-MM-DD)
     * @return data convertida ou null em caso de erro
     */
    private LocalDate parseLocalDate(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (Exception e) {
            System.out.println("Erro ao converter data (" + texto + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * Converte com segurança uma string para número inteiro, retornando um valor padrão em caso de falha.
     * Previne exceções do tipo {@link NumberFormatException} que resultariam em erro HTTP 500.
     *
     * @param texto  string a ser convertida
     * @param padrao valor padrão de retorno em caso de ausência ou falha de conversão
     * @return inteiro convertido ou valor padrão
     */
    private int parseInt(String texto, int padrao) {
        if (texto == null || texto.isBlank()) {
            return padrao;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            System.out.println("Erro ao converter número (" + texto + "): " + e.getMessage());
            return padrao;
        }
    }
}
