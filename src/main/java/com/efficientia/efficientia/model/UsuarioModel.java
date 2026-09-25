package com.efficientia.efficientia.model;

import java.time.LocalDate;

/**
 * Modelo base para os usuários do sistema (DonoFazenda, Motorista, Analista, Admin).
 */
public abstract class UsuarioModel implements Model {

    // ==================== ATRIBUTOS ====================
    protected int id;
    protected EmpresaModel empresaModel;
    protected String nome;
    protected String assinatura;
    protected LocalDate dataNascimento;
    protected String senha;
    protected String email;
    protected String telefone;

    // ==================== CONSTRUTORES ====================

    // Construtor completo com id e empresa (leitura do banco)
    public UsuarioModel(int id,
                        EmpresaModel empresaModel,
                        String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this.id = id;
        this.empresaModel = empresaModel;
        this.nome = nome;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
    }

    // Construtor com id sem empresa
    public UsuarioModel(int id,
                        String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this(id, null, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    // Construtor sem id com empresa (novo cadastro)
    public UsuarioModel(EmpresaModel empresaModel,
                        String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this.empresaModel = empresaModel;
        this.nome = nome;
        this.assinatura = assinatura;
        this.dataNascimento = dataNascimento;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
    }

    // Construtor sem id sem empresa (novo cadastro básico)
    public UsuarioModel(String nome,
                        String assinatura,
                        LocalDate dataNascimento,
                        String senha,
                        String email,
                        String telefone) {
        this(null, nome, assinatura, dataNascimento, senha, email, telefone);
    }

    // ==================== GETTERS E SETTERS ====================

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmpresaModel getEmpresaModel() {
        return empresaModel;
    }

    public void setEmpresaModel(EmpresaModel empresaModel) {
        this.empresaModel = empresaModel;
    }

    public EmpresaModel getEmpresa() {
        return empresaModel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // ==================== TO STRING ====================

    @Override
    public String toString() {
        return "UsuarioModel{" +
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
