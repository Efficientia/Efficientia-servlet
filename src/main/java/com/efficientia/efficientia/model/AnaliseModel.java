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

    /** Status atual da análise (ex: "CONCLUIDA", "EM_ANDAMENTO"). */
    private String statusAnalise;

    /** Parecer técnico, observações qualitativas ou apontamentos de não conformidade. */
    private String observacao;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID.
     * Utilizado na reconstituição da análise a partir de registros do banco de dados.
     *
     * @param id            identificador único da análise
     * @param analistaModel analista responsável
     * @param trajetoModel  trajeto analisado
     * @param dataAnalise   data e hora da realização da análise
     * @param statusAnalise status da análise técnica
     * @param observacao    parecer ou apontamentos técnicos
     */
    public AnaliseModel(int id,
                        AnalistaModel analistaModel,
                        TrajetoModel trajetoModel,
                        LocalDateTime dataAnalise,
                        String statusAnalise,
                        String observacao) {
        this.id = id;
        this.analistaModel = analistaModel;
        this.trajetoModel = trajetoModel;
        this.dataAnalise = dataAnalise;
        this.statusAnalise = statusAnalise;
        this.observacao = observacao;
    }

    /**
     * Construtor sem ID.
     * Utilizado no momento da criação de um novo laudo de análise antes da persistência.
     *
     * @param analistaModel analista responsável
     * @param trajetoModel  trajeto analisado
     * @param dataAnalise   data e hora da realização da análise
     * @param statusAnalise status da análise técnica
     * @param observacao    parecer ou apontamentos técnicos
     */
    public AnaliseModel(AnalistaModel analistaModel,
                        TrajetoModel trajetoModel,
                        LocalDateTime dataAnalise,
                        String statusAnalise,
                        String observacao) {
        this.analistaModel = analistaModel;
        this.trajetoModel = trajetoModel;
        this.dataAnalise = dataAnalise;
        this.statusAnalise = statusAnalise;
        this.observacao = observacao;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único da análise.
     * @return ID numérico
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único da análise.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Método utilitário alternativo para obter o analista responsável.
     * @return objeto {@link AnalistaModel} associado
     */
    public AnalistaModel getAnalista() {
        return analistaModel;
    }

    /**
     * Obtém o analista responsável pela análise.
     * @return objeto {@link AnalistaModel} associado
     */
    public AnalistaModel getAnalistaModel() {
        return analistaModel;
    }

    /**
     * Define o analista responsável pela análise.
     * @param analistaModel novo analista
     */
    public void setAnalistaModel(AnalistaModel analistaModel) {
        this.analistaModel = analistaModel;
    }

    /**
     * Método utilitário alternativo para obter o trajeto analisado.
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    /**
     * Obtém o trajeto submetido à análise.
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajetoModel() {
        return trajetoModel;
    }

    /**
     * Define o trajeto submetido à análise.
     * @param trajetoModel novo trajeto
     */
    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

    /**
     * Obtém a data e hora da análise.
     * @return data e hora (LocalDateTime)
     */
    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    /**
     * Define a data e hora da análise.
     * @param dataAnalise nova data e hora
     */
    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    /**
     * Obtém o status atual da análise.
     * @return descrição do status (ex: CONCLUIDA)
     */
    public String getStatusAnalise() {
        return statusAnalise;
    }

    /**
     * Define o status atual da análise.
     * @param statusAnalise novo status
     */
    public void setStatusAnalise(String statusAnalise) {
        this.statusAnalise = statusAnalise;
    }

    /**
     * Obtém o parecer ou observação do analista.
     * @return texto do parecer
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Define o parecer ou observação do analista.
     * @param observacao novo parecer ou anotações
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados da análise.
     * @return string formatada contendo os atributos da análise
     */
    @Override
    public String toString() {
        return "AnaliseModel{" +
                "id=" + id +
                ", analistaModel=" + analistaModel +
                ", trajetoModel=" + trajetoModel +
                ", dataAnalise=" + dataAnalise +
                ", statusAnalise='" + statusAnalise + '\'' +
                ", observacao='" + observacao + '\'' +
                '}';
    }
}
