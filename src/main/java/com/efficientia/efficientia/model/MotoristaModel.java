package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class MotoristaModel extends UsuarioModel implements Model {

//Construtor com id do banco
    public MotoristaModel(int id,
                          EmpresaModel empresaModel,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    public MotoristaModel(int id,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
    }

//Construtor sem id padrão
    public MotoristaModel(EmpresaModel empresaModel,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
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
                ", empresaModel=" + empresaModel +
                ", nome='" + nome + '\'' +
                ", assinatura='" + assinatura + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", senha='" + senha + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}