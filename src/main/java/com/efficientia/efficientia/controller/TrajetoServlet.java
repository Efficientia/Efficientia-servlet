package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.CaminhaoDAO;
import com.efficientia.efficientia.dao.impl.MotoristaDAO;
import com.efficientia.efficientia.dao.impl.PecuaristaDAO;
import com.efficientia.efficientia.dao.impl.TrajetoDAO;
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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

        // Buscas especializadas e busca flexível de trajetos
        String gta = obterParametro(req, "gta", "numeroGta", "numero_gta");
        String nf = obterParametro(req, "nf", "notaFiscal", "numeroNotaFiscal", "numero_nota_fiscal");
        String statusStr = obterParametro(req, "status");
        String idMotoristaStr = obterParametro(req, "idMotorista", "id_motorista");
        String idCaminhaoStr = obterParametro(req, "idCaminhao", "id_caminhao");
        String idPecuaristaStr = obterParametro(req, "idPecuarista", "id_pecuarista");
        String inicioStr = obterParametro(req, "inicio", "dataInicio", "data_inicio");
        String fimStr = obterParametro(req, "fim", "dataFim", "data_fim");
        String busca = obterParametro(req, "busca", "q", "pesquisa", "termo");

        List<TrajetoModel> trajetoModels;

        if (gta != null && !gta.isBlank()) {
            TrajetoModel t = dao.buscarPorGTA(gta);
            trajetoModels = new ArrayList<>();
            if (t != null) trajetoModels.add(t);
            req.setAttribute("termoBusca", "GTA: " + gta);
        } else if (nf != null && !nf.isBlank()) {
            TrajetoModel t = dao.buscarPorNotaFiscal(nf);
            trajetoModels = new ArrayList<>();
            if (t != null) trajetoModels.add(t);
            req.setAttribute("termoBusca", "NF: " + nf);
        } else if (statusStr != null && !statusStr.isBlank()) {
            StatusTrajeto st = StatusTrajeto.from(statusStr);
            trajetoModels = dao.buscarPorStatus(st);
            req.setAttribute("termoBusca", "Status: " + statusStr);
        } else if (idMotoristaStr != null && !idMotoristaStr.isBlank()) {
            int idMot = parseInt(idMotoristaStr, 0);
            trajetoModels = dao.buscarPorMotorista(idMot);
            req.setAttribute("termoBusca", "Motorista #" + idMot);
        } else if (idCaminhaoStr != null && !idCaminhaoStr.isBlank()) {
            int idCam = parseInt(idCaminhaoStr, 0);
            trajetoModels = dao.buscarPorCaminhao(idCam);
            req.setAttribute("termoBusca", "Caminhão #" + idCam);
        } else if (idPecuaristaStr != null && !idPecuaristaStr.isBlank()) {
            int idPec = parseInt(idPecuaristaStr, 0);
            trajetoModels = dao.buscarPorPecuarista(idPec);
            req.setAttribute("termoBusca", "Pecuarista #" + idPec);
        } else if (inicioStr != null && !inicioStr.isBlank() && fimStr != null && !fimStr.isBlank()) {
            try {
                LocalDateTime ini = parseLocalDateTime(inicioStr);
                LocalDateTime fim = parseLocalDateTime(fimStr);
                trajetoModels = dao.buscarPorPeriodo(ini, fim);
                req.setAttribute("termoBusca", inicioStr + " a " + fimStr);
            } catch (Exception e) {
                trajetoModels = dao.listar();
                req.setAttribute("termoBusca", "");
            }
        } else if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();
            Set<Integer> ids = new LinkedHashSet<>();
            trajetoModels = new ArrayList<>();

            // 1. GTA
            TrajetoModel tGta = dao.buscarPorGTA(termo);
            if (tGta != null && ids.add(tGta.getId())) trajetoModels.add(tGta);

            // 2. Nota Fiscal
            TrajetoModel tNf = dao.buscarPorNotaFiscal(termo);
            if (tNf != null && ids.add(tNf.getId())) trajetoModels.add(tNf);

            // 3. Status coincidente (normalizado contra acentos como 'Concluída')
            String termoNorm = normalizar(termo);
            for (StatusTrajeto st : StatusTrajeto.values()) {
                String stNorm = normalizar(st.name());
                if (stNorm.equalsIgnoreCase(termoNorm)
                        || stNorm.contains(termoNorm)
                        || termoNorm.contains(stNorm)) {
                    for (TrajetoModel t : dao.buscarPorStatus(st)) {
                        if (ids.add(t.getId())) trajetoModels.add(t);
                    }
                }
            }

            // 4. Motorista (por nome)
            for (MotoristaModel m : motoristaDAO.buscarPorNome(termo)) {
                for (TrajetoModel t : dao.buscarPorMotorista(m.getId())) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
            }

            // 5. Caminhão (por placa)
            for (CaminhaoModel c : caminhaoDAO.buscarPorPlaca(termo)) {
                for (TrajetoModel t : dao.buscarPorCaminhao(c.getId())) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
            }

            // 6. Pecuarista (por nome ou CPF)
            for (PecuaristaModel p : pecuaristaDAO.buscarPorNome(termo)) {
                for (TrajetoModel t : dao.buscarPorPecuarista(p.getId())) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
            }
            for (PecuaristaModel p : pecuaristaDAO.buscarPorCpf(termo)) {
                for (TrajetoModel t : dao.buscarPorPecuarista(p.getId())) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
            }

            // 7. Se for número: ID direto, ID motorista, caminhão ou pecuarista
            try {
                int idNum = Integer.parseInt(termo);
                TrajetoModel tId = dao.buscar(idNum);
                if (tId != null && ids.add(tId.getId())) trajetoModels.add(tId);
                for (TrajetoModel t : dao.buscarPorMotorista(idNum)) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
                for (TrajetoModel t : dao.buscarPorCaminhao(idNum)) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
                for (TrajetoModel t : dao.buscarPorPecuarista(idNum)) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
            } catch (NumberFormatException ignored) {}

            req.setAttribute("termoBusca", busca);
        } else {
            trajetoModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }

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

        //Atualização Rápida de Status (Conclusão / Retorno a Em Andamento)
        if ("atualizarStatus".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                StatusTrajeto novoStatus = parseStatus(req.getParameter("status"));
                dao.atualizarStatus(id, novoStatus);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar status do trajeto: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

        //Inserção Rápida / Simplificada
        if ("inserirSimples".equals(acao)) {
            try {
                Integer idMotorista = parseIntegerNull(req.getParameter("idMotorista"));
                Integer idCaminhao = parseIntegerNull(req.getParameter("idCaminhao"));
                Integer idPecuarista = parseIntegerNull(req.getParameter("idPecuarista"));
                StatusTrajeto status = parseStatus(req.getParameter("status"));
                String numeroGTA = obterParametro(req, "numeroGTA", "numero_gta", "numeroGta");
                String numeroNotaFiscal = obterParametro(req, "numeroNotaFiscal", "numero_nota_fiscal");
                dao.inserirSimples(idMotorista, idCaminhao, idPecuarista, status, numeroGTA, numeroNotaFiscal);
            } catch (Exception e) {
                System.out.println("Erro ao inserir trajeto simples: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

        //Atualização Rápida de GTA
        if ("atualizarGTA".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                String novoGta = obterParametro(req, "numeroGTA", "numero_gta", "gta");
                dao.atualizarGTA(id, novoGta);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar GTA: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

        //Atualização Rápida de Nota Fiscal
        if ("atualizarNotaFiscal".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                String novaNf = obterParametro(req, "numeroNotaFiscal", "numero_nota_fiscal", "nf");
                dao.atualizarNotaFiscal(id, novaNf);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar Nota Fiscal: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

        //Atualização Rápida de Km
        if ("atualizarKm".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                int kmSaida = parseInt(obterParametro(req, "kmSaida", "km_saida"), 0);
                int kmChegada = parseInt(obterParametro(req, "kmChegada", "km_chegada"), 0);
                dao.atualizarKm(id, kmSaida, kmChegada);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar Km: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

        //Atualização Rápida de Curral
        if ("atualizarCurral".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                String curral = obterParametro(req, "numeroCurral", "numero_curral");
                String curraleiro = obterParametro(req, "nomeCurraleiro", "nome_curraleiro");
                dao.atualizarCurral(id, curral, curraleiro);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar curral: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

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
            LocalDateTime horarioDesembarque = parseLocalDateTime(obterParametro(req, "horarioDesembarque", "horario_desembarque"));

            if (status == StatusTrajeto.CONCLUIDA && dataHoraFim == null) {
                dataHoraFim = horarioDesembarque != null ? horarioDesembarque : LocalDateTime.now();
            }

            int kmSaida = parseInt(obterParametro(req, "kmSaida", "km_saida"), 0);
            int kmChegada = parseInt(obterParametro(req, "kmChegada", "km_chegada"), 0);

            String numeroGTA = obterParametro(req, "numeroGTA", "numero_gta", "numeroGta");
            String numeroNotaFiscal = obterParametro(req, "numeroNotaFiscal", "numero_nota_fiscal");

            LocalDateTime horarioEmbarque = parseLocalDateTime(obterParametro(req, "horarioEmbarque", "horario_embarque"));

            int qtdMacho = parseInt(obterParametro(req, "qtdMacho", "qtd_macho"), 0);
            int qtdFemea = parseInt(obterParametro(req, "qtdFemea", "qtd_femea"), 0);
            int qtdMarruco = parseInt(obterParametro(req, "qtdMarruco", "qtd_marruco"), 0);

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
        if (horarioEmbarque == null) {
            horarioEmbarque = dataHoraInicio != null ? dataHoraInicio : LocalDateTime.now();
        }

        int qtdMacho = parseInt(obterParametro(req, "qtdMacho", "qtd_macho"), 0);
        int qtdFemea = parseInt(obterParametro(req, "qtdFemea", "qtd_femea"), 0);
        int qtdMarruco = parseInt(obterParametro(req, "qtdMarruco", "qtd_marruco"), 0);

        LocalDateTime horarioDesembarque = parseLocalDateTime(obterParametro(req, "horarioDesembarque", "horario_desembarque"));

        if (status == StatusTrajeto.CONCLUIDA && dataHoraFim == null) {
            dataHoraFim = horarioDesembarque != null ? horarioDesembarque : LocalDateTime.now();
        }

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
        return StatusTrajeto.from(statusTexto, StatusTrajeto.EM_ANDAMENTO);
    }

    private Integer parseIntegerNull(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            int v = Integer.parseInt(texto.trim());
            return v > 0 ? v : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String normalizar(String str) {
        if (str == null) return "";
        return java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase()
                .replace(" ", "_");
    }
}
