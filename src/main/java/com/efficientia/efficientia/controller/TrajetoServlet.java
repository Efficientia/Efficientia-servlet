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
import com.efficientia.efficientia.util.ValidadorRegex;
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

/**
 * Servlet controlador central da operação logística de viagens e transportes pecuários.
 *
 * Mapeado no endpoint '/trajeto', coordena todo o fluxo operacional do transporte de gado no sistema Efficientia:
 * - Emissão e controle documental de GTA (Guia de Trânsito Animal) e Nota Fiscal
 * - Alocação de motorista condutor, caminhão de transporte e pecuarista (produtor remetente)
 * - Monitoramento do ciclo de vida da viagem (status EM_ANDAMENTO e CONCLUIDA)
 * - Registro de odômetro e quilometragens de saída e chegada
 * - Horários de embarque e desembarque com contagem segregada por categoria animal (macho, fêmea e marruco)
 * - Gestão de destinação em curral, conferência por curraleiro e manobrista, e assinaturas digitais
 * - Ações pontuais rápidas (atualização de status, GTA, NF, Km, Curral) e inserção simplificada
 * - Mecanismos de busca especializada e pesquisa flexível global insensível a acentos
 */
@WebServlet(name = "TrajetoServlet", value = "/trajeto")
public class TrajetoServlet extends HttpServlet {

    private TrajetoDAO dao;
    private MotoristaDAO motoristaDAO;
    private CaminhaoDAO caminhaoDAO;
    private PecuaristaDAO pecuaristaDAO;

    // ==================== INICIALIZAÇÃO ====================

    /**
     * Inicializa os Data Access Objects (DAOs) necessários para o ciclo de vida do servlet,
     * permitindo a recuperação e montagem de todo o grafo relacional da viagem.
     */
    @Override
    public void init() {
        dao = new TrajetoDAO();
        motoristaDAO = new MotoristaDAO();
        caminhaoDAO = new CaminhaoDAO();
        pecuaristaDAO = new PecuaristaDAO();
    }

    // ==================== REQUISIÇÕES GET ====================

    /**
     * Processa requisições HTTP GET para consulta, filtros especializados e edição de viagens pecuárias.
     * Suporta a ação 'editar' para carregamento dos dados da viagem e coleções relacionais,
     * bem como múltiplos filtros específicos (GTA, Nota Fiscal, Status, Motorista, Caminhão, Pecuarista, Período)
     * e mecanismo de busca global flexível com normalização textual contra acentos.
     *
     * @param req  objeto {@link HttpServletRequest} contendo os parâmetros e filtros da requisição
     * @param resp objeto {@link HttpServletResponse} para direcionamento ou encaminhamento HTTP
     * @throws ServletException caso ocorra erro no despacho para a visão JSP
     * @throws IOException      caso ocorra erro de entrada/saída durante o encaminhamento
     */
    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String acao = req.getParameter("acao");

        // Edição: busca o trajeto pelo ID e popula os modelos relacionais para os selects da visão
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

