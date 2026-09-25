package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.model.PecuaristaModel;
import com.efficientia.efficientia.DAO.impl.PecuaristaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;

@WebServlet(name= "PecuaristaServlet", value = "/pecuarista")

public class PecuaristaServlet extends HttpServlet {

    private PecuaristaDAO dao = new PecuaristaDAO();

    @Override
    public void init(){
        dao = new PecuaristaDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
    throws ServletException, IOException {

        String acao = req.getParameter("acao");

        if ("editar".equals(acao)) {
            int id = Integer.parseInt(req.getParameter("id"));

            PecuaristaModel pecuaristaModel = dao.buscar(id);

            req.setAttribute("pecuaristaModel", pecuaristaModel);
            req.setAttribute("pecuarista", pecuaristaModel);

            req.getRequestDispatcher(
                    "/WEB-INF/views/editar.jsp"
            ).forward(req, resp);

            return;
        }

        List<PecuaristaModel> pecuaristaModels = dao.listar();

        req.setAttribute("pecuaristaModels", pecuaristaModels);

        req.getRequestDispatcher(
                "/WEB-INF/views/pecuarista.jsp"
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
                System.out.println("Erro ao excluir pecuarista: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/pecuarista");
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

            PecuaristaModel pecuaristaModel = new PecuaristaModel(
                    id,
                    cpf,
                    assinatura,
                    dataNascimento,
                    nome,
                    senha,
                    email,
                    telefone
            );

            dao.atualizar(pecuaristaModel, id);

            resp.sendRedirect(req.getContextPath() + "/pecuarista");
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

        PecuaristaModel novoPecuarista = new PecuaristaModel(
                cpf,
                assinatura,
                dataNascimento,
                nome,
                senha,
                email,
                telefone
        );

        dao.inserir(novoPecuarista);

        resp.sendRedirect(req.getContextPath() + "/pecuarista");
    }
}
