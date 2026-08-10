package com.efficientia.efficientia.model;

public class InfoEmbarque {
    private int id; //PK
    private Trajeto trajeto; //Trajeto da relação
    private String nome;//Mudar para enum depois //Parte informações embarque do documento com o checklist

    public InfoEmbarque(String nome,
                        Trajeto trajeto,
                        int id) {
        this.nome = nome;
        this.trajeto = trajeto;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Trajeto getTrajeto() {
        return trajeto;
    }

    public String getNome() {
        return nome;
    }


    //Não possui setters por nao ter nada mutável
}
