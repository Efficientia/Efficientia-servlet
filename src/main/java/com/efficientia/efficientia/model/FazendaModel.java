package com.efficientia.efficientia.model;

/**
 * Modelo para Fazenda (propriedade rural).
 */
public class FazendaModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private DonoFazendaModel donoFazendaModel;
    private EnderecoModel enderecoModel;
    private String nome;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
    public FazendaModel(int id,
                        DonoFazendaModel donoFazendaModel,
                        EnderecoModel enderecoModel,
                        String nome) {
        this.id = id;
        this.donoFazendaModel = donoFazendaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // Construtor sem id (novo cadastro)
    public FazendaModel(DonoFazendaModel donoFazendaModel,
                        EnderecoModel enderecoModel,
                        String nome) {
        this.donoFazendaModel = donoFazendaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // ==================== GETTERS E SETTERS ====================

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DonoFazendaModel getDonoFazendaModel() {
        return donoFazendaModel;
    }

    public void setDonoFazendaModel(DonoFazendaModel donoFazendaModel) {
        this.donoFazendaModel = donoFazendaModel;
    }

    public EnderecoModel getEnderecoModel() {
        return enderecoModel;
    }

    public void setEnderecoModel(EnderecoModel enderecoModel) {
        this.enderecoModel = enderecoModel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // ==================== TO STRING ====================

    @Override
    public String toString() {
        return "FazendaModel{" +
                "id=" + id +
                ", donoFazendaModel=" + donoFazendaModel +
                ", enderecoModel=" + enderecoModel +
                ", nome='" + nome + '\'' +
                '}';
    }
}
