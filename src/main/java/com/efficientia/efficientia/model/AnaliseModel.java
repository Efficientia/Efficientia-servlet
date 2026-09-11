package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class AnaliseModel implements Model {

    private int id;
    private AnalistaModel analistaModel;
    private TrajetoModel trajetoModel;
    private LocalDateTime dataAnalise;
    private String statusAnalise;
    private String observacao;

//Construtor com id do banco
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

//Construtor sem id padrão
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

//Setters sem id
    public void setAnalistaModel(AnalistaModel analistaModel) {
        this.analistaModel = analistaModel;
    }

    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    public void setStatusAnalise(String statusAnalise) {
        this.statusAnalise = statusAnalise;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public AnalistaModel getAnalista() {
        return analistaModel;
    }

    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    public String getStatusAnalise() {
        return statusAnalise;
    }

    public String getObservacao() {
        return observacao;
    }

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
