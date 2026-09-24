package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo para Analista (especialização de UsuarioModel).
 */
public class AnalistaModel extends UsuarioModel implements Model {

    // ==================== ATRIBUTOS ====================
    private String cpf;
    private String codigo;

    // ==================== CONSTRUTORES ====================

    // Construtor completo com id e empresa (leitura do banco)
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

    // Construtor com id sem empresa
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

    // Construtor sem id com empresa (novo cadastro)
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

    // Construtor sem id sem empresa (novo cadastro avulso)
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

    // ==================== GETTERS E SETTERS ====================

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    // ==================== TO STRING ====================

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

