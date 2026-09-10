package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class AnalistaModel extends UsuarioModel implements Model {

    private String codigo;

//Construtor com id do banco
    public AnalistaModel(int id,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
        this.codigo = codigo;
    }

//Construtor sem id padrão
    public AnalistaModel(String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(nome, assinatura, dataNascimento, senha, email, telefone);
        this.codigo = codigo;
    }

//Setters sem id
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

//Getters
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return "AnalistaModel{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", assinatura='" + assinatura + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", senha='" + senha + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", codigo='" + codigo + '\'' +
                '}';
    }
}

