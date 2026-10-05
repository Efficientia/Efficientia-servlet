package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.DAO.impl.EmpresaDAO;
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
 * Servlet responsável pelo controle de requisições relacionadas à entidade Empresa.
 *
 * Gerencia operações de listagem, consulta, cadastro, atualização e exclusão
 * de empresas parceiras ou contratantes no sistema Efficientia.
 */
@WebServlet(name = "EmpresaServlet", value = "/empresa")
public class EmpresaServlet extends HttpServlet {

    private EmpresaDAO dao;

    @Override
    public void init() {
        dao = new EmpresaDAO();
    }

    // ==================== REQUISIÇÕES GET ====================

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String acao = req.getParameter("acao");

        // Edição: busca a empresa pelo ID e despacha para a página de edição
        if ("editar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                EmpresaModel empresaModel = dao.buscar(id);

                if (empresaModel != null) {
                    req.setAttribute("empresaModel", empresaModel);
                    req.setAttribute("empresa", empresaModel);
                    req.getRequestDispatcher(
                            "/WEB-INF/views/editar-empresa.jsp"
                    ).forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar empresa para edição: " + e.getMessage());
            }
        }

        // Busca flexível por código, nome ou listagem geral de empresas cadastradas
        String codigo = obterParametro(req, "codigo");
        String busca = obterParametro(req, "busca", "nome", "q", "pesquisa");
        List<EmpresaModel> empresaModels;

        if (codigo != null && !codigo.isBlank()) {
            empresaModels = dao.buscarPorCodigoLista(codigo);
            req.setAttribute("termoBusca", codigo);
        } else if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();
            Set<Integer> ids = new LinkedHashSet<>();
            empresaModels = new ArrayList<>();

            for (EmpresaModel e : dao.buscarPorCodigoLista(termo)) {
                if (ids.add(e.getId())) empresaModels.add(e);
            }
            for (EmpresaModel e : dao.buscarPorNome(termo)) {
                if (ids.add(e.getId())) empresaModels.add(e);
            }
            req.setAttribute("termoBusca", busca);
        } else {
            empresaModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }
        req.setAttribute("empresaModels", empresaModels);
        req.setAttribute("empresas", empresaModels);

        req.getRequestDispatcher(
                "/WEB-INF/views/empresa.jsp"
        ).forward(req, resp);
    }

    // ==================== REQUISIÇÕES POST ====================

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        req.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // Exclusão de empresa por ID
        if ("excluir".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir empresa: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/empresa");
            return;
        }

        // Atualização de empresa existente
        if ("atualizar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);

                String nome = obterParametro(req, "nome", "razaoSocial", "razao_social");
                String cnpj = obterParametro(req, "cnpj");
                String codigo = obterParametro(req, "codigo");

                EmpresaModel empresaModel = new EmpresaModel(id, nome, cnpj, codigo);
                dao.atualizar(empresaModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar empresa: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/empresa");
            return;
        }

        // Cadastro de nova empresa
        try {
            String nome = obterParametro(req, "nome", "razaoSocial", "razao_social");
            String cnpj = obterParametro(req, "cnpj");

            EmpresaModel novaEmpresa = new EmpresaModel(nome, cnpj);
            dao.inserir(novaEmpresa);
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar empresa: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/empresa");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Obtém o primeiro valor não nulo e não em branco correspondente aos nomes informados nos parâmetros da requisição.
     *
     * @param req   requisição HTTP
     * @param nomes nomes de parâmetros aceitos (suporte a camelCase e snake_case)
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
     * Converte com segurança uma string para inteiro, retornando um valor padrão em caso de falha.
     *
     * @param texto  string a ser convertida
     * @param padrao valor de retorno caso ocorra erro ou o valor seja inválido
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
