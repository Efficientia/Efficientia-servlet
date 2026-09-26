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
                int id = Integer.parseInt(req.getParameter("id"));
                CaminhaoModel caminhaoModel = dao.buscar(id);

                if (caminhaoModel == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Caminhão não encontrado.");
                    return;
                }

                req.setAttribute("caminhaoModel", caminhaoModel);
                req.getRequestDispatcher(
                        "/WEB-INF/views/editar-caminhao.jsp"
                ).forward(req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "ID do caminhão inválido.");
            }
            return;
        }

        List<CaminhaoModel> caminhaoModels = dao.listar();
        req.setAttribute("caminhaoModels", caminhaoModels);
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

        try {
            // Exclusão
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                if (!dao.excluir(id)) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Não foi possível excluir o caminhão.");
                    return;
                }
                resp.sendRedirect(req.getContextPath() + "/caminhao");
                return;
            }

            // Dados usados no cadastro e na atualização
            String placaCavalo = req.getParameter("placaCavalo");
            String placaCarreta = req.getParameter("placaCarreta");
            int capacidadeMaxima = Integer.parseInt(
                    req.getParameter("capacidadeMaxima")
            );
            CaminhaoModel caminhaoModel = new CaminhaoModel(
                    placaCavalo, placaCarreta, capacidadeMaxima
            );

            boolean sucesso;
            if ("atualizar".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                caminhaoModel.setId(id);
                sucesso = dao.atualizar(caminhaoModel, id);
            } else {
                sucesso = dao.inserir(caminhaoModel);
            }

            if (!sucesso) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Não foi possível salvar o caminhão.");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/caminhao");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "ID e capacidade máxima devem ser números inteiros.");
        }
    }
}
