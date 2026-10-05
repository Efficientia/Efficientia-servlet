package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.DAO.impl.PecuaristaDAO;
import com.efficientia.efficientia.model.PecuaristaModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "PecuaristaServlet", value = "/pecuarista")
public class PecuaristaServlet extends HttpServlet {

    private PecuaristaDAO dao;

    @Override
    public void init() {
        dao = new PecuaristaDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String acao = req.getParameter("acao");

        if ("editar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                PecuaristaModel pecuaristaModel = dao.buscar(id);

                if (pecuaristaModel != null) {
                    req.setAttribute("pecuaristaModel", pecuaristaModel);
                    req.setAttribute("pecuarista", pecuaristaModel);
                    req.getRequestDispatcher(
                            "/WEB-INF/views/editar.jsp"
                    ).forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar pecuarista para edição: " + e.getMessage());
            }
        }

        // Busca flexível por nome ou listagem geral de pecuaristas
        String busca = obterParametro(req, "busca", "nome", "q", "pesquisa");
        List<PecuaristaModel> pecuaristaModels;

        if (busca != null && !busca.isBlank()) {
            pecuaristaModels = dao.buscarPorNome(busca);
        } else {
            pecuaristaModels = dao.listar();
        }

        req.setAttribute("termoBusca", busca != null ? busca : "");
        req.setAttribute("pecuaristaModels", pecuaristaModels);
        req.setAttribute("pecuaristas", pecuaristaModels);

        req.getRequestDispatcher(
                "/WEB-INF/views/pecuarista.jsp"
        ).forward(req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        req.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // Exclusão
        if ("excluir".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir pecuarista: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/pecuarista");
            return;
        }

        // Atualização
        if ("atualizar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);

                String cpf = obterParametro(req, "cpf");
                LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
                String nome = obterParametro(req, "nome");
                String senha = obterParametro(req, "senha");
                String email = obterParametro(req, "email");
                String telefone = obterParametro(req, "telefone");

                PecuaristaModel pecuaristaModel = new PecuaristaModel(
                        id,
                        cpf,
                        dataNascimento,
                        nome,
                        senha,
                        email,
                        telefone
                );

                dao.atualizar(pecuaristaModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar pecuarista: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/pecuarista");
            return;
        }

        // Cadastro
        String cpf = obterParametro(req, "cpf");
        LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
        String nome = obterParametro(req, "nome");
        String senha = obterParametro(req, "senha");
        String email = obterParametro(req, "email");
        String telefone = obterParametro(req, "telefone");

        PecuaristaModel novoPecuarista = new PecuaristaModel(
                cpf,
                dataNascimento,
                nome,
                senha,
                email,
                telefone
        );

        dao.inserir(novoPecuarista);

        resp.sendRedirect(req.getContextPath() + "/pecuarista");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private String obterParametro(HttpServletRequest req, String... nomes) {
        for (String nome : nomes) {
            String valor = req.getParameter(nome);
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }

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
