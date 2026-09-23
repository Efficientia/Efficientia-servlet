package com.efficientia.efficientia.model;

public class PropriedadeModel implements Model {

    private int id;
    private PecuaristaModel pecuaristaModel;
    private EnderecoModel enderecoModel;
    private String nome;

    // Construtor com id do banco
    public PropriedadeModel(int id,
                            PecuaristaModel pecuaristaModel,
                            EnderecoModel enderecoModel,
                            String nome) {
        this.id = id;
        this.pecuaristaModel = pecuaristaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // Construtor sem id padrão
    public PropriedadeModel(PecuaristaModel pecuaristaModel,
                            EnderecoModel enderecoModel,
                            String nome) {
        this.pecuaristaModel = pecuaristaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // Setters
    public void setPecuaristaModel(PecuaristaModel pecuaristaModel) {
        this.pecuaristaModel = pecuaristaModel;
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

    public PecuaristaModel getPecuaristaModel() {
        return pecuaristaModel;
    }

    public EnderecoModel getEnderecoModel() {
        return enderecoModel;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "PropriedadeModel{" +
                "id=" + id +
                ", pecuaristaModel=" + pecuaristaModel +
                ", enderecoModel=" + enderecoModel +
                ", nome='" + nome + '\'' +
                '}';
    }
}
