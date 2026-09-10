package com.efficientia.efficientia.model;

import java.time.LocalDate;

public abstract class UsuarioModel implements Model {
    protected int id;
    protected String nome;
    protected String assinatura;
    protected LocalDate dataNascimento;
    protected String senha;
    protected String email;
    protected String telefone;

//Construtor com id do banco
    public UsuarioModel(int id,
                        String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this.id = id;
        this.nome = nome;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
    }

//Construtor sem id padrão
    public UsuarioModel(String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this.nome = nome;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
    }

//Setters sem id
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

//Getters
    @Override
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    @Override
    public String toString() {
        return "UsuarioModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", assinatura='" + assinatura + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", senha='" + senha + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
