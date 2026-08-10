package com.efficientia.efficientia.model;

public class TrajetoCarga {
    private int id;
    private CargaEmbarque cargaEmbarque; //Carga de animais
    private Trajeto trajeto; //Classe que representa o documento
    private int quantidade;//QUnatidade de animais

    public TrajetoCarga(int id,
                        CargaEmbarque cargaEmbarque,
                        Trajeto trajeto,
                        int quantidade) {
        this.id = id;
        this.cargaEmbarque = cargaEmbarque;
        this.trajeto = trajeto;
        this.quantidade = quantidade;
    }

    public TrajetoCarga() {
    }

    public int getId() {
        return id;
    }

    public CargaEmbarque getCargaEmbarque() {
        return cargaEmbarque;
    }

    public Trajeto getTrajeto() {
        return trajeto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    //Sem setters mas posso por o set quantidade
}
