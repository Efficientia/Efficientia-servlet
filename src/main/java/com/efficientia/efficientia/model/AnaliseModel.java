package com.efficientia.efficientia.model;
//testando
import java.time.LocalDateTime;

public class AnaliseModel {
    private int id;
    private AnalistaModel analistaModel;
    private TrajetoModel trajetoModel;
    private LocalDateTime dataAnalise;
    private String statusAnalise; //pode virar enum
    private String observacao; //pode virar um txt

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

    public AnaliseModel() {
    }

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

    public void setStatusAnalise(String statusAnalise) {
        this.statusAnalise = statusAnalise;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
