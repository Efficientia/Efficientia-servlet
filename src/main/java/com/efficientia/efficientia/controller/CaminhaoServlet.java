package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.DAO.impl.CaminhaoDAO;
import com.efficientia.efficientia.model.CaminhaoModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CaminhaoServlet", value = "/caminhao")
public class CaminhaoServlet extends HttpServlet {

    private CaminhaoDAO dao;

    @Override
    public void init() {
        dao = new CaminhaoDAO();
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
                CaminhaoModel caminhaoModel = dao.buscar(id);

                if (caminhaoModel != null) {
                    req.setAttribute("caminhaoModel", caminhaoModel);
                    req.setAttribute("caminhao", caminhaoModel);
                    req.getRequestDispatcher(
                            "/WEB-INF/views/editar-caminhao.jsp"
                    ).forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar caminhão para edição: " + e.getMessage());
            }
        }

        List<CaminhaoModel> caminhaoModels = dao.listar();

        req.setAttribute("caminhaoModels", caminhaoModels);
        req.setAttribute("caminhoes", caminhaoModels);

        req.getRequestDispatcher(
                "/WEB-INF/views/caminhao.jsp"
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
                System.out.println("Erro ao excluir caminhão: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/caminhao");
            return;
        }

        // Atualização
        if ("atualizar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);

                String placaCavalo = obterParametro(req, "placaCavalo", "placa_cavalo");
                String placaCarreta = obterParametro(req, "placaCarreta", "placa_carreta");
                int capacidadeMaxima = parseInt(obterParametro(req, "capacidadeMaxima", "capacidade_maxima"), 0);

                CaminhaoModel caminhaoModel = new CaminhaoModel(id, placaCavalo, placaCarreta, capacidadeMaxima);
                dao.atualizar(caminhaoModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar caminhão: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/caminhao");
            return;
        }

        // Cadastro
        String placaCavalo = obterParametro(req, "placaCavalo", "placa_cavalo");
        String placaCarreta = obterParametro(req, "placaCarreta", "placa_carreta");
        int capacidadeMaxima = parseInt(obterParametro(req, "capacidadeMaxima", "capacidade_maxima"), 0);

        CaminhaoModel novoCaminhao = new CaminhaoModel(placaCavalo, placaCarreta, capacidadeMaxima);
        dao.inserir(novoCaminhao);

        resp.sendRedirect(req.getContextPath() + "/caminhao");
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
