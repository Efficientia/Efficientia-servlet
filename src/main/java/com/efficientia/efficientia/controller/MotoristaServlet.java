package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.DAO.impl.EmpresaDAO;
import com.efficientia.efficientia.DAO.impl.MotoristaDAO;
import com.efficientia.efficientia.model.EmpresaModel;
import com.efficientia.efficientia.model.MotoristaModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet(name = "MotoristaServlet", value = "/motorista")
public class MotoristaServlet extends HttpServlet {

    private MotoristaDAO dao;
    private EmpresaDAO empresaDAO;

    @Override
    public void init() {
        dao = new MotoristaDAO();
        empresaDAO = new EmpresaDAO();
    }

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        String acao = req.getParameter("acao");

        if ("editar".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                MotoristaModel motoristaModel = dao.buscar(id);

                if (motoristaModel == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Motorista não encontrado.");
                    return;
                }

                req.setAttribute("motoristaModel", motoristaModel);
                req.setAttribute("empresaModels", empresaDAO.listar());
                req.getRequestDispatcher(
                        "/WEB-INF/views/editar-motorista.jsp"
                ).forward(req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "ID do motorista inválido.");
            }
            return;
        }

        List<MotoristaModel> motoristaModels = dao.listar();
        req.setAttribute("motoristaModels", motoristaModels);
        req.setAttribute("empresaModels", empresaDAO.listar());
        req.getRequestDispatcher(
                "/WEB-INF/views/motorista.jsp"
        ).forward(req, resp);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");

        try {
            // Exclusão
            if ("excluir".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                if (!dao.excluir(id)) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Não foi possível excluir o motorista.");
                    return;
                }
                resp.sendRedirect(req.getContextPath() + "/motorista");
                return;
            }

            // Empresa vinculada ao motorista
            String idEmpresaTexto = req.getParameter("idEmpresa");
            EmpresaModel empresaModel = null;
            if (idEmpresaTexto != null && !idEmpresaTexto.isBlank()) {
                int idEmpresa = Integer.parseInt(idEmpresaTexto);
                for (EmpresaModel empresa : empresaDAO.listar()) {
                    if (empresa.getId() == idEmpresa) {
                        empresaModel = empresa;
                        break;
                    }
                }
                if (empresaModel == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Empresa não encontrada.");
                    return;
                }
            }

            // Dados usados no cadastro e na atualização
            String nome = req.getParameter("nome");
            String assinatura = req.getParameter("assinatura");
            String dataNascimentoTexto = req.getParameter("dataNascimento");
            LocalDate dataNascimento = null;
            if (dataNascimentoTexto != null && !dataNascimentoTexto.isBlank()) {
                dataNascimento = LocalDate.parse(dataNascimentoTexto);
            }
            String senha = req.getParameter("senha");
            String email = req.getParameter("email");
            String telefone = req.getParameter("telefone");

            MotoristaModel motoristaModel = new MotoristaModel(
                    empresaModel, nome, assinatura, dataNascimento,
                    senha, email, telefone
            );

            boolean sucesso;
            if ("atualizar".equals(acao)) {
                int id = Integer.parseInt(req.getParameter("id"));
                motoristaModel.setId(id);
                sucesso = dao.atualizar(motoristaModel, id);
            } else {
                sucesso = dao.inserir(motoristaModel);
            }

            if (!sucesso) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Não foi possível salvar o motorista.");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/motorista");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "ID do motorista ou da empresa inválido.");
        } catch (DateTimeParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Data de nascimento inválida.");
        }
    }
}
