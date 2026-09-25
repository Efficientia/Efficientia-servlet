package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo para Motorista (especialização de UsuarioModel).
 */
public class MotoristaModel extends UsuarioModel implements Model {

    // ==================== CONSTRUTORES ====================

    // Construtor completo com id e empresa (leitura do banco)
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

    // Construtor com id sem empresa
    public MotoristaModel(int id,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(id, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    // Construtor sem id com empresa (novo cadastro)
    public MotoristaModel(EmpresaModel empresaModel,
                          String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(empresaModel, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    // Construtor sem id sem empresa (novo cadastro avulso)
    public MotoristaModel(String nome,
                          String assinatura,
                          LocalDate dataNascimento,
                          String senha,
                          String email,
                          String telefone) {
        super(nome, assinatura, dataNascimento, senha, email, telefone);
    }

    // ==================== TO STRING ====================

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