package com.efficientia.efficientia.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

/**
 * Filtro global de codificação de caracteres (UTF-8).
 * Garante que tanto as requisições (parâmetros de formulário e query strings)
 * quanto as respostas enviadas pelo servidor utilizem explicitamente UTF-8,
 * evitando problemas de mojibake e caracteres corrompidos em acentos e pontuação.
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = {"/*"})
public class CharacterEncodingFilter implements Filter {

    private static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
