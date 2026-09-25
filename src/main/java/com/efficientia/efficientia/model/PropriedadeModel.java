package com.efficientia.efficientia.model;

/**
 * Modelo para Propriedade (propriedade rural / fazenda).
 */
public class PropriedadeModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private PecuaristaModel pecuaristaModel;
    private EnderecoModel enderecoModel;
    private String nome;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
    public PropriedadeModel(int id,
                            PecuaristaModel pecuaristaModel,
                            EnderecoModel enderecoModel,
                            String nome) {
        this.id = id;
        this.pecuaristaModel = pecuaristaModel;
        this.enderecoModel = enderecoModel;
        this.nome = nome;
    }

    // Construtor sem id (novo cadastro)
    public PropriedadeModel(PecuaristaModel pecuaristaModel,
                            EnderecoModel enderecoModel,
                            String nome) {
        this.pecuaristaModel = pecuaristaModel;
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

    public PecuaristaModel getPecuaristaModel() {
        return pecuaristaModel;
    }

    public void setPecuaristaModel(PecuaristaModel pecuaristaModel) {
        this.pecuaristaModel = pecuaristaModel;
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
        return "PropriedadeModel{" +
                "id=" + id +
                ", pecuaristaModel=" + pecuaristaModel +
                ", enderecoModel=" + enderecoModel +
                ", nome='" + nome + '\'' +
                '}';
    }
}
