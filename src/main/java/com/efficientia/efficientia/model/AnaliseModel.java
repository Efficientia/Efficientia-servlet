package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

/**
 * Modelo representativo de uma Análise de Trajeto no sistema Efficientia.
 *
 * Contém o parecer técnico emitido por um {@link AnalistaModel} sobre a viagem
 * de um {@link TrajetoModel}, avaliando tempos de deslocamento, condições da carga
 * viva, eventuais paradas e conformidade geral do transporte pecuário.
 */
public class AnaliseModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único da análise (chave primária no banco de dados). */
    private int id;

    /** Analista técnico responsável pela avaliação do trajeto. */
    private AnalistaModel analistaModel;

    /** Trajeto / viagem que está sendo submetido a auditoria. */
    private TrajetoModel trajetoModel;

    /** Data e hora em que a análise técnica foi registrada ou atualizada. */
    private LocalDateTime dataAnalise;

    /** Status atual da análise ({@link StatusAnalise#EM_ANDAMENTO} ou {@link StatusAnalise#CONCLUIDA}). */
    private StatusAnalise status;

    /** Parecer técnico, observações qualitativas ou apontamentos de não conformidade. */
    private String observacao;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e enum StatusAnalise.
     * Utilizado na reconstituição da análise a partir de registros do banco de dados.
     *
     * @param id            identificador único da análise
     * @param analistaModel analista responsável
     * @param trajetoModel  trajeto analisado
     * @param dataAnalise   data e hora da realização da análise
     * @param status        status da análise técnica
     * @param observacao    parecer ou apontamentos técnicos
     */
    public AnaliseModel(int id,
                        AnalistaModel analistaModel,
                        TrajetoModel trajetoModel,
                        LocalDateTime dataAnalise,
                        StatusAnalise status,
                        String observacao) {
        this.id = id;
        this.analistaModel = analistaModel;
        this.trajetoModel = trajetoModel;
        this.dataAnalise = dataAnalise;
        this.status = status;
        this.observacao = observacao;
    }

    /**
     * Construtor completo com ID e status em texto (compatibilidade).
     *
     * @param id            identificador único da análise
     * @param analistaModel analista responsável
     * @param trajetoModel  trajeto analisado
     * @param dataAnalise   data e hora da realização da análise
     * @param statusAnalise status da análise técnica em texto
     * @param observacao    parecer ou apontamentos técnicos
     */
    public AnaliseModel(int id,
                        AnalistaModel analistaModel,
                        TrajetoModel trajetoModel,
                        LocalDateTime dataAnalise,
                        String statusAnalise,
                        String observacao) {
        this(id, analistaModel, trajetoModel, dataAnalise, converterStatus(statusAnalise), observacao);
    }

    /**
     * Construtor sem ID com enum StatusAnalise.
     * Utilizado no momento da criação de um novo laudo de análise antes da persistência.
     *
     * @param analistaModel analista responsável
     * @param trajetoModel  trajeto analisado
     * @param dataAnalise   data e hora da realização da análise
     * @param status        status da análise técnica
     * @param observacao    parecer ou apontamentos técnicos
     */
    public AnaliseModel(AnalistaModel analistaModel,
                        TrajetoModel trajetoModel,
                        LocalDateTime dataAnalise,
                        StatusAnalise status,
                        String observacao) {
        this.analistaModel = analistaModel;
        this.trajetoModel = trajetoModel;
        this.dataAnalise = dataAnalise;
        this.status = status;
        this.observacao = observacao;
    }

    /**
     * Construtor sem ID e status em texto (compatibilidade).
     *
     * @param analistaModel analista responsável
     * @param trajetoModel  trajeto analisado
     * @param dataAnalise   data e hora da realização da análise
     * @param statusAnalise status da análise técnica em texto
     * @param observacao    parecer ou apontamentos técnicos
     */
    public AnaliseModel(AnalistaModel analistaModel,
                        TrajetoModel trajetoModel,
                        LocalDateTime dataAnalise,
                        String statusAnalise,
                        String observacao) {
        this(analistaModel, trajetoModel, dataAnalise, converterStatus(statusAnalise), observacao);
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único da análise.
     *
     * @return ID numérico
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único da análise.
     *
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Método utilitário alternativo para obter o analista responsável.
     *
     * @return objeto {@link AnalistaModel} associado
     */
    public AnalistaModel getAnalista() {
        return analistaModel;
    }

    /**
     * Obtém o analista responsável pela análise.
     *
     * @return objeto {@link AnalistaModel} associado
     */
    public AnalistaModel getAnalistaModel() {
        return analistaModel;
    }

    /**
     * Define o analista responsável pela análise.
     *
     * @param analistaModel novo analista
     */
    public void setAnalistaModel(AnalistaModel analistaModel) {
        this.analistaModel = analistaModel;
    }

    /**
     * Método utilitário alternativo para obter o trajeto analisado.
     *
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    /**
     * Obtém o trajeto submetido à análise.
     *
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajetoModel() {
        return trajetoModel;
    }

    /**
     * Define o trajeto submetido à análise.
     *
     * @param trajetoModel novo trajeto
     */
    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

    /**
     * Obtém a data e hora da análise.
     *
     * @return data e hora (LocalDateTime)
     */
    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    /**
     * Define a data e hora da análise.
     *
     * @param dataAnalise nova data e hora
     */
    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    /**
     * Obtém o status da análise como enum.
     *
     * @return status da análise ({@link StatusAnalise})
     */
    public StatusAnalise getStatus() {
        return status;
    }

    /**
     * Define o status da análise.
     *
     * @param status novo status ({@link StatusAnalise})
     */
    public void setStatus(StatusAnalise status) {
        this.status = status;
    }

    /**
     * Obtém o status da análise (compatibilidade com convenção anterior).
     *
     * @return status da análise
     */
    public StatusAnalise getStatusAnalise() {
        return status;
    }

    /**
     * Define o status da análise via enum.
     *
     * @param status novo status
     */
    public void setStatusAnalise(StatusAnalise status) {
        this.status = status;
    }

    /**
     * Define o status da análise a partir de uma String (compatibilidade).
     *
     * @param statusAnalise string com o status
     */
    public void setStatusAnalise(String statusAnalise) {
        this.status = converterStatus(statusAnalise);
    }

    /**
     * Obtém o parecer ou observação do analista.
     *
     * @return texto do parecer
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Define o parecer ou observação do analista.
     *
     * @param observacao novo parecer ou anotações
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Converte texto em {@link StatusAnalise} defensivamente.
     *
     * @param statusStr texto representando o status
     * @return enum {@link StatusAnalise} correspondente ou null
     */
    private static StatusAnalise converterStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            return null;
        }
        try {
            return StatusAnalise.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados da análise.
     *
     * @return string formatada contendo os atributos da análise
     */
    @Override
    public String toString() {
        return "AnaliseModel{" +
                "id=" + id +
                ", analistaModel=" + analistaModel +
                ", trajetoModel=" + trajetoModel +
                ", dataAnalise=" + dataAnalise +
                ", status=" + status +
                ", observacao='" + observacao + '\'' +
                '}';
    }
}
