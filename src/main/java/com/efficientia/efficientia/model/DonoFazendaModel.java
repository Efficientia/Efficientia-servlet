package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class DonoFazendaModel {
    private int id;
    private String cpf;
    private String assinatura; //vai virar hash
    private LocalDateTime dataNascimento;
    private String nome;
    private String senha; //vai virar hash
    private String email;
    private String telefone;

    public DonoFazendaModel(int id, String cpf,
                            String assinatura,
                            LocalDateTime dataNascimento,
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

        if(cpf.length() > 11){
            this.cpf = "nulo";
        }

        if(telefone.length() > 11){
            this.telefone = "nulo";
        }
    }

    public DonoFazendaModel() {
    }

    public int getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public LocalDateTime getDataNascimento() {
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

    //adicionar setter onde necessario


    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
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
}
