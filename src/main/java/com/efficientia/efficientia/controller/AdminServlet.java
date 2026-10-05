package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.DAO.impl.AdminDAO;
import com.efficientia.efficientia.DAO.impl.EmpresaDAO;
import com.efficientia.efficientia.model.AdminModel;
import com.efficientia.efficientia.model.EmpresaModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminServlet", value = "/admin")
public class AdminServlet extends HttpServlet {
    private AdminDAO dao;
    private EmpresaDAO empresaDAO;

    @Override
    public void init() {
        dao = new AdminDAO();
        empresaDAO = new EmpresaDAO();
    }

    //get
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
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
        List<AdminModel> adminModels = dao.listar();
        req.setAttribute("adminModels", adminModels);
        req.setAttribute("admins", adminModels);
        req.setAttribute("empresas", empresaDAO.listar());
        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
    }

    //alt
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");
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
    private String obterParametro(HttpServletRequest req, String... nomes) {
        for (String nome : nomes) {
            String valor = req.getParameter(nome);
            if (valor != null && !valor.isBlank()) {
                return valor.trim();
            }
        }
        return null;
    }
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