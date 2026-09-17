package com.efficientia.efficientia.model;

import java.time.LocalDate;

public class AnalistaModel extends UsuarioModel implements Model {

    private String cpf;
    private String codigo;

//Construtor com id do banco
    public AnalistaModel(int id,
                         EmpresaModel empresaModel,
                         String cpf,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(id, empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
        this.cpf = cpf;
        this.codigo = codigo;
        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }
    }

    public AnalistaModel(int id,
                         String cpf,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
        this.cpf = cpf;
        this.codigo = codigo;
        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }
    }

//Construtor sem id do banco
    public AnalistaModel(EmpresaModel empresaModel,
                         String cpf,
                         String nome,
                         String assinatura,
                         LocalDate dataNascimento,
                         String senha,
                         String email,
                         String telefone,
                         String codigo) {
        super(empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
        this.cpf = cpf;
        this.codigo = codigo;
        if (cpf != null && cpf.length() > 11) {
            this.cpf = "nulo";
        }
    }

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
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

//Getters
    public String getCpf() {
        return cpf;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return "AnalistaModel{" +
                "id=" + id +
                ", empresaModel=" + empresaModel +
                ", cpf='" + cpf + '\'' +
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

