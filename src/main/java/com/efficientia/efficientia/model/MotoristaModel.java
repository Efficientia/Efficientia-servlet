package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class MotoristaModel extends UsuarioModel implements Model{

    public MotoristaModel(int id,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    public MotoristaModel(String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(nome, assinatura, dataNascimento, senha, email, telefone);
    }

    @Override
    public String toString() {
        return "MotoristaModel{" +
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