        // 1. Filtro especializado: busca exata por GTA (Guia de Trânsito Animal)
        if (gta != null && !gta.isBlank()) {
            TrajetoModel t = dao.buscarPorGTA(gta);
            trajetoModels = new ArrayList<>();
            if (t != null) trajetoModels.add(t);
            req.setAttribute("termoBusca", "GTA: " + gta);
        // 2. Filtro especializado: busca exata por número de Nota Fiscal
        } else if (nf != null && !nf.isBlank()) {
            TrajetoModel t = dao.buscarPorNotaFiscal(nf);
            trajetoModels = new ArrayList<>();
            if (t != null) trajetoModels.add(t);
            req.setAttribute("termoBusca", "NF: " + nf);
        // 3. Filtro especializado: busca por status do ciclo de vida (EM_ANDAMENTO / CONCLUIDA)
        } else if (statusStr != null && !statusStr.isBlank()) {
            StatusTrajeto st = StatusTrajeto.from(statusStr);
            trajetoModels = dao.buscarPorStatus(st);
            req.setAttribute("termoBusca", "Status: " + statusStr);
        // 4. Filtro especializado: busca por motorista condutor
        } else if (idMotoristaStr != null && !idMotoristaStr.isBlank()) {
            int idMot = parseInt(idMotoristaStr, 0);
            trajetoModels = dao.buscarPorMotorista(idMot);
            req.setAttribute("termoBusca", "Motorista #" + idMot);
        // 5. Filtro especializado: busca por caminhão de transporte
        } else if (idCaminhaoStr != null && !idCaminhaoStr.isBlank()) {
            int idCam = parseInt(idCaminhaoStr, 0);
            trajetoModels = dao.buscarPorCaminhao(idCam);
            req.setAttribute("termoBusca", "Caminhão #" + idCam);
        // 6. Filtro especializado: busca por produtor rural / pecuarista remetente
        } else if (idPecuaristaStr != null && !idPecuaristaStr.isBlank()) {
            int idPec = parseInt(idPecuaristaStr, 0);
            trajetoModels = dao.buscarPorPecuarista(idPec);
            req.setAttribute("termoBusca", "Pecuarista #" + idPec);
        // 7. Filtro especializado: busca por período de datas (intervalo entre início e fim)
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
        // 8. Busca flexível unificada: varre GTA, NF, status, motorista, caminhão, pecuarista e IDs numéricos
        } else if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();
            Set<Integer> ids = new LinkedHashSet<>();
            trajetoModels = new ArrayList<>();

            // 8.1. Correspondência por GTA
            TrajetoModel tGta = dao.buscarPorGTA(termo);
            if (tGta != null && ids.add(tGta.getId())) trajetoModels.add(tGta);

            // 8.2. Correspondência por Nota Fiscal
            TrajetoModel tNf = dao.buscarPorNotaFiscal(termo);
            if (tNf != null && ids.add(tNf.getId())) trajetoModels.add(tNf);

            // 8.3. Correspondência por Status do Enum (com normalização contra acentos como 'Concluída')
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

            // 8.4. Correspondência por condutor (nome do motorista)
            for (MotoristaModel m : motoristaDAO.buscarPorNome(termo)) {
                for (TrajetoModel t : dao.buscarPorMotorista(m.getId())) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
            }

            // 8.5. Correspondência por caminhão (placa do cavalo ou carreta)
            for (CaminhaoModel c : caminhaoDAO.buscarPorPlaca(termo)) {
                for (TrajetoModel t : dao.buscarPorCaminhao(c.getId())) {
                    if (ids.add(t.getId())) trajetoModels.add(t);
                }
            }

            // 8.6. Correspondência por pecuarista (nome ou CPF)
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

