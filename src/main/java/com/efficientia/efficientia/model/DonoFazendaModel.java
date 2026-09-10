package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class DonoFazendaModel implements Model {
    private int id;
    private String cpf;
    private String assinatura;
    private LocalDate dataNascimento;
    private String nome;
    private String senha;
    private String email;
    private String telefone;

//Construtor com id do banco
    public DonoFazendaModel(int id,
                            String cpf,
                            String assinatura,
                            LocalDate dataNascimento,
                            String nome,
                            String senha,
                            String email,
                            String telefone) {
        this.id = id;
        this.cpf = cpf;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;

        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }

        if (telefone != null && telefone.length() > 11) {
            this.telefone = "nulo";
        }
    }

//Construtor sem id padrão
    public DonoFazendaModel(String cpf,
                            String assinatura,
                            LocalDate dataNascimento,
                            String nome,
                            String senha,
                            String email,
                            String telefone) {
        this.cpf = cpf;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;

        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }

        if (telefone != null && telefone.length() > 11) {
            this.telefone = "nulo";
        }
    }

//Setters sem id
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getCpf() {
        return cpf;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getNome() {
        return nome;
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
        return "DonoFazendaModel{" +
                "id=" + id +
                ", cpf='" + cpf + '\'' +
                ", assinatura='" + assinatura + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", nome='" + nome + '\'' +
                ", senha='" + senha + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
