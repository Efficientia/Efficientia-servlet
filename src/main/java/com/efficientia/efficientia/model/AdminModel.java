package com.efficientia.efficientia.model;

/**
 * Modelo para o Administrador do sistema.
 */
public class AdminModel implements Model {

    // ==================== ATRIBUTOS ====================
    private int id;
    private String email;
    private String senha;
    private String nome;

    // ==================== CONSTRUTORES ====================

    // Construtor com id (banco)
    public AdminModel(int id, String email, String senha, String nome) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    // Construtor sem id (novo cadastro)
    public AdminModel(String email, String senha, String nome) {
        this.email = email;
        this.senha = senha;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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
        return "AdminModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
