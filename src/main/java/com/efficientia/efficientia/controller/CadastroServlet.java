package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import com.efficientia.efficientia.model.AdminModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsável pelo fluxo de cadastro público de novos Administradores.
 */
@WebServlet(name = "CadastroServlet", value = "/cadastro")
public class CadastroServlet extends HttpServlet {

    private AdminDAO adminDAO;

    @Override
    public void init() {
        adminDAO = new AdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        if (nome == null || nome.isBlank() || email == null || email.isBlank() || senha == null || senha.isBlank()) {
            req.setAttribute("erro", "Todos os campos são obrigatórios.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            return;
        }

        // Verifica se o e-mail já está cadastrado
        List<AdminModel> existentes = adminDAO.buscarPorEmail(email.trim());
        boolean emailJaExiste = existentes.stream()
                .anyMatch(a -> a.getEmail() != null && a.getEmail().equalsIgnoreCase(email.trim()));

        if (emailJaExiste) {
            req.setAttribute("erro", "Este e-mail já está em uso por outro administrador.");
            req.setAttribute("nomeInformado", nome);
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            return;
        }

        boolean sucesso = adminDAO.inserirSimples(nome.trim(), email.trim(), senha, null);

        if (sucesso) {
            resp.sendRedirect(req.getContextPath() + "/login?msg=cadastrado");
        } else {
            req.setAttribute("erro", "Ocorreu um erro ao salvar o cadastro. Tente novamente.");
            req.setAttribute("nomeInformado", nome);
            req.setAttribute("emailInformado", email);
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
        }
    }
}
