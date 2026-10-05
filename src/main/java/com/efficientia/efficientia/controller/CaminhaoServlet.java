package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.DAO.impl.CaminhaoDAO;
import com.efficientia.efficientia.DAO.impl.EmpresaDAO;
import com.efficientia.efficientia.model.CaminhaoModel;
import com.efficientia.efficientia.model.EmpresaModel;
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
    private EmpresaDAO empresaDAO;

    @Override
    public void init() {
        dao = new CaminhaoDAO();
        empresaDAO = new EmpresaDAO();
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
                    req.setAttribute("empresaModels", empresaDAO.listar());
                    req.getRequestDispatcher(
                            "/WEB-INF/views/editar-caminhao.jsp"
                    ).forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar caminhão para edição: " + e.getMessage());
            }
        }

        // Busca flexível: por empresa, por placa (cavalo ou carreta) ou listagem geral
        String idEmpresaStr = obterParametro(req, "idEmpresa", "id_empresa");
        String busca = obterParametro(req, "placa", "busca", "q", "pesquisa");
        List<CaminhaoModel> caminhaoModels;

        if (idEmpresaStr != null && !idEmpresaStr.isBlank()) {
            int idEmpresa = parseInt(idEmpresaStr, 0);
            caminhaoModels = dao.buscarPorEmpresa(idEmpresa);
            req.setAttribute("termoBusca", "Empresa #" + idEmpresa);
        } else if (busca != null && !busca.isBlank()) {
            caminhaoModels = dao.buscarPorPlaca(busca);
            req.setAttribute("termoBusca", busca);
        } else {
            caminhaoModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }

        req.setAttribute("termoBusca", busca != null ? busca : "");
        req.setAttribute("caminhaoModels", caminhaoModels);
        req.setAttribute("caminhoes", caminhaoModels);
        req.setAttribute("empresaModels", empresaDAO.listar());

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
                EmpresaModel empresaModel = buscarEmpresa(req);
                String placaCavalo = obterParametro(req, "placaCavalo", "placa_cavalo");
                String placaCarreta = obterParametro(req, "placaCarreta", "placa_carreta");
                int capacidadeMaxima = parseInt(obterParametro(req, "capacidadeMaxima", "capacidade_maxima"), 0);

                CaminhaoModel caminhaoModel = new CaminhaoModel(id, empresaModel, placaCavalo, placaCarreta, capacidadeMaxima);
                dao.atualizar(caminhaoModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar caminhão: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/caminhao");
            return;
        }

        // Cadastro
        EmpresaModel empresaModel = buscarEmpresa(req);
        String placaCavalo = obterParametro(req, "placaCavalo", "placa_cavalo");
        String placaCarreta = obterParametro(req, "placaCarreta", "placa_carreta");
        int capacidadeMaxima = parseInt(obterParametro(req, "capacidadeMaxima", "capacidade_maxima"), 0);

        CaminhaoModel novoCaminhao = new CaminhaoModel(empresaModel, placaCavalo, placaCarreta, capacidadeMaxima);
        dao.inserir(novoCaminhao);

        resp.sendRedirect(req.getContextPath() + "/caminhao");
    }

    // ==================== MÉTODOS AUXILIARES ====================

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
                System.out.println("Erro ao buscar empresa do caminhão: " + e.getMessage());
            }
        }
        return null;
    }

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
