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
                int id = parseInt(req.getParameter("id"), 0);
                MotoristaModel motoristaModel = dao.buscar(id);

                if (motoristaModel != null) {
                    req.setAttribute("motoristaModel", motoristaModel);
                    req.setAttribute("motorista", motoristaModel);
                    req.setAttribute("empresaModels", empresaDAO.listar());
                    req.getRequestDispatcher(
                            "/WEB-INF/views/editar-motorista.jsp"
                    ).forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar motorista para edição: " + e.getMessage());
            }
        }

        // Busca flexível por nome ou listagem geral de motoristas
        String busca = obterParametro(req, "busca", "nome", "q", "pesquisa");
        List<MotoristaModel> motoristaModels;

        if (busca != null && !busca.isBlank()) {
            motoristaModels = dao.buscarPorNome(busca);
        } else {
            motoristaModels = dao.listar();
        }

        req.setAttribute("termoBusca", busca != null ? busca : "");
        req.setAttribute("motoristaModels", motoristaModels);
        req.setAttribute("motoristas", motoristaModels);
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

        // Exclusão
        if ("excluir".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir motorista: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/motorista");
            return;
        }

        // Atualização
        if ("atualizar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);

                EmpresaModel empresaModel = buscarEmpresa(req);
                String nome = obterParametro(req, "nome");
                LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
                String senha = obterParametro(req, "senha");
                String email = obterParametro(req, "email");
                String telefone = obterParametro(req, "telefone");

                MotoristaModel motoristaModel = new MotoristaModel(
                        id,
                        empresaModel,
                        nome,
                        dataNascimento,
                        senha,
                        email,
                        telefone
                );

                dao.atualizar(motoristaModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar motorista: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/motorista");
            return;
        }

        // Cadastro
        EmpresaModel empresaModel = buscarEmpresa(req);
        String nome = obterParametro(req, "nome");
        LocalDate dataNascimento = parseLocalDate(obterParametro(req, "dataNascimento", "data_nascimento"));
        String senha = obterParametro(req, "senha");
        String email = obterParametro(req, "email");
        String telefone = obterParametro(req, "telefone");

        MotoristaModel novoMotorista = new MotoristaModel(
                empresaModel,
                nome,
                dataNascimento,
                senha,
                email,
                telefone
        );

        dao.inserir(novoMotorista);

        resp.sendRedirect(req.getContextPath() + "/motorista");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private EmpresaModel buscarEmpresa(HttpServletRequest req) {
        String idTexto = obterParametro(req, "idEmpresa", "id_empresa");
        if (idTexto != null && !idTexto.isBlank()) {
            try {
                int idEmpresa = Integer.parseInt(idTexto.trim());
                for (EmpresaModel empresa : empresaDAO.listar()) {
                    if (empresa.getId() == idEmpresa) {
                        return empresa;
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar empresa do motorista: " + e.getMessage());
            }
        }
        return null;
    }

    private String obterParametro(HttpServletRequest req, String... nomes) {
        for (String nome : nomes) {
            String valor = req.getParameter(nome);
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }

    private LocalDate parseLocalDate(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (Exception e) {
            System.out.println("Erro ao converter data (" + texto + "): " + e.getMessage());
            return null;
        }
    }

    private int parseInt(String texto, int padrao) {
        if (texto == null || texto.isBlank()) {
            return padrao;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            System.out.println("Erro ao converter número (" + texto + "): " + e.getMessage());
            return padrao;
        }
    }
}
