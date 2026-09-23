package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

/**
 * Modelo para Análise de Trajeto realizada por um Analista.
 */
public class AnaliseModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private AnalistaModel analistaModel;
    private TrajetoModel trajetoModel;
    private LocalDateTime dataAnalise;
    private String statusAnalise;
    private String observacao;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
    public AnaliseModel(int id, AnalistaModel analistaModel,
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

    // Construtor sem id (novo cadastro)
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

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AnalistaModel getAnalista() {
        return analistaModel;
    }

    public AnalistaModel getAnalistaModel() {
        return analistaModel;
    }

    public void setAnalistaModel(AnalistaModel analistaModel) {
        this.analistaModel = analistaModel;
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

    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    public String getStatusAnalise() {
        return statusAnalise;
    }

    public void setStatusAnalise(String statusAnalise) {
        this.statusAnalise = statusAnalise;
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