            // 8.7. Correspondência por identificadores numéricos diretos (ID trajeto, motorista, caminhão, pecuarista)
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
        // 9. Listagem padrão de todos os trajetos cadastrados
        } else {
            trajetoModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }

        // Define os dados consultados e as coleções relacionais na requisição para a visão JSP
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

    // ==================== REQUISIÇÕES POST ====================

    /**
     * Processa requisições HTTP POST para criação, exclusão e alterações operacionais do trajeto.
     * Implementa o padrão Post-Redirect-Get (PRG) e suporta múltiplas rotas de ação operacional:
     * - 'atualizarStatus': transição rápida do ciclo de vida da viagem (ex.: conclusão ou reabertura)
     * - 'inserirSimples': cadastro enxuto de viagem com motorista, caminhão, pecuarista, GTA e NF
     * - 'atualizarGTA': atualização pontual da Guia de Trânsito Animal
     * - 'atualizarNotaFiscal': atualização pontual da Nota Fiscal
     * - 'atualizarKm': registro do odômetro de saída e chegada
     * - 'atualizarCurral': destinação de desembarque no frigorífico/curral com registro do curraleiro
     * - 'excluir': exclusão física de um registro de trajeto
     * - 'atualizar': edição completa de todos os dados do trajeto, contagens e assinaturas
     * - Cadastro padrão: inserção detalhada de nova viagem pecuária
     *
     * @param req  objeto {@link HttpServletRequest} contendo os dados submetidos pelo formulário
     * @param resp objeto {@link HttpServletResponse} para redirecionamento após a operação
     * @throws IOException caso ocorra erro no redirecionamento HTTP
     */
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        // Garante suporte adequado à codificação UTF-8
        req.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // 1. Ação rápida: Atualização de Status (Conclusão da viagem ou retorno a Em Andamento)
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

        // 2. Ação rápida: Inserção Simplificada de Viagem (fluxo ágil de despacho)
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

        // 3. Ação rápida: Atualização de GTA (Guia de Trânsito Animal)
        if ("atualizarGTA".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                String novoGta = obterParametro(req, "numeroGTA", "numero_gta", "gta");

                // Validação defensiva com REGEX
                if (novoGta != null && !novoGta.isBlank() && !ValidadorRegex.isGtaValido(novoGta)) {
                    resp.sendRedirect(req.getContextPath() + "/trajeto?erro=gta_invalido");
                    return;
                }

                dao.atualizarGTA(id, novoGta);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar GTA: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

        // 4. Ação rápida: Atualização de Nota Fiscal
        if ("atualizarNotaFiscal".equals(acao)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                String novaNf = obterParametro(req, "numeroNotaFiscal", "numero_nota_fiscal", "nf");

                // Validação defensiva com REGEX
                if (novaNf != null && !novaNf.isBlank() && !ValidadorRegex.isNotaFiscalValida(novaNf)) {
                    resp.sendRedirect(req.getContextPath() + "/trajeto?erro=nf_invalida");
                    return;
                }

                dao.atualizarNotaFiscal(id, novaNf);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar Nota Fiscal: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + (req.getParameter("redirect") != null ? req.getParameter("redirect") : "/trajeto"));
            return;
        }

        // 5. Ação rápida: Atualização de Odômetro (Km Saída e Km Chegada)
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

        // 6. Ação rápida: Destinação de Desembarque (Número do Curral e Curraleiro responsável)
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

        // 7. Ação: Exclusão de Trajeto por ID
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

        // 8. Ação: Atualização Completa de Trajeto (parâmetros operacionais, desembarque e assinaturas)
        if ("atualizar".equals(acao)) {
            int id = Integer.parseInt(req.getParameter("id"));

            MotoristaModel motoristaModel = buscarMotorista(req);
            CaminhaoModel caminhaoModel = buscarCaminhao(req);
            PecuaristaModel pecuaristaModel = buscarPecuarista(req);
            StatusTrajeto status = parseStatus(req.getParameter("status"));

            LocalDateTime dataHoraInicio = parseLocalDateTime(obterParametro(req, "dataHoraInicio", "data_hora_inicio"));
            LocalDateTime dataHoraFim = parseLocalDateTime(obterParametro(req, "dataHoraFim", "data_hora_fim"));
            LocalDateTime horarioDesembarque = parseLocalDateTime(obterParametro(req, "horarioDesembarque", "horario_desembarque"));

            // Se o status for CONCLUIDA e dataHoraFim não foi informada, utiliza o horário de desembarque ou o momento atual
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

            // Vincula assinaturas caso tenham sido fornecidas
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

        // 9. Ação padrão: Cadastro Completo de Novo Trajeto
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

        // Fallback operacional para horário de embarque quando não informado explicitamente
        LocalDateTime horarioEmbarque = parseLocalDateTime(obterParametro(req, "horarioEmbarque", "horario_embarque"));
        if (horarioEmbarque == null) {
            horarioEmbarque = dataHoraInicio != null ? dataHoraInicio : LocalDateTime.now();
        }

        int qtdMacho = parseInt(obterParametro(req, "qtdMacho", "qtd_macho"), 0);
        int qtdFemea = parseInt(obterParametro(req, "qtdFemea", "qtd_femea"), 0);
        int qtdMarruco = parseInt(obterParametro(req, "qtdMarruco", "qtd_marruco"), 0);

        LocalDateTime horarioDesembarque = parseLocalDateTime(obterParametro(req, "horarioDesembarque", "horario_desembarque"));

        // Fallback operacional para horário de conclusão em viagens finalizadas
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

        // Vincula assinaturas digitais capturadas
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

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Recupera a entidade {@link MotoristaModel} a partir do ID enviado na requisição.
     * Resolve a chave estrangeira do motorista condutor da viagem.
     *
     * @param req requisição HTTP contendo o identificador do motorista
     * @return objeto {@link MotoristaModel} correspondente ou null se inválido ou não informado
     */
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

    /**
     * Recupera a entidade {@link CaminhaoModel} a partir do ID enviado na requisição.
     * Resolve a chave estrangeira do veículo de transporte pecuário.
     *
     * @param req requisição HTTP contendo o identificador do caminhão
     * @return objeto {@link CaminhaoModel} correspondente ou null se inválido ou não informado
     */
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

    /**
     * Recupera a entidade {@link PecuaristaModel} a partir do ID enviado na requisição.
     * Resolve a chave estrangeira do produtor rural remetente dos animais.
     *
     * @param req requisição HTTP contendo o identificador do pecuarista
     * @return objeto {@link PecuaristaModel} correspondente ou null se inválido ou não informado
     */
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

    /**
     * Obtém o primeiro valor não nulo e não em branco correspondente aos nomes informados nos parâmetros da requisição.
     * Fornece resiliência contra variações de parâmetros (suporte a camelCase e snake_case).
     *
     * @param req   requisição HTTP
     * @param nomes nomes alternativos de parâmetros aceitos
     * @return valor do parâmetro ou null caso não encontrado
     */
    private String obterParametro(HttpServletRequest req, String... nomes) {
        for (String nome : nomes) {
            String valor = req.getParameter(nome);
            if (valor != null && !valor.isBlank()) {
                return valor;
            }
        }
        return null;
    }

    /**
     * Converte com segurança uma string de data e hora para {@link LocalDateTime}.
     * Trata variações de formato comuns em inputs HTML datetime-local (substitui espaços por 'T').
     *
     * @param texto texto contendo a data e hora
     * @return objeto {@link LocalDateTime} correspondente ou null em caso de erro ou valor vazio
     */
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

    /**
     * Converte com segurança uma string para número inteiro, retornando um valor padrão em caso de falha.
     * Previne exceções do tipo {@link NumberFormatException} que resultariam em erro HTTP 500.
     *
     * @param texto  string a ser convertida
     * @param padrao valor padrão de retorno em caso de ausência ou falha de conversão
     * @return inteiro convertido ou valor padrão
     */
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

    /**
     * Converte com segurança uma string para o enum {@link StatusTrajeto}.
     * Utiliza o método defensivo {@link StatusTrajeto#from(String, StatusTrajeto)} com fallback para EM_ANDAMENTO.
     *
     * @param statusTexto texto representando o status do trajeto
     * @return valor do enum {@link StatusTrajeto} correspondente
     */
    private StatusTrajeto parseStatus(String statusTexto) {
        return StatusTrajeto.from(statusTexto, StatusTrajeto.EM_ANDAMENTO);
    }

    /**
     * Converte uma string para Integer permitindo retorno null para valores em branco ou menores/iguais a zero.
     * Utilizado para campos de chave estrangeira opcionais.
     *
     * @param texto texto contendo o número inteiro
     * @return valor {@link Integer} ou null
     */
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

    /**
     * Normaliza uma string removendo diacríticos (acentos), espaços extras e convertendo para maiúsculas.
     * Garante comparações consistentes em buscas textuais (ex.: 'Concluída' vs 'CONCLUIDA').
     *
     * @param str string a ser normalizada
     * @return string tratada em caixa alta sem acentuação
     */
    private static String normalizar(String str) {
        if (str == null) return "";
        return java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase()
                .replace(" ", "_");
    }
}
