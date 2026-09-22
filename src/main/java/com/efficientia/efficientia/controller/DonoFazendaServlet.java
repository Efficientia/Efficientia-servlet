package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.model.DonoFazendaModel;
import com.efficientia.efficientia.DAO.impl.DonoFazendaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;

@WebServlet(name= "DonoFazendaServlet", value = "/donoFazenda")

public class DonoFazendaServlet extends HttpServlet {

    private DonoFazendaDAO dao = new DonoFazendaDAO();

    @Override
    public void init(){
        dao = new DonoFazendaDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
    throws ServletException, IOException {

        String acao = req.getParameter("acao");

        if ("editar".equals(acao)) {
            int id = Integer.parseInt(req.getParameter("id"));

            DonoFazendaModel donoFazendaModel = dao.buscar(id);

            req.setAttribute("donoFazendaModel", donoFazendaModel);
            req.setAttribute("dono", donoFazendaModel);

            req.getRequestDispatcher(
                    "/WEB-INF/views/editar.jsp"
            ).forward(req, resp);

            return;
        }

        List<DonoFazendaModel> donoFazendaModels = dao.listar();

        req.setAttribute("donoFazendaModels", donoFazendaModels);

        req.getRequestDispatcher(
                "/WEB-INF/views/donoFazenda.jsp"
        ).forward(req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        req.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        //Exclusão
        if ("excluir".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir donoFazenda: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/donoFazenda");
            return;
        }

        //Atualização
        if ("atualizar".equals(acao)) {
            int id = Integer.parseInt(req.getParameter("id"));

            String cpf = req.getParameter("cpf");
            String assinatura = req.getParameter("assinatura");
            String dataNascimentoTexto = req.getParameter("dataNascimento");

            LocalDate dataNascimento = null;
            if (dataNascimentoTexto != null && !dataNascimentoTexto.isBlank()) {
                try {
                    dataNascimento = LocalDate.parse(dataNascimentoTexto);
                } catch (Exception e) {
                    System.out.println("Erro ao converter data de nascimento: " + e.getMessage());
                }
            }

            String nome = req.getParameter("nome");
            String senha = req.getParameter("senha");
            String email = req.getParameter("email");
            String telefone = req.getParameter("telefone");

            DonoFazendaModel donoFazendaModel = new DonoFazendaModel(
                    id,
                    cpf,
                    assinatura,
                    dataNascimento,
                    nome,
                    senha,
                    email,
                    telefone
            );

            dao.atualizar(donoFazendaModel, id);

            resp.sendRedirect(req.getContextPath() + "/donoFazenda");
            return;
        }

        //Cadastro
        String cpf = req.getParameter("cpf");
        String assinatura = req.getParameter("assinatura");
        String dataNascimentoTexto = req.getParameter("dataNascimento");

        LocalDate dataNascimento = null;
        if (dataNascimentoTexto != null && !dataNascimentoTexto.isBlank()) {
            try {
                dataNascimento = LocalDate.parse(dataNascimentoTexto);
            } catch (Exception e) {
                System.out.println("Erro ao converter data de nascimento: " + e.getMessage());
            }
        }

        String nome = req.getParameter("nome");
        String senha = req.getParameter("senha");
        String email = req.getParameter("email");
        String telefone = req.getParameter("telefone");

        DonoFazendaModel novoDono = new DonoFazendaModel(
                cpf,
                assinatura,
                dataNascimento,
                nome,
                senha,
                email,
                telefone
        );

        dao.inserir(novoDono);

        resp.sendRedirect(req.getContextPath() + "/donoFazenda");
    }
}
