package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import com.efficientia.efficientia.dao.impl.EmpresaDAO;
import com.efficientia.efficientia.model.AdminModel;
import com.efficientia.efficientia.model.EmpresaModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet responsável pelo controle de requisições relacionadas à entidade Administrador.
 *
 * Mapeado no endpoint '/admin', atua como Controller na arquitetura MVC do sistema Efficientia,
 * coordenando o fluxo de dados entre as visões JSP e a camada DAO para operações de consulta,
 * listagem, cadastro, atualização e exclusão de contas administrativas.
 */
@WebServlet(name = "AdminServlet", value = "/admin")
public class AdminServlet extends HttpServlet {

    private AdminDAO dao;
    private EmpresaDAO empresaDAO;

    // ==================== INICIALIZAÇÃO ====================

    /**
     * Inicializa os Data Access Objects (DAOs) necessários durante o ciclo de vida do servlet.
     */
    @Override
    public void init() {
        dao = new AdminDAO();
        empresaDAO = new EmpresaDAO();
    }

    // ==================== REQUISIÇÕES GET ====================

    /**
     * Processa requisições HTTP GET para consulta, busca e exibição de telas de administradores.
     * Suporta a ação 'editar' para carregar dados de um administrador específico,
     * bem como buscas por e-mail, empresa vinculada, busca textual unificada ou listagem geral.
     *
     * @param req  objeto {@link HttpServletRequest} contendo os parâmetros da requisição
     * @param resp objeto {@link HttpServletResponse} para direcionamento e resposta HTTP
     * @throws ServletException caso ocorra erro no despacho para a visão JSP
     * @throws IOException      caso ocorra erro de entrada/saída durante o encaminhamento
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String acao = req.getParameter("acao");

        // Edição: busca o administrador pelo ID e despacha para o formulário de edição com a lista de empresas
        if ("editar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                AdminModel adminModel = dao.buscar(id);
                if (adminModel != null) {
                    req.setAttribute("adminModel", adminModel);
                    req.setAttribute("admin", adminModel);
                    req.setAttribute("empresas", empresaDAO.listar());
                    req.getRequestDispatcher("/WEB-INF/views/editarAdmin.jsp")
                            .forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar Admin para edição: " + e.getMessage());
            }
        }

        // Busca flexível: e-mail, empresa vinculada, termo geral (nome/e-mail) ou listagem geral de administradores
        String email = obterParametro(req, "email");
        String idEmpresaStr = obterParametro(req, "idEmpresa", "id_empresa");
        String busca = obterParametro(req, "busca", "nome", "q", "pesquisa");
        List<AdminModel> adminModels;

        // 1. Busca por e-mail exato
        if (email != null && !email.isBlank()) {
            adminModels = dao.buscarPorEmail(email);
            req.setAttribute("termoBusca", email);
        // 2. Busca por empresa vinculada
        } else if (idEmpresaStr != null && !idEmpresaStr.isBlank()) {
            int idEmpresa = parseInt(idEmpresaStr, 0);
            adminModels = dao.buscarPorEmpresa(idEmpresa);
            req.setAttribute("termoBusca", "Empresa #" + idEmpresa);
        // 3. Busca textual unificada com desduplicação por ID via LinkedHashSet
        } else if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();
            Set<Integer> ids = new LinkedHashSet<>();
            adminModels = new ArrayList<>();

            for (AdminModel a : dao.buscarPorNome(termo)) {
                if (ids.add(a.getId())) adminModels.add(a);
            }
            for (AdminModel a : dao.buscarPorEmail(termo)) {
                if (ids.add(a.getId())) adminModels.add(a);
            }
            req.setAttribute("termoBusca", busca);
        // 4. Listagem geral de todos os administradores cadastrados
        } else {
            adminModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }

        // Define os atributos na requisição e encaminha para a visão principal
        req.setAttribute("adminModels", adminModels);
        req.setAttribute("admins", adminModels);
        req.setAttribute("empresas", empresaDAO.listar());
        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Garante suporte adequado a caracteres com acentuação
        req.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");

        // Exclusão: remove o administrador a partir do identificador fornecido
        if ("excluir".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir Admin: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }

        // Atualização: modifica os dados de um administrador existente
        if ("atualizar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                String email = obterParametro(req, "email");
                String senha = obterParametro(req, "senha");
                String nome = obterParametro(req, "nome");
                EmpresaModel empresa = buscarEmpresa(req);
                AdminModel adminModel = new AdminModel(id, empresa, email, senha, nome);
                dao.atualizar(adminModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar Admin: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }

        // Cadastro: persiste um novo administrador no sistema
        try {
            String email = obterParametro(req, "email");
            String senha = obterParametro(req, "senha");
            String nome = obterParametro(req, "nome");
            EmpresaModel empresa = buscarEmpresa(req);

            AdminModel novoAdmin = new AdminModel(empresa, email, senha, nome);
            dao.inserir(novoAdmin);
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar Admin: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Recupera a entidade {@link EmpresaModel} associada a partir do ID enviado na requisição.
     * Resolve o relacionamento relacional entre administrador e empresa transportadora.
     *
     * @param req requisição HTTP contendo o identificador da empresa
     * @return objeto {@link EmpresaModel} correspondente ou null se não informado ou inválido
     */
    private EmpresaModel buscarEmpresa(HttpServletRequest req) {
        String idTexto = obterParametro(req, "idEmpresa", "id_empresa");
        if (idTexto != null) {
            try {
                int idEmpresa = Integer.parseInt(idTexto.trim());
                return empresaDAO.buscar(idEmpresa);
            } catch (Exception e) {
                System.out.println("Erro ao buscar empresa do admin: " + e.getMessage());
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
                return valor.trim();
            }
        }
        return null;
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
        if (texto == null || texto.isBlank()) return padrao;
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            System.out.println("Erro parse int: " + e.getMessage());
            return padrao;
        }
    }
}