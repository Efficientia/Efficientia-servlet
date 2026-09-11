package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class ParadaImprevistaModel implements Model {

    private int id;
    private TrajetoModel trajetoModel;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private String motivo;
    private String observacao;

//Construtor com id do banco (completo)
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

//Construtor com id do banco (legado)
    public ParadaImprevistaModel(int id,
                                 TrajetoModel trajetoModel,
                                 LocalDateTime dataHoraInicio,
                                 LocalDateTime dataHoraFim,
                                 String motivo) {
        this.id = id;
        this.trajetoModel = trajetoModel;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.motivo = motivo;
    }

//Construtor sem id padrão (completo)
    public ParadaImprevistaModel(TrajetoModel trajetoModel,
                                 LocalDateTime dataHoraInicio,
                                 LocalDateTime dataHoraFim,
                                 String motivo,
                                 String observacao) {
        this.trajetoModel = trajetoModel;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.motivo = motivo;
        this.observacao = observacao;
    }

//Construtor sem id padrão (legado)
    public ParadaImprevistaModel(TrajetoModel trajetoModel,
                                 LocalDateTime dataHoraInicio,
                                 LocalDateTime dataHoraFim,
                                 String motivo) {
        this.trajetoModel = trajetoModel;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.motivo = motivo;
    }

//Setters sem id
    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getObservacao() {
        return observacao;
    }

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
