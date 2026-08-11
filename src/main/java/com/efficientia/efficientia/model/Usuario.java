package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class Usuario {
    private String nome;
    private String assinatura; //Virar hash
    private LocalDate dataNascimento;
    private String senha; //Hash
    private String email;
    private String telefone;
    private String codigo; //codigo na empresa

    public Usuario(String codigo,
                   String telefone,
                   String email,
                   String senha,
                   LocalDate dataNascimento,
                   String assinatura,
                   String nome) {
        this.codigo = codigo;
        this.telefone = telefone;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
        this.assinatura = assinatura;
        this.nome = nome;

        if(telefone.length() > 11){
            this.telefone = "nulo";
        }
    }

    public Usuario() {
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

    public String getCodigo() {
        return codigo;
    }

    //adicionar setters
}
