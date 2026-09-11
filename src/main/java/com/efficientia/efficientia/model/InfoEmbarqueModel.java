package com.efficientia.efficientia.model;

public class InfoEmbarqueModel implements Model {
    private int id;
    private String nome;
    private TrajetoModel trajetoModel;

//Construtor com id do banco
    public InfoEmbarqueModel(int id,
                             String nome,
                             TrajetoModel trajetoModel) {
        this.id = id;
        this.nome = nome;
        this.trajetoModel = trajetoModel;
    }

//Construtor sem id padrão
    public InfoEmbarqueModel(String nome,
                             TrajetoModel trajetoModel) {
        this.nome = nome;
        this.trajetoModel = trajetoModel;
    }

//Setters sem id
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTrajetoModel(TrajetoModel trajetoModel) {
        this.trajetoModel = trajetoModel;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TrajetoModel getTrajeto() {
        return trajetoModel;
    }

    @Override
    public String toString() {
        return "InfoEmbarqueModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", trajetoModel=" + trajetoModel +
                '}';
    }
}
