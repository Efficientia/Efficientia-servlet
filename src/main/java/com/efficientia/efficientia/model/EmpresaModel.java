package com.efficientia.efficientia.model;

public class EmpresaModel implements Model {

    private int id;
    private String nome;
    private String cnpj;

//Construtor com id do banco
    public EmpresaModel(int id, String nome, String cnpj) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
    }

//Construtor sem id padrão
    public EmpresaModel(String nome, String cnpj) {
        this.nome = nome;
        this.cnpj = cnpj;
    }

//Setters sem id
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    @Override
    public String toString() {
        return "EmpresaModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
