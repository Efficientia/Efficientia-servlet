package com.efficientia.efficientia.model;

public class FazendaModel implements Model {

    private int id;
    private DonoFazendaModel donoFazendaModel;
    private EnderecoModel enderecoModel;
    private String nome;

    // Construtor com id do banco
    public FazendaModel(int id,
                        DonoFazendaModel donoFazendaModel,
                        EnderecoModel enderecoModel,
                        String nome) {
        this.id = id;
        this.donoFazendaModel = donoFazendaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // Construtor sem id padrão
    public FazendaModel(DonoFazendaModel donoFazendaModel,
                        EnderecoModel enderecoModel,
                        String nome) {
        this.donoFazendaModel = donoFazendaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // Setters
    public void setDonoFazendaModel(DonoFazendaModel donoFazendaModel) {
        this.donoFazendaModel = donoFazendaModel;
    }

    public void setEnderecoModel(EnderecoModel enderecoModel) {
        this.enderecoModel = enderecoModel;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getters
    @Override
    public int getId() {
        return id;
    }

    public DonoFazendaModel getDonoFazendaModel() {
        return donoFazendaModel;
    }

    public EnderecoModel getEnderecoModel() {
        return enderecoModel;
    }

    public String getNome() {
        return nome;
    }

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
