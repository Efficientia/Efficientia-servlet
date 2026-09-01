package com.efficientia.efficientia.model;

public class InfoEmbarqueModel implements Model{
    private int id; //PK
    private TrajetoModel trajetoModel; //Trajeto da relação
    private String nome;//Mudar para enum depois //Parte informações embarque do documento com o checklist

    public InfoEmbarqueModel(String nome,
                             TrajetoModel trajetoModel,
                             int id) {
        this.nome = nome;
        this.trajetoModel = trajetoModel;
        this.id = id;
    }

    public InfoEmbarqueModel() {
    }

    @Override
    public int getId() {
        return id;
    }

    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    public String getNome() {
        return nome;
    }


    //Não possui setters por nao ter nada mutável, mas dar uma olhada
}
