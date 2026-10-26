package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import com.efficientia.efficientia.model.AdminModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import com.efficientia.efficientia.util.ValidadorRegex;

/**
 * Servlet responsável pela autenticação, login e logout de Administradores.
 * Gerencia o ciclo de vida da sessão do usuário no sistema Efficientia.
 */
@WebServlet(name = "LoginServlet", value = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    private AdminDAO adminDAO;

    // =========================================================================
    // [ALTERAÇÃO FUTURA]: Caso o arquivo ou a rota da área restrita mude
    // (por exemplo: de /pecuarista para um futuro /dashboard ou outra JSP/Servlet),
    // altere a constante abaixo para o novo caminho de destino:
    // =========================================================================
    public static final String ROTA_AREA_RESTRITA = "/pecuarista";

    @Override
    public void init() {
        adminDAO = new AdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // Operação de Logout: encerra a sessão ativa
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login?msg=desconectado");
            return;
        }

        // Operação de Login (GET):
        // Se o usuário já estiver logado, redireciona diretamente para a área restrita
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("adminLogado") != null) {
            resp.sendRedirect(req.getContextPath() + ROTA_AREA_RESTRITA);
            return;
        }

        // Caso contrário, encaminha para a página de exibição do formulário de login
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        // Autentica as credenciais com o AdminDAO
        req.setAttribute("emailInformado", email);
        if (!ValidadorRegex.isEmailValido(email) || email.length() > 254
                || senha == null || senha.isBlank() || senha.length() > 128) {
            req.setAttribute("erro", "Informe um e-mail válido e sua senha.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }
        AdminModel admin;
        try {
            admin = adminDAO.autenticar(email, senha);
        } catch (SQLException ex) {
            getServletContext().log("Falha de conexão na autenticação.", ex);
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            req.setAttribute("erro", "Não foi possível acessar o banco de dados. Tente novamente mais tarde.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        if (admin != null) {
            // Sucesso: cria ou obtém a sessão HTTP e armazena o objeto do administrador
            HttpSession session = req.getSession(true);
            req.changeSessionId();
            session.setMaxInactiveInterval(30 * 60);
            session.setAttribute("adminLogado", admin);

            // Redireciona para a área restrita configurada na constante
            resp.sendRedirect(req.getContextPath() + ROTA_AREA_RESTRITA);
        } else {
            // Falha: repassa mensagem de erro e retorna para a página de login
            req.setAttribute("erro", "E-mail ou senha incorretos.");
            req.setAttribute("emailInformado", email);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
