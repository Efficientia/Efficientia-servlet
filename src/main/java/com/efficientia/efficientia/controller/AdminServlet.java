package com.efficientia.efficientia.controller;
import com.efficientia.efficientia.DAO.impl.AdminDAO;
import com.efficientia.efficientia.model.AdminModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@WebServlet(name = "AdminServlet", value = "/admin")
public class AdminServlet extends HttpServlet {
    private AdminDAO dao = new AdminDAO();
    @Override
    public void init() {
        dao = new AdminDAO();
    }
    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        // Edição
        if ("editar".equals(acao)) {
            int id = Integer.parseInt(req.getParameter("id"));
            AdminModel adminModel = dao.buscar(id);
            req.setAttribute("adminModel", adminModel);
            req.setAttribute("admin", adminModel);
            req.getRequestDispatcher(
                    "/WEB-INF/views/editarAdmin.jsp"
            ).forward(req, resp);
            return;
        }
        // Listagem
        List<AdminModel> adminModels = dao.listar();
        req.setAttribute("adminModels", adminModels);
        req.getRequestDispatcher(
                "/WEB-INF/views/admin.jsp"
        ).forward(req, resp);
    }
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");
        // Exclusão
        if ("excluir".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir Admin: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }
        // Atualização
        if ("atualizar".equals(acao)) {
            int id = Integer.parseInt(req.getParameter("id"));
            String email = req.getParameter("email");
            String senha = req.getParameter("senha");
            String nome = req.getParameter("nome");
            AdminModel adminModel = new AdminModel(
                    id,
                    email,
                    senha,
                    nome
            );
            dao.atualizar(adminModel, id);
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }
        // Cadastro
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");
        String nome = req.getParameter("nome");
        AdminModel novoAdmin = new AdminModel(
                email,
                senha,
                nome
        );
        dao.inserir(novoAdmin);
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
}