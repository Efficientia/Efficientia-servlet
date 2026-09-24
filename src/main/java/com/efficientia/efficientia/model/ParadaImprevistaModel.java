package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

/**
 * Modelo para registro de Paradas Imprevistas durante um trajeto.
 */
public class ParadaImprevistaModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private TrajetoModel trajetoModel;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String motivo;
    private String observacao;

    // ==================== CONSTRUTORES ====================

    // Construtor completo com id e observação (banco)
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

    // Construtor com id sem observação
    public ParadaImprevistaModel(int id,
                                 TrajetoModel trajetoModel,
                                 LocalDateTime dataHoraInicio,
                                 LocalDateTime dataHoraFim,
                                 String motivo) {
        this(id, trajetoModel, dataHoraInicio, dataHoraFim, motivo, null);
    }

    // Construtor sem id (novo cadastro)
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

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    public TrajetoModel getTrajetoModel() {
        return trajetoModel;
    }

    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    // ==================== TO STRING ====================

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
