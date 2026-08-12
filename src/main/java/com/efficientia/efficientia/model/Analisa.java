package com.efficientia.efficientia.model;
//testando
import java.time.LocalDateTime;

public class Analisa {
    private int id;
    private Analista analista;
    private Trajeto trajeto;
    private LocalDateTime dataAnalise;
    private String statusAnalise; //pode virar enum
    private String observacao; //pode virar um txt

    public Analisa(int id, Analista analista,
                   Trajeto trajeto,
                   LocalDateTime dataAnalise,
                   String statusAnalise,
                   String observacao) {
        this.id = id;
        this.analista = analista;
        this.trajeto = trajeto;
        this.dataAnalise = dataAnalise;
        this.statusAnalise = statusAnalise;
        this.observacao = observacao;
    }

    public Analisa() {
    }

    public int getId() {
        return id;
    }

    public Analista getAnalista() {
        return analista;
    }

    public Trajeto getTrajeto() {
        return trajeto;
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

    //Adicionar setter onde for necessario
}
