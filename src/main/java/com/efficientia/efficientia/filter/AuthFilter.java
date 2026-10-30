package com.efficientia.efficientia.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtro de autenticação e segurança.
 *
 * Intercepta as requisições destinadas à área restrita e verifica se o usuário
 * possui uma sessão ativa de Administrador. Caso contrário, impede o acesso
 * direto pela URL e o redireciona para a página de login.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {
        // =========================================================================
        // [ALTERAÇÃO FUTURA]: Caso o arquivo ou a rota da área restrita mude
        // (por exemplo: de /pecuarista para um futuro /dashboard ou outra rota),
        // altere ou adicione o caminho do arquivo na linha abaixo:
        // =========================================================================
        "/home", "/admin", "/empresa", "/motorista", "/caminhao", "/pecuarista", "/trajeto"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro se necessário
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Cache-Control", "no-store");

        // Obtém a sessão existente sem criar uma nova caso não exista
        HttpSession session = req.getSession(false);

        // Verifica se a sessão existe e se o atributo do admin autenticado está presente
        boolean logado = (session != null && session.getAttribute("adminLogado") != null);

        if (logado) {
            // Usuário devidamente logado: segue o fluxo normal
            chain.doFilter(request, response);
        } else {
            // Acesso direto não autorizado via URL: redireciona para o login com aviso
            resp.sendRedirect(req.getContextPath() + "/login?erro=nao_autorizado");
        }
    }

    @Override
    public void destroy() {
        // Finalização do filtro se necessário
    }
}
