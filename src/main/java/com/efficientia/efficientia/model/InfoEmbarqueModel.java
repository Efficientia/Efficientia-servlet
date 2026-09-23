package com.efficientia.efficientia.model;

/**
 * Modelo para Informações de Embarque vinculadas a um trajeto.
 */
public class InfoEmbarqueModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private String nome;
    private TrajetoModel trajetoModel;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
    public InfoEmbarqueModel(int id,
                             String nome,
                             TrajetoModel trajetoModel) {
        this.id = id;
        this.nome = nome;
        this.trajetoModel = trajetoModel;
    }

    // Construtor sem id (novo cadastro)
    public InfoEmbarqueModel(String nome,
                             TrajetoModel trajetoModel) {
        this.nome = nome;
        this.trajetoModel = trajetoModel;
    }

    // ==================== GETTERS E SETTERS ====================

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    // ==================== TO STRING ====================

    @Override
    public String toString() {
        return "InfoEmbarqueModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", trajetoModel=" + trajetoModel +
                '}';
    }
}
