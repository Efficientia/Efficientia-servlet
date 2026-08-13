package com.efficientia.efficientia.model;

import java.time.LocalDate;

public abstract class Usuario {
    private int id;
    private String nome;
    private String assinatura; //Virar hash
    private LocalDate dataNascimento;
    private String senha; //Hash
    private String email;
    private String telefone;

    public Usuario(int id,
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

    public Usuario() {
    }

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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
