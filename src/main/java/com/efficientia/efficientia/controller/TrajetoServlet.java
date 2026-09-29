package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.DAO.impl.CaminhaoDAO;
import com.efficientia.efficientia.DAO.impl.MotoristaDAO;
import com.efficientia.efficientia.DAO.impl.PecuaristaDAO;
import com.efficientia.efficientia.DAO.impl.TrajetoDAO;
import com.efficientia.efficientia.model.CaminhaoModel;
import com.efficientia.efficientia.model.MotoristaModel;
import com.efficientia.efficientia.model.PecuaristaModel;
import com.efficientia.efficientia.model.StatusTrajeto;
import com.efficientia.efficientia.model.TrajetoModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "TrajetoServlet", value = "/trajeto")
public class TrajetoServlet extends HttpServlet {

    private TrajetoDAO dao;
    private MotoristaDAO motoristaDAO;
    private CaminhaoDAO caminhaoDAO;
    private PecuaristaDAO pecuaristaDAO;

    @Override
    public void init() {
        dao = new TrajetoDAO();
        motoristaDAO = new MotoristaDAO();
        caminhaoDAO = new CaminhaoDAO();
        pecuaristaDAO = new PecuaristaDAO();
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

                TrajetoModel trajetoModel = dao.buscar(id);

                req.setAttribute("trajetoModel", trajetoModel);
                req.setAttribute("trajeto", trajetoModel);
                req.setAttribute("motoristaModels", motoristaDAO.listar());
                req.setAttribute("caminhaoModels", caminhaoDAO.listar());
                req.setAttribute("pecuaristaModels", pecuaristaDAO.listar());
                req.setAttribute("statusList", StatusTrajeto.values());

                req.getRequestDispatcher(
                        "/WEB-INF/views/editar-trajeto.jsp"
                ).forward(req, resp);

                return;
            } catch (Exception e) {
                System.out.println("Erro ao buscar trajeto para edicao: " + e.getMessage());
            }
        }

        List<TrajetoModel> trajetoModels = dao.listar();

        req.setAttribute("trajetoModels", trajetoModels);
        req.setAttribute("trajetos", trajetoModels);
        req.setAttribute("motoristaModels", motoristaDAO.listar());
        req.setAttribute("caminhaoModels", caminhaoDAO.listar());
        req.setAttribute("pecuaristaModels", pecuaristaDAO.listar());
        req.setAttribute("statusList", StatusTrajeto.values());

        req.getRequestDispatcher(
                "/WEB-INF/views/trajeto.jsp"
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
                System.out.println("Erro ao excluir trajeto: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/trajeto");
            return;
        }

        //Atualização
        if ("atualizar".equals(acao)) {
            int id = Integer.parseInt(req.getParameter("id"));

            MotoristaModel motoristaModel = buscarMotorista(req);
            CaminhaoModel caminhaoModel = buscarCaminhao(req);
            PecuaristaModel pecuaristaModel = buscarPecuarista(req);
            StatusTrajeto status = parseStatus(req.getParameter("status"));

            LocalDateTime dataHoraInicio = parseLocalDateTime(obterParametro(req, "dataHoraInicio", "data_hora_inicio"));
            LocalDateTime dataHoraFim = parseLocalDateTime(obterParametro(req, "dataHoraFim", "data_hora_fim"));

            int kmSaida = parseInt(obterParametro(req, "kmSaida", "km_saida"), 0);
            int kmChegada = parseInt(obterParametro(req, "kmChegada", "km_chegada"), 0);

            String numeroGTA = obterParametro(req, "numeroGTA", "numero_gta", "numeroGta");
            String numeroNotaFiscal = obterParametro(req, "numeroNotaFiscal", "numero_nota_fiscal");

            LocalDateTime horarioEmbarque = parseLocalDateTime(obterParametro(req, "horarioEmbarque", "horario_embarque"));

            int qtdMacho = parseInt(obterParametro(req, "qtdMacho", "qtd_macho"), 0);
            int qtdFemea = parseInt(obterParametro(req, "qtdFemea", "qtd_femea"), 0);
            int qtdMarruco = parseInt(obterParametro(req, "qtdMarruco", "qtd_marruco"), 0);

            LocalDateTime horarioDesembarque = parseLocalDateTime(obterParametro(req, "horarioDesembarque", "horario_desembarque"));

            String numeroCurral = obterParametro(req, "numeroCurral", "numero_curral");
            String nomeCurraleiro = obterParametro(req, "nomeCurraleiro", "nome_curraleiro");
            String nomeManobrista = obterParametro(req, "nomeManobrista", "nome_manobrista");

            String assinaturaCurraleiro = obterParametro(req, "assinaturaCurraleiro", "assinatura_curraleiro");
            String assinaturaManobrista = obterParametro(req, "assinaturaManobrista", "assinatura_manobrista");
            String assinaturaMotorista = obterParametro(req, "assinaturaMotorista", "assinatura_motorista");

            TrajetoModel trajetoModel = new TrajetoModel(
                    id,
                    motoristaModel,
                    caminhaoModel,
                    status,
                    dataHoraInicio,
                    dataHoraFim,
                    kmSaida,
                    kmChegada,
                    pecuaristaModel,
                    numeroGTA,
                    numeroNotaFiscal,
                    horarioEmbarque,
                    qtdMacho,
                    qtdFemea,
                    qtdMarruco,
                    horarioDesembarque,
                    numeroCurral,
                    nomeCurraleiro,
                    nomeManobrista
            );

            if (assinaturaCurraleiro != null) {
                trajetoModel.setAssinaturaCurraleiro(assinaturaCurraleiro);
            }
            if (assinaturaManobrista != null) {
                trajetoModel.setAssinaturaManobrista(assinaturaManobrista);
            }
            if (assinaturaMotorista != null && !assinaturaMotorista.isBlank()) {
                trajetoModel.setAssinaturaMotorista(assinaturaMotorista);
            }

            dao.atualizar(trajetoModel, id);

            resp.sendRedirect(req.getContextPath() + "/trajeto");
            return;
        }

        //Cadastro
        MotoristaModel motoristaModel = buscarMotorista(req);
        CaminhaoModel caminhaoModel = buscarCaminhao(req);
        PecuaristaModel pecuaristaModel = buscarPecuarista(req);
        StatusTrajeto status = parseStatus(req.getParameter("status"));

        LocalDateTime dataHoraInicio = parseLocalDateTime(obterParametro(req, "dataHoraInicio", "data_hora_inicio"));
        LocalDateTime dataHoraFim = parseLocalDateTime(obterParametro(req, "dataHoraFim", "data_hora_fim"));

        int kmSaida = parseInt(obterParametro(req, "kmSaida", "km_saida"), 0);
        int kmChegada = parseInt(obterParametro(req, "kmChegada", "km_chegada"), 0);

        String numeroGTA = obterParametro(req, "numeroGTA", "numero_gta", "numeroGta");
        String numeroNotaFiscal = obterParametro(req, "numeroNotaFiscal", "numero_nota_fiscal");

        LocalDateTime horarioEmbarque = parseLocalDateTime(obterParametro(req, "horarioEmbarque", "horario_embarque"));

        int qtdMacho = parseInt(obterParametro(req, "qtdMacho", "qtd_macho"), 0);
        int qtdFemea = parseInt(obterParametro(req, "qtdFemea", "qtd_femea"), 0);
        int qtdMarruco = parseInt(obterParametro(req, "qtdMarruco", "qtd_marruco"), 0);

        LocalDateTime horarioDesembarque = parseLocalDateTime(obterParametro(req, "horarioDesembarque", "horario_desembarque"));

        String numeroCurral = obterParametro(req, "numeroCurral", "numero_curral");
        String nomeCurraleiro = obterParametro(req, "nomeCurraleiro", "nome_curraleiro");
        String nomeManobrista = obterParametro(req, "nomeManobrista", "nome_manobrista");

        String assinaturaCurraleiro = obterParametro(req, "assinaturaCurraleiro", "assinatura_curraleiro");
        String assinaturaManobrista = obterParametro(req, "assinaturaManobrista", "assinatura_manobrista");
        String assinaturaMotorista = obterParametro(req, "assinaturaMotorista", "assinatura_motorista");

        TrajetoModel novoTrajeto = new TrajetoModel(
                motoristaModel,
                caminhaoModel,
                status,
                dataHoraInicio,
                dataHoraFim,
                kmSaida,
                kmChegada,
                pecuaristaModel,
                numeroGTA,
                numeroNotaFiscal,
                horarioEmbarque,
                qtdMacho,
                qtdFemea,
                qtdMarruco,
                horarioDesembarque,
                numeroCurral,
                nomeCurraleiro,
                nomeManobrista
        );

        if (assinaturaCurraleiro != null) {
            novoTrajeto.setAssinaturaCurraleiro(assinaturaCurraleiro);
        }
        if (assinaturaManobrista != null) {
            novoTrajeto.setAssinaturaManobrista(assinaturaManobrista);
        }
        if (assinaturaMotorista != null && !assinaturaMotorista.isBlank()) {
            novoTrajeto.setAssinaturaMotorista(assinaturaMotorista);
        }

        dao.inserir(novoTrajeto);

        resp.sendRedirect(req.getContextPath() + "/trajeto");
    }

    private MotoristaModel buscarMotorista(HttpServletRequest req) {
        String idTexto = obterParametro(req, "idMotorista", "id_motorista");
        if (idTexto != null && !idTexto.isBlank()) {
            try {
                return motoristaDAO.buscar(Integer.parseInt(idTexto.trim()));
            } catch (Exception e) {
                System.out.println("Erro ao buscar motorista: " + e.getMessage());
            }
        }
        return null;
    }

    private CaminhaoModel buscarCaminhao(HttpServletRequest req) {
        String idTexto = obterParametro(req, "idCaminhao", "id_caminhao");
        if (idTexto != null && !idTexto.isBlank()) {
            try {
                return caminhaoDAO.buscar(Integer.parseInt(idTexto.trim()));
            } catch (Exception e) {
                System.out.println("Erro ao buscar caminhao: " + e.getMessage());
            }
        }
        return null;
    }

    private PecuaristaModel buscarPecuarista(HttpServletRequest req) {
        String idTexto = obterParametro(req, "idPecuarista", "id_pecuarista");
        if (idTexto != null && !idTexto.isBlank()) {
            try {
                return pecuaristaDAO.buscar(Integer.parseInt(idTexto.trim()));
            } catch (Exception e) {
                System.out.println("Erro ao buscar pecuarista: " + e.getMessage());
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

    private LocalDateTime parseLocalDateTime(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(texto.replace(" ", "T"));
        } catch (Exception e) {
            System.out.println("Erro ao converter data e hora (" + texto + "): " + e.getMessage());
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
            System.out.println("Erro ao converter numero (" + texto + "): " + e.getMessage());
            return padrao;
        }
    }

    private StatusTrajeto parseStatus(String statusTexto) {
        if (statusTexto == null || statusTexto.isBlank()) {
            return StatusTrajeto.EM_ANDAMENTO;
        }
        try {
            return StatusTrajeto.valueOf(statusTexto.trim().toUpperCase());
        } catch (Exception e) {
            System.out.println("Erro ao converter status (" + statusTexto + "): " + e.getMessage());
            return StatusTrajeto.EM_ANDAMENTO;
        }
    }
}
