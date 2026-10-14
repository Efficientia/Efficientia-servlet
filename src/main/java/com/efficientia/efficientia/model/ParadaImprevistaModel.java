package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

/**
 * Modelo representativo de uma Parada Imprevista durante o trajeto no sistema Efficientia.
 *
 * Registra ocorrências inesperadas que interrompem a viagem de transporte pecuário
 * (tais como defeito mecânico, bloqueio de pista, fiscalização da PRF/órgão sanitário,
 * problemas com a carga viva, etc.), permitindo auditoria detalhada de tempo e causas de atraso.
 */
public class ParadaImprevistaModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único da parada imprevista (chave primária no banco). */
    private int id;

    /** Trajeto / viagem no qual a parada ocorreu. */
    private TrajetoModel trajetoModel;

    /** Data e hora exatas de início da parada imprevista. */
    private LocalDateTime dataHoraInicio;

    /** Data e hora de término da parada / retomada da viagem. */
    private LocalDateTime dataHoraFim;

    /** Motivo principal da interrupção (ex: Pneu furado, Fiscalização, Condição climática). */
    private String motivo;

    /** Observações detalhadas, providências adotadas ou justificativas adicionais. */
    private String observacao;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e observação.
     * Utilizado na reconstituição da ocorrência a partir do banco de dados.
     *
     * @param id             identificador único do registro
     * @param trajetoModel   trajeto no qual a parada ocorreu
     * @param dataHoraInicio momento de início da parada
     * @param dataHoraFim    momento de término da parada
     * @param motivo         motivo principal da parada
     * @param observacao     detalhes adicionais ou justificativa
     */
    public ParadaImprevistaModel(int id,
                                 TrajetoModel trajetoModel,
                                 LocalDateTime dataHoraInicio,
                                 LocalDateTime dataHoraFim,
                                 String motivo,
                                 String observacao) {
        this.id = id;
        this.trajetoModel = trajetoModel;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.motivo = motivo;
        this.observacao = observacao;
    }

    /**
     * Construtor com ID e sem campo de observação.
     * Utilizado para carregar paradas registradas sem notas adicionais.
     *
     * @param id             identificador único do registro
     * @param trajetoModel   trajeto no qual a parada ocorreu
     * @param dataHoraInicio momento de início da parada
     * @param dataHoraFim    momento de término da parada
     * @param motivo         motivo principal da parada
     */
    public ParadaImprevistaModel(int id,
                                 TrajetoModel trajetoModel,
                                 LocalDateTime dataHoraInicio,
                                 LocalDateTime dataHoraFim,
                                 String motivo) {
        this(id, trajetoModel, dataHoraInicio, dataHoraFim, motivo, null);
    }

    /**
     * Construtor sem ID.
     * Utilizado no registro em tempo real de uma nova parada imprevista.
     *
     * @param trajetoModel   trajeto no qual a parada ocorreu
     * @param dataHoraInicio momento de início da parada
     * @param dataHoraFim    momento de término da parada
     * @param motivo         motivo principal da parada
     */
    public ParadaImprevistaModel(TrajetoModel trajetoModel,
                                 LocalDateTime dataHoraInicio,
                                 LocalDateTime dataHoraFim,
                                 String motivo) {
        this.trajetoModel = trajetoModel;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.motivo = motivo;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único da parada.
     * @return ID numérico
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único da parada.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Método utilitário alternativo para obter o trajeto vinculado.
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    /**
     * Obtém o trajeto vinculado à ocorrência.
     * @return objeto {@link TrajetoModel} vinculado
     */
    public TrajetoModel getTrajetoModel() {
        return trajetoModel;
    }

    /**
     * Define o trajeto vinculado à ocorrência.
     * @param trajetoModel novo trajeto vinculado
     */
    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

    /**
     * Obtém o momento de início da parada.
     * @return data e hora de início (LocalDateTime)
     */
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Define o momento de início da parada.
     * @param dataHoraInicio nova data e hora de início
     */
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Obtém o momento de encerramento da parada.
     * @return data e hora de término (LocalDateTime)
     */
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    /**
     * Define o momento de encerramento da parada.
     * @param dataHoraFim nova data e hora de término
     */
    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    /**
     * Obtém o motivo da parada imprevista.
     * @return descrição do motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Define o motivo da parada imprevista.
     * @param motivo nova descrição do motivo
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Obtém as observações adicionais sobre a parada.
     * @return texto de observação ou null
     */
    public String getObservacao() {
        return observacao;
    }

    /**
     * Define observações adicionais sobre a parada.
     * @param observacao novas notas ou providências tomadas
     */
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados da parada imprevista.
     * @return string formatada contendo os atributos da ocorrência
     */
    @Override
    public String toString() {
        return "ParadaImprevistaModel{" +
                "id=" + id +
                ", trajetoModel=" + trajetoModel +
                ", dataHoraInicio=" + dataHoraInicio +
                ", dataHoraFim=" + dataHoraFim +
                ", motivo='" + motivo + '\'' +
                ", observacao='" + observacao + '\'' +
                '}';
    }
}
