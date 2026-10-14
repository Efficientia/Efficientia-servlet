package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import com.efficientia.efficientia.util.ValidadorRegex;

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
        req.setAttribute("nomeInformado", nome);
        req.setAttribute("emailInformado", email);

        if (nome == null || nome.isBlank() || email == null || email.isBlank() || senha == null || senha.isBlank()) {
            req.setAttribute("erro", "Todos os campos são obrigatórios.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            return;
        }

        if (nome.trim().length() > 100 || !ValidadorRegex.isEmailValido(email)
                || email.trim().length() > 254 || senha.length() < 15 || senha.length() > 128) {
            req.setAttribute("erro", "Informe nome (até 100 caracteres), e-mail válido e senha de 15 a 128 caracteres.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            return;
        }

        try {
            if (adminDAO.emailCadastrado(email)) {
                req.setAttribute("erro", "Este e-mail já está em uso por outro administrador.");
                req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
                return;
            }

            boolean sucesso = adminDAO.inserirSimples(nome.trim(),
                    email.trim().toLowerCase(java.util.Locale.ROOT), senha, null);
            if (sucesso) {
                resp.sendRedirect(req.getContextPath() + "/login?msg=cadastrado");
            } else {
                resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                req.setAttribute("erro", "Não foi possível salvar no banco de dados. Tente novamente mais tarde.");
                req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
            }
        } catch (SQLException ex) {
            boolean duplicado = "23505".equals(ex.getSQLState());
            if (!duplicado) getServletContext().log("Falha no cadastro de administrador.", ex);
            resp.setStatus(duplicado ? HttpServletResponse.SC_CONFLICT : HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            req.setAttribute("erro", duplicado ? "Este e-mail já está em uso."
                    : "Não foi possível acessar o banco de dados. Tente novamente mais tarde.");
            req.getRequestDispatcher("/cadastro.jsp").forward(req, resp);
        }
    }
}
