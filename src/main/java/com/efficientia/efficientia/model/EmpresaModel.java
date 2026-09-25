package com.efficientia.efficientia.model;

/**
 * Modelo para Empresa (transportadora ou cliente).
 */
public class EmpresaModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private String nome;
    private String cnpj;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
    public EmpresaModel(int id, String nome, String cnpj) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
    }

    // Construtor sem id (novo cadastro)
    public EmpresaModel(String nome, String cnpj) {
        this.nome = nome;
        this.cnpj = cnpj;
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    // ==================== TO STRING ====================

    @Override
    public String toString() {
        return "EmpresaModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
