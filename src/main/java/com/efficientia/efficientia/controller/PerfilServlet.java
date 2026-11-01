package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.AdminDAO;
import com.efficientia.efficientia.exception.DadoDuplicadoException;
import com.efficientia.efficientia.model.AdminModel;
import com.efficientia.efficientia.util.ValidadorRegex;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;

@WebServlet(name = "PerfilServlet", value = "/perfil")
public class PerfilServlet extends HttpServlet {
    private static final String TOKEN = "perfilCsrfToken";
    private static final String SUCESSO = "perfilSalvo";
    private static final SecureRandom RANDOM = new SecureRandom();
    private AdminDAO adminDAO;

    @Override
    public void init() {
        adminDAO = new AdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = obterSessao(req, resp);
        if (session == null) return;
        synchronized (session) {
            AdminModel logado = (AdminModel) session.getAttribute("adminLogado");
            AdminModel atual = adminDAO.buscar(logado.getId());
            if (atual == null) {
                indisponivel(req, resp, logado);
            } else {
                sincronizarSessao(session, atual, atual.getNome(), atual.getEmail());
                apresentarConta(req, atual);
                preencher(req, atual.getNome(), atual.getEmail());
                if (Boolean.TRUE.equals(session.getAttribute(SUCESSO))) {
                    req.setAttribute("sucessoPerfil", "Alterações salvas com sucesso.");
                    session.removeAttribute(SUCESSO);
                }
            }
            exibir(req, resp, session);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = obterSessao(req, resp);
        if (session == null) return;
        // A rotação do token e a gravação são serializadas para impedir reenvios concorrentes.
        synchronized (session) {
            AdminModel logado = (AdminModel) session.getAttribute("adminLogado");
            String recebido = req.getParameter("csrfToken");
            String esperado = (String) session.getAttribute(TOKEN);
            if (esperado == null || recebido == null || recebido.length() != esperado.length()
                    || !MessageDigest.isEqual(esperado.getBytes(StandardCharsets.UTF_8),
                    recebido.getBytes(StandardCharsets.UTF_8))) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                req.setAttribute("erroPerfil", "O formulário expirou ou já foi enviado. Confira os dados e tente novamente.");
                apresentarConta(req, logado);
                preencher(req, logado.getNome(), logado.getEmail());
                exibir(req, resp, session);
                return;
            }

            String nome = parametro(req, "nome");
            String email = parametro(req, "email");
            AdminModel atual = adminDAO.buscar(logado.getId());
            if (atual == null) {
                indisponivel(req, resp, logado);
                preencher(req, nome, email);
                exibir(req, resp, session);
                return;
            }
            apresentarConta(req, atual);
            preencher(req, nome, email);
            boolean nomeInvalido = nome.isBlank() || nome.length() > 150
                    || nome.codePoints().anyMatch(Character::isISOControl);
            boolean emailInvalido = email.length() > 254 || !ValidadorRegex.isEmailValido(email);
            if (nomeInvalido || emailInvalido) {
                resp.setStatus(422);
                req.setAttribute("erroPerfil", "Revise os campos indicados antes de salvar.");
                if (nomeInvalido) req.setAttribute("erroNome", "Informe um nome de até 150 caracteres, sem quebras de linha.");
                if (emailInvalido) req.setAttribute("erroEmail", "Informe um e-mail válido de até 254 caracteres.");
                exibir(req, resp, session);
                return;
            }

            try {
                if (!adminDAO.atualizarPerfil(logado.getId(), nome, email)) {
                    resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    req.setAttribute("erroPerfil", "Não foi possível salvar as alterações. Tente novamente.");
                    exibir(req, resp, session);
                    return;
                }
            } catch (DadoDuplicadoException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                req.setAttribute("erroPerfil", "Não foi possível salvar as alterações. Revise o e-mail informado.");
                req.setAttribute("erroEmail", "Este e-mail já está em uso por outra conta.");
                exibir(req, resp, session);
                return;
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                req.setAttribute("erroPerfil", "Não foi possível acessar o banco de dados. Tente novamente mais tarde.");
                exibir(req, resp, session);
                return;
            }
            sincronizarSessao(session, atual, nome, email);
            session.setAttribute(TOKEN, novoToken());
            session.setAttribute(SUCESSO, true);
            resp.sendRedirect(req.getContextPath() + "/perfil");
        }
    }

    private HttpSession obterSessao(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Cache-Control", "no-store");
        HttpSession session = req.getSession(false);
        if (session == null || !(session.getAttribute("adminLogado") instanceof AdminModel admin) || admin.getId() <= 0) {
            resp.sendRedirect(req.getContextPath() + "/login?erro=nao_autorizado");
            return null;
        }
        return session;
    }

    private void sincronizarSessao(HttpSession session, AdminModel atual, String nome, String email) {
        session.setAttribute("adminLogado", new AdminModel(atual.getId(), atual.getEmpresa(), email, null, nome));
    }

    private void apresentarConta(HttpServletRequest req, AdminModel admin) {
        String nome = admin.getNome() == null ? "" : admin.getNome().strip();
        req.setAttribute("identificacaoPerfil", nome);
        req.setAttribute("inicialPerfil", nome.isEmpty() ? "A"
                : new String(Character.toChars(nome.codePointAt(0))).toUpperCase(Locale.ROOT));
        req.setAttribute("empresaPerfil", admin.getEmpresa() == null ? "Nenhuma empresa vinculada" : admin.getEmpresa().getNome());
    }

    private void preencher(HttpServletRequest req, String nome, String email) {
        req.setAttribute("nomePerfil", nome);
        req.setAttribute("emailPerfil", email);
    }

    private void indisponivel(HttpServletRequest req, HttpServletResponse resp, AdminModel admin) {
        resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        req.setAttribute("erroPerfil", "Não foi possível carregar sua conta. Tente novamente mais tarde.");
        req.setAttribute("perfilIndisponivel", true);
        apresentarConta(req, admin);
        preencher(req, admin.getNome(), admin.getEmail());
    }

    private void exibir(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws ServletException, IOException {
        if (session.getAttribute(TOKEN) == null) session.setAttribute(TOKEN, novoToken());
        req.setAttribute("csrfToken", session.getAttribute(TOKEN));
        req.getRequestDispatcher("/WEB-INF/views/perfil.jsp").forward(req, resp);
    }

    private static String novoToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String parametro(HttpServletRequest req, String nome) {
        String valor = req.getParameter(nome);
        return valor == null ? "" : valor.strip();
    }
}
