package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

public class Analista {
    private int id;
    private String cpf;
    private String assinatura; //Vai virar hash depois
    private String codigo;
    private LocalDateTime dataNascimento; //A hora não vai importar
    private String nome;
    private String senha; //Deve virar hash
    private String email;
    private String telefone;

    public Analista(int id, String cpf,
                    String assinatura,
                    String codigo,
                    LocalDateTime dataNascimento,
                    String nome,
                    String senha,
                    String email,
                    String telefone) {
        this.id = id;
        this.cpf = cpf;
        this.assinatura = assinatura;
        this.codigo = codigo;
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

    public Analista() {
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

    public String getCodigo() {
        return codigo;
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

    //Adicionar setters onde for necessario
}

