package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.EmpresaDAO;
import com.efficientia.efficientia.dao.impl.MotoristaDAO;
import com.efficientia.efficientia.model.EmpresaModel;
import com.efficientia.efficientia.model.MotoristaModel;
import com.efficientia.efficientia.util.ValidadorRegex;
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
 * Servlet responsável pelo controle de requisições relacionadas à entidade Motorista.
 *
 * Mapeado no endpoint '/motorista', atua como Controller na arquitetura MVC do sistema Efficientia,
 * gerenciando operações de listagem, consulta, cadastro, atualização e exclusão
 * dos condutores de veículos pesados de carga pecuária, vinculados às suas transportadoras parceiras.
 */
@WebServlet(name = "MotoristaServlet", value = "/motorista")
public class MotoristaServlet extends HttpServlet {

    private MotoristaDAO dao;
    private EmpresaDAO empresaDAO;

    // ==================== INICIALIZAÇÃO ====================

    /**
     * Inicializa os Data Access Objects (DAOs) necessários durante o ciclo de vida do servlet.
     */
    @Override
    public void init() {
        dao = new MotoristaDAO();
        empresaDAO = new EmpresaDAO();
    }

    // ==================== REQUISIÇÕES GET ====================

    /**
     * Processa requisições HTTP GET para consulta e exibição de motoristas.
     * Suporta a ação 'editar' para carregar os dados de um motorista específico,
     * bem como buscas por e-mail, telefone, empresa vinculada, busca por nome/termo geral
     * ou listagem completa.
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

        // Edição: busca o motorista pelo ID e despacha para o formulário de edição com as empresas
        if ("editar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                MotoristaModel motoristaModel = dao.buscar(id);

                if (motoristaModel != null) {
                    req.setAttribute("motoristaModel", motoristaModel);
                    req.setAttribute("motorista", motoristaModel);
                    req.setAttribute("empresaModels", empresaDAO.listar());
                    req.getRequestDispatcher(
                            "/WEB-INF/views/editar-motorista.jsp"
                    ).forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar motorista para edição: " + e.getMessage());
            }
        }

        // Busca flexível: e-mail, telefone, empresa vinculada, termo geral (nome/e-mail/telefone) ou listagem geral
        String email = obterParametro(req, "email");
        String telefone = obterParametro(req, "telefone");
        String idEmpresaStr = obterParametro(req, "idEmpresa", "id_empresa");
        String busca = obterParametro(req, "busca", "nome", "q", "pesquisa");
        List<MotoristaModel> motoristaModels;

        // 1. Busca por e-mail exato
        if (email != null && !email.isBlank()) {
            motoristaModels = dao.buscarPorEmail(email);
            req.setAttribute("termoBusca", email);
        // 2. Busca por telefone
        } else if (telefone != null && !telefone.isBlank()) {
            motoristaModels = dao.buscarPorTelefone(telefone);
            req.setAttribute("termoBusca", telefone);
        // 3. Busca por empresa vinculada
        } else if (idEmpresaStr != null && !idEmpresaStr.isBlank()) {
            int idEmpresa = parseInt(idEmpresaStr, 0);
            motoristaModels = dao.buscarPorEmpresa(idEmpresa);
            req.setAttribute("termoBusca", "Empresa #" + idEmpresa);
        // 4. Busca textual unificada com desduplicação por ID via LinkedHashSet
        } else if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();
            Set<Integer> ids = new LinkedHashSet<>();
            motoristaModels = new ArrayList<>();

            for (MotoristaModel m : dao.buscarPorNome(termo)) {
                if (ids.add(m.getId())) motoristaModels.add(m);
            }
            for (MotoristaModel m : dao.buscarPorEmail(termo)) {
                if (ids.add(m.getId())) motoristaModels.add(m);
            }
            for (MotoristaModel m : dao.buscarPorTelefone(termo)) {
                if (ids.add(m.getId())) motoristaModels.add(m);
            }
            req.setAttribute("termoBusca", busca);
        // 5. Listagem geral de todos os motoristas cadastrados
        } else {
            motoristaModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }

        // Define os atributos na requisição e encaminha para a visão principal
        req.setAttribute("motoristaModels", motoristaModels);
        req.setAttribute("motoristas", motoristaModels);
        req.setAttribute("empresaModels", empresaDAO.listar());

        req.getRequestDispatcher(
                "/WEB-INF/views/motorista.jsp"
        ).forward(req, resp);
    }

    // ==================== REQUISIÇÕES POST ====================

    /**
     * Processa requisições HTTP POST para operações de modificação (CUD: Cadastro, Atualização e Exclusão).
     * Aplica o padrão Post-Redirect-Get (PRG) para evitar submissões acidentais duplicadas.
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

        // Exclusão: remove o motorista a partir do identificador informado
        if ("excluir".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir motorista: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/motorista");
            return;
        }

        // Atualização: modifica os dados do motorista existente
        if ("atualizar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);

                EmpresaModel empresaModel = buscarEmpresa(req);
                String nome = obterParametro(req, "nome");
                LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
                String senha = obterParametro(req, "senha");
                String email = obterParametro(req, "email");
                String telefone = obterParametro(req, "telefone");

                // Validação defensiva com REGEX (Demanda de Sistemas Operacionais & Lógica)
                if (telefone != null && !telefone.isBlank() && !ValidadorRegex.isCelularValido(telefone)) {
                    resp.sendRedirect(req.getContextPath() + "/motorista?acao=editar&id=" + id + "&erro=telefone_invalido");
                    return;
                }
                if (email != null && !email.isBlank() && !ValidadorRegex.isEmailValido(email)) {
                    resp.sendRedirect(req.getContextPath() + "/motorista?acao=editar&id=" + id + "&erro=email_invalido");
                    return;
                }

                MotoristaModel motoristaModel = new MotoristaModel(
                        id,
                        empresaModel,
                        nome,
                        dataNascimento,
                        senha,
                        email,
                        telefone
                );

                dao.atualizar(motoristaModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar motorista: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/motorista");
            return;
        }

        // Cadastro: persiste um novo motorista no sistema
        EmpresaModel empresaModel = buscarEmpresa(req);
        String nome = obterParametro(req, "nome");
        LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
        String senha = obterParametro(req, "senha");
        String email = obterParametro(req, "email");
        String telefone = obterParametro(req, "telefone");

        // Validação defensiva com REGEX (Demanda de Sistemas Operacionais & Lógica)
        if (telefone != null && !telefone.isBlank() && !ValidadorRegex.isCelularValido(telefone)) {
            resp.sendRedirect(req.getContextPath() + "/motorista?erro=telefone_invalido");
            return;
        }
        if (email != null && !email.isBlank() && !ValidadorRegex.isEmailValido(email)) {
            resp.sendRedirect(req.getContextPath() + "/motorista?erro=email_invalido");
            return;
        }

        MotoristaModel novoMotorista = new MotoristaModel(
                empresaModel,
                nome,
                dataNascimento,
                senha,
                email,
                telefone
        );

        dao.inserir(novoMotorista);

        resp.sendRedirect(req.getContextPath() + "/motorista");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Recupera a entidade {@link EmpresaModel} associada a partir do ID enviado na requisição.
     * Resolve o relacionamento relacional entre motorista e transportadora contratante.
     *
     * @param req requisição HTTP contendo o identificador da empresa
     * @return objeto {@link EmpresaModel} correspondente ou null se não informado ou inválido
     */
    private EmpresaModel buscarEmpresa(HttpServletRequest req) {
        String idTexto = obterParametro(req, "idEmpresa", "id_empresa");
        if (idTexto != null && !idTexto.isBlank()) {
            try {
                int idEmpresa = Integer.parseInt(idTexto.trim());
                for (EmpresaModel empresa : empresaDAO.listar()) {
                    if (empresa.getId() == idEmpresa) {
                        return empresa;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar empresa do motorista: " + e.getMessage());
            }
        }
        return null;
    }

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
