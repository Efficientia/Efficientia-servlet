package com.efficientia.efficientia.model;

public class AdminModel implements Model{

    private int id;
    private String email;
    private String senha;
    private String nome;
//Construtor com id do banco
    public AdminModel(int id, String email, String senha, String nome) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }
//Construtor sem id padrão
    public AdminModel(String email, String senha, String nome) {
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }
//Setters sem id
    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
//Getters
    @Override
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "AdminModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
