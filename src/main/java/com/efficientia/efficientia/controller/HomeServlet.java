package com.efficientia.efficientia.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet responsável pelo controle e exibição da tela inicial (Visão Geral / Dashboard) da Área Restrita.
 *
 * Mapeado no endpoint '/home', atua como ponto de entrada principal pós-autenticação,
 * encaminhando a requisição para a visão JSP correspondente com os cards de navegação do sistema.
 */
@WebServlet(name = "HomeServlet", value = "/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
