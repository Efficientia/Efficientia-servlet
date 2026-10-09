package com.efficientia.efficientia.controller;

import com.efficientia.efficientia.dao.impl.CaminhaoDAO;
import com.efficientia.efficientia.dao.impl.EmpresaDAO;
import com.efficientia.efficientia.model.CaminhaoModel;
import com.efficientia.efficientia.model.EmpresaModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsável pelo controle de requisições relacionadas à entidade Caminhão.
 *
 * Mapeado no endpoint '/caminhao', atua como Controller na arquitetura MVC do sistema Efficientia,
 * gerenciando operações de listagem, consulta, cadastro, atualização e exclusão
 * dos veículos de transporte de carga pecuária, associados às suas respectivas transportadoras.
 */
@WebServlet(name = "CaminhaoServlet", value = "/caminhao")
public class CaminhaoServlet extends HttpServlet {

    private CaminhaoDAO dao;
    private EmpresaDAO empresaDAO;

    // ==================== INICIALIZAÇÃO ====================

    /**
     * Inicializa os Data Access Objects (DAOs) necessários para o ciclo de vida do servlet.
     */
    @Override
    public void init() {
        dao = new CaminhaoDAO();
        empresaDAO = new EmpresaDAO();
    }

    // ==================== REQUISIÇÕES GET ====================

    /**
     * Processa requisições HTTP GET para consulta e exibição de veículos.
     * Suporta a ação 'editar' para carregar os dados de um caminhão específico,
     * bem como buscas por empresa vinculada, placa (cavalo ou carreta) ou listagem geral.
     *
     * @param req  objeto {@link HttpServletRequest} contendo os parâmetros da requisição
     * @param resp objeto {@link HttpServletResponse} para direcionamento e resposta HTTP
     * @throws ServletException caso ocorra erro no despacho para a visão JSP
     * @throws IOException      caso ocorra erro de entrada/saída durante o encaminhamento
     */
    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {

        String acao = req.getParameter("acao");

        // Edição: busca o caminhão pelo ID e despacha para o formulário de edição com as empresas
        if ("editar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                CaminhaoModel caminhaoModel = dao.buscar(id);

                if (caminhaoModel != null) {
                    req.setAttribute("caminhaoModel", caminhaoModel);
                    req.setAttribute("caminhao", caminhaoModel);
                    req.setAttribute("empresaModels", empresaDAO.listar());
                    req.getRequestDispatcher(
                            "/WEB-INF/views/editar-caminhao.jsp"
                    ).forward(req, resp);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Erro ao buscar caminhão para edição: " + e.getMessage());
            }
        }

        // Busca flexível: por empresa vinculada, por placa (cavalo ou carreta) ou listagem geral
        String idEmpresaStr = obterParametro(req, "idEmpresa", "id_empresa");
        String busca = obterParametro(req, "placa", "busca", "q", "pesquisa");
        List<CaminhaoModel> caminhaoModels;

        // 1. Busca por empresa vinculada
        if (idEmpresaStr != null && !idEmpresaStr.isBlank()) {
            int idEmpresa = parseInt(idEmpresaStr, 0);
            caminhaoModels = dao.buscarPorEmpresa(idEmpresa);
            req.setAttribute("termoBusca", "Empresa #" + idEmpresa);
        // 2. Busca por placa do veículo (cavalo mecânico ou carreta)
        } else if (busca != null && !busca.isBlank()) {
            caminhaoModels = dao.buscarPorPlaca(busca);
            req.setAttribute("termoBusca", busca);
        // 3. Listagem geral de todos os caminhões cadastrados
        } else {
            caminhaoModels = dao.listar();
            req.setAttribute("termoBusca", "");
        }

        // Define os atributos na requisição e encaminha para a visão principal
        req.setAttribute("termoBusca", busca != null ? busca : "");
        req.setAttribute("caminhaoModels", caminhaoModels);
        req.setAttribute("caminhoes", caminhaoModels);
        req.setAttribute("empresaModels", empresaDAO.listar());

        req.getRequestDispatcher(
                "/WEB-INF/views/caminhao.jsp"
        ).forward(req, resp);
    }

    // ==================== REQUISIÇÕES POST ====================

    /**
     * Processa requisições HTTP POST para operações de modificação (CUD: Cadastro, Atualização e Exclusão).
     * Aplica o padrão Post-Redirect-Get (PRG) para evitar submissões acidentais duplicadas.
     *
     * @param req  objeto {@link HttpServletRequest} contendo os dados enviados no formulário
     * @param resp objeto {@link HttpServletResponse} para redirecionamento após a operação
     * @throws IOException caso ocorra erro no redirecionamento HTTP
     */
    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {

        // Garante suporte adequado a caracteres UTF-8
        req.setCharacterEncoding("UTF-8");

        String acao = req.getParameter("acao");

        // Exclusão: remove o caminhão a partir do identificador informado
        if ("excluir".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                dao.excluir(id);
            } catch (Exception e) {
                System.out.println("Erro ao excluir caminhão: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/caminhao");
            return;
        }

        // Atualização: modifica os dados do veículo existente (empresa, placas, capacidade)
        if ("atualizar".equals(acao)) {
            try {
                int id = parseInt(req.getParameter("id"), 0);
                EmpresaModel empresaModel = buscarEmpresa(req);
                String placaCavalo = obterParametro(req, "placaCavalo", "placa_cavalo");
                String placaCarreta = obterParametro(req, "placaCarreta", "placa_carreta");
                int capacidadeMaxima = parseInt(obterParametro(req, "capacidadeMaxima", "capacidade_maxima"), 0);

                CaminhaoModel caminhaoModel = new CaminhaoModel(id, empresaModel, placaCavalo, placaCarreta, capacidadeMaxima);
                dao.atualizar(caminhaoModel, id);
            } catch (Exception e) {
                System.out.println("Erro ao atualizar caminhão: " + e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/caminhao");
            return;
        }

        // Cadastro: persiste um novo caminhão no sistema
        EmpresaModel empresaModel = buscarEmpresa(req);
        String placaCavalo = obterParametro(req, "placaCavalo", "placa_cavalo");
        String placaCarreta = obterParametro(req, "placaCarreta", "placa_carreta");
        int capacidadeMaxima = parseInt(obterParametro(req, "capacidadeMaxima", "capacidade_maxima"), 0);

        CaminhaoModel novoCaminhao = new CaminhaoModel(empresaModel, placaCavalo, placaCarreta, capacidadeMaxima);
        dao.inserir(novoCaminhao);

        resp.sendRedirect(req.getContextPath() + "/caminhao");
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Recupera a entidade {@link EmpresaModel} associada a partir do ID enviado na requisição.
     * Resolve o relacionamento relacional entre caminhão e empresa transportadora proprietária.
     *
     * @param req requisição HTTP contendo o identificador da empresa
     * @return objeto {@link EmpresaModel} correspondente ou null se não informado ou inválido
     */
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
                System.out.println("Erro ao buscar empresa do caminhão: " + e.getMessage());
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
            System.out.println("Erro ao converter número (" + texto + "): " + e.getMessage());
            return padrao;
        }
    }
}